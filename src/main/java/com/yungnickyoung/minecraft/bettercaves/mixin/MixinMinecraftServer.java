package com.yungnickyoung.minecraft.bettercaves.mixin;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.init.BCModFeature;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerResources;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
    @Shadow @Final protected RegistryAccess.RegistryHolder registryHolder;

    /**
     * In 1.16.2 data driven biomes were introduced. We mixin into the server's copy of the Biome Registry to add BC Carvers to both modded and data pack biomes.
     */
    @Inject(at = @At("RETURN"), method = "<init>(Ljava/lang/Thread;Lnet/minecraft/core/RegistryAccess$RegistryHolder;Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Lnet/minecraft/world/level/storage/WorldData;Lnet/minecraft/server/packs/repository/PackRepository;Ljava/net/Proxy;Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/server/ServerResources;Lcom/mojang/authlib/minecraft/MinecraftSessionService;Lcom/mojang/authlib/GameProfileRepository;Lnet/minecraft/server/players/GameProfileCache;Lnet/minecraft/server/level/progress/ChunkProgressListenerFactory;)V")
    private void implementBCCarver(Thread thread, RegistryAccess.RegistryHolder registryHolder, LevelStorageSource.LevelStorageAccess levelStorageAccess, WorldData worldData, PackRepository packRepository, Proxy proxy, DataFixer dataFixer, ServerResources serverResources, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, GameProfileCache gameProfileCache, ChunkProgressListenerFactory chunkProgressListenerFactory, CallbackInfo ci) {
        if (this.registryHolder.registry(Registry.BIOME_REGISTRY).isPresent()) {
            for (Biome biome : registryHolder.registry(Registry.BIOME_REGISTRY).get()) {
                convertImmutableFeatures(biome);

                // Save all pre-existing carvers for biome.
                // These will be used in dimensions where Better Caves is not whitelisted.
                List<Supplier<ConfiguredWorldCarver<?>>> defaultAirCarvers = biome.getGenerationSettings().getCarvers(GenerationStep.Carving.AIR);
                List<Supplier<ConfiguredWorldCarver<?>>> defaultLiquidCarvers = biome.getGenerationSettings().getCarvers(GenerationStep.Carving.LIQUID);
                BetterCaves.defaultBiomeAirCarvers.put(biome.toString(), convertImmutableList(defaultAirCarvers));
                BetterCaves.defaultBiomeLiquidCarvers.put(biome.toString(), convertImmutableList(defaultLiquidCarvers));

                // Use Access Transformer to make carvers field public so we can replace with empty list
                biome.getGenerationSettings().carvers = Maps.newHashMap();

                // Use Access Transformer to make features field public so we can put our carver
                // at the front of the list to give it guaranteed priority.
                List<List<Supplier<ConfiguredFeature<?, ?>>>> biomeFeatures = biome.getGenerationSettings().features;
                while (biomeFeatures.size() <= GenerationStep.Decoration.RAW_GENERATION.ordinal()) {
                    biomeFeatures.add(Lists.newArrayList());
                }
                List<Supplier<ConfiguredFeature<?, ?>>> rawGenSuppliers = convertImmutableList(biomeFeatures.get(GenerationStep.Decoration.RAW_GENERATION.ordinal()));
                rawGenSuppliers.add(0, () -> BCModFeature.CONFIGURED_BETTERCAVES_FEATURE);
                biomeFeatures.set(GenerationStep.Decoration.RAW_GENERATION.ordinal(), rawGenSuppliers);
            }
        }
    }
    /**
     * In 1.16.2, many lists were made immutable. Other modders seemingly have confused themselves and made
     * mutable lists immutable after processing them. This method serves to help avoid problems arising from
     * attempting to modify immutable collections.
     */
    private static void convertImmutableFeatures(Biome biome) {
        if (biome.getGenerationSettings().features instanceof ImmutableList) {
            biome.getGenerationSettings().features = biome.getGenerationSettings().features.stream().map(Lists::newArrayList).collect(Collectors.toList());
        }
    }

    /**
     * In 1.16.2, many lists were made immutable. Other modders seemingly have confused themselves and made
     * mutable lists immutable after processing them. This method serves to help avoid problems arising from
     * attempting to modify immutable collections.
     */
    private static <T> List<T> convertImmutableList(List<T> list) {
        return new ArrayList<>(list);
    }
}
