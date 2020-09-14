package com.yungnickyoung.minecraft.bettercaves.config.util;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds all the config values specific to a single dimension.
 * By default, a dimension's values are equivalent to the global configuration found in
 * the base config file. This lets users only specify the options they want to override
 * for each dimension.
 */
public class ConfigHolder {
    /** Map of full names to ConfigOptions. Holds all config options.  */
    public CaseInsensitiveMap properties = new CaseInsensitiveMap();

    /**
     * Constructor loads in default global values for all vars.
     * If a config file for a specific dimension is present, its values will override the ones loaded
     * in here when the config file is loaded via the ConfigLoader.
     */
     public ConfigHolder() {
        /* ============================== Settings Visible to User ============================== */
        // Dimension-wide cave settings
        caveRegionSize = new ConfigOption<>("caveRegionSize", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.caveRegionSize)
            .setCategory("betterCaves.undergroundSettings.caves")
            .addToMap(properties);
        caveRegionCustomSize = new ConfigOption<>("caveRegionSizeCustomValue", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.caveRegionSizeCustomValue)
            .setCategory("betterCaves.undergroundSettings.caves")
            .addToMap(properties);
        caveSpawnChance = new ConfigOption<>("caveSpawnChance", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.caveSpawnChance)
            .setCategory("betterCaves.undergroundSettings.caves")
            .addToMap(properties);

        // Cubic (Type 1) cave settings
        cubicCaveBottom = new ConfigOption<>("caveBottom", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.caveBottom)
            .setCategory("betterCaves.undergroundSettings.caves.type1Caves")
            .addToMap(properties);
        cubicCaveTop = new ConfigOption<>("caveTop", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.caveTop)
            .setCategory("betterCaves.undergroundSettings.caves.type1Caves")
            .addToMap(properties);
        cubicCaveSurfaceCutoffDepth = new ConfigOption<>("caveSurfaceCutoff", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.caveSurfaceCutoff)
            .setCategory("betterCaves.undergroundSettings.caves.type1Caves")
            .addToMap(properties);
        cubicCaveYCompression = new ConfigOption<>("yCompression", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.yCompression)
            .setCategory("betterCaves.undergroundSettings.caves.type1Caves")
            .addToMap(properties);
        cubicCaveXZCompression = new ConfigOption<>("xzCompression", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.xzCompression)
            .setCategory("betterCaves.undergroundSettings.caves.type1Caves")
            .addToMap(properties);
        cubicCavePriority = new ConfigOption<>("cavePriority", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.cavePriority)
            .setCategory("betterCaves.undergroundSettings.caves.type1Caves")
            .addToMap(properties);

        // Simplex (Type 2) cave settings
        simplexCaveBottom = new ConfigOption<>("caveBottom", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.caveBottom)
            .setCategory("betterCaves.undergroundSettings.caves.type2Caves")
            .addToMap(properties);
        simplexCaveTop = new ConfigOption<>("caveTop", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.caveTop)
            .setCategory("betterCaves.undergroundSettings.caves.type2Caves")
            .addToMap(properties);
        simplexCaveSurfaceCutoffDepth = new ConfigOption<>("caveSurfaceCutoff", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.caveSurfaceCutoff)
            .setCategory("betterCaves.undergroundSettings.caves.type2Caves")
            .addToMap(properties);
        simplexCaveYCompression = new ConfigOption<>("yCompression", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.yCompression)
            .setCategory("betterCaves.undergroundSettings.caves.type2Caves")
            .addToMap(properties);
        simplexCaveXZCompression = new ConfigOption<>("xzCompression", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.xzCompression)
            .setCategory("betterCaves.undergroundSettings.caves.type2Caves")
            .addToMap(properties);
        simplexCavePriority = new ConfigOption<>("cavePriority", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.cavePriority)
            .setCategory("betterCaves.undergroundSettings.caves.type2Caves")
            .addToMap(properties);

        // Surface cave settings
        isSurfaceCavesEnabled = new ConfigOption<>("enableSurfaceCaves", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.surfaceCaves.enableSurfaceCaves)
            .setCategory("betterCaves.undergroundSettings.caves.surfaceCaves")
            .addToMap(properties);
        surfaceCaveBottom = new ConfigOption<>("caveBottom", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.surfaceCaves.caveBottom)
            .setCategory("betterCaves.undergroundSettings.caves.surfaceCaves")
            .addToMap(properties);
        surfaceCaveTop = new ConfigOption<>("caveTop", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.surfaceCaves.caveTop)
            .setCategory("betterCaves.undergroundSettings.caves.surfaceCaves")
            .addToMap(properties);
        surfaceCaveDensity = new ConfigOption<>("caveDensity", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.surfaceCaves.caveDensity)
            .setCategory("betterCaves.undergroundSettings.caves.surfaceCaves")
            .addToMap(properties);

        // Vanilla cave settings
        vanillaCaveBottom = new ConfigOption<>("caveBottom", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.vanillaCaves.caveBottom)
            .setCategory("betterCaves.undergroundSettings.caves.vanillaCaves")
            .addToMap(properties);
        vanillaCaveTop = new ConfigOption<>("caveTop", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.vanillaCaves.caveTop)
            .setCategory("betterCaves.undergroundSettings.caves.vanillaCaves")
            .addToMap(properties);
        vanillaCaveDensity = new ConfigOption<>("caveDensity", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.vanillaCaves.caveDensity)
            .setCategory("betterCaves.undergroundSettings.caves.vanillaCaves")
            .addToMap(properties);
        vanillaCavePriority = new ConfigOption<>("cavePriority", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.vanillaCaves.cavePriority)
            .setCategory("betterCaves.undergroundSettings.caves.vanillaCaves")
            .addToMap(properties);

        // Dimension-wide cavern settings
        cavernRegionSize = new ConfigOption<>("cavernRegionSize", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.cavernRegionSize)
            .setCategory("betterCaves.undergroundSettings.caverns")
            .addToMap(properties);
        cavernRegionCustomSize = new ConfigOption<>("cavernRegionSizeCustomValue", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.cavernRegionSizeCustomValue)
            .setCategory("betterCaves.undergroundSettings.caverns")
            .addToMap(properties);
        cavernSpawnChance = new ConfigOption<>("cavernSpawnChance", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.cavernSpawnChance)
            .setCategory("betterCaves.undergroundSettings.caverns")
            .addToMap(properties);

        // Liquid cavern settings
        liquidCavernBottom = new ConfigOption<>("cavernBottom", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.liquidCaverns.cavernBottom)
            .setCategory("betterCaves.undergroundSettings.caverns.liquidCaverns")
            .addToMap(properties);
        liquidCavernTop = new ConfigOption<>("cavernTop", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.liquidCaverns.cavernTop)
            .setCategory("betterCaves.undergroundSettings.caverns.liquidCaverns")
            .addToMap(properties);
        liquidCavernYCompression = new ConfigOption<>("yCompression", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.liquidCaverns.yCompression)
            .setCategory("betterCaves.undergroundSettings.caverns.liquidCaverns")
            .addToMap(properties);
        liquidCavernXZCompression = new ConfigOption<>("xzCompression", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.liquidCaverns.xzCompression)
            .setCategory("betterCaves.undergroundSettings.caverns.liquidCaverns")
            .addToMap(properties);
        liquidCavernPriority = new ConfigOption<>("cavernPriority", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.liquidCaverns.cavernPriority)
            .setCategory("betterCaves.undergroundSettings.caverns.liquidCaverns")
            .addToMap(properties);

        // Floored cavern settings
        flooredCavernBottom = new ConfigOption<>("cavernBottom", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.flooredCaverns.cavernBottom)
            .setCategory("betterCaves.undergroundSettings.caverns.flooredCaverns")
            .addToMap(properties);
        flooredCavernTop = new ConfigOption<>("cavernTop", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.flooredCaverns.cavernTop)
            .setCategory("betterCaves.undergroundSettings.caverns.flooredCaverns")
            .addToMap(properties);
        flooredCavernYCompression = new ConfigOption<>("yCompression", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.flooredCaverns.yCompression)
            .setCategory("betterCaves.undergroundSettings.caverns.flooredCaverns")
            .addToMap(properties);
        flooredCavernXZCompression = new ConfigOption<>("xzCompression", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.flooredCaverns.xzCompression)
            .setCategory("betterCaves.undergroundSettings.caverns.flooredCaverns")
            .addToMap(properties);
        flooredCavernPriority = new ConfigOption<>("cavernPriority", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.flooredCaverns.cavernPriority)
            .setCategory("betterCaves.undergroundSettings.caverns.flooredCaverns")
            .addToMap(properties);

        // Water region settings
        waterRegionSpawnChance = new ConfigOption<>("waterRegionSpawnChance", BetterCaves.CONFIG.betterCaves.undergroundSettings.waterRegions.waterRegionSpawnChance)
            .setCategory("betterCaves.undergroundSettings.waterRegions")
            .addToMap(properties);
        waterRegionSize = new ConfigOption<>("waterRegionSize", BetterCaves.CONFIG.betterCaves.undergroundSettings.waterRegions.waterRegionSize)
            .setCategory("betterCaves.undergroundSettings.waterRegions")
            .addToMap(properties);
        waterRegionCustomSize = new ConfigOption<>("waterRegionSizeCustomValue", BetterCaves.CONFIG.betterCaves.undergroundSettings.waterRegions.waterRegionSizeCustomValue)
            .setCategory("betterCaves.undergroundSettings.waterRegions")
            .addToMap(properties);

        // Ravines
        enableVanillaRavines = new ConfigOption<>("enableRavines", BetterCaves.CONFIG.betterCaves.undergroundSettings.ravines.enableRavines)
            .setCategory("betterCaves.undergroundSettings.Ravines")
            .addToMap(properties);
        enableFloodedRavines = new ConfigOption<>("enableFloodedRavines", BetterCaves.CONFIG.betterCaves.undergroundSettings.ravines.enableFloodedRavines)
            .setCategory("betterCaves.undergroundSettings.Ravines")
            .addToMap(properties);

        // Miscellaneous cave & cavern settings
        lavaBlock = new ConfigOption<>("lavaBlock", BetterCaves.CONFIG.betterCaves.undergroundSettings.miscellaneous.lavaBlock)
            .setCategory("betterCaves.undergroundSettings.Miscellaneous")
            .addToMap(properties);
        waterBlock = new ConfigOption<>("waterBlock", BetterCaves.CONFIG.betterCaves.undergroundSettings.miscellaneous.waterBlock)
            .setCategory("betterCaves.undergroundSettings.Miscellaneous")
            .addToMap(properties);
        liquidAltitude = new ConfigOption<>("liquidAltitude", BetterCaves.CONFIG.betterCaves.undergroundSettings.miscellaneous.liquidAltitude)
            .setCategory("betterCaves.undergroundSettings.Miscellaneous")
            .addToMap(properties);
        replaceFloatingGravel = new ConfigOption<>("preventCascadingGravel", BetterCaves.CONFIG.betterCaves.undergroundSettings.miscellaneous.preventCascadingGravel)
            .setCategory("betterCaves.undergroundSettings.Miscellaneous")
            .addToMap(properties);
        overrideSurfaceDetection = new ConfigOption<>("overrideSurfaceDetection", BetterCaves.CONFIG.betterCaves.undergroundSettings.miscellaneous.overrideSurfaceDetection)
            .setCategory("betterCaves.undergroundSettings.Miscellaneous")
            .addToMap(properties);
        enableFloodedUnderground = new ConfigOption<>("enableFloodedUnderground", BetterCaves.CONFIG.betterCaves.undergroundSettings.miscellaneous.enableFloodedUnderground)
            .setCategory("betterCaves.undergroundSettings.Miscellaneous")
            .addToMap(properties);

        // Bedrock settings
        flattenBedrock = new ConfigOption<>("flattenBedrock", BetterCaves.CONFIG.betterCaves.bedrockSettings.flattenBedrock)
            .setCategory("betterCaves.bedrockSettings")
            .addToMap(properties);
        bedrockWidth = new ConfigOption<>("bedrockWidth", BetterCaves.CONFIG.betterCaves.bedrockSettings.bedrockWidth)
            .setCategory("betterCaves.bedrockSettings")
            .addToMap(properties);

        // Debug settings
        debugVisualizer = new ConfigOption<>("enableDebugVisualizer", BetterCaves.CONFIG.betterCaves.debugSettings.enableDebugVisualizer)
            .setCategory("betterCaves.debugSettings")
            .addToMap(properties);

        /* ============================== Settings Hidden from User ============================== */
        // These are settings that are ordinarily hidden from users ...
        // ... because they are very important to cave gen and sensitive to change.

        // Cubic (Type 1) cave settings
        cubicCaveNoiseThreshold = new ConfigOption<>("noiseThreshold", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.advancedSettings.noiseThreshold)
            .setCategory("betterCaves.undergroundSettings.caves.type1Caves.advancedSettings")
            .addToMap(properties)
            .hidden();
        cubicCaveFractalOctaves = new ConfigOption<>("fractalOctaves", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.advancedSettings.fractalOctaves)
            .setCategory("betterCaves.undergroundSettings.caves.type1Caves.advancedSettings")
            .addToMap(properties)
            .hidden();
        cubicCaveFractalGain = new ConfigOption<>("fractalGain", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.advancedSettings.fractalGain)
            .setCategory("betterCaves.undergroundSettings.caves.type1Caves.advancedSettings")
            .addToMap(properties)
            .hidden();
        cubicCaveFractalFrequency = new ConfigOption<>("fractalFrequency", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.advancedSettings.fractalFrequency)
            .setCategory("betterCaves.undergroundSettings.caves.type1Caves.advancedSettings")
            .addToMap(properties)
            .hidden();
        cubicCaveNumGenerators = new ConfigOption<>("numGenerators", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.advancedSettings.numGenerators)
            .setCategory("betterCaves.undergroundSettings.caves.type1Caves.advancedSettings")
            .addToMap(properties)
            .hidden();
        cubicCaveEnableVerticalAdjustment = new ConfigOption<>("yAdjust", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.advancedSettings.yAdjust)
            .setCategory("betterCaves.undergroundSettings.caves.type1Caves.advancedSettings")
            .addToMap(properties)
            .hidden();
        cubicCaveYAdjustF1 = new ConfigOption<>("yAdjustF1", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.advancedSettings.yAdjustF1)
            .setCategory("betterCaves.undergroundSettings.caves.type1Caves.advancedSettings")
            .addToMap(properties)
            .hidden();
        cubicCaveYAdjustF2 = new ConfigOption<>("yAdjustF2", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.advancedSettings.yAdjustF2)
            .setCategory("betterCaves.undergroundSettings.caves.type1Caves.advancedSettings")
            .addToMap(properties)
            .hidden();
        cubicCaveNoiseType = new ConfigOption<>("noiseType", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.advancedSettings.noiseType)
            .setCategory("betterCaves.undergroundSettings.caves.type1Caves.advancedSettings")
            .addToMap(properties)
            .hidden();

        // Simplex (Type 2) cave settings
        simplexCaveNoiseThreshold = new ConfigOption<>("noiseThreshold", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.advancedSettings.noiseThreshold)
            .setCategory("betterCaves.undergroundSettings.caves.type2Caves.advancedSettings")
            .addToMap(properties)
            .hidden();
        simplexCaveFractalOctaves = new ConfigOption<>("fractalOctaves", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.advancedSettings.fractalOctaves)
            .setCategory("betterCaves.undergroundSettings.caves.type2Caves.advancedSettings")
            .addToMap(properties)
            .hidden();
        simplexCaveFractalGain = new ConfigOption<>("fractalGain", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.advancedSettings.fractalGain)
            .setCategory("betterCaves.undergroundSettings.caves.type2Caves.advancedSettings")
            .addToMap(properties)
            .hidden();
        simplexCaveFractalFrequency = new ConfigOption<>("fractalFrequency", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.advancedSettings.fractalFrequency)
            .setCategory("betterCaves.undergroundSettings.caves.type2Caves.advancedSettings")
            .addToMap(properties)
            .hidden();
        simplexCaveNumGenerators = new ConfigOption<>("numGenerators", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.advancedSettings.numGenerators)
            .setCategory("betterCaves.undergroundSettings.caves.type2Caves.advancedSettings")
            .addToMap(properties)
            .hidden();
        simplexCaveEnableVerticalAdjustment = new ConfigOption<>("yAdjust", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.advancedSettings.yAdjust)
            .setCategory("betterCaves.undergroundSettings.caves.type2Caves.advancedSettings")
            .addToMap(properties)
            .hidden();
        simplexCaveYAdjustF1 = new ConfigOption<>("yAdjustF1", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.advancedSettings.yAdjustF1)
            .setCategory("betterCaves.undergroundSettings.caves.type2Caves.advancedSettings")
            .addToMap(properties)
            .hidden();
        simplexCaveYAdjustF2 = new ConfigOption<>("yAdjustF2", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.advancedSettings.yAdjustF2)
            .setCategory("betterCaves.undergroundSettings.caves.type2Caves.advancedSettings")
            .addToMap(properties)
            .hidden();
        simplexCaveNoiseType = new ConfigOption<>("noiseType", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.advancedSettings.noiseType)
            .setCategory("betterCaves.undergroundSettings.caves.type2Caves.advancedSettings")
            .addToMap(properties)
            .hidden();

        //  Liquid cavern settings
        liquidCavernNoiseThreshold = new ConfigOption<>("noiseThreshold", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.liquidCaverns.advancedSettings.noiseThreshold)
            .setCategory("betterCaves.undergroundSettings.caverns.liquidCaverns.advancedSettings")
            .addToMap(properties)
            .hidden();
        liquidCavernFractalOctaves = new ConfigOption<>("fractalOctaves", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.liquidCaverns.advancedSettings.fractalOctaves)
            .setCategory("betterCaves.undergroundSettings.caverns.liquidCaverns.advancedSettings")
            .addToMap(properties)
            .hidden();
        liquidCavernFractalGain = new ConfigOption<>("fractalGain", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.liquidCaverns.advancedSettings.fractalGain)
            .setCategory("betterCaves.undergroundSettings.caverns.liquidCaverns.advancedSettings")
            .addToMap(properties)
            .hidden();
        liquidCavernFractalFrequency = new ConfigOption<>("fractalFrequency", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.liquidCaverns.advancedSettings.fractalFrequency)
            .setCategory("betterCaves.undergroundSettings.caverns.liquidCaverns.advancedSettings")
            .addToMap(properties)
            .hidden();
        liquidCavernNumGenerators = new ConfigOption<>("numGenerators", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.liquidCaverns.advancedSettings.numGenerators)
            .setCategory("betterCaves.undergroundSettings.caverns.liquidCaverns.advancedSettings")
            .addToMap(properties)
            .hidden();
        liquidCavernNoiseType = new ConfigOption<>("noiseType", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.liquidCaverns.advancedSettings.noiseType)
            .setCategory("betterCaves.undergroundSettings.caverns.liquidCaverns.advancedSettings")
            .addToMap(properties)
            .hidden();

        // Floored cavern settings
        flooredCavernNoiseThreshold = new ConfigOption<>("noiseThreshold", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.flooredCaverns.advancedSettings.noiseThreshold)
            .setCategory("betterCaves.undergroundSettings.caverns.flooredCaverns.advancedSettings")
            .addToMap(properties)
            .hidden();
        flooredCavernFractalOctaves = new ConfigOption<>("fractalOctaves", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.flooredCaverns.advancedSettings.fractalOctaves)
            .setCategory("betterCaves.undergroundSettings.caverns.flooredCaverns.advancedSettings")
            .addToMap(properties)
            .hidden();
        flooredCavernFractalGain = new ConfigOption<>("fractalGain", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.flooredCaverns.advancedSettings.fractalGain)
            .setCategory("betterCaves.undergroundSettings.caverns.flooredCaverns.advancedSettings")
            .addToMap(properties)
            .hidden();
        flooredCavernFractalFrequency = new ConfigOption<>("fractalFrequency", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.flooredCaverns.advancedSettings.fractalFrequency)
            .setCategory("betterCaves.undergroundSettings.caverns.flooredCaverns.advancedSettings")
            .addToMap(properties)
            .hidden();
        flooredCavernNumGenerators = new ConfigOption<>("numGenerators", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.flooredCaverns.advancedSettings.numGenerators)
            .setCategory("betterCaves.undergroundSettings.caverns.flooredCaverns.advancedSettings")
            .addToMap(properties)
            .hidden();
        flooredCavernNoiseType = new ConfigOption<>("noiseType", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.flooredCaverns.advancedSettings.noiseType)
            .setCategory("betterCaves.undergroundSettings.caverns.flooredCaverns.advancedSettings")
            .addToMap(properties)
            .hidden();
    }

    /* ============================== Settings Visible to User ============================== */
    // Dimension-wide cave settings
    public ConfigOption<String> caveRegionSize;
    public ConfigOption<Double> caveRegionCustomSize;
    public ConfigOption<Double> caveSpawnChance;

    // Cubic (Type 1) cave settings
    public ConfigOption<Integer> cubicCaveBottom;
    public ConfigOption<Integer> cubicCaveTop;
    public ConfigOption<Integer> cubicCaveSurfaceCutoffDepth;
    public ConfigOption<Double>  cubicCaveYCompression;
    public ConfigOption<Double>  cubicCaveXZCompression;
    public ConfigOption<Integer> cubicCavePriority;

    // Simplex (Type 2) cave settings
    public ConfigOption<Integer> simplexCaveBottom;
    public ConfigOption<Integer> simplexCaveTop;
    public ConfigOption<Integer> simplexCaveSurfaceCutoffDepth;
    public ConfigOption<Double>  simplexCaveYCompression;
    public ConfigOption<Double>  simplexCaveXZCompression;
    public ConfigOption<Integer> simplexCavePriority;

    // Surface cave settings
    public ConfigOption<Boolean> isSurfaceCavesEnabled;
    public ConfigOption<Integer> surfaceCaveBottom;
    public ConfigOption<Integer> surfaceCaveTop;
    public ConfigOption<Integer> surfaceCaveDensity;

    // Vanilla cave settings
    public ConfigOption<Integer> vanillaCaveBottom;
    public ConfigOption<Integer> vanillaCaveTop;
    public ConfigOption<Integer> vanillaCaveDensity;
    public ConfigOption<Integer> vanillaCavePriority;

    // Dimension-wide cavern settings
    public ConfigOption<String> cavernRegionSize;
    public ConfigOption<Double> cavernRegionCustomSize;
    public ConfigOption<Double> cavernSpawnChance;

    // Liquid cavern settings
    public ConfigOption<Integer> liquidCavernBottom;
    public ConfigOption<Integer> liquidCavernTop;
    public ConfigOption<Double>  liquidCavernYCompression;
    public ConfigOption<Double>  liquidCavernXZCompression;
    public ConfigOption<Integer> liquidCavernPriority;

    // Floored cavern settings
    public ConfigOption<Integer>  flooredCavernBottom;
    public ConfigOption<Integer>  flooredCavernTop;
    public ConfigOption<Double>   flooredCavernYCompression;
    public ConfigOption<Double>   flooredCavernXZCompression;
    public ConfigOption<Integer>  flooredCavernPriority;

    // Water region settings
    public ConfigOption<Double>  waterRegionSpawnChance;
    public ConfigOption<String>  waterRegionSize;
    public ConfigOption<Double>  waterRegionCustomSize;

    // Ravines
    public ConfigOption<Boolean> enableVanillaRavines;
    public ConfigOption<Boolean> enableFloodedRavines;

    // Miscellaneous cave & cavern settings
    public ConfigOption<String>  lavaBlock;
    public ConfigOption<String>  waterBlock;
    public ConfigOption<Integer> liquidAltitude;
    public ConfigOption<Boolean> replaceFloatingGravel;
    public ConfigOption<Boolean> overrideSurfaceDetection;
    public ConfigOption<Boolean> enableFloodedUnderground;

    // Bedrock settings
    public ConfigOption<Boolean> flattenBedrock;
    public ConfigOption<Integer> bedrockWidth;

    // Debug settings
    public ConfigOption<Boolean> debugVisualizer;

    /* ============================== Settings Hidden from User ============================== */
    // These are settings that are ordinarily hidden from users ...
    // ... because they are very important to cave gen and sensitive to change.

    // Cubic (Type 1) cave settings
    public ConfigOption<Double>  cubicCaveNoiseThreshold;
    public ConfigOption<Integer> cubicCaveFractalOctaves;
    public ConfigOption<Double>  cubicCaveFractalGain;
    public ConfigOption<Double>  cubicCaveFractalFrequency;
    public ConfigOption<Integer> cubicCaveNumGenerators;
    public ConfigOption<Boolean> cubicCaveEnableVerticalAdjustment;
    public ConfigOption<Double>  cubicCaveYAdjustF1;
    public ConfigOption<Double>  cubicCaveYAdjustF2;
    public ConfigOption<String>  cubicCaveNoiseType;

    // Simplex (Type 2) cave settings
    public ConfigOption<Double>  simplexCaveNoiseThreshold;
    public ConfigOption<Integer> simplexCaveFractalOctaves;
    public ConfigOption<Double>  simplexCaveFractalGain;
    public ConfigOption<Double>  simplexCaveFractalFrequency;
    public ConfigOption<Integer> simplexCaveNumGenerators;
    public ConfigOption<Boolean> simplexCaveEnableVerticalAdjustment;
    public ConfigOption<Double>  simplexCaveYAdjustF1;
    public ConfigOption<Double>  simplexCaveYAdjustF2;
    public ConfigOption<String>  simplexCaveNoiseType;

    // Liquid cavern settings
    public ConfigOption<Double>  liquidCavernNoiseThreshold;
    public ConfigOption<Integer> liquidCavernFractalOctaves;
    public ConfigOption<Double>  liquidCavernFractalGain;
    public ConfigOption<Double>  liquidCavernFractalFrequency;
    public ConfigOption<Integer> liquidCavernNumGenerators;
    public ConfigOption<String>  liquidCavernNoiseType;

    // Floored cavern settings
    public ConfigOption<Double>  flooredCavernNoiseThreshold;
    public ConfigOption<Integer> flooredCavernFractalOctaves;
    public ConfigOption<Double>  flooredCavernFractalGain;
    public ConfigOption<Double>  flooredCavernFractalFrequency;
    public ConfigOption<Integer> flooredCavernNumGenerators;
    public ConfigOption<String>  flooredCavernNoiseType;

    public static class ConfigOption<T> {
        public String name;
        public String fullName;
        private Class<?> type;
        private T value;
        private boolean hidden = false;
        private String category = "";

        public ConfigOption(String name, T value) {
            this.name = name;
            this.value = value;
            this.type = value.getClass();
        }

        public T get() {
            return value;
        }

        @SuppressWarnings("unchecked")
        public void set(Object value) {
            this.value = (T)value;
        }

        public ConfigOption<T> hidden() {
            this.hidden = true;
            return this;
        }

        public ConfigOption<T> setCategory(String category) {
            this.category = category;
            this.fullName = category + "." + name;
            return this;
        }

        public ConfigOption<T> addToMap(Map<String, ConfigOption<?>> map) {
            map.put(fullName, this);
            return this;
        }

        public Class<?> getType() {
            return type;
        }

        public String getCategory() {
            return category;
        }

        public String getFullName() {
            return fullName;
        }
    }

    public static class CaseInsensitiveMap extends HashMap<String, ConfigOption<?>> {
        @Override
        public ConfigOption<?> put(String key, ConfigOption<?> value) {
            return super.put(key.toLowerCase(), value);
        }

        public ConfigOption<?> get(String key) {
            return super.get(key.toLowerCase());
        }
    }
}
