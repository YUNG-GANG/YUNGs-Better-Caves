package com.yungnickyoung.minecraft.bettercaves.config.util;

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
    public Map<String, ConfigOption<?>> properties = new HashMap<>();

    /**
     * Constructor loads in default global values for all vars.
     * If a config file for a specific dimension is present, its values will override the ones loaded
     * in here when the config file is loaded via the ConfigLoader.
     */
    public ConfigHolder() {
        bakeClient();
    }

    public void bakeClient() {
        /* ============================== Settings Visible to User ============================== */
        // Dimension-wide cave settings
        caveRegionSize = new ConfigOption<>("Cave Region Size", Configuration.caveSettings.caves.caveRegionSize.get())
            .setCategory("Better Caves.Underground Generation.Caves")
            .addToMap(properties);
        caveRegionCustomSize = new ConfigOption<>("Cave Region Size Custom Value", Configuration.caveSettings.caves.customRegionSize.get())
            .setCategory("Better Caves.Underground Generation.Caves")
            .addToMap(properties);
        caveSpawnChance = new ConfigOption<>("Cave Spawn Chance", Configuration.caveSettings.caves.caveSpawnChance.get())
            .setCategory("Better Caves.Underground Generation.Caves")
            .addToMap(properties);

        // Cubic (Type 1) cave settings
        cubicCaveBottom = new ConfigOption<>("Type 1 Cave Minimum Altitude", Configuration.caveSettings.caves.cubicCave.caveBottom.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves")
            .addToMap(properties);
        cubicCaveTop = new ConfigOption<>("Type 1 Cave Maximum Altitude", Configuration.caveSettings.caves.cubicCave.caveTop.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves")
            .addToMap(properties);
        cubicCaveSurfaceCutoffDepth = new ConfigOption<>("Type 1 Cave Surface Cutoff Depth", Configuration.caveSettings.caves.cubicCave.caveSurfaceCutoff.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves")
            .addToMap(properties);
        cubicCaveYCompression = new ConfigOption<>("Compression - Vertical", Configuration.caveSettings.caves.cubicCave.yCompression.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves")
            .addToMap(properties);
        cubicCaveXZCompression = new ConfigOption<>("Compression - Horizontal", Configuration.caveSettings.caves.cubicCave.xzCompression.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves")
            .addToMap(properties);
        cubicCavePriority = new ConfigOption<>("Type 1 Cave Priority", Configuration.caveSettings.caves.cubicCave.cavePriority.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves")
            .addToMap(properties);

        // Simplex (Type 2) cave settings
        simplexCaveBottom = new ConfigOption<>("Type 2 Cave Minimum Altitude", Configuration.caveSettings.caves.simplexCave.caveBottom.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves")
            .addToMap(properties);
        simplexCaveTop = new ConfigOption<>("Type 2 Cave Maximum Altitude", Configuration.caveSettings.caves.simplexCave.caveTop.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves")
            .addToMap(properties);
        simplexCaveSurfaceCutoffDepth = new ConfigOption<>("Type 2 Cave Surface Cutoff Depth", Configuration.caveSettings.caves.simplexCave.caveSurfaceCutoff.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves")
            .addToMap(properties);
        simplexCaveYCompression = new ConfigOption<>("Compression - Vertical", Configuration.caveSettings.caves.simplexCave.yCompression.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves")
            .addToMap(properties);
        simplexCaveXZCompression = new ConfigOption<>("Compression - Horizontal", Configuration.caveSettings.caves.simplexCave.xzCompression.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves")
            .addToMap(properties);
        simplexCavePriority = new ConfigOption<>("Type 2 Cave Priority", Configuration.caveSettings.caves.simplexCave.cavePriority.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves")
            .addToMap(properties);

        // Surface cave settings
        isSurfaceCavesEnabled = new ConfigOption<>("Enable Surface Caves", Configuration.caveSettings.caves.surfaceCave.enableSurfaceCaves.get())
            .setCategory("Better Caves.Underground Generation.Caves.surface.Caves")
            .addToMap(properties);
        surfaceCaveBottom = new ConfigOption<>("Surface Cave Minimum Altitude", Configuration.caveSettings.caves.surfaceCave.caveBottom.get())
            .setCategory("Better Caves.Underground Generation.Caves.surface.Caves")
            .addToMap(properties);
        surfaceCaveTop = new ConfigOption<>("Surface Cave Maximum Altitude", Configuration.caveSettings.caves.surfaceCave.caveTop.get())
            .setCategory("Better Caves.Underground Generation.Caves.surface.Caves")
            .addToMap(properties);
        surfaceCaveDensity = new ConfigOption<>("Surface Cave Density", Configuration.caveSettings.caves.surfaceCave.caveDensity.get())
            .setCategory("Better Caves.Underground Generation.Caves.surface.Caves")
            .addToMap(properties);

        // Vanilla cave settings
        vanillaCaveBottom = new ConfigOption<>("Vanilla Cave Minimum Altitude", Configuration.caveSettings.caves.vanillaCave.caveBottom.get())
            .setCategory("Better Caves.Underground Generation.Caves.vanilla.Caves")
            .addToMap(properties);
        vanillaCaveTop = new ConfigOption<>("Vanilla Cave Maximum Altitude", Configuration.caveSettings.caves.vanillaCave.caveTop.get())
            .setCategory("Better Caves.Underground Generation.Caves.vanilla.Caves")
            .addToMap(properties);
        vanillaCaveDensity = new ConfigOption<>("Vanilla Cave Density", Configuration.caveSettings.caves.vanillaCave.caveDensity.get())
            .setCategory("Better Caves.Underground Generation.Caves.vanilla.Caves")
            .addToMap(properties);
        vanillaCavePriority = new ConfigOption<>("Vanilla Cave Priority", Configuration.caveSettings.caves.vanillaCave.cavePriority.get())
            .setCategory("Better Caves.Underground Generation.Caves.vanilla.Caves")
            .addToMap(properties);

        // Dimension-wide cavern settings
        cavernRegionSize = new ConfigOption<>("Cavern Region Size", Configuration.caveSettings.caverns.cavernRegionSize.get())
            .setCategory("Better Caves.Underground Generation.Caverns")
            .addToMap(properties);
        cavernRegionCustomSize = new ConfigOption<>("Cavern Region Size Custom Value", Configuration.caveSettings.caverns.customRegionSize.get())
            .setCategory("Better Caves.Underground Generation.Caverns")
            .addToMap(properties);
        cavernSpawnChance = new ConfigOption<>("Cavern Spawn Chance", Configuration.caveSettings.caverns.cavernSpawnChance.get())
            .setCategory("Better Caves.Underground Generation.Caverns")
            .addToMap(properties);

        // Liquid cavern settings
        liquidCavernBottom = new ConfigOption<>("Liquid Cavern Minimum Altitude", Configuration.caveSettings.caverns.liquidCavern.cavernBottom.get())
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns")
            .addToMap(properties);
        liquidCavernTop = new ConfigOption<>("Liquid Cavern Maximum Altitude", Configuration.caveSettings.caverns.liquidCavern.cavernTop.get())
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns")
            .addToMap(properties);
        liquidCavernYCompression = new ConfigOption<>("Compression - Vertical", Configuration.caveSettings.caverns.liquidCavern.yCompression.get())
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns")
            .addToMap(properties);
        liquidCavernXZCompression = new ConfigOption<>("Compression - Horizontal", Configuration.caveSettings.caverns.liquidCavern.xzCompression.get())
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns")
            .addToMap(properties);
        liquidCavernPriority = new ConfigOption<>("Liquid Cavern Priority", Configuration.caveSettings.caverns.liquidCavern.cavernPriority.get())
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns")
            .addToMap(properties);

        // Floored cavern settings
        flooredCavernBottom = new ConfigOption<>("Floored Cavern Minimum Altitude", Configuration.caveSettings.caverns.flooredCavern.cavernBottom.get())
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns")
            .addToMap(properties);
        flooredCavernTop = new ConfigOption<>("Floored Cavern Maximum Altitude", Configuration.caveSettings.caverns.flooredCavern.cavernTop.get())
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns")
            .addToMap(properties);
        flooredCavernYCompression = new ConfigOption<>("Compression - Vertical", Configuration.caveSettings.caverns.flooredCavern.yCompression.get())
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns")
            .addToMap(properties);
        flooredCavernXZCompression = new ConfigOption<>("Compression - Horizontal", Configuration.caveSettings.caverns.flooredCavern.xzCompression.get())
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns")
            .addToMap(properties);
        flooredCavernPriority = new ConfigOption<>("Floored Cavern Priority", Configuration.caveSettings.caverns.flooredCavern.cavernPriority.get())
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns")
            .addToMap(properties);

        // Water region settings
        waterRegionSpawnChance = new ConfigOption<>("Water Region Spawn Chance", Configuration.caveSettings.waterRegions.waterRegionSpawnChance.get())
            .setCategory("Better Caves.Underground Generation.water regions")
            .addToMap(properties);
        waterRegionSize = new ConfigOption<>("Water Region Size", Configuration.caveSettings.waterRegions.waterRegionSize.get())
            .setCategory("Better Caves.Underground Generation.water regions")
            .addToMap(properties);
        waterRegionCustomSize = new ConfigOption<>("Water Region Size Custom Value", Configuration.caveSettings.waterRegions.waterRegionCustomSize.get())
            .setCategory("Better Caves.Underground Generation.water regions")
            .addToMap(properties);

        // Ravines
        enableVanillaRavines = new ConfigOption<>("Enable Ravines", Configuration.caveSettings.ravines.enableVanillaRavines.get())
            .setCategory("Better Caves.Underground Generation.ravines")
            .addToMap(properties);
        enableFloodedRavines = new ConfigOption<>("Enable Flooded Ravines", Configuration.caveSettings.ravines.enableFloodedRavines.get())
            .setCategory("Better Caves.Underground Generation.ravines")
            .addToMap(properties);

        // Miscellaneous cave & cavern settings
        lavaBlock = new ConfigOption<>("Lava Block", Configuration.caveSettings.miscellaneous.lavaBlock.get())
            .setCategory("Better Caves.Underground Generation.miscellaneous")
            .addToMap(properties);
        waterBlock = new ConfigOption<>("Water Block", Configuration.caveSettings.miscellaneous.waterBlock.get())
            .setCategory("Better Caves.Underground Generation.miscellaneous")
            .addToMap(properties);
        liquidAltitude = new ConfigOption<>("Liquid Altitude", Configuration.caveSettings.miscellaneous.liquidAltitude.get())
            .setCategory("Better Caves.Underground Generation.miscellaneous")
            .addToMap(properties);
        replaceFloatingGravel = new ConfigOption<>("Prevent Cascading Gravel", Configuration.caveSettings.miscellaneous.replaceFloatingGravel.get())
            .setCategory("Better Caves.Underground Generation.miscellaneous")
            .addToMap(properties);
        overrideSurfaceDetection = new ConfigOption<>("Override Surface Detection", Configuration.caveSettings.miscellaneous.overrideSurfaceDetection.get())
            .setCategory("Better Caves.Underground Generation.miscellaneous")
            .addToMap(properties);
        enableFloodedUnderground = new ConfigOption<>("Enable Flooded Underground", Configuration.caveSettings.miscellaneous.enableFloodedUnderground.get())
            .setCategory("Better Caves.Underground Generation.miscellaneous")
            .addToMap(properties);

        // Bedrock settings
        flattenBedrock = new ConfigOption<>("Flatten Bedrock", Configuration.bedrockSettings.flattenBedrock.get())
            .setCategory("Better Caves.Bedrock Generation")
            .addToMap(properties);
        bedrockWidth = new ConfigOption<>("Bedrock Layer Width", Configuration.bedrockSettings.bedrockWidth.get())
            .setCategory("Better Caves.Bedrock Generation")
            .addToMap(properties);

        // Debug settings
        debugVisualizer = new ConfigOption<>("Enable DEBUG Visualizer", Configuration.debugSettings.debugVisualizer.get())
            .setCategory("Better Caves.Debug Settings")
            .addToMap(properties);

        /* ============================== Settings Hidden from User ============================== */
        // These are settings that are ordinarily hidden from users ...
        // ... because they are very important to cave gen and sensitive to change.

        // Cubic (Type 1) cave settings
        cubicCaveNoiseThreshold = new ConfigOption<>("Noise Threshold", Configuration.caveSettings.caves.cubicCave.advancedSettings.noiseThreshold.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        cubicCaveFractalOctaves = new ConfigOption<>("Fractal Octaves", Configuration.caveSettings.caves.cubicCave.advancedSettings.fractalOctaves.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        cubicCaveFractalGain = new ConfigOption<>("Fractal Gain", Configuration.caveSettings.caves.cubicCave.advancedSettings.fractalGain.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        cubicCaveFractalFrequency = new ConfigOption<>("Fractal Frequency", Configuration.caveSettings.caves.cubicCave.advancedSettings.fractalFrequency.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        cubicCaveNumGenerators = new ConfigOption<>("Number of Generators", Configuration.caveSettings.caves.cubicCave.advancedSettings.numGenerators.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        cubicCaveEnableVerticalAdjustment = new ConfigOption<>("Enable y-adjustment", Configuration.caveSettings.caves.cubicCave.advancedSettings.yAdjust.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        cubicCaveYAdjustF1 = new ConfigOption<>("y-adjustment Variable 1", Configuration.caveSettings.caves.cubicCave.advancedSettings.yAdjustF1.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        cubicCaveYAdjustF2 = new ConfigOption<>("y-adjustment Variable 2", Configuration.caveSettings.caves.cubicCave.advancedSettings.yAdjustF2.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        cubicCaveNoiseType = new ConfigOption<>("Noise Type", Configuration.caveSettings.caves.cubicCave.advancedSettings.noiseType.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 1 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();

        // Simplex (Type 2) cave settings
        simplexCaveNoiseThreshold = new ConfigOption<>("Noise Threshold", Configuration.caveSettings.caves.simplexCave.advancedSettings.noiseThreshold.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        simplexCaveFractalOctaves = new ConfigOption<>("Fractal Octaves", Configuration.caveSettings.caves.simplexCave.advancedSettings.fractalOctaves.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        simplexCaveFractalGain = new ConfigOption<>("Fractal Gain", Configuration.caveSettings.caves.simplexCave.advancedSettings.fractalGain.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        simplexCaveFractalFrequency = new ConfigOption<>("Fractal Frequency", Configuration.caveSettings.caves.simplexCave.advancedSettings.fractalFrequency.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        simplexCaveNumGenerators = new ConfigOption<>("Number of Generators", Configuration.caveSettings.caves.simplexCave.advancedSettings.numGenerators.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        simplexCaveEnableVerticalAdjustment = new ConfigOption<>("Enable y-adjustment", Configuration.caveSettings.caves.simplexCave.advancedSettings.yAdjust.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        simplexCaveYAdjustF1 = new ConfigOption<>("y-adjustment Variable 1", Configuration.caveSettings.caves.simplexCave.advancedSettings.yAdjustF1.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        simplexCaveYAdjustF2 = new ConfigOption<>("y-adjustment Variable 2", Configuration.caveSettings.caves.simplexCave.advancedSettings.yAdjustF2.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();
        simplexCaveNoiseType = new ConfigOption<>("Noise Type", Configuration.caveSettings.caves.simplexCave.advancedSettings.noiseType.get())
            .setCategory("Better Caves.Underground Generation.Caves.Type 2 Caves.Advanced Settings")
            .addToMap(properties)
            .hidden();

        //  Liquid cavern settings
        liquidCavernNoiseThreshold = new ConfigOption<>("Noise Threshold", Configuration.caveSettings.caverns.liquidCavern.advancedSettings.noiseThreshold.get())
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        liquidCavernFractalOctaves = new ConfigOption<>("Fractal Octaves", Configuration.caveSettings.caverns.liquidCavern.advancedSettings.fractalOctaves.get())
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        liquidCavernFractalGain = new ConfigOption<>("Fractal Gain", Configuration.caveSettings.caverns.liquidCavern.advancedSettings.fractalGain.get())
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        liquidCavernFractalFrequency = new ConfigOption<>("Fractal Frequency", Configuration.caveSettings.caverns.liquidCavern.advancedSettings.fractalFrequency.get())
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        liquidCavernNumGenerators = new ConfigOption<>("Number of Generators", Configuration.caveSettings.caverns.liquidCavern.advancedSettings.numGenerators.get())
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        liquidCavernNoiseType = new ConfigOption<>("Noise Type", Configuration.caveSettings.caverns.liquidCavern.advancedSettings.noiseType.get())
            .setCategory("Better Caves.Underground Generation.Caverns.Liquid Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();

        // Floored cavern settings
        flooredCavernNoiseThreshold = new ConfigOption<>("Noise Threshold", Configuration.caveSettings.caverns.flooredCavern.advancedSettings.noiseThreshold.get())
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        flooredCavernFractalOctaves = new ConfigOption<>("Fractal Octaves", Configuration.caveSettings.caverns.flooredCavern.advancedSettings.fractalOctaves.get())
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        flooredCavernFractalGain = new ConfigOption<>("Fractal Gain", Configuration.caveSettings.caverns.flooredCavern.advancedSettings.fractalGain.get())
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        flooredCavernFractalFrequency = new ConfigOption<>("Fractal Frequency", Configuration.caveSettings.caverns.flooredCavern.advancedSettings.fractalFrequency.get())
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        flooredCavernNumGenerators = new ConfigOption<>("Number of Generators", Configuration.caveSettings.caverns.flooredCavern.advancedSettings.numGenerators.get())
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
        flooredCavernNoiseType = new ConfigOption<>("Noise Type", Configuration.caveSettings.caverns.flooredCavern.advancedSettings.noiseType.get())
            .setCategory("Better Caves.Underground Generation.Caverns.Floored Caverns.Advanced Settings")
            .addToMap(properties)
            .hidden();
    }

    /* ============================== Settings Visible to User ============================== */
    // Dimension-wide cave settings
    public ConfigOption<String> caveRegionSize;
    public ConfigOption<Double>  caveRegionCustomSize;
    public ConfigOption<Double>  caveSpawnChance;

    // Cubic (Type 1) cave settings
    public ConfigOption<Integer> cubicCaveBottom;
    public ConfigOption<Integer> cubicCaveTop;
    public ConfigOption<Integer> cubicCaveSurfaceCutoffDepth;
    public ConfigOption<Double>   cubicCaveYCompression;
    public ConfigOption<Double>   cubicCaveXZCompression;
    public ConfigOption<Integer> cubicCavePriority;

    // Simplex (Type 2) cave settings
    public ConfigOption<Integer> simplexCaveBottom;
    public ConfigOption<Integer> simplexCaveTop;
    public ConfigOption<Integer> simplexCaveSurfaceCutoffDepth;
    public ConfigOption<Double>   simplexCaveYCompression;
    public ConfigOption<Double>   simplexCaveXZCompression;
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
    public ConfigOption<Double>  cavernRegionCustomSize;
    public ConfigOption<Double>  cavernSpawnChance;

    // Liquid cavern settings
    public ConfigOption<Integer> liquidCavernBottom;
    public ConfigOption<Integer> liquidCavernTop;
    public ConfigOption<Double>   liquidCavernYCompression;
    public ConfigOption<Double>   liquidCavernXZCompression;
    public ConfigOption<Integer> liquidCavernPriority;

    // Floored cavern settings
    public ConfigOption<Integer>  flooredCavernBottom;
    public ConfigOption<Integer>  flooredCavernTop;
    public ConfigOption<Double>    flooredCavernYCompression;
    public ConfigOption<Double>    flooredCavernXZCompression;
    public ConfigOption<Integer>  flooredCavernPriority;

    // Water region settings
    public ConfigOption<Double>  waterRegionSpawnChance;
    public ConfigOption<String> waterRegionSize;
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
    public ConfigOption<Double>               cubicCaveNoiseThreshold;
    public ConfigOption<Integer>             cubicCaveFractalOctaves;
    public ConfigOption<Double>               cubicCaveFractalGain;
    public ConfigOption<Double>               cubicCaveFractalFrequency;
    public ConfigOption<Integer>             cubicCaveNumGenerators;
    public ConfigOption<Boolean>             cubicCaveEnableVerticalAdjustment;
    public ConfigOption<Double>               cubicCaveYAdjustF1;
    public ConfigOption<Double>               cubicCaveYAdjustF2;
    public ConfigOption<String> cubicCaveNoiseType;

    // Simplex (Type 2) cave settings
    public ConfigOption<Double>               simplexCaveNoiseThreshold;
    public ConfigOption<Integer>             simplexCaveFractalOctaves;
    public ConfigOption<Double>               simplexCaveFractalGain;
    public ConfigOption<Double>               simplexCaveFractalFrequency;
    public ConfigOption<Integer>             simplexCaveNumGenerators;
    public ConfigOption<Boolean>             simplexCaveEnableVerticalAdjustment;
    public ConfigOption<Double>               simplexCaveYAdjustF1;
    public ConfigOption<Double>               simplexCaveYAdjustF2;
    public ConfigOption<String> simplexCaveNoiseType;

    // Liquid cavern settings
    public ConfigOption<Double>               liquidCavernNoiseThreshold;
    public ConfigOption<Integer>             liquidCavernFractalOctaves;
    public ConfigOption<Double>               liquidCavernFractalGain;
    public ConfigOption<Double>               liquidCavernFractalFrequency;
    public ConfigOption<Integer>             liquidCavernNumGenerators;
    public ConfigOption<String> liquidCavernNoiseType;

    // Floored cavern settings
    public ConfigOption<Double>               flooredCavernNoiseThreshold;
    public ConfigOption<Integer>             flooredCavernFractalOctaves;
    public ConfigOption<Double>               flooredCavernFractalGain;
    public ConfigOption<Double>               flooredCavernFractalFrequency;
    public ConfigOption<Integer>             flooredCavernNumGenerators;
    public ConfigOption<String> flooredCavernNoiseType;

    public static class ConfigOption<T> {
        public String name;
        public String fullName;
        public Class<?> type;
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
    }
}
