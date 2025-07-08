package com.yungnickyoung.minecraft.bettercaves.module;

import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.BetterCavesNeoForge;
import com.yungnickyoung.minecraft.bettercaves.config.BCConfigNeoForge;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;

public class ConfigModuleNeoForge {
    public static final String VERSION_PATH = "neoforge-" + BetterCavesCommon.MC_VERSION_STRING;

    public static void init(ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, BCConfigNeoForge.SPEC,
                BetterCavesCommon.MOD_ID + "-" + VERSION_PATH + ".toml");
        NeoForge.EVENT_BUS.addListener(ConfigModuleNeoForge::onWorldLoad);
        BetterCavesNeoForge.loadingContextEventBus.addListener(ConfigModuleNeoForge::onConfigChange);
    }

    private static void onWorldLoad(LevelEvent.Load event) {
        bakeConfig();
    }

    private static void onConfigChange(ModConfigEvent event) {
        if (event.getConfig().getSpec() == BCConfigNeoForge.SPEC) {
            bakeConfig();
        }
    }

    private static void bakeConfig() {
        BetterCavesCommon.CONFIG.liquidRegions.liquidAltitude = BCConfigNeoForge.liquidRegions.liquidAltitude.get();
        BetterCavesCommon.CONFIG.liquidRegions.liquidRegionSize = BCConfigNeoForge.liquidRegions.liquidRegionSize.get();
        BetterCavesCommon.CONFIG.liquidRegions.waterRegionSpawnChance = BCConfigNeoForge.liquidRegions.waterRegionSpawnChance.get();
    }
}
