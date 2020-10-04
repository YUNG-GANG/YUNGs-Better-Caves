package com.yungnickyoung.minecraft.bettercaves.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.init.BCFeature;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.listener.IChunkStatusListenerFactory;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.SaveFormat;
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
    @Shadow @Final protected DynamicRegistries.Impl field_240767_f_;

    /**
     * Iterates over all biomes, removing their carvers and adding the Better Caves feature
     * (which wraps all the pre-existing carvers) to the front of their feature lists
     */
    @Inject(at = @At("RETURN"), method = "<init>(Ljava/lang/Thread;Lnet/minecraft/util/registry/DynamicRegistries$Impl;Lnet/minecraft/world/storage/SaveFormat$LevelSave;Lnet/minecraft/world/storage/IServerConfiguration;Lnet/minecraft/resources/ResourcePackList;Ljava/net/Proxy;Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/resources/DataPackRegistries;Lcom/mojang/authlib/minecraft/MinecraftSessionService;Lcom/mojang/authlib/GameProfileRepository;Lnet/minecraft/server/management/PlayerProfileCache;Lnet/minecraft/world/chunk/listener/IChunkStatusListenerFactory;)V", cancellable = true)
    private void replaceDefaultCarvers(Thread thread, DynamicRegistries.Impl impl, SaveFormat.LevelSave session, IServerConfiguration saveProperties, ResourcePackList resourcePackManager, Proxy proxy, DataFixer dataFixer, DataPackRegistries serverResourceManager, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, PlayerProfileCache userCache, IChunkStatusListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci) {
        if (this.field_240767_f_.func_230521_a_(Registry.BIOME_KEY).isPresent()) {
            for (Biome biome : field_240767_f_.func_230521_a_(Registry.BIOME_KEY).get()) {
                convertImmutableFeatures(biome);

                // Save all pre-existing carvers for biome.
                // These will be used in dimensions where Better Caves is not whitelisted.
                List<Supplier<ConfiguredCarver<?>>> defaultAirCarvers = biome.func_242440_e().func_242489_a(GenerationStage.Carving.AIR);
                List<Supplier<ConfiguredCarver<?>>> defaultLiquidCarvers = biome.func_242440_e().func_242489_a(GenerationStage.Carving.LIQUID);
                BetterCaves.defaultBiomeAirCarvers.put(biome.toString(), convertImmutableList(defaultAirCarvers));
                BetterCaves.defaultBiomeLiquidCarvers.put(biome.toString(), convertImmutableList(defaultLiquidCarvers));

                // Use Access Transformer to make carvers field public so we can replace with empty list
                biome.func_242440_e().field_242483_e = Maps.newHashMap();

                // Use Access Transformer to make features field public so we can put our carver
                // at the front of the list to give it guaranteed priority.
                List<List<Supplier<ConfiguredFeature<?, ?>>>> biomeFeatures = biome.func_242440_e().field_242484_f;
                while (biomeFeatures.size() <= GenerationStage.Decoration.RAW_GENERATION.ordinal()) {
                    biomeFeatures.add(Lists.newArrayList());
                }
                List<Supplier<ConfiguredFeature<?, ?>>> rawGenSuppliers = convertImmutableList(biomeFeatures.get(GenerationStage.Decoration.RAW_GENERATION.ordinal()));
                rawGenSuppliers.add(0, () -> BCFeature.CONFIGURED_BETTERCAVES_FEATURE);
                biomeFeatures.set(GenerationStage.Decoration.RAW_GENERATION.ordinal(), rawGenSuppliers);
            }
        }
    }


    /**
     * In 1.16.2, many lists were made immutable. Other modders seemingly have confused themselves and made
     * mutable lists immutable after processing them. This method serves to help avoid problems arising from
     * attempting to modify immutable collections.
     */
    private static void convertImmutableFeatures(Biome biome) {
        if (biome.func_242440_e().field_242484_f instanceof ImmutableList) {
            biome.func_242440_e().field_242484_f = biome.func_242440_e().field_242484_f.stream().map(Lists::newArrayList).collect(Collectors.toList());
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