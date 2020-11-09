package com.yungnickyoung.minecraft.bettercaves.init;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.BCSettings;
import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.world.feature.CarverFeature;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
//import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.Objects;

public class BCModFeature {
    public static final CarverFeature BETTERCAVES_FEATURE = new CarverFeature(NoneFeatureConfiguration.CODEC);
    public static final ConfiguredFeature<?, ?> CONFIGURED_BETTERCAVES_FEATURE = new ConfiguredFeature<>(BETTERCAVES_FEATURE, new NoneFeatureConfiguration());

    public static void init() {
        registerFeature();
        //        ServerWorldEvents.UNLOAD.register((unload, serverWorld) -> worldUnload(serverWorld)); // TODO - re-add fabric API
        BetterCaves.CONFIG = AutoConfig.getConfigHolder(Configuration.class).getConfig();
    }

    private static void registerFeature() {
        Registry.register(Registry.FEATURE, new ResourceLocation(BCSettings.MOD_ID, "bettercaves"), BETTERCAVES_FEATURE);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation(BCSettings.MOD_ID, "bettercaves"), CONFIGURED_BETTERCAVES_FEATURE);
    }

    /**
     * Removes the unloaded dimension's carver from the active carvers map.
     */
    private static void onWorldUnload(ServerLevel world) {
        try {
            String key = Objects.requireNonNull(world.registryAccess().dimensionTypes().getKey(world.dimensionType())).toString();
            BetterCaves.activeCarversMap.remove(key);
        } catch (NullPointerException e) {
            BetterCaves.LOGGER.error("ERROR: Unable to unload carver for dimension!");
        }
    }
}
