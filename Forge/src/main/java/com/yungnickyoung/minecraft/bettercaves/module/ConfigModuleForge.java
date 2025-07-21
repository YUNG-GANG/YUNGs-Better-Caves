package com.yungnickyoung.minecraft.bettercaves.module;

import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.config.BCConfigForge;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ConfigModuleForge {
    public static final String VERSION_PATH = "forge-" + BetterCavesCommon.MC_VERSION_STRING;

    public static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BCConfigForge.SPEC,
                BetterCavesCommon.MOD_ID + "-" + VERSION_PATH + ".toml");
        MinecraftForge.EVENT_BUS.addListener(ConfigModuleForge::onWorldLoad);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ConfigModuleForge::onConfigChange);
    }

    private static void onWorldLoad(LevelEvent.Load event) {
        bakeConfig();
    }

    private static void onConfigChange(ModConfigEvent event) {
        if (event.getConfig().getSpec() == BCConfigForge.SPEC) {
            bakeConfig();
        }
    }

    private static void bakeConfig() {
        BetterCavesCommon.CONFIG.liquidRegions.liquidAltitude = BCConfigForge.liquidRegions.liquidAltitude.get();
        BetterCavesCommon.CONFIG.liquidRegions.liquidRegionSize = BCConfigForge.liquidRegions.liquidRegionSize.get();
        BetterCavesCommon.CONFIG.liquidRegions.waterRegionSpawnChance = BCConfigForge.liquidRegions.waterRegionSpawnChance.get();
    }
}
