package com.yungnickyoung.minecraft.bettercaves.config.util;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.Configuration;

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
        caveRegionSize = new ConfigOption<>("Cave Region Size", BetterCaves.BC_CONFIG.underGroundGeneration.caves.caveRegionSize)
            .setCategory("Better Caves.Underground Generation.Caves")
            .addToMap(properties);
        caveRegionCustomSize = new ConfigOption<>("Cave Region Size Custom Value", BetterCaves.BC_CONFIG.underGroundGeneration.caves.caveRegionSizeCustomValue)
            .setCategory("Better Caves.Underground Generation.Caves")
            .addToMap(properties);
        caveSpawnChance = new ConfigOption<>("Cave Spawn Chance", BetterCaves.BC_CONFIG.underGroundGeneration.caves.caveSpawnChance)
            .setCategory("Better Caves.Underground Generation.Caves")
            .addToMap(properties);

        // Cubic (Type 1) cave settings
        cubicCaveBottom = new ConfigOption<>("Type 1 Cave Minimum Altitude", BetterCaves.BC_CONFIG.underGroundGeneration.caves.type1Caves.type1CaveMinimumAltitude)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves")
            .addToMap(properties);
        cubicCaveTop = new ConfigOption<>("Type 1 Cave Maximum Altitude", BetterCaves.BC_CONFIG.underGroundGeneration.caves.type1Caves.type1CaveMaximumAltitude)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves")
            .addToMap(properties);
        cubicCaveSurfaceCutoffDepth = new ConfigOption<>("Type 1 Cave Surface Cutoff Depth", BetterCaves.BC_CONFIG.underGroundGeneration.caves.type1Caves.type1CaveSurfaceCutoffDepth)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves")
            .addToMap(properties);
        cubicCaveYCompression = new ConfigOption<>("Compression - Vertical", BetterCaves.BC_CONFIG.underGroundGeneration.caves.type1Caves.compressionVertical)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves")
            .addToMap(properties);
        cubicCaveXZCompression = new ConfigOption<>("Compression - Horizontal", BetterCaves.BC_CONFIG.underGroundGeneration.caves.type1Caves.compressionHorizontal)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves")
            .addToMap(properties);
        cubicCavePriority = new ConfigOption<>("Type 1 Cave Priority", BetterCaves.BC_CONFIG.underGroundGeneration.caves.type1Caves.type1CavePriority)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves")
            .addToMap(properties);

        // Simplex (Type 2) cave settings
        simplexCaveBottom = new ConfigOption<>("Type 2 Cave Minimum Altitude", BetterCaves.BC_CONFIG.underGroundGeneration.caves.type2Caves.type2CaveMinimumAltitude)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves")
            .addToMap(properties);
        simplexCaveTop = new ConfigOption<>("Type 2 Cave Maximum Altitude", BetterCaves.BC_CONFIG.underGroundGeneration.caves.type2Caves.type2CaveMaximumAltitude)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves")
            .addToMap(properties);
        simplexCaveSurfaceCutoffDepth = new ConfigOption<>("Type 2 Cave Surface Cutoff Depth", BetterCaves.BC_CONFIG.underGroundGeneration.caves.type2Caves.type2CaveSurfaceCutoffDepth)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves")
            .addToMap(properties);
        simplexCaveYCompression = new ConfigOption<>("Compression - Vertical", BetterCaves.BC_CONFIG.underGroundGeneration.caves.type2Caves.compressionVertical)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves")
            .addToMap(properties);
        simplexCaveXZCompression = new ConfigOption<>("Compression - Horizontal", BetterCaves.BC_CONFIG.underGroundGeneration.caves.type2Caves.compressionHorizontal)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves")
            .addToMap(properties);
        simplexCavePriority = new ConfigOption<>("Type 2 Cave Priority", BetterCaves.BC_CONFIG.underGroundGeneration.caves.type2Caves.type2CavePriority)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves")
            .addToMap(properties);

        // Surface cave settings
        isSurfaceCavesEnabled = new ConfigOption<>("Enable Surface Caves", BetterCaves.BC_CONFIG.underGroundGeneration.caves.surfaceCaves.enableSurfaceCaves)
            .setCategory("Better Caves.Underground Generation.Caves.surface caves")
            .addToMap(properties);
        surfaceCaveBottom = new ConfigOption<>("Surface Cave Minimum Altitude", BetterCaves.BC_CONFIG.underGroundGeneration.caves.surfaceCaves.surfaceCaveMinimumAltitude)
            .setCategory("Better Caves.Underground Generation.Caves.surface caves")
            .addToMap(properties);
        surfaceCaveTop = new ConfigOption<>("Surface Cave Maximum Altitude", BetterCaves.BC_CONFIG.underGroundGeneration.caves.surfaceCaves.surfaceCaveMaximumAltitude)
            .setCategory("Better Caves.Underground Generation.Caves.surface caves")
            .addToMap(properties);
        surfaceCaveDensity = new ConfigOption<>("Surface Cave Density", BetterCaves.BC_CONFIG.underGroundGeneration.caves.surfaceCaves.surfaceCaveDensity)
            .setCategory("Better Caves.Underground Generation.Caves.surface caves")
            .addToMap(properties);

        // Vanilla cave settings
        vanillaCaveBottom = new ConfigOption<>("Vanilla Cave Minimum Altitude", BetterCaves.BC_CONFIG.underGroundGeneration.caves.vanilla.vanillaCaveMinimumAltitude)
            .setCategory("Better Caves.Underground Generation.Caves.vanilla Caves")
            .addToMap(properties);
        vanillaCaveTop = new ConfigOption<>("Vanilla Cave Maximum Altitude", BetterCaves.BC_CONFIG.underGroundGeneration.caves.vanilla.vanillaCaveMaximumAltitude)
            .setCategory("Better Caves.Underground Generation.Caves.vanilla Caves")
            .addToMap(properties);
        vanillaCaveDensity = new ConfigOption<>("Vanilla Cave Density", BetterCaves.BC_CONFIG.underGroundGeneration.caves.vanilla.vanillaCaveDensity)
            .setCategory("Better Caves.Underground Generation.Caves.vanilla Caves")
            .addToMap(properties);
        vanillaCavePriority = new ConfigOption<>("Vanilla Cave Priority", BetterCaves.BC_CONFIG.underGroundGeneration.caves.vanilla.vanillaCavePriority)
            .setCategory("Better Caves.Underground Generation.Caves.vanilla Caves")
            .addToMap(properties);

        // Dimension-wide cavern settings
        cavernRegionSize = new ConfigOption<>("Cavern Region Size", BetterCaves.BC_CONFIG.underGroundGeneration.caverns.cavernRegionSize)
            .setCategory("Better Caves.Underground Generation.Caverns")
            .addToMap(properties);
        cavernRegionCustomSize = new ConfigOption<>("Cavern Region Size Custom Value", BetterCaves.BC_CONFIG.underGroundGeneration.caverns.cavernRegionSizeCustomValue)
            .setCategory("Better Caves.Underground Generation.Caverns")
            .addToMap(properties);
        cavernSpawnChance = new ConfigOption<>("Cavern Spawn Chance", BetterCaves.BC_CONFIG.underGroundGeneration.caverns.cavernSpawnChance)
            .setCategory("Better Caves.Underground Generation.Caverns")
            .addToMap(properties);

        // Liquid cavern settings
        liquidCavernBottom = new ConfigOption<>("Liquid Cavern Minimum Altitude", BetterCaves.BC_CONFIG.underGroundGeneration.caverns.liquidCaverns.liquidCavernCaveMinimumAltitude)
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns")
            .addToMap(properties);
        liquidCavernTop = new ConfigOption<>("Liquid Cavern Maximum Altitude", BetterCaves.BC_CONFIG.underGroundGeneration.caverns.liquidCaverns.liquidCavernCaveMaximumAltitude)
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns")
            .addToMap(properties);
        liquidCavernYCompression = new ConfigOption<>("Compression - Vertical", BetterCaves.BC_CONFIG.underGroundGeneration.caverns.liquidCaverns.compressionVertical)
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns")
            .addToMap(properties);
        liquidCavernXZCompression = new ConfigOption<>("Compression - Horizontal", BetterCaves.BC_CONFIG.underGroundGeneration.caverns.liquidCaverns.compressionHorizontal)
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns")
            .addToMap(properties);
        liquidCavernPriority = new ConfigOption<>("Liquid Cavern Priority", BetterCaves.BC_CONFIG.underGroundGeneration.caverns.liquidCaverns.liquidCavernCavePriority)
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns")
            .addToMap(properties);

        // Floored cavern settings
        flooredCavernBottom = new ConfigOption<>("Floored Cavern Minimum Altitude", BetterCaves.BC_CONFIG.underGroundGeneration.caverns.flooredCaverns.flooredCavernCaveMinimumAltitude)
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns")
            .addToMap(properties);
        flooredCavernTop = new ConfigOption<>("Floored Cavern Maximum Altitude", BetterCaves.BC_CONFIG.underGroundGeneration.caverns.flooredCaverns.flooredCavernCaveMaximumAltitude)
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns")
            .addToMap(properties);
        flooredCavernYCompression = new ConfigOption<>("Compression - Vertical", BetterCaves.BC_CONFIG.underGroundGeneration.caverns.flooredCaverns.compressionVertical)
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns")
            .addToMap(properties);
        flooredCavernXZCompression = new ConfigOption<>("Compression - Horizontal", BetterCaves.BC_CONFIG.underGroundGeneration.caverns.flooredCaverns.compressionHorizontal)
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns")
            .addToMap(properties);
        flooredCavernPriority = new ConfigOption<>("Floored Cavern Priority", BetterCaves.BC_CONFIG.underGroundGeneration.caverns.flooredCaverns.flooredCavernCavePriority)
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns")
            .addToMap(properties);

        // Water region settings
        waterRegionSpawnChance = new ConfigOption<>("Water Region Spawn Chance", BetterCaves.BC_CONFIG.underGroundGeneration.waterRegions.waterRegionSpawnChance)
            .setCategory("Better Caves.Underground Generation.water regions")
            .addToMap(properties);
        waterRegionSize = new ConfigOption<>("Water Region Size", BetterCaves.BC_CONFIG.underGroundGeneration.waterRegions.waterRegionSize)
            .setCategory("Better Caves.Underground Generation.water regions")
            .addToMap(properties);
        waterRegionCustomSize = new ConfigOption<>("Water Region Size Custom Value", BetterCaves.BC_CONFIG.underGroundGeneration.waterRegions.waterRegionSizeCustomValue)
            .setCategory("Better Caves.Underground Generation.water regions")
            .addToMap(properties);

        // Ravines
        enableVanillaRavines = new ConfigOption<>("Enable Ravines", BetterCaves.BC_CONFIG.underGroundGeneration.ravines.enableRavines)
            .setCategory("Better Caves.Underground Generation.ravines")
            .addToMap(properties);
        enableFloodedRavines = new ConfigOption<>("Enable Flooded Ravines", BetterCaves.BC_CONFIG.underGroundGeneration.ravines.enableFloodedRavines)
            .setCategory("Better Caves.Underground Generation.ravines")
            .addToMap(properties);

        // Miscellaneous cave & cavern settings
        lavaBlock = new ConfigOption<>("Lava Block", BetterCaves.BC_CONFIG.underGroundGeneration.miscellaneous.lavaBlock)
            .setCategory("Better Caves.Underground Generation.miscellaneous")
            .addToMap(properties);
        waterBlock = new ConfigOption<>("Water Block", BetterCaves.BC_CONFIG.underGroundGeneration.miscellaneous.waterBlock)
            .setCategory("Better Caves.Underground Generation.miscellaneous")
            .addToMap(properties);
        liquidAltitude = new ConfigOption<>("Liquid Altitude", BetterCaves.BC_CONFIG.underGroundGeneration.miscellaneous.liquidAltitude)
            .setCategory("Better Caves.Underground Generation.miscellaneous")
            .addToMap(properties);
        replaceFloatingGravel = new ConfigOption<>("Prevent Cascading Gravel", BetterCaves.BC_CONFIG.underGroundGeneration.miscellaneous.preventCascadingGravel)
            .setCategory("Better Caves.Underground Generation.miscellaneous")
            .addToMap(properties);
        overrideSurfaceDetection = new ConfigOption<>("Override Surface Detection", BetterCaves.BC_CONFIG.underGroundGeneration.miscellaneous.overrideSurfaceDetection)
            .setCategory("Better Caves.Underground Generation.miscellaneous")
            .addToMap(properties);
        enableFloodedUnderground = new ConfigOption<>("Enable Flooded Underground", BetterCaves.BC_CONFIG.underGroundGeneration.miscellaneous.enableFloodedUnderGround)
            .setCategory("Better Caves.Underground Generation.miscellaneous")
            .addToMap(properties);

        // Bedrock settings
        flattenBedrock = new ConfigOption<>("Flatten Bedrock", BetterCaves.BC_CONFIG.underGroundGeneration.flattenBedrock)
            .setCategory("Better Caves.Bedrock Generation")
            .addToMap(properties);
        bedrockWidth = new ConfigOption<>("Bedrock Layer Width", BetterCaves.BC_CONFIG.underGroundGeneration.bedrockLayerWidth)
            .setCategory("Better Caves.Bedrock Generation")
            .addToMap(properties);

        // Debug settings
        debugVisualizer = new ConfigOption<>("Enable DEBUG Visualizer", BetterCaves.BC_CONFIG.underGroundGeneration.debug.enableDebugVisualizer)
            .setCategory("Better Caves.Debug Settings")
            .addToMap(properties);

        /* ============================== Settings Hidden from User ============================== */
        // These are settings that are ordinarily hidden from users ...
        // ... because they are very important to cave gen and sensitive to change.

        // Cubic (Type 1) cave settings
        cubicCaveNoiseThreshold = new ConfigOption<>("Noise Threshold", Configuration.caveSettings.caves.cubicCave.advancedSettings.noiseThreshold)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        cubicCaveFractalOctaves = new ConfigOption<>("Fractal Octaves", Configuration.caveSettings.caves.cubicCave.advancedSettings.fractalOctaves)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        cubicCaveFractalGain = new ConfigOption<>("Fractal Gain", Configuration.caveSettings.caves.cubicCave.advancedSettings.fractalGain)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        cubicCaveFractalFrequency = new ConfigOption<>("Fractal Frequency", Configuration.caveSettings.caves.cubicCave.advancedSettings.fractalFrequency)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        cubicCaveNumGenerators = new ConfigOption<>("Number of Generators", Configuration.caveSettings.caves.cubicCave.advancedSettings.numGenerators)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        cubicCaveEnableVerticalAdjustment = new ConfigOption<>("Enable y-adjustment", Configuration.caveSettings.caves.cubicCave.advancedSettings.yAdjust)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        cubicCaveYAdjustF1 = new ConfigOption<>("y-adjustment Variable 1", Configuration.caveSettings.caves.cubicCave.advancedSettings.yAdjustF1)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        cubicCaveYAdjustF2 = new ConfigOption<>("y-adjustment Variable 2", Configuration.caveSettings.caves.cubicCave.advancedSettings.yAdjustF2)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        cubicCaveNoiseType = new ConfigOption<>("Noise Type", Configuration.caveSettings.caves.cubicCave.advancedSettings.noiseType)
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();

        // Simplex (Type 2) cave settings
        simplexCaveNoiseThreshold = new ConfigOption<>("Noise Threshold", Configuration.caveSettings.caves.simplexCave.advancedSettings.noiseThreshold)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        simplexCaveFractalOctaves = new ConfigOption<>("Fractal Octaves", Configuration.caveSettings.caves.simplexCave.advancedSettings.fractalOctaves)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        simplexCaveFractalGain = new ConfigOption<>("Fractal Gain", Configuration.caveSettings.caves.simplexCave.advancedSettings.fractalGain)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        simplexCaveFractalFrequency = new ConfigOption<>("Fractal Frequency", Configuration.caveSettings.caves.simplexCave.advancedSettings.fractalFrequency)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        simplexCaveNumGenerators = new ConfigOption<>("Number of Generators", Configuration.caveSettings.caves.simplexCave.advancedSettings.numGenerators)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        simplexCaveEnableVerticalAdjustment = new ConfigOption<>("Enable y-adjustment", Configuration.caveSettings.caves.simplexCave.advancedSettings.yAdjust)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        simplexCaveYAdjustF1 = new ConfigOption<>("y-adjustment Variable 1", Configuration.caveSettings.caves.simplexCave.advancedSettings.yAdjustF1)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        simplexCaveYAdjustF2 = new ConfigOption<>("y-adjustment Variable 2", Configuration.caveSettings.caves.simplexCave.advancedSettings.yAdjustF2)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        simplexCaveNoiseType = new ConfigOption<>("Noise Type", Configuration.caveSettings.caves.simplexCave.advancedSettings.noiseType)
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();

        //  Liquid cavern settings
        liquidCavernNoiseThreshold = new ConfigOption<>("Noise Threshold", Configuration.caveSettings.caverns.liquidCavern.advancedSettings.noiseThreshold)
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        liquidCavernFractalOctaves = new ConfigOption<>("Fractal Octaves", Configuration.caveSettings.caverns.liquidCavern.advancedSettings.fractalOctaves)
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        liquidCavernFractalGain = new ConfigOption<>("Fractal Gain", Configuration.caveSettings.caverns.liquidCavern.advancedSettings.fractalGain)
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        liquidCavernFractalFrequency = new ConfigOption<>("Fractal Frequency", Configuration.caveSettings.caverns.liquidCavern.advancedSettings.fractalFrequency)
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        liquidCavernNumGenerators = new ConfigOption<>("Number of Generators", Configuration.caveSettings.caverns.liquidCavern.advancedSettings.numGenerators)
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        liquidCavernNoiseType = new ConfigOption<>("Noise Type", Configuration.caveSettings.caverns.liquidCavern.advancedSettings.noiseType)
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();

        // Floored cavern settings
        flooredCavernNoiseThreshold = new ConfigOption<>("Noise Threshold", Configuration.caveSettings.caverns.flooredCavern.advancedSettings.noiseThreshold)
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        flooredCavernFractalOctaves = new ConfigOption<>("Fractal Octaves", Configuration.caveSettings.caverns.flooredCavern.advancedSettings.fractalOctaves)
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        flooredCavernFractalGain = new ConfigOption<>("Fractal Gain", Configuration.caveSettings.caverns.flooredCavern.advancedSettings.fractalGain)
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        flooredCavernFractalFrequency = new ConfigOption<>("Fractal Frequency", Configuration.caveSettings.caverns.flooredCavern.advancedSettings.fractalFrequency)
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        flooredCavernNumGenerators = new ConfigOption<>("Number of Generators", Configuration.caveSettings.caverns.flooredCavern.advancedSettings.numGenerators)
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        flooredCavernNoiseType = new ConfigOption<>("Noise Type", Configuration.caveSettings.caverns.flooredCavern.advancedSettings.noiseType)
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
