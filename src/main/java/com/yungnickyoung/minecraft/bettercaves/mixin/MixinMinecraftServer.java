package com.yungnickyoung.minecraft.bettercaves.mixin;


import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.init.BCModCarver;
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
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
    @Shadow @Final protected DynamicRegistryManager.Impl registryManager;

    /**
     * In 1.16.2 data driven biomes were introduced. We mixin into the server's copy of the Biome Registry to add BC Carvers to both modded and data pack biomes.
     */
    @Inject(at = @At("RETURN"), method = "<init>(Ljava/lang/Thread;Lnet/minecraft/util/registry/DynamicRegistryManager$Impl;Lnet/minecraft/world/level/storage/LevelStorage$Session;Lnet/minecraft/world/SaveProperties;Lnet/minecraft/resource/ResourcePackManager;Ljava/net/Proxy;Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/resource/ServerResourceManager;Lcom/mojang/authlib/minecraft/MinecraftSessionService;Lcom/mojang/authlib/GameProfileRepository;Lnet/minecraft/util/UserCache;Lnet/minecraft/server/WorldGenerationProgressListenerFactory;)V")
    private void implementBCCarver(Thread thread, DynamicRegistryManager.Impl impl, LevelStorage.Session session, SaveProperties saveProperties, ResourcePackManager resourcePackManager, Proxy proxy, DataFixer dataFixer, ServerResourceManager serverResourceManager, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, UserCache userCache, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci) {
        if(this.registryManager.getOptional(Registry.BIOME_KEY).isPresent()) {
            for (Biome biome : registryManager.getOptional(Registry.BIOME_KEY).get()) {

                // Save all pre-existing carvers for biome.
                // These will be used in dimensions where Better Caves is not whitelisted.
                List<Supplier<ConfiguredCarver<?>>> defaultAirCarvers = biome.getGenerationSettings().getCarversForStep(GenerationStep.Carver.AIR);
                List<Supplier<ConfiguredCarver<?>>> defaultLiquidCarvers = biome.getGenerationSettings().getCarversForStep(GenerationStep.Carver.LIQUID);
                BetterCaves.defaultBiomeAirCarvers.put(biome.toString(), mutableListOf(defaultAirCarvers));
                BetterCaves.defaultBiomeLiquidCarvers.put(biome.toString(), mutableListOf(defaultLiquidCarvers));

                // Clear the biome's carvers
                biome.getGenerationSettings().carvers = new HashMap<>();
                biome.getGenerationSettings().carvers.put(GenerationStep.Carver.AIR, new ArrayList<>());
                biome.getGenerationSettings().carvers.put(GenerationStep.Carver.LIQUID, new ArrayList<>());

                // Add Better Caves carver
                biome.getGenerationSettings().getCarversForStep(GenerationStep.Carver.AIR).add(() -> BCModCarver.CONFIGURED_BETTERCAVES_CARVER);
            }
        }
    }

    /**
     * In 1.16.2, many lists were made immutable. Other modders seemingly have confused themselves and made
     * mutable lists immutable after processing them. This method serves to help avoid problems arising from
     * attempting to modify immutable collections.
     */
    private static <T> List<T> mutableListOf(List<T> list) {
        return new ArrayList<>(list);
    }
}
