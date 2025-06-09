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
    public static final String VERSION_PATH = "neoforge-1_21";

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
        // UndergroundGen
        BetterCavesCommon.CONFIG.undergroundGen.caves.caveSpawnChance = BCConfigNeoForge.caveSettings.caves.caveSpawnChance.get();
        BetterCavesCommon.CONFIG.undergroundGen.caves.caveRegionSize = BCConfigNeoForge.caveSettings.caves.caveRegionSize.get();
        BetterCavesCommon.CONFIG.undergroundGen.caves.customRegionSize = BCConfigNeoForge.caveSettings.caves.customRegionSize.get();

        // Cubic caves
        BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.caveBottom = BCConfigNeoForge.caveSettings.caves.cubicCave.caveBottom.get();
        BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.caveTop = BCConfigNeoForge.caveSettings.caves.cubicCave.caveTop.get();
        BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.caveSurfaceCutoff = BCConfigNeoForge.caveSettings.caves.cubicCave.caveSurfaceCutoff.get();
        BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.yCompression = BCConfigNeoForge.caveSettings.caves.cubicCave.yCompression.get();
        BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.xzCompression = BCConfigNeoForge.caveSettings.caves.cubicCave.xzCompression.get();
        BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.cavePriority = BCConfigNeoForge.caveSettings.caves.cubicCave.cavePriority.get();
        BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.advancedSettings.noiseThreshold = BCConfigNeoForge.caveSettings.caves.cubicCave.advancedSettings.noiseThreshold;
        BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.advancedSettings.fractalOctaves = BCConfigNeoForge.caveSettings.caves.cubicCave.advancedSettings.fractalOctaves;
        BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.advancedSettings.fractalGain = BCConfigNeoForge.caveSettings.caves.cubicCave.advancedSettings.fractalGain;
        BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.advancedSettings.fractalFrequency = BCConfigNeoForge.caveSettings.caves.cubicCave.advancedSettings.fractalFrequency;
        BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.advancedSettings.numGenerators = BCConfigNeoForge.caveSettings.caves.cubicCave.advancedSettings.numGenerators;
        BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.advancedSettings.yAdjust = BCConfigNeoForge.caveSettings.caves.cubicCave.advancedSettings.yAdjust;
        BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.advancedSettings.yAdjustF1 = BCConfigNeoForge.caveSettings.caves.cubicCave.advancedSettings.yAdjustF1;
        BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.advancedSettings.yAdjustF2 = BCConfigNeoForge.caveSettings.caves.cubicCave.advancedSettings.yAdjustF2;
        BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.advancedSettings.noiseType = BCConfigNeoForge.caveSettings.caves.cubicCave.advancedSettings.noiseType;

        // Simplex caves
        BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.caveBottom = BCConfigNeoForge.caveSettings.caves.simplexCave.caveBottom.get();
        BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.caveTop = BCConfigNeoForge.caveSettings.caves.simplexCave.caveTop.get();
        BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.caveSurfaceCutoff = BCConfigNeoForge.caveSettings.caves.simplexCave.caveSurfaceCutoff.get();
        BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.yCompression = BCConfigNeoForge.caveSettings.caves.simplexCave.yCompression.get();
        BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.xzCompression = BCConfigNeoForge.caveSettings.caves.simplexCave.xzCompression.get();
        BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.cavePriority = BCConfigNeoForge.caveSettings.caves.simplexCave.cavePriority.get();
        BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.advancedSettings.noiseThreshold = BCConfigNeoForge.caveSettings.caves.simplexCave.advancedSettings.noiseThreshold;
        BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.advancedSettings.fractalOctaves = BCConfigNeoForge.caveSettings.caves.simplexCave.advancedSettings.fractalOctaves;
        BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.advancedSettings.fractalGain = BCConfigNeoForge.caveSettings.caves.simplexCave.advancedSettings.fractalGain;
        BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.advancedSettings.fractalFrequency = BCConfigNeoForge.caveSettings.caves.simplexCave.advancedSettings.fractalFrequency;
        BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.advancedSettings.numGenerators = BCConfigNeoForge.caveSettings.caves.simplexCave.advancedSettings.numGenerators;
        BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.advancedSettings.yAdjust = BCConfigNeoForge.caveSettings.caves.simplexCave.advancedSettings.yAdjust;
        BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.advancedSettings.yAdjustF1 = BCConfigNeoForge.caveSettings.caves.simplexCave.advancedSettings.yAdjustF1;
        BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.advancedSettings.yAdjustF2 = BCConfigNeoForge.caveSettings.caves.simplexCave.advancedSettings.yAdjustF2;
        BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.advancedSettings.noiseType = BCConfigNeoForge.caveSettings.caves.simplexCave.advancedSettings.noiseType;

        // Surface Caves
        BetterCavesCommon.CONFIG.undergroundGen.caves.surfaceCaves.enableSurfaceCaves = BCConfigNeoForge.caveSettings.caves.surfaceCave.enableSurfaceCaves.get();
        BetterCavesCommon.CONFIG.undergroundGen.caves.surfaceCaves.caveBottom = BCConfigNeoForge.caveSettings.caves.surfaceCave.caveBottom.get();
        BetterCavesCommon.CONFIG.undergroundGen.caves.surfaceCaves.caveTop = BCConfigNeoForge.caveSettings.caves.surfaceCave.caveTop.get();
        BetterCavesCommon.CONFIG.undergroundGen.caves.surfaceCaves.caveDensity = BCConfigNeoForge.caveSettings.caves.surfaceCave.caveDensity.get();

        // Vanilla Caves
        BetterCavesCommon.CONFIG.undergroundGen.caves.vanillaCaves.caveBottom = BCConfigNeoForge.caveSettings.caves.vanillaCave.caveBottom.get();
        BetterCavesCommon.CONFIG.undergroundGen.caves.vanillaCaves.caveTop = BCConfigNeoForge.caveSettings.caves.vanillaCave.caveTop.get();
        BetterCavesCommon.CONFIG.undergroundGen.caves.vanillaCaves.caveDensity = BCConfigNeoForge.caveSettings.caves.vanillaCave.caveDensity.get();
        BetterCavesCommon.CONFIG.undergroundGen.caves.vanillaCaves.cavePriority = BCConfigNeoForge.caveSettings.caves.vanillaCave.cavePriority.get();

        // Water Regions
        BetterCavesCommon.CONFIG.undergroundGen.waterRegions.waterRegionSpawnChance = BCConfigNeoForge.caveSettings.waterRegions.waterRegionSpawnChance.get();
        BetterCavesCommon.CONFIG.undergroundGen.waterRegions.waterRegionSize = BCConfigNeoForge.caveSettings.waterRegions.waterRegionSize.get();
        BetterCavesCommon.CONFIG.undergroundGen.waterRegions.waterRegionCustomSize = BCConfigNeoForge.caveSettings.waterRegions.waterRegionCustomSize.get();

        // Underground Misc
        BetterCavesCommon.CONFIG.undergroundGen.misc.liquidAltitude = BCConfigNeoForge.caveSettings.miscellaneous.liquidAltitude.get();
//        BetterCavesCommon.CONFIG.undergroundGen.misc.lavaBlock = BCConfigNeoForge.caveSettings.miscellaneous.lavaBlock.get();
//        BetterCavesCommon.CONFIG.undergroundGen.misc.waterBlock = BCConfigNeoForge.caveSettings.miscellaneous.waterBlock.get();
        BetterCavesCommon.CONFIG.undergroundGen.misc.replaceFloatingGravel = BCConfigNeoForge.caveSettings.miscellaneous.replaceFloatingGravel.get();
        BetterCavesCommon.CONFIG.undergroundGen.misc.overrideSurfaceDetection = BCConfigNeoForge.caveSettings.miscellaneous.overrideSurfaceDetection.get();
        BetterCavesCommon.CONFIG.undergroundGen.misc.enableFloodedUnderground = BCConfigNeoForge.caveSettings.miscellaneous.enableFloodedUnderground.get();
    }
}
