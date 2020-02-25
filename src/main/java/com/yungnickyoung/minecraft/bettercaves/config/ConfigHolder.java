package com.yungnickyoung.minecraft.bettercaves.config;

import com.yungnickyoung.minecraft.bettercaves.enums.*;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds all the config values specific to a single dimension.
 * By default, a dimension's values are equivalent to the global configuration found in
 * YUNG's Better Caves.cfg
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
        /* ============================== Settings Visible to User ============================== */
        // Dimension-wide cave & cavern settings
        lavaBlock = new ConfigOption<>("Lava Block", Configuration.caveSettings.lavaBlock)
                .setCategory("general.cave & cavern generation")
                .addToMap(properties);
        waterBlock = new ConfigOption<>("Water Block", Configuration.caveSettings.waterBlock)
                .setCategory("general.cave & cavern generation")
                .addToMap(properties);
        liquidAltitude = new ConfigOption<>("Liquid Altitude", Configuration.caveSettings.liquidAltitude)
                .setCategory("general.cave & cavern generation")
                .addToMap(properties);
        replaceFloatingGravel = new ConfigOption<>("Replace Floating Gravel", Configuration.caveSettings.replaceFloatingGravel)
                .setCategory("general.cave & cavern generation")
                .addToMap(properties);

        // Dimension-wide cave settings
        caveRegionSize = new ConfigOption<>("Cave Region Size", Configuration.caveSettings.caves.caveRegionSize)
                .setCategory("general.cave & cavern generation.caves")
                .addToMap(properties);
        surfaceCutoff = new ConfigOption<>("Cave Surface Cutoff Depth", Configuration.caveSettings.caves.surfaceCutoff)
                .setCategory("general.cave & cavern generation.caves")
                .addToMap(properties);
        maxCaveAltitude = new ConfigOption<>("Max Cave Altitude", Configuration.caveSettings.caves.maxCaveAltitude)
                .setCategory("general.cave & cavern generation.caves")
                .addToMap(properties);


        // Cubic (Type 1) cave settings
        cubicCaveBottom = new ConfigOption<>("Cave Bottom Altitude", Configuration.caveSettings.caves.cubicCave.caveBottom)
                .setCategory("general.cave & cavern generation.caves.type 1 caves")
                .addToMap(properties);
        cubicCaveYCompression = new ConfigOption<>("Compression - Vertical", Configuration.caveSettings.caves.cubicCave.yCompression)
                .setCategory("general.cave & cavern generation.caves.type 1 caves")
                .addToMap(properties);
        cubicCaveXZCompression = new ConfigOption<>("Compression - Horizontal", Configuration.caveSettings.caves.cubicCave.xzCompression)
                .setCategory("general.cave & cavern generation.caves.type 1 caves")
                .addToMap(properties);
        cubicCaveFrequency = new ConfigOption<>("Type 1 Cave Frequency", Configuration.caveSettings.caves.cubicCave.caveFrequency)
                .setCategory("general.cave & cavern generation.caves.type 1 caves")
                .addToMap(properties);
        cubicCaveCustomFrequency = new ConfigOption<>("Type 1 Cave Frequency Custom Value", Configuration.caveSettings.caves.cubicCave.customFrequency)
                .setCategory("general.cave & cavern generation.caves.type 1 caves")
                .addToMap(properties);

        // Simplex (Type 2) cave settings
        simplexCaveBottom = new ConfigOption<>("Cave Bottom Altitude", Configuration.caveSettings.caves.simplexCave.caveBottom)
                 .setCategory("general.cave & cavern generation.caves.type 2 caves")
                 .addToMap(properties);
        simplexCaveYCompression = new ConfigOption<>("Compression - Vertical", Configuration.caveSettings.caves.simplexCave.yCompression)
                .setCategory("general.cave & cavern generation.caves.type 2 caves")
                .addToMap(properties);
        simplexCaveXZCompression = new ConfigOption<>("Compression - Horizontal", Configuration.caveSettings.caves.simplexCave.xzCompression)
                .setCategory("general.cave & cavern generation.caves.type 2 caves")
                .addToMap(properties);
        simplexCaveFrequency = new ConfigOption<>("Type 2 Cave Frequency", Configuration.caveSettings.caves.simplexCave.caveFrequency)
                .setCategory("general.cave & cavern generation.caves.type 2 caves")
                .addToMap(properties);
        simplexCaveCustomFrequency = new ConfigOption<>("Type 2 Cave Frequency Custom Value", Configuration.caveSettings.caves.simplexCave.customFrequency)
                .setCategory("general.cave & cavern generation.caves.type 2 caves")
                .addToMap(properties);

        // Vanilla cave settings
        enableVanillaCaves = new ConfigOption<>("Enable Vanilla Caves", Configuration.caveSettings.caves.vanillaCave.enableVanillaCaves)
                .setCategory("general.cave & cavern generation.caves.vanilla caves")
                .addToMap(properties);
        enableVanillaRavines = new ConfigOption<>("Enable Ravines", Configuration.caveSettings.caves.vanillaCave.enableVanillaRavines)
                .setCategory("general.cave & cavern generation.caves.vanilla caves")
                .addToMap(properties);

        // Dimension-wide cavern settings
        cavernRegionSize = new ConfigOption<>("Cavern Region Size", Configuration.caveSettings.caverns.cavernRegionSize)
                .setCategory("general.cave & cavern generation.caverns")
                .addToMap(properties);

        // Lava cavern settings
        lavaCavernBottom = new ConfigOption<>("Cavern Bottom Altitude", Configuration.caveSettings.caverns.lavaCavern.caveBottom)
                .setCategory("general.cave & cavern generation.caverns.lava caverns")
                .addToMap(properties);
        lavaCavernTop = new ConfigOption<>("Cavern Top Altitude", Configuration.caveSettings.caverns.lavaCavern.caveTop)
                .setCategory("general.cave & cavern generation.caverns.lava caverns")
                .addToMap(properties);
        lavaCavernYCompression = new ConfigOption<>("Compression - Vertical", Configuration.caveSettings.caverns.lavaCavern.yCompression)
                .setCategory("general.cave & cavern generation.caverns.lava caverns")
                .addToMap(properties);
        lavaCavernXZCompression = new ConfigOption<>("Compression - Horizontal", Configuration.caveSettings.caverns.lavaCavern.xzCompression)
                .setCategory("general.cave & cavern generation.caverns.lava caverns")
                .addToMap(properties);
        lavaCavernFrequency = new ConfigOption<>("Lava Cavern Frequency", Configuration.caveSettings.caverns.lavaCavern.caveFrequency)
                .setCategory("general.cave & cavern generation.caverns.lava caverns")
                .addToMap(properties);
        lavaCavernCustomFrequency = new ConfigOption<>("Lava Cavern Frequency Custom Value", Configuration.caveSettings.caverns.lavaCavern.customFrequency)
                .setCategory("general.cave & cavern generation.caverns.lava caverns")
                .addToMap(properties);

        // Floored cavern settings
        flooredCavernBottom = new ConfigOption<>("Cavern Bottom Altitude", Configuration.caveSettings.caverns.flooredCavern.caveBottom)
                .setCategory("general.cave & cavern generation.caverns.floored caverns")
                .addToMap(properties);
        flooredCavernTop = new ConfigOption<>("Cavern Top Altitude", Configuration.caveSettings.caverns.flooredCavern.caveTop)
                .setCategory("general.cave & cavern generation.caverns.floored caverns")
                .addToMap(properties);
        flooredCavernYCompression = new ConfigOption<>("Compression - Vertical", Configuration.caveSettings.caverns.flooredCavern.yCompression)
                .setCategory("general.cave & cavern generation.caverns.floored caverns")
                .addToMap(properties);
        flooredCavernXZCompression = new ConfigOption<>("Compression - Horizontal", Configuration.caveSettings.caverns.flooredCavern.xzCompression)
                .setCategory("general.cave & cavern generation.caverns.floored caverns")
                .addToMap(properties);
        flooredCavernFrequency = new ConfigOption<>("Floored Cavern Frequency", Configuration.caveSettings.caverns.flooredCavern.caveFrequency)
                .setCategory("general.cave & cavern generation.caverns.floored caverns")
                .addToMap(properties);
        flooredCavernCustomFrequency = new ConfigOption<>("Floored Cavern Frequency Custom Value", Configuration.caveSettings.caverns.flooredCavern.customFrequency)
                .setCategory("general.cave & cavern generation.caverns.floored caverns")
                .addToMap(properties);

        // Water region settings
        enableWaterRegions = new ConfigOption<>("Enable Water Regions", Configuration.caveSettings.waterRegions.enableWaterRegions)
                .setCategory("general.cave & cavern generation.water regions")
                .addToMap(properties);
        waterRegionFrequency = new ConfigOption<>("Water Region Frequency", Configuration.caveSettings.waterRegions.waterRegionFrequency)
                .setCategory("general.cave & cavern generation.water regions")
                .addToMap(properties);
        waterRegionCustomFrequency = new ConfigOption<>("Water Region Frequency Custom Value", Configuration.caveSettings.waterRegions.customFrequency)
                .setCategory("general.cave & cavern generation.water regions")
                .addToMap(properties);

        // Water cavern settings
        waterCavernYCompression = new ConfigOption<>("Compression - Vertical", Configuration.caveSettings.waterRegions.waterCavern.yCompression)
                .setCategory("general.cave & cavern generation.water regions.water caverns")
                .addToMap(properties);
        waterCavernXZCompression = new ConfigOption<>("Compression - Horizontal", Configuration.caveSettings.waterRegions.waterCavern.xzCompression)
                .setCategory("general.cave & cavern generation.water regions.water caverns")
                .addToMap(properties);

        // Bedrock settings
        flattenBedrock = new ConfigOption<>("Flatten Bedrock", Configuration.bedrockSettings.flattenBedrock)
                .setCategory("general.bedrock generation")
                .addToMap(properties);
        bedrockWidth = new ConfigOption<>("Bedrock Layer Width", Configuration.bedrockSettings.bedrockWidth)
                .setCategory("general.bedrock generation")
                .addToMap(properties);

        // Debug settings
        debugVisualizer = new ConfigOption<>("Enable DEBUG Visualizer", Configuration.debugsettings.debugVisualizer)
                .setCategory("general.debug settings")
                .addToMap(properties);

        /* ============================== Settings Hidden from User ============================== */
        // These are settings that are ordinarily hidden from users ...
        // ... because they are very important to cave gen and sensitive to change.

        // Cubic (Type 1) cave settings
        cubicCaveNoiseThreshold = new ConfigOption<>("Noise Threshold", Configuration.caveSettings.caves.cubicCave.noiseThreshold)
                .setCategory("general.cave & cavern generation.caves.type 1 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        cubicCaveFractalOctaves = new ConfigOption<>("Fractal Octaves", Configuration.caveSettings.caves.cubicCave.fractalOctaves)
                .setCategory("general.cave & cavern generation.caves.type 1 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        cubicCaveFractalGain = new ConfigOption<>("Fractal Gain", Configuration.caveSettings.caves.cubicCave.fractalGain)
                .setCategory("general.cave & cavern generation.caves.type 1 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        cubicCaveFractalFrequency = new ConfigOption<>("Fractal Frequency", Configuration.caveSettings.caves.cubicCave.fractalFrequency)
                .setCategory("general.cave & cavern generation.caves.type 1 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        cubicCaveEnableTurbulence = new ConfigOption<>("Use Turbulence", Configuration.caveSettings.caves.cubicCave.enableTurbulence)
                .setCategory("general.cave & cavern generation.caves.type 1 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        cubicCaveTurbulenceOctaves = new ConfigOption<>("Turbulence Octaves", Configuration.caveSettings.caves.cubicCave.turbulenceOctaves)
                .setCategory("general.cave & cavern generation.caves.type 1 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        cubicCaveTurbulenceGain = new ConfigOption<>("Turbulence Gain", Configuration.caveSettings.caves.cubicCave.turbulenceGain)
                .setCategory("general.cave & cavern generation.caves.type 1 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        cubicCaveTurbulenceFrequency = new ConfigOption<>("Turbulence Frequency", Configuration.caveSettings.caves.cubicCave.turbulenceFrequency)
                .setCategory("general.cave & cavern generation.caves.type 1 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        cubicCaveNumGenerators = new ConfigOption<>("Number of Generators", Configuration.caveSettings.caves.cubicCave.numGenerators)
                .setCategory("general.cave & cavern generation.caves.type 1 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        cubicCaveEnableVerticalAdjustment = new ConfigOption<>("Enable y-adjustment", Configuration.caveSettings.caves.cubicCave.yAdjust)
                .setCategory("general.cave & cavern generation.caves.type 1 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        cubicCaveYAdjustF1 = new ConfigOption<>("y-adjustment Variable 1", Configuration.caveSettings.caves.cubicCave.yAdjustF1)
                .setCategory("general.cave & cavern generation.caves.type 1 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        cubicCaveYAdjustF2 = new ConfigOption<>("y-adjustment Variable 2", Configuration.caveSettings.caves.cubicCave.yAdjustF2)
                .setCategory("general.cave & cavern generation.caves.type 1 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        cubicCaveNoiseType = new ConfigOption<>("Noise Type", Configuration.caveSettings.caves.cubicCave.noiseType)
                .setCategory("general.cave & cavern generation.caves.type 1 caves.advanced settings")
                .addToMap(properties)
                .hidden();

        // Simplex (Type 2) cave settings
        simplexCaveNoiseThreshold = new ConfigOption<>("Noise Threshold", Configuration.caveSettings.caves.simplexCave.noiseThreshold)
                .setCategory("general.cave & cavern generation.caves.type 2 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        simplexCaveFractalOctaves = new ConfigOption<>("Fractal Octaves", Configuration.caveSettings.caves.simplexCave.fractalOctaves)
                .setCategory("general.cave & cavern generation.caves.type 2 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        simplexCaveFractalGain = new ConfigOption<>("Fractal Gain", Configuration.caveSettings.caves.simplexCave.fractalGain)
                .setCategory("general.cave & cavern generation.caves.type 2 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        simplexCaveFractalFrequency = new ConfigOption<>("Fractal Frequency", Configuration.caveSettings.caves.simplexCave.fractalFrequency)
                .setCategory("general.cave & cavern generation.caves.type 2 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        simplexCaveEnableTurbulence = new ConfigOption<>("Use Turbulence", Configuration.caveSettings.caves.simplexCave.enableTurbulence)
                .setCategory("general.cave & cavern generation.caves.type 2 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        simplexCaveTurbulenceOctaves = new ConfigOption<>("Turbulence Octaves", Configuration.caveSettings.caves.simplexCave.turbulenceOctaves)
                .setCategory("general.cave & cavern generation.caves.type 2 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        simplexCaveTurbulenceGain = new ConfigOption<>("Turbulence Gain", Configuration.caveSettings.caves.simplexCave.turbulenceGain)
                .setCategory("general.cave & cavern generation.caves.type 2 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        simplexCaveTurbulenceFrequency = new ConfigOption<>("Turbulence Frequency", Configuration.caveSettings.caves.simplexCave.turbulenceFrequency)
                .setCategory("general.cave & cavern generation.caves.type 2 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        simplexCaveNumGenerators = new ConfigOption<>("Number of Generators", Configuration.caveSettings.caves.simplexCave.numGenerators)
                .setCategory("general.cave & cavern generation.caves.type 2 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        simplexCaveEnableVerticalAdjustment = new ConfigOption<>("Enable y-adjustment", Configuration.caveSettings.caves.simplexCave.yAdjust)
                .setCategory("general.cave & cavern generation.caves.type 2 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        simplexCaveYAdjustF1 = new ConfigOption<>("y-adjustment Variable 1", Configuration.caveSettings.caves.simplexCave.yAdjustF1)
                .setCategory("general.cave & cavern generation.caves.type 2 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        simplexCaveYAdjustF2 = new ConfigOption<>("y-adjustment Variable 2", Configuration.caveSettings.caves.simplexCave.yAdjustF2)
                .setCategory("general.cave & cavern generation.caves.type 2 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        simplexCaveNoiseType = new ConfigOption<>("Noise Type", Configuration.caveSettings.caves.simplexCave.noiseType)
                .setCategory("general.cave & cavern generation.caves.type 2 caves.advanced settings")
                .addToMap(properties)
                .hidden();

        // Water cavern settings
        waterCavernNoiseThreshold = new ConfigOption<>("Noise Threshold", Configuration.caveSettings.waterRegions.waterCavern.noiseThreshold)
                .setCategory("general.cave & cavern generation.water regions.water caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        waterCavernFractalOctaves = new ConfigOption<>("Fractal Octaves", Configuration.caveSettings.waterRegions.waterCavern.fractalOctaves)
                .setCategory("general.cave & cavern generation.water regions.water caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        waterCavernFractalGain = new ConfigOption<>("Fractal Gain", Configuration.caveSettings.waterRegions.waterCavern.fractalGain)
                .setCategory("general.cave & cavern generation.water regions.water caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        waterCavernFractalFrequency = new ConfigOption<>("Fractal Frequency", Configuration.caveSettings.waterRegions.waterCavern.fractalFrequency)
                .setCategory("general.cave & cavern generation.water regions.water caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        waterCavernNumGenerators = new ConfigOption<>("Number of Generators", Configuration.caveSettings.waterRegions.waterCavern.numGenerators)
                .setCategory("general.cave & cavern generation.water regions.water caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        waterCavernNoiseType = new ConfigOption<>("Noise Type", Configuration.caveSettings.waterRegions.waterCavern.noiseType)
                .setCategory("general.cave & cavern generation.water regions.water caverns.advanced settings")
                .addToMap(properties)
                .hidden();

        // Lava cavern settings
        lavaCavernNoiseThreshold = new ConfigOption<>("Noise Threshold", Configuration.caveSettings.caverns.lavaCavern.noiseThreshold)
                .setCategory("general.cave & cavern generation.caverns.lava caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        lavaCavernFractalOctaves = new ConfigOption<>("Fractal Octaves", Configuration.caveSettings.caverns.lavaCavern.fractalOctaves)
                .setCategory("general.cave & cavern generation.caverns.lava caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        lavaCavernFractalGain = new ConfigOption<>("Fractal Gain", Configuration.caveSettings.caverns.lavaCavern.fractalGain)
                .setCategory("general.cave & cavern generation.caverns.lava caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        lavaCavernFractalFrequency = new ConfigOption<>("Fractal Frequency", Configuration.caveSettings.caverns.lavaCavern.fractalFrequency)
                .setCategory("general.cave & cavern generation.caverns.lava caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        lavaCavernNumGenerators = new ConfigOption<>("Number of Generators", Configuration.caveSettings.caverns.lavaCavern.numGenerators)
                .setCategory("general.cave & cavern generation.caverns.lava caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        lavaCavernNoiseType = new ConfigOption<>("Noise Type", Configuration.caveSettings.caverns.lavaCavern.noiseType)
                .setCategory("general.cave & cavern generation.caverns.lava caverns.advanced settings")
                .addToMap(properties)
                .hidden();

        // Floored cavern settings
        flooredCavernNoiseThreshold = new ConfigOption<>("Noise Threshold", Configuration.caveSettings.caverns.flooredCavern.noiseThreshold)
                .setCategory("general.cave & cavern generation.caverns.floored caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        flooredCavernFractalOctaves = new ConfigOption<>("Fractal Octaves", Configuration.caveSettings.caverns.flooredCavern.fractalOctaves)
                .setCategory("general.cave & cavern generation.caverns.floored caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        flooredCavernFractalGain = new ConfigOption<>("Fractal Gain", Configuration.caveSettings.caverns.flooredCavern.fractalGain)
                .setCategory("general.cave & cavern generation.caverns.floored caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        flooredCavernFractalFrequency = new ConfigOption<>("Fractal Frequency", Configuration.caveSettings.caverns.flooredCavern.fractalFrequency)
                .setCategory("general.cave & cavern generation.caverns.floored caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        flooredCavernNumGenerators = new ConfigOption<>("Number of Generators", Configuration.caveSettings.caverns.flooredCavern.numGenerators)
                .setCategory("general.cave & cavern generation.caverns.floored caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        flooredCavernNoiseType = new ConfigOption<>("Noise Type", Configuration.caveSettings.caverns.flooredCavern.noiseType)
                .setCategory("general.cave & cavern generation.caverns.floored caverns.advanced settings")
                .addToMap(properties)
                .hidden();
    }

    /* ============================== Settings Visible to User ============================== */
    // Dimension-wide cave & cavern settings
    public ConfigOption<String>     lavaBlock;
    public ConfigOption<String>     waterBlock;
    public ConfigOption<Integer>    liquidAltitude;
    public ConfigOption<Boolean>    replaceFloatingGravel;

    // Dimension-wide cave settings
    public ConfigOption<RegionSize> caveRegionSize;
    public ConfigOption<Integer>    surfaceCutoff;
    public ConfigOption<Integer>    maxCaveAltitude;

    // Cubic (Type 1) cave settings
    public ConfigOption<Integer>       cubicCaveBottom;
    public ConfigOption<Float>         cubicCaveYCompression;
    public ConfigOption<Float>         cubicCaveXZCompression;
    public ConfigOption<CaveFrequency> cubicCaveFrequency;
    public ConfigOption<Float>         cubicCaveCustomFrequency;

    // Simplex (Type 2) cave settings
    public ConfigOption<Integer>       simplexCaveBottom;
    public ConfigOption<Float>         simplexCaveYCompression;
    public ConfigOption<Float>         simplexCaveXZCompression;
    public ConfigOption<CaveFrequency> simplexCaveFrequency;
    public ConfigOption<Float>         simplexCaveCustomFrequency;

    // Vanilla cave settings
    public ConfigOption<Boolean> enableVanillaCaves;
    public ConfigOption<Boolean> enableVanillaRavines;

    // Dimension-wide cavern settings
    public ConfigOption<RegionSize> cavernRegionSize;

    // Lava cavern settings
    public ConfigOption<Integer>         lavaCavernBottom;
    public ConfigOption<Integer>         lavaCavernTop;
    public ConfigOption<Float>           lavaCavernYCompression;
    public ConfigOption<Float>           lavaCavernXZCompression;
    public ConfigOption<CavernFrequency> lavaCavernFrequency;
    public ConfigOption<Float>           lavaCavernCustomFrequency;

    // Floored cavern settings
    public ConfigOption<Integer>         flooredCavernBottom;
    public ConfigOption<Integer>         flooredCavernTop;
    public ConfigOption<Float>           flooredCavernYCompression;
    public ConfigOption<Float>           flooredCavernXZCompression;
    public ConfigOption<CavernFrequency> flooredCavernFrequency;
    public ConfigOption<Float>           flooredCavernCustomFrequency;

    // Water region settings
    public ConfigOption<Boolean>              enableWaterRegions;
    public ConfigOption<WaterRegionFrequency> waterRegionFrequency;
    public ConfigOption<Float>                waterRegionCustomFrequency;

    // Water cavern settings
    public ConfigOption<Float> waterCavernYCompression;
    public ConfigOption<Float> waterCavernXZCompression;

    // Bedrock settings
    public ConfigOption<Boolean> flattenBedrock;
    public ConfigOption<Integer> bedrockWidth;

    // Debug settings
    public ConfigOption<Boolean> debugVisualizer;

    /* ============================== Settings Hidden from User ============================== */
    // These are settings that are ordinarily hidden from users ...
    // ... because they are very important to cave gen and sensitive to change.

    // Cubic (Type 1) cave settings
    public ConfigOption<Float>               cubicCaveNoiseThreshold;
    public ConfigOption<Integer>             cubicCaveFractalOctaves;
    public ConfigOption<Float>               cubicCaveFractalGain;
    public ConfigOption<Float>               cubicCaveFractalFrequency;
    public ConfigOption<Boolean>             cubicCaveEnableTurbulence;
    public ConfigOption<Integer>             cubicCaveTurbulenceOctaves;
    public ConfigOption<Float>               cubicCaveTurbulenceGain;
    public ConfigOption<Float>               cubicCaveTurbulenceFrequency;
    public ConfigOption<Integer>             cubicCaveNumGenerators;
    public ConfigOption<Boolean>             cubicCaveEnableVerticalAdjustment;
    public ConfigOption<Float>               cubicCaveYAdjustF1;
    public ConfigOption<Float>               cubicCaveYAdjustF2;
    public ConfigOption<FastNoise.NoiseType> cubicCaveNoiseType;

    // Simplex (Type 2) cave settings
    public ConfigOption<Float>               simplexCaveNoiseThreshold;
    public ConfigOption<Integer>             simplexCaveFractalOctaves;
    public ConfigOption<Float>               simplexCaveFractalGain;
    public ConfigOption<Float>               simplexCaveFractalFrequency;
    public ConfigOption<Boolean>             simplexCaveEnableTurbulence;
    public ConfigOption<Integer>             simplexCaveTurbulenceOctaves;
    public ConfigOption<Float>               simplexCaveTurbulenceGain;
    public ConfigOption<Float>               simplexCaveTurbulenceFrequency;
    public ConfigOption<Integer>             simplexCaveNumGenerators;
    public ConfigOption<Boolean>             simplexCaveEnableVerticalAdjustment;
    public ConfigOption<Float>               simplexCaveYAdjustF1;
    public ConfigOption<Float>               simplexCaveYAdjustF2;
    public ConfigOption<FastNoise.NoiseType> simplexCaveNoiseType;

    // Water cavern settings
    public ConfigOption<Float>               waterCavernNoiseThreshold;
    public ConfigOption<Integer>             waterCavernFractalOctaves;
    public ConfigOption<Float>               waterCavernFractalGain;
    public ConfigOption<Float>               waterCavernFractalFrequency;
    public ConfigOption<Integer>             waterCavernNumGenerators;
    public ConfigOption<FastNoise.NoiseType> waterCavernNoiseType;

    // Lava cavern settings
    public ConfigOption<Float>               lavaCavernNoiseThreshold;
    public ConfigOption<Integer>             lavaCavernFractalOctaves;
    public ConfigOption<Float>               lavaCavernFractalGain;
    public ConfigOption<Float>               lavaCavernFractalFrequency;
    public ConfigOption<Integer>             lavaCavernNumGenerators;
    public ConfigOption<FastNoise.NoiseType> lavaCavernNoiseType;

    // Floored cavern settings
    public ConfigOption<Float>               flooredCavernNoiseThreshold;
    public ConfigOption<Integer>             flooredCavernFractalOctaves;
    public ConfigOption<Float>               flooredCavernFractalGain;
    public ConfigOption<Float>               flooredCavernFractalFrequency;
    public ConfigOption<Integer>             flooredCavernNumGenerators;
    public ConfigOption<FastNoise.NoiseType> flooredCavernNoiseType;

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
