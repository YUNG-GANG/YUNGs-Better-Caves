package com.yungnickyoung.minecraft.bettercaves.mixin;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.init.BCModFeature;
import com.yungnickyoung.minecraft.bettercaves.world.feature.CarverFeature;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.util.UserCache;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.level.storage.LevelStorage;
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

    @Shadow @Final protected DynamicRegistryManager.Impl registryManager;

    /**
     * In 1.16.2 data driven biomes were introduced. We mixin into the server's copy of the Biome Registry to add BC Carvers to both modded and data pack biomes.
     */
    @Inject(at = @At("RETURN"), method = "<init>(Ljava/lang/Thread;Lnet/minecraft/util/registry/DynamicRegistryManager$Impl;Lnet/minecraft/world/level/storage/LevelStorage$Session;Lnet/minecraft/world/SaveProperties;Lnet/minecraft/resource/ResourcePackManager;Ljava/net/Proxy;Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/resource/ServerResourceManager;Lcom/mojang/authlib/minecraft/MinecraftSessionService;Lcom/mojang/authlib/GameProfileRepository;Lnet/minecraft/util/UserCache;Lnet/minecraft/server/WorldGenerationProgressListenerFactory;)V", cancellable = true)
    private void implementBCCarver(Thread thread, DynamicRegistryManager.Impl impl, LevelStorage.Session session, SaveProperties saveProperties, ResourcePackManager resourcePackManager, Proxy proxy, DataFixer dataFixer, ServerResourceManager serverResourceManager, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, UserCache userCache, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci) {
        if(this.registryManager.getOptional(Registry.BIOME_KEY).isPresent()) {
            for (Biome biome : registryManager.getOptional(Registry.BIOME_KEY).get()) {
                convertImmutableFeatures(biome);

                // Save all pre-existing carvers for biome.
                // These will be used in dimensions where Better Caves is not whitelisted.
                List<Supplier<ConfiguredCarver<?>>> defaultAirCarvers = biome.getGenerationSettings().getCarversForStep(GenerationStep.Carver.AIR);
                List<Supplier<ConfiguredCarver<?>>> defaultLiquidCarvers = biome.getGenerationSettings().getCarversForStep(GenerationStep.Carver.LIQUID);
                BetterCaves.defaultBiomeAirCarvers.put(biome.toString(), convertImmutableList(defaultAirCarvers));
                BetterCaves.defaultBiomeLiquidCarvers.put(biome.toString(), convertImmutableList(defaultLiquidCarvers));

                // Use Access Transformer to make carvers field public so we can replace with empty list
                biome.getGenerationSettings().carvers = Maps.newHashMap();

                // Use Access Transformer to make features field public so we can put our carver
                // at the front of the list to give it guaranteed priority.
                List<List<Supplier<ConfiguredFeature<?, ?>>>> biomeFeatures = biome.getGenerationSettings().features;
                while (biomeFeatures.size() <= GenerationStep.Feature.RAW_GENERATION.ordinal()) {
                    biomeFeatures.add(Lists.newArrayList());
                }
                List<Supplier<ConfiguredFeature<?, ?>>> rawGenSuppliers = convertImmutableList(biomeFeatures.get(GenerationStep.Feature.RAW_GENERATION.ordinal()));
                rawGenSuppliers.add(0, () -> BCModFeature.CONFIGURED_BETTERCAVES_FEATURE);
                biomeFeatures.set(GenerationStep.Feature.RAW_GENERATION.ordinal(), rawGenSuppliers);
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
