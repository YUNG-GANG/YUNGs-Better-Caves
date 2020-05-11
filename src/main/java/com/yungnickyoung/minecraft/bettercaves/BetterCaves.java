package com.yungnickyoung.minecraft.bettercaves;

import com.google.common.collect.Lists;
import com.yungnickyoung.minecraft.bettercaves.config.BCSettings;
import com.yungnickyoung.minecraft.bettercaves.init.BCModConfig;
import com.yungnickyoung.minecraft.bettercaves.init.BCFeature;
import com.yungnickyoung.minecraft.bettercaves.world.carver.BetterCavesCarver;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entry point for Better Caves
 */
@Mod(BCSettings.MOD_ID)
public class BetterCaves {
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
    public static Map<Class<? extends Biome>, List<ConfiguredCarver<?>>> defaultBiomeAirCarvers = new HashMap<>();
    public static Map<Class<? extends Biome>, List<ConfiguredCarver<?>>> defaultBiomeLiquidCarvers = new HashMap<>();

    /** File referring to the overarching directory for custom dimension configs **/
    public static File customConfigDir;

    public static final Logger LOGGER = LogManager.getLogger(BCSettings.MOD_ID);

    public BetterCaves() {
        LOGGER.debug("Better Caves entry point");
        init();
//        proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
//        proxy.start();
    }

    private void init() {
        BCModConfig.init();
        BCFeature.init();
    }
}