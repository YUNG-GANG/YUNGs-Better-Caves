package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.fml.config.ModConfig;

/**
 * This bakes the config values to normal fields
 *
 * @author Cadiboo
 * It can be merged into the main BetterCavesConfig class, but is separate because of personal preference and to keep the code organised
 */
public final class ConfigHelper {

    // We store a reference to the ModConfigs here to be able to change the values in them from our code
    // (For example from a config GUI) - this is not currently a feature
    private static ModConfig clientConfig;

    public static void bakeClient(final ModConfig config) {
        clientConfig = config;

        // General
        BetterCavesConfig.lavaDepth = ConfigHolder.CLIENT.lavaDepth.get();
        BetterCavesConfig.lavaBlock = ConfigHolder.CLIENT.lavaBlock.get();
        BetterCavesConfig.waterBlock = ConfigHolder.CLIENT.waterBlock.get();

        // Cave gen settings
        BetterCavesConfig.caveRegionSize = ConfigHolder.CLIENT.configCaveGen.caveRegionSize.get();
        BetterCavesConfig.cavernRegionSize = ConfigHolder.CLIENT.configCaveGen.cavernRegionSize.get();
        BetterCavesConfig.surfaceCutoff = ConfigHolder.CLIENT.configCaveGen.surfaceCutoff.get();
        BetterCavesConfig.maxCaveAltitude = ConfigHolder.CLIENT.configCaveGen.maxCaveAltitude.get();

        // Cubic Cave settings
        BetterCavesConfig.cubicCaveBottom = ConfigHolder.CLIENT.configCaveGen.configCubicCave.caveBottom.get();
        BetterCavesConfig.cubicYComp = ConfigHolder.CLIENT.configCaveGen.configCubicCave.yCompression.get();
        BetterCavesConfig.cubicXZComp = ConfigHolder.CLIENT.configCaveGen.configCubicCave.xzCompression.get();
        BetterCavesConfig.cubicCaveFreq = ConfigHolder.CLIENT.configCaveGen.configCubicCave.caveFrequency.get();
        BetterCavesConfig.cubicCustomFrequency = ConfigHolder.CLIENT.configCaveGen.configCubicCave.customFrequency.get();

        // Simplex Cave settings
        BetterCavesConfig.simplexCaveBottom = ConfigHolder.CLIENT.configCaveGen.configSimplexCave.caveBottom.get();
        BetterCavesConfig.simplexYComp = ConfigHolder.CLIENT.configCaveGen.configSimplexCave.yCompression.get();
        BetterCavesConfig.simplexXZComp = ConfigHolder.CLIENT.configCaveGen.configSimplexCave.xzCompression.get();
        BetterCavesConfig.simplexCaveFreq = ConfigHolder.CLIENT.configCaveGen.configSimplexCave.caveFrequency.get();
        BetterCavesConfig.simplexCustomFrequency = ConfigHolder.CLIENT.configCaveGen.configSimplexCave.customFrequency.get();

        // Lava Cavern settings
        BetterCavesConfig.lavaCavernCaveTop = ConfigHolder.CLIENT.configCaveGen.configLavaCavern.caveTop.get();
        BetterCavesConfig.lavaCavernCaveBottom = ConfigHolder.CLIENT.configCaveGen.configLavaCavern.caveBottom.get();
        BetterCavesConfig.lavaCavernYComp = ConfigHolder.CLIENT.configCaveGen.configLavaCavern.yCompression.get();
        BetterCavesConfig.lavaCavernXZComp = ConfigHolder.CLIENT.configCaveGen.configLavaCavern.xzCompression.get();
        BetterCavesConfig.lavaCavernCaveFreq = ConfigHolder.CLIENT.configCaveGen.configLavaCavern.caveFrequency.get();
        BetterCavesConfig.lavaCavernCustomFrequency = ConfigHolder.CLIENT.configCaveGen.configLavaCavern.customFrequency.get();

        // Floored Cavern settings
        BetterCavesConfig.flooredCavernCaveTop = ConfigHolder.CLIENT.configCaveGen.configFlooredCavern.caveTop.get();
        BetterCavesConfig.flooredCavernCaveBottom = ConfigHolder.CLIENT.configCaveGen.configFlooredCavern.caveBottom.get();
        BetterCavesConfig.flooredCavernYComp = ConfigHolder.CLIENT.configCaveGen.configFlooredCavern.yCompression.get();
        BetterCavesConfig.flooredCavernXZComp = ConfigHolder.CLIENT.configCaveGen.configFlooredCavern.xzCompression.get();
        BetterCavesConfig.flooredCavernCaveFreq = ConfigHolder.CLIENT.configCaveGen.configFlooredCavern.caveFrequency.get();
        BetterCavesConfig.flooredCavernCustomFrequency = ConfigHolder.CLIENT.configCaveGen.configFlooredCavern.customFrequency.get();

        // Water Cavern settings
        BetterCavesConfig.waterCavernYComp = ConfigHolder.CLIENT.configCaveGen.configWaterRegions.configWaterCavern.yCompression.get();
        BetterCavesConfig.waterCavernXZComp = ConfigHolder.CLIENT.configCaveGen.configWaterRegions.configWaterCavern.xzCompression.get();

        // Water Region settings
        BetterCavesConfig.enableWaterRegions = ConfigHolder.CLIENT.configCaveGen.configWaterRegions.enableWaterRegions.get();
        BetterCavesConfig.waterRegionFreq = ConfigHolder.CLIENT.configCaveGen.configWaterRegions.waterRegionFrequency.get();
        BetterCavesConfig.waterRegionCustomFreq = ConfigHolder.CLIENT.configCaveGen.configWaterRegions.customFrequency.get();

        // Flatten Bedrock settings
        BetterCavesConfig.flattenBedrock = ConfigHolder.CLIENT.flattenBedrock.get();

        //Ocean Settings
        BetterCavesConfig.oceanFloorSetting = ConfigHolder.CLIENT.oceanConfig.oceanFloorSetting.get();

        // Vanilla ravine settings
        BetterCavesConfig.enableRavines = ConfigHolder.CLIENT.configCaveGen.configVanillaCave.enableRavines.get();
        BetterCavesConfig.enableUnderwaterRavines = ConfigHolder.CLIENT.configCaveGen.configVanillaCave.enableUnderwaterRavines.get();

        // Debug settings
        BetterCavesConfig.enableDebugVisualizer = ConfigHolder.CLIENT.configDebug.debugVisualizer.get();
    }

    private static void setValueAndSave(final ModConfig modConfig, final String path, final Object newValue) {
        modConfig.getConfigData().set(path, newValue);
        modConfig.save();
    }

}