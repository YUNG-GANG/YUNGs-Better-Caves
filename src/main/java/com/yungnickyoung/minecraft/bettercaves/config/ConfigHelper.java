package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.fml.config.ModConfig;

/**
 * This bakes the config values to normal fields
 *
 * @author Cadiboo
 * It can be merged into the main BetterCavesConfig class, but is separate because of personal preference and to keep the code organised
 */
public final class ConfigHelper {
    public static void bakeClient(final ModConfig config) {
        // General
        BetterCavesConfig.liquidAltitude = Configuration.liquidAltitude.get();
        BetterCavesConfig.lavaBlock = Configuration.lavaBlock.get();
        BetterCavesConfig.waterBlock = Configuration.waterBlock.get();

        // Cave gen settings
        BetterCavesConfig.caveRegionSize = Configuration.configCaveGen.caveRegionSize.get();
        BetterCavesConfig.cavernRegionSize = Configuration.configCaveGen.cavernRegionSize.get();
        BetterCavesConfig.surfaceCutoff = Configuration.configCaveGen.surfaceCutoff.get();
        BetterCavesConfig.maxCaveAltitude = Configuration.configCaveGen.maxCaveAltitude.get();

        // Cubic Cave settings
        BetterCavesConfig.cubicCaveBottom = Configuration.configCaveGen.configCubicCave.caveBottom.get();
        BetterCavesConfig.cubicYComp = Configuration.configCaveGen.configCubicCave.yCompression.get();
        BetterCavesConfig.cubicXZComp = Configuration.configCaveGen.configCubicCave.xzCompression.get();
        BetterCavesConfig.cubicCaveFreq = Configuration.configCaveGen.configCubicCave.caveFrequency.get();
        BetterCavesConfig.cubicCustomFrequency = Configuration.configCaveGen.configCubicCave.customFrequency.get();

        // Simplex Cave settings
        BetterCavesConfig.simplexCaveBottom = Configuration.configCaveGen.configSimplexCave.caveBottom.get();
        BetterCavesConfig.simplexYComp = Configuration.configCaveGen.configSimplexCave.yCompression.get();
        BetterCavesConfig.simplexXZComp = Configuration.configCaveGen.configSimplexCave.xzCompression.get();
        BetterCavesConfig.simplexCaveFreq = Configuration.configCaveGen.configSimplexCave.caveFrequency.get();
        BetterCavesConfig.simplexCustomFrequency = Configuration.configCaveGen.configSimplexCave.customFrequency.get();

        // Lava Cavern settings
        BetterCavesConfig.liquidCavernTop = Configuration.configCaveGen.configLavaCavern.caveTop.get();
        BetterCavesConfig.liquidCavernBottom = Configuration.configCaveGen.configLavaCavern.caveBottom.get();
        BetterCavesConfig.liquidCavernYComp = Configuration.configCaveGen.configLavaCavern.yCompression.get();
        BetterCavesConfig.liquidCavernXZComp = Configuration.configCaveGen.configLavaCavern.xzCompression.get();
        BetterCavesConfig.liquidCavernFreq = Configuration.configCaveGen.configLavaCavern.caveFrequency.get();
        BetterCavesConfig.liquidCavernCustomFreq = Configuration.configCaveGen.configLavaCavern.customFrequency.get();

        // Floored Cavern settings
        BetterCavesConfig.flooredCavernTop = Configuration.configCaveGen.configFlooredCavern.caveTop.get();
        BetterCavesConfig.flooredCavernBottom = Configuration.configCaveGen.configFlooredCavern.caveBottom.get();
        BetterCavesConfig.flooredCavernYComp = Configuration.configCaveGen.configFlooredCavern.yCompression.get();
        BetterCavesConfig.flooredCavernXZComp = Configuration.configCaveGen.configFlooredCavern.xzCompression.get();
        BetterCavesConfig.flooredCavernCaveFreq = Configuration.configCaveGen.configFlooredCavern.caveFrequency.get();
        BetterCavesConfig.flooredCavernCustomFrequency = Configuration.configCaveGen.configFlooredCavern.customFrequency.get();

        // Water Cavern settings
        BetterCavesConfig.waterCavernYComp = Configuration.configCaveGen.configWaterRegions.configWaterCavern.yCompression.get();
        BetterCavesConfig.waterCavernXZComp = Configuration.configCaveGen.configWaterRegions.configWaterCavern.xzCompression.get();

        // Water Region settings
        BetterCavesConfig.enableWaterRegions = Configuration.configCaveGen.configWaterRegions.enableWaterRegions.get();
        BetterCavesConfig.waterRegionFreq = Configuration.configCaveGen.configWaterRegions.waterRegionFrequency.get();
        BetterCavesConfig.waterRegionCustomFreq = Configuration.configCaveGen.configWaterRegions.customFrequency.get();

        // Flatten Bedrock settings
        BetterCavesConfig.flattenBedrock = Configuration.flattenBedrock.get();

        // Vanilla ravine settings
        BetterCavesConfig.enableRavines = Configuration.configCaveGen.configVanillaCave.enableRavines.get();
        BetterCavesConfig.enableUnderwaterRavines = Configuration.configCaveGen.configVanillaCave.enableUnderwaterRavines.get();

        // Debug settings
        BetterCavesConfig.enableDebugVisualizer = Configuration.configDebug.debugVisualizer.get();
    }
}