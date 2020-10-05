package com.yungnickyoung.minecraft.bettercaves.init;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.BCSettings;
import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.world.feature.CarverFeature;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.*;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BCModFeature {
    public static final CarverFeature BETTERCAVES_FEATURE = new CarverFeature(DefaultFeatureConfig.CODEC);
    public static final ConfiguredFeature<?, ?> CONFIGURED_BETTERCAVES_FEATURE = new ConfiguredFeature<>(BETTERCAVES_FEATURE, new DefaultFeatureConfig());

    public static void init() {
        registerFeature();
        addFeatureToBiomes();
        BetterCaves.CONFIG = AutoConfig.getConfigHolder(Configuration.class).getConfig();
    }

    private static void registerFeature() {
        Registry.register(Registry.FEATURE, new Identifier(BCSettings.MOD_ID, "bettercaves"), BETTERCAVES_FEATURE);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(BCSettings.MOD_ID, "bettercaves"), CONFIGURED_BETTERCAVES_FEATURE);
    }

    /**
     * Better Caves removes all current carvers and saves them to be used in dimensions where Better Caves is disabled.
     * Better Caves is actually implemented as a featue (despite only having carver behavior) because it needs access
     * to an instance of the World, which is not available to carvers.
     */
    private static void addFeatureToBiomes() {
        ServerWorldEvents.UNLOAD.register((unload, serverWorld) -> worldUnload(serverWorld));
    }

    /**
     * Removes the unloaded dimension's carver from the active carvers map.
     */
    private static void worldUnload(ServerWorld event) {
        try {
            String key = Objects.requireNonNull((World)event).getRegistryKey().getValue().toString();
            BetterCaves.activeCarversMap.remove(key);
        } catch (NullPointerException e) {
            BetterCaves.LOGGER.error("ERROR: Unable to unload carver for dimension!");
        }
    }
}
