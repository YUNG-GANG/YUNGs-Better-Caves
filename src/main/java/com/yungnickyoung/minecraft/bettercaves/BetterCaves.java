package com.yungnickyoung.minecraft.bettercaves;

import com.yungnickyoung.minecraft.bettercaves.config.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.proxy.NewCommonProxy;
import com.yungnickyoung.minecraft.bettercaves.world.BetterCavesCarver;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entry point for Better Caves
 */
@Mod(Settings.MOD_ID)
public class BetterCaves {
    /**
     * Table of active Better Caves carvers. Maps dimension ID to its carver.
     * We create separate carvers per dimension to allow for dimension-specific configuration.
     */
    public static Map<Integer, BetterCavesCarver> activeCarversMap = new HashMap<>();

    public static Map<Class<? extends Biome>, List<ConfiguredCarver<?>>> defaultBiomeAirCarvers = new HashMap<>();
    public static Map<Class<? extends Biome>, List<ConfiguredCarver<?>>> defaultBiomeLiquidCarvers = new HashMap<>();

    /** File referring to the overarching directory for custom dimension configs **/
    public static File customConfigDir;

    public static final Logger LOGGER = LogManager.getLogger(Settings.MOD_ID);
//    public static CommonProxy proxy;

    public BetterCaves() {
        LOGGER.debug("Better Caves entry point");

        final ModLoadingContext modLoadingContext = ModLoadingContext.get();
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, ConfigHolder.CLIENT_SPEC);

//        proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
//        proxy.start();
        NewCommonProxy.init();
    }
}