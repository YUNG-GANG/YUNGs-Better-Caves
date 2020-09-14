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
        caveRegionSize = new ConfigOption<>("Cave Region Size", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.caveRegionSize)
            .setCategory("Better Caves.Underground Generation.Caves")
            .addToMap(properties);
        caveRegionCustomSize = new ConfigOption<>("Cave Region Size Custom Value", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.caveRegionSizeCustomValue)
            .setCategory("Better Caves.Underground Generation.Caves")
            .addToMap(properties);
        caveSpawnChance = new ConfigOption<>("Cave Spawn Chance", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.caveSpawnChance)
            .setCategory("Better Caves.Underground Generation.Caves")
            .addToMap(properties);

        // Cubic (Type 1) cave settings
        cubicCaveBottom = new ConfigOption<>("Type 1 Cave Minimum Altitude", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.caveBottom)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves")
            .addToMap(properties);
        cubicCaveTop = new ConfigOption<>("Type 1 Cave Maximum Altitude", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.caveTop)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves")
            .addToMap(properties);
        cubicCaveSurfaceCutoffDepth = new ConfigOption<>("Type 1 Cave Surface Cutoff Depth", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.caveSurfaceCutoff)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves")
            .addToMap(properties);
        cubicCaveYCompression = new ConfigOption<>("Compression - Vertical", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.yCompression)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves")
            .addToMap(properties);
        cubicCaveXZCompression = new ConfigOption<>("Compression - Horizontal", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.xzCompression)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves")
            .addToMap(properties);
        cubicCavePriority = new ConfigOption<>("Type 1 Cave Priority", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.cavePriority)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves")
            .addToMap(properties);

        // Simplex (Type 2) cave settings
        simplexCaveBottom = new ConfigOption<>("Type 2 Cave Minimum Altitude", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.caveBottom)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves")
            .addToMap(properties);
        simplexCaveTop = new ConfigOption<>("Type 2 Cave Maximum Altitude", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.caveTop)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves")
            .addToMap(properties);
        simplexCaveSurfaceCutoffDepth = new ConfigOption<>("Type 2 Cave Surface Cutoff Depth", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.caveSurfaceCutoff)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves")
            .addToMap(properties);
        simplexCaveYCompression = new ConfigOption<>("Compression - Vertical", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.yCompression)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves")
            .addToMap(properties);
        simplexCaveXZCompression = new ConfigOption<>("Compression - Horizontal", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.xzCompression)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves")
            .addToMap(properties);
        simplexCavePriority = new ConfigOption<>("Type 2 Cave Priority", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.cavePriority)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves")
            .addToMap(properties);

        // Surface cave settings
        isSurfaceCavesEnabled = new ConfigOption<>("Enable Surface Caves", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.surfaceCaves.enableSurfaceCaves)
            .setCategory("Better Caves.Underground Generation.Caves.Surface Caves")
            .addToMap(properties);
        surfaceCaveBottom = new ConfigOption<>("Surface Cave Minimum Altitude", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.surfaceCaves.caveBottom)
            .setCategory("Better Caves.Underground Generation.Caves.Surface Caves")
            .addToMap(properties);
        surfaceCaveTop = new ConfigOption<>("Surface Cave Maximum Altitude", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.surfaceCaves.caveTop)
            .setCategory("Better Caves.Underground Generation.Caves.Surface Caves")
            .addToMap(properties);
        surfaceCaveDensity = new ConfigOption<>("Surface Cave Density", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.surfaceCaves.caveDensity)
            .setCategory("Better Caves.Underground Generation.Caves.Surface Caves")
            .addToMap(properties);

        // Vanilla cave settings
        vanillaCaveBottom = new ConfigOption<>("Vanilla Cave Minimum Altitude", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.vanillaCaves.caveBottom)
            .setCategory("Better Caves.Underground Generation.Caves.Vanilla Caves")
            .addToMap(properties);
        vanillaCaveTop = new ConfigOption<>("Vanilla Cave Maximum Altitude", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.vanillaCaves.caveTop)
            .setCategory("Better Caves.Underground Generation.Caves.Vanilla Caves")
            .addToMap(properties);
        vanillaCaveDensity = new ConfigOption<>("Vanilla Cave Density", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.vanillaCaves.caveDensity)
            .setCategory("Better Caves.Underground Generation.Caves.Vanilla Caves")
            .addToMap(properties);
        vanillaCavePriority = new ConfigOption<>("Vanilla Cave Priority", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.vanillaCaves.cavePriority)
            .setCategory("Better Caves.Underground Generation.Caves.Vanilla Caves")
            .addToMap(properties);

        // Dimension-wide cavern settings
        cavernRegionSize = new ConfigOption<>("Cavern Region Size", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.cavernRegionSize)
            .setCategory("Better Caves.Underground Generation.Caverns")
            .addToMap(properties);
        cavernRegionCustomSize = new ConfigOption<>("Cavern Region Size Custom Value", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.cavernRegionSizeCustomValue)
            .setCategory("Better Caves.Underground Generation.Caverns")
            .addToMap(properties);
        cavernSpawnChance = new ConfigOption<>("Cavern Spawn Chance", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.cavernSpawnChance)
            .setCategory("Better Caves.Underground Generation.Caverns")
            .addToMap(properties);

        // Liquid cavern settings
        liquidCavernBottom = new ConfigOption<>("Liquid Cavern Minimum Altitude", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.liquidCaverns.cavernBottom)
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns")
            .addToMap(properties);
        liquidCavernTop = new ConfigOption<>("Liquid Cavern Maximum Altitude", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.liquidCaverns.cavernTop)
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns")
            .addToMap(properties);
        liquidCavernYCompression = new ConfigOption<>("Compression - Vertical", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.liquidCaverns.yCompression)
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns")
            .addToMap(properties);
        liquidCavernXZCompression = new ConfigOption<>("Compression - Horizontal", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.liquidCaverns.xzCompression)
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns")
            .addToMap(properties);
        liquidCavernPriority = new ConfigOption<>("Liquid Cavern Priority", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.liquidCaverns.cavernPriority)
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns")
            .addToMap(properties);

        // Floored cavern settings
        flooredCavernBottom = new ConfigOption<>("Floored Cavern Minimum Altitude", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.flooredCaverns.cavernBottom)
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns")
            .addToMap(properties);
        flooredCavernTop = new ConfigOption<>("Floored Cavern Maximum Altitude", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.flooredCaverns.cavernTop)
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns")
            .addToMap(properties);
        flooredCavernYCompression = new ConfigOption<>("Compression - Vertical", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.flooredCaverns.yCompression)
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns")
            .addToMap(properties);
        flooredCavernXZCompression = new ConfigOption<>("Compression - Horizontal", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.flooredCaverns.xzCompression)
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns")
            .addToMap(properties);
        flooredCavernPriority = new ConfigOption<>("Floored Cavern Priority", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.flooredCaverns.cavernPriority)
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns")
            .addToMap(properties);

        // Water region settings
        waterRegionSpawnChance = new ConfigOption<>("Water Region Spawn Chance", BetterCaves.CONFIG.betterCaves.undergroundSettings.waterRegions.waterRegionSpawnChance)
            .setCategory("Better Caves.Underground Generation.Water Regions")
            .addToMap(properties);
        waterRegionSize = new ConfigOption<>("Water Region Size", BetterCaves.CONFIG.betterCaves.undergroundSettings.waterRegions.waterRegionSize)
            .setCategory("Better Caves.Underground Generation.Water Regions")
            .addToMap(properties);
        waterRegionCustomSize = new ConfigOption<>("Water Region Size Custom Value", BetterCaves.CONFIG.betterCaves.undergroundSettings.waterRegions.waterRegionSizeCustomValue)
            .setCategory("Better Caves.Underground Generation.Water Regions")
            .addToMap(properties);

        // Ravines
        enableVanillaRavines = new ConfigOption<>("Enable Ravines", BetterCaves.CONFIG.betterCaves.undergroundSettings.ravines.enableRavines)
            .setCategory("Better Caves.Underground Generation.Ravines")
            .addToMap(properties);
        enableFloodedRavines = new ConfigOption<>("Enable Flooded Ravines", BetterCaves.CONFIG.betterCaves.undergroundSettings.ravines.enableFloodedRavines)
            .setCategory("Better Caves.Underground Generation.Ravines")
            .addToMap(properties);

        // Miscellaneous cave & cavern settings
        lavaBlock = new ConfigOption<>("Lava Block", BetterCaves.CONFIG.betterCaves.undergroundSettings.miscellaneous.lavaBlock)
            .setCategory("Better Caves.Underground Generation.Miscellaneous")
            .addToMap(properties);
        waterBlock = new ConfigOption<>("Water Block", BetterCaves.CONFIG.betterCaves.undergroundSettings.miscellaneous.waterBlock)
            .setCategory("Better Caves.Underground Generation.Miscellaneous")
            .addToMap(properties);
        liquidAltitude = new ConfigOption<>("Liquid Altitude", BetterCaves.CONFIG.betterCaves.undergroundSettings.miscellaneous.liquidAltitude)
            .setCategory("Better Caves.Underground Generation.Miscellaneous")
            .addToMap(properties);
        replaceFloatingGravel = new ConfigOption<>("Prevent Cascading Gravel", BetterCaves.CONFIG.betterCaves.undergroundSettings.miscellaneous.preventCascadingGravel)
            .setCategory("Better Caves.Underground Generation.Miscellaneous")
            .addToMap(properties);
        overrideSurfaceDetection = new ConfigOption<>("Override Surface Detection", BetterCaves.CONFIG.betterCaves.undergroundSettings.miscellaneous.overrideSurfaceDetection)
            .setCategory("Better Caves.Underground Generation.Miscellaneous")
            .addToMap(properties);
        enableFloodedUnderground = new ConfigOption<>("Enable Flooded Underground", BetterCaves.CONFIG.betterCaves.undergroundSettings.miscellaneous.enableFloodedUnderground)
            .setCategory("Better Caves.Underground Generation.Miscellaneous")
            .addToMap(properties);

        // Bedrock settings
        flattenBedrock = new ConfigOption<>("Flatten Bedrock", BetterCaves.CONFIG.betterCaves.bedrockSettings.flattenBedrock)
            .setCategory("Better Caves.Bedrock Generation")
            .addToMap(properties);
        bedrockWidth = new ConfigOption<>("Bedrock Layer Width", BetterCaves.CONFIG.betterCaves.bedrockSettings.bedrockWidth)
            .setCategory("Better Caves.Bedrock Generation")
            .addToMap(properties);

        // Debug settings
        debugVisualizer = new ConfigOption<>("Enable DEBUG Visualizer", BetterCaves.CONFIG.betterCaves.debugSettings.enableDebugVisualizer)
            .setCategory("Better Caves.Debug Settings")
            .addToMap(properties);

        /* ============================== Settings Hidden from User ============================== */
        // These are settings that are ordinarily hidden from users ...
        // ... because they are very important to cave gen and sensitive to change.

        // Cubic (Type 1) cave settings
        cubicCaveNoiseThreshold = new ConfigOption<>("Noise Threshold", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.advancedSettings.noiseThreshold)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        cubicCaveFractalOctaves = new ConfigOption<>("Fractal Octaves", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.advancedSettings.fractalOctaves)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        cubicCaveFractalGain = new ConfigOption<>("Fractal Gain", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.advancedSettings.fractalGain)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        cubicCaveFractalFrequency = new ConfigOption<>("Fractal Frequency", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.advancedSettings.fractalFrequency)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        cubicCaveNumGenerators = new ConfigOption<>("Number of Generators", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.advancedSettings.numGenerators)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        cubicCaveEnableVerticalAdjustment = new ConfigOption<>("Enable y-adjustment", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.advancedSettings.yAdjust)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        cubicCaveYAdjustF1 = new ConfigOption<>("y-adjustment Variable 1", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.advancedSettings.yAdjustF1)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        cubicCaveYAdjustF2 = new ConfigOption<>("y-adjustment Variable 2", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.advancedSettings.yAdjustF2)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        cubicCaveNoiseType = new ConfigOption<>("Noise Type", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type1Caves.advancedSettings.noiseType)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();

        // Simplex (Type 2) cave settings
        simplexCaveNoiseThreshold = new ConfigOption<>("Noise Threshold", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.advancedSettings.noiseThreshold)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        simplexCaveFractalOctaves = new ConfigOption<>("Fractal Octaves", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.advancedSettings.fractalOctaves)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        simplexCaveFractalGain = new ConfigOption<>("Fractal Gain", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.advancedSettings.fractalGain)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        simplexCaveFractalFrequency = new ConfigOption<>("Fractal Frequency", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.advancedSettings.fractalFrequency)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        simplexCaveNumGenerators = new ConfigOption<>("Number of Generators", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.advancedSettings.numGenerators)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        simplexCaveEnableVerticalAdjustment = new ConfigOption<>("Enable y-adjustment", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.advancedSettings.yAdjust)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        simplexCaveYAdjustF1 = new ConfigOption<>("y-adjustment Variable 1", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.advancedSettings.yAdjustF1)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        simplexCaveYAdjustF2 = new ConfigOption<>("y-adjustment Variable 2", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.advancedSettings.yAdjustF2)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        simplexCaveNoiseType = new ConfigOption<>("Noise Type", BetterCaves.CONFIG.betterCaves.undergroundSettings.caves.type2Caves.advancedSettings.noiseType)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();

        //  Liquid cavern settings
        liquidCavernNoiseThreshold = new ConfigOption<>("Noise Threshold", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.liquidCaverns.advancedSettings.noiseThreshold)
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        liquidCavernFractalOctaves = new ConfigOption<>("Fractal Octaves", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.liquidCaverns.advancedSettings.fractalOctaves)
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        liquidCavernFractalGain = new ConfigOption<>("Fractal Gain", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.liquidCaverns.advancedSettings.fractalGain)
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        liquidCavernFractalFrequency = new ConfigOption<>("Fractal Frequency", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.liquidCaverns.advancedSettings.fractalFrequency)
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        liquidCavernNumGenerators = new ConfigOption<>("Number of Generators", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.liquidCaverns.advancedSettings.numGenerators)
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        liquidCavernNoiseType = new ConfigOption<>("Noise Type", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.liquidCaverns.advancedSettings.noiseType)
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();

        // Floored cavern settings
        flooredCavernNoiseThreshold = new ConfigOption<>("Noise Threshold", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.flooredCaverns.advancedSettings.noiseThreshold)
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        flooredCavernFractalOctaves = new ConfigOption<>("Fractal Octaves", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.flooredCaverns.advancedSettings.fractalOctaves)
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        flooredCavernFractalGain = new ConfigOption<>("Fractal Gain", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.flooredCaverns.advancedSettings.fractalGain)
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        flooredCavernFractalFrequency = new ConfigOption<>("Fractal Frequency", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.flooredCaverns.advancedSettings.fractalFrequency)
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        flooredCavernNumGenerators = new ConfigOption<>("Number of Generators", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.flooredCaverns.advancedSettings.numGenerators)
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        flooredCavernNoiseType = new ConfigOption<>("Noise Type", BetterCaves.CONFIG.betterCaves.undergroundSettings.caverns.flooredCaverns.advancedSettings.noiseType)
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns.Advanced Settings")
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
