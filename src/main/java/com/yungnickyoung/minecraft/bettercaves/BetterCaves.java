package com.yungnickyoung.minecraft.bettercaves;

import com.google.common.collect.Lists;
import com.yungnickyoung.minecraft.bettercaves.config.BCSettings;
import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.init.BCModConfig;
import com.yungnickyoung.minecraft.bettercaves.init.BCModFeature;
import com.yungnickyoung.minecraft.bettercaves.world.carver.BetterCavesCarver;
import net.fabricmc.api.ModInitializer;

import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Entry point for Better Caves
 */
public class BetterCaves implements ModInitializer {
    /**
     * Table of active Better Caves carvers. Maps dimension name to its carver.
     * We create separate carvers per dimension to allow for dimension-specific configuration.
     */
    public static Map<String, BetterCavesCarver> activeCarversMap = new HashMap<>();

    /**
     * List of whitelisted dimensions.
     * Will be ignored if global whitelisting is enabled.
     */
    public static List<String> whitelistedDimensions = Lists.newArrayList();

    /**
     * Map of all biomes to their default carvers.
     * Better Caves deletes these and wraps them in its feature so that they can be
     * used for dimensions in which Better Caves is disabled.
     */
    public static Map<String, List<Supplier<ConfiguredWorldCarver<?>>>> defaultBiomeAirCarvers = new HashMap<>();
    public static Map<String, List<Supplier<ConfiguredWorldCarver<?>>>> defaultBiomeLiquidCarvers = new HashMap<>();

    /** Better Caves config. Uses AutoConfig. **/
    public static Configuration CONFIG = new Configuration();

    /** File referring to the overarching directory for custom dimension configs **/
    public static File customConfigDir;

    public static final Logger LOGGER = LogManager.getLogger(BCSettings.MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.debug("Better Caves entry point");
        BCModConfig.init();
        BCModFeature.init();
    }
}