package com.yungnickyoung.minecraft.bettercaves.config.util;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.enums.*;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;

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
        /* ============================== Settings Visible to User ============================== */
        // Dimension-wide cave settings
        caveRegionSize = new ConfigOption<>("Cave Region Size", Configuration.caveSettings.caves.caveRegionSize)
                .setCategory("general.underground generation.caves")
                .addToMap(properties);
        caveRegionCustomSize = new ConfigOption<>("Cave Region Size Custom Value", Configuration.caveSettings.caves.customRegionSize)
                .setCategory("general.underground generation.caves")
                .addToMap(properties);
        caveSpawnChance = new ConfigOption<>("Cave Spawn Chance", Configuration.caveSettings.caves.caveSpawnChance)
                .setCategory("general.underground generation.caves")
                .addToMap(properties);

        // Cubic (Type 1) cave settings
        cubicCaveBottom = new ConfigOption<>("Type 1 Cave Minimum Altitude", Configuration.caveSettings.caves.cubicCave.caveBottom)
                .setCategory("general.underground generation.caves.type 1 caves")
                .addToMap(properties);
        cubicCaveTop = new ConfigOption<>("Type 1 Cave Maximum Altitude", Configuration.caveSettings.caves.cubicCave.caveTop)
                .setCategory("general.underground generation.caves.type 1 caves")
                .addToMap(properties);
        cubicCaveSurfaceCutoffDepth = new ConfigOption<>("Type 1 Cave Surface Cutoff Depth", Configuration.caveSettings.caves.cubicCave.caveSurfaceCutoff)
                .setCategory("general.underground generation.caves.type 1 caves")
                .addToMap(properties);
        cubicCaveYCompression = new ConfigOption<>("Compression - Vertical", Configuration.caveSettings.caves.cubicCave.yCompression)
                .setCategory("general.underground generation.caves.type 1 caves")
                .addToMap(properties);
        cubicCaveXZCompression = new ConfigOption<>("Compression - Horizontal", Configuration.caveSettings.caves.cubicCave.xzCompression)
                .setCategory("general.underground generation.caves.type 1 caves")
                .addToMap(properties);
        cubicCavePriority = new ConfigOption<>("Type 1 Cave Priority", Configuration.caveSettings.caves.cubicCave.cavePriority)
                .setCategory("general.underground generation.caves.type 1 caves")
                .addToMap(properties);

        // Simplex (Type 2) cave settings
        simplexCaveBottom = new ConfigOption<>("Type 2 Cave Minimum Altitude", Configuration.caveSettings.caves.simplexCave.caveBottom)
                .setCategory("general.underground generation.caves.type 2 caves")
                .addToMap(properties);
        simplexCaveTop = new ConfigOption<>("Type 2 Cave Maximum Altitude", Configuration.caveSettings.caves.simplexCave.caveTop)
                .setCategory("general.underground generation.caves.type 2 caves")
                .addToMap(properties);
        simplexCaveSurfaceCutoffDepth = new ConfigOption<>("Type 2 Cave Surface Cutoff Depth", Configuration.caveSettings.caves.simplexCave.caveSurfaceCutoff)
                .setCategory("general.underground generation.caves.type 2 caves")
                .addToMap(properties);
        simplexCaveYCompression = new ConfigOption<>("Compression - Vertical", Configuration.caveSettings.caves.simplexCave.yCompression)
                .setCategory("general.underground generation.caves.type 2 caves")
                .addToMap(properties);
        simplexCaveXZCompression = new ConfigOption<>("Compression - Horizontal", Configuration.caveSettings.caves.simplexCave.xzCompression)
                .setCategory("general.underground generation.caves.type 2 caves")
                .addToMap(properties);
        simplexCavePriority = new ConfigOption<>("Type 2 Cave Priority", Configuration.caveSettings.caves.simplexCave.cavePriority)
                .setCategory("general.underground generation.caves.type 2 caves")
                .addToMap(properties);

        // Surface cave settings
        isSurfaceCavesEnabled = new ConfigOption<>("Enable Surface Caves", Configuration.caveSettings.caves.surfaceCave.enableSurfaceCaves)
            .setCategory("general.underground generation.caves.surface caves")
            .addToMap(properties);
        surfaceCaveBottom = new ConfigOption<>("Surface Cave Minimum Altitude", Configuration.caveSettings.caves.surfaceCave.caveBottom)
            .setCategory("general.underground generation.caves.surface caves")
            .addToMap(properties);
        surfaceCaveTop = new ConfigOption<>("Surface Cave Maximum Altitude", Configuration.caveSettings.caves.surfaceCave.caveTop)
            .setCategory("general.underground generation.caves.surface caves")
            .addToMap(properties);
        surfaceCaveDensity = new ConfigOption<>("Surface Cave Density", Configuration.caveSettings.caves.surfaceCave.caveDensity)
            .setCategory("general.underground generation.caves.surface caves")
            .addToMap(properties);

        // Vanilla cave settings
        vanillaCaveBottom = new ConfigOption<>("Vanilla Cave Minimum Altitude", Configuration.caveSettings.caves.vanillaCave.caveBottom)
            .setCategory("general.underground generation.caves.vanilla caves")
            .addToMap(properties);
        vanillaCaveTop = new ConfigOption<>("Vanilla Cave Maximum Altitude", Configuration.caveSettings.caves.vanillaCave.caveTop)
            .setCategory("general.underground generation.caves.vanilla caves")
            .addToMap(properties);
        vanillaCaveDensity = new ConfigOption<>("Vanilla Cave Density", Configuration.caveSettings.caves.vanillaCave.caveDensity)
            .setCategory("general.underground generation.caves.vanilla caves")
            .addToMap(properties);
        vanillaCavePriority = new ConfigOption<>("Vanilla Cave Priority", Configuration.caveSettings.caves.vanillaCave.cavePriority)
            .setCategory("general.underground generation.caves.vanilla caves")
            .addToMap(properties);

        // Dimension-wide cavern settings
        cavernRegionSize = new ConfigOption<>("Cavern Region Size", Configuration.caveSettings.caverns.cavernRegionSize)
                .setCategory("general.underground generation.caverns")
                .addToMap(properties);
        cavernRegionCustomSize = new ConfigOption<>("Cavern Region Size Custom Value", Configuration.caveSettings.caverns.customRegionSize)
                .setCategory("general.underground generation.caverns")
                .addToMap(properties);
        cavernSpawnChance = new ConfigOption<>("Cavern Spawn Chance", Configuration.caveSettings.caverns.cavernSpawnChance)
                .setCategory("general.underground generation.caverns")
                .addToMap(properties);

        // Liquid cavern settings
        liquidCavernBottom = new ConfigOption<>("Liquid Cavern Minimum Altitude", Configuration.caveSettings.caverns.liquidCavern.cavernBottom)
                .setCategory("general.underground generation.caverns.liquid caverns")
                .addToMap(properties);
        liquidCavernTop = new ConfigOption<>("Liquid Cavern Maximum Altitude", Configuration.caveSettings.caverns.liquidCavern.cavernTop)
                .setCategory("general.underground generation.caverns.liquid caverns")
                .addToMap(properties);
        liquidCavernYCompression = new ConfigOption<>("Compression - Vertical", Configuration.caveSettings.caverns.liquidCavern.yCompression)
                .setCategory("general.underground generation.caverns.liquid caverns")
                .addToMap(properties);
        liquidCavernXZCompression = new ConfigOption<>("Compression - Horizontal", Configuration.caveSettings.caverns.liquidCavern.xzCompression)
                .setCategory("general.underground generation.caverns.liquid caverns")
                .addToMap(properties);
        liquidCavernPriority = new ConfigOption<>("Liquid Cavern Priority", Configuration.caveSettings.caverns.liquidCavern.cavernPriority)
                .setCategory("general.underground generation.caverns.liquid caverns")
                .addToMap(properties);

        // Floored cavern settings
        flooredCavernBottom = new ConfigOption<>("Floored Cavern Minimum Altitude", Configuration.caveSettings.caverns.flooredCavern.cavernBottom)
                .setCategory("general.underground generation.caverns.floored caverns")
                .addToMap(properties);
        flooredCavernTop = new ConfigOption<>("Floored Cavern Maximum Altitude", Configuration.caveSettings.caverns.flooredCavern.cavernTop)
                .setCategory("general.underground generation.caverns.floored caverns")
                .addToMap(properties);
        flooredCavernYCompression = new ConfigOption<>("Compression - Vertical", Configuration.caveSettings.caverns.flooredCavern.yCompression)
                .setCategory("general.underground generation.caverns.floored caverns")
                .addToMap(properties);
        flooredCavernXZCompression = new ConfigOption<>("Compression - Horizontal", Configuration.caveSettings.caverns.flooredCavern.xzCompression)
                .setCategory("general.underground generation.caverns.floored caverns")
                .addToMap(properties);
        flooredCavernPriority = new ConfigOption<>("Floored Cavern Priority", Configuration.caveSettings.caverns.flooredCavern.cavernPriority)
                .setCategory("general.underground generation.caverns.floored caverns")
                .addToMap(properties);

        // Water region settings
        waterRegionSpawnChance = new ConfigOption<>("Water Region Spawn Chance", Configuration.caveSettings.waterRegions.waterRegionSpawnChance)
            .setCategory("general.underground generation.water regions")
            .addToMap(properties);
        waterRegionSize = new ConfigOption<>("Water Region Size", Configuration.caveSettings.waterRegions.waterRegionSize)
            .setCategory("general.underground generation.water regions")
            .addToMap(properties);
        waterRegionCustomSize = new ConfigOption<>("Water Region Custom Size", Configuration.caveSettings.waterRegions.waterRegionCustomSize)
            .setCategory("general.underground generation.water regions")
            .addToMap(properties);

        // Ravines
        enableVanillaRavines = new ConfigOption<>("Enable Ravines", Configuration.caveSettings.ravines.enableVanillaRavines)
            .setCategory("general.underground generation.ravines")
            .addToMap(properties);
        enableFloodedRavines = new ConfigOption<>("Enable Flooded Ravines", Configuration.caveSettings.ravines.enableFloodedRavines)
            .setCategory("general.underground generation.ravines")
            .addToMap(properties);

        // Miscellaneous cave & cavern settings
        lavaBlock = new ConfigOption<>("Lava Block", Configuration.caveSettings.miscellaneous.lavaBlock)
                .setCategory("general.underground generation.miscellaneous")
                .addToMap(properties);
        waterBlock = new ConfigOption<>("Water Block", Configuration.caveSettings.miscellaneous.waterBlock)
                .setCategory("general.underground generation.miscellaneous")
                .addToMap(properties);
        liquidAltitude = new ConfigOption<>("Liquid Altitude", Configuration.caveSettings.miscellaneous.liquidAltitude)
                .setCategory("general.underground generation.miscellaneous")
                .addToMap(properties);
        replaceFloatingGravel = new ConfigOption<>("Replace Floating Gravel", Configuration.caveSettings.miscellaneous.replaceFloatingGravel)
                .setCategory("general.underground generation.miscellaneous")
                .addToMap(properties);
        overrideSurfaceDetection = new ConfigOption<>("Override Surface Detection", Configuration.caveSettings.miscellaneous.overrideSurfaceDetection)
            .setCategory("general.underground generation.miscellaneous")
            .addToMap(properties);
        enableFloodedUnderground = new ConfigOption<>("Enable Flooded Underground", Configuration.caveSettings.miscellaneous.enableFloodedUnderground)
            .setCategory("general.underground generation.miscellaneous")
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
        cubicCaveNoiseThreshold = new ConfigOption<>("Noise Threshold", Configuration.caveSettings.caves.cubicCave.advancedSettings.noiseThreshold)
                .setCategory("general.underground generation.caves.type 1 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        cubicCaveFractalOctaves = new ConfigOption<>("Fractal Octaves", Configuration.caveSettings.caves.cubicCave.advancedSettings.fractalOctaves)
                .setCategory("general.underground generation.caves.type 1 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        cubicCaveFractalGain = new ConfigOption<>("Fractal Gain", Configuration.caveSettings.caves.cubicCave.advancedSettings.fractalGain)
                .setCategory("general.underground generation.caves.type 1 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        cubicCaveFractalFrequency = new ConfigOption<>("Fractal Frequency", Configuration.caveSettings.caves.cubicCave.advancedSettings.fractalFrequency)
                .setCategory("general.underground generation.caves.type 1 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        cubicCaveNumGenerators = new ConfigOption<>("Number of Generators", Configuration.caveSettings.caves.cubicCave.advancedSettings.numGenerators)
                .setCategory("general.underground generation.caves.type 1 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        cubicCaveEnableVerticalAdjustment = new ConfigOption<>("Enable y-adjustment", Configuration.caveSettings.caves.cubicCave.advancedSettings.yAdjust)
                .setCategory("general.underground generation.caves.type 1 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        cubicCaveYAdjustF1 = new ConfigOption<>("y-adjustment Variable 1", Configuration.caveSettings.caves.cubicCave.advancedSettings.yAdjustF1)
                .setCategory("general.underground generation.caves.type 1 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        cubicCaveYAdjustF2 = new ConfigOption<>("y-adjustment Variable 2", Configuration.caveSettings.caves.cubicCave.advancedSettings.yAdjustF2)
                .setCategory("general.underground generation.caves.type 1 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        cubicCaveNoiseType = new ConfigOption<>("Noise Type", Configuration.caveSettings.caves.cubicCave.advancedSettings.noiseType)
                .setCategory("general.underground generation.caves.type 1 caves.advanced settings")
                .addToMap(properties)
                .hidden();

        // Simplex (Type 2) cave settings
        simplexCaveNoiseThreshold = new ConfigOption<>("Noise Threshold", Configuration.caveSettings.caves.simplexCave.advancedSettings.noiseThreshold)
                .setCategory("general.underground generation.caves.type 2 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        simplexCaveFractalOctaves = new ConfigOption<>("Fractal Octaves", Configuration.caveSettings.caves.simplexCave.advancedSettings.fractalOctaves)
                .setCategory("general.underground generation.caves.type 2 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        simplexCaveFractalGain = new ConfigOption<>("Fractal Gain", Configuration.caveSettings.caves.simplexCave.advancedSettings.fractalGain)
                .setCategory("general.underground generation.caves.type 2 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        simplexCaveFractalFrequency = new ConfigOption<>("Fractal Frequency", Configuration.caveSettings.caves.simplexCave.advancedSettings.fractalFrequency)
                .setCategory("general.underground generation.caves.type 2 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        simplexCaveNumGenerators = new ConfigOption<>("Number of Generators", Configuration.caveSettings.caves.simplexCave.advancedSettings.numGenerators)
                .setCategory("general.underground generation.caves.type 2 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        simplexCaveEnableVerticalAdjustment = new ConfigOption<>("Enable y-adjustment", Configuration.caveSettings.caves.simplexCave.advancedSettings.yAdjust)
                .setCategory("general.underground generation.caves.type 2 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        simplexCaveYAdjustF1 = new ConfigOption<>("y-adjustment Variable 1", Configuration.caveSettings.caves.simplexCave.advancedSettings.yAdjustF1)
                .setCategory("general.underground generation.caves.type 2 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        simplexCaveYAdjustF2 = new ConfigOption<>("y-adjustment Variable 2", Configuration.caveSettings.caves.simplexCave.advancedSettings.yAdjustF2)
                .setCategory("general.underground generation.caves.type 2 caves.advanced settings")
                .addToMap(properties)
                .hidden();
        simplexCaveNoiseType = new ConfigOption<>("Noise Type", Configuration.caveSettings.caves.simplexCave.advancedSettings.noiseType)
                .setCategory("general.underground generation.caves.type 2 caves.advanced settings")
                .addToMap(properties)
                .hidden();

        //  Liquid cavern settings
        liquidCavernNoiseThreshold = new ConfigOption<>("Noise Threshold", Configuration.caveSettings.caverns.liquidCavern.advancedSettings.noiseThreshold)
                .setCategory("general.underground generation.caverns.liquid caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        liquidCavernFractalOctaves = new ConfigOption<>("Fractal Octaves", Configuration.caveSettings.caverns.liquidCavern.advancedSettings.fractalOctaves)
                .setCategory("general.underground generation.caverns.liquid caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        liquidCavernFractalGain = new ConfigOption<>("Fractal Gain", Configuration.caveSettings.caverns.liquidCavern.advancedSettings.fractalGain)
                .setCategory("general.underground generation.caverns.liquid caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        liquidCavernFractalFrequency = new ConfigOption<>("Fractal Frequency", Configuration.caveSettings.caverns.liquidCavern.advancedSettings.fractalFrequency)
                .setCategory("general.underground generation.caverns.liquid caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        liquidCavernNumGenerators = new ConfigOption<>("Number of Generators", Configuration.caveSettings.caverns.liquidCavern.advancedSettings.numGenerators)
                .setCategory("general.underground generation.caverns.liquid caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        liquidCavernNoiseType = new ConfigOption<>("Noise Type", Configuration.caveSettings.caverns.liquidCavern.advancedSettings.noiseType)
                .setCategory("general.underground generation.caverns.liquid caverns.advanced settings")
                .addToMap(properties)
                .hidden();

        // Floored cavern settings
        flooredCavernNoiseThreshold = new ConfigOption<>("Noise Threshold", Configuration.caveSettings.caverns.flooredCavern.advancedSettings.noiseThreshold)
                .setCategory("general.underground generation.caverns.floored caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        flooredCavernFractalOctaves = new ConfigOption<>("Fractal Octaves", Configuration.caveSettings.caverns.flooredCavern.advancedSettings.fractalOctaves)
                .setCategory("general.underground generation.caverns.floored caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        flooredCavernFractalGain = new ConfigOption<>("Fractal Gain", Configuration.caveSettings.caverns.flooredCavern.advancedSettings.fractalGain)
                .setCategory("general.underground generation.caverns.floored caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        flooredCavernFractalFrequency = new ConfigOption<>("Fractal Frequency", Configuration.caveSettings.caverns.flooredCavern.advancedSettings.fractalFrequency)
                .setCategory("general.underground generation.caverns.floored caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        flooredCavernNumGenerators = new ConfigOption<>("Number of Generators", Configuration.caveSettings.caverns.flooredCavern.advancedSettings.numGenerators)
                .setCategory("general.underground generation.caverns.floored caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        flooredCavernNoiseType = new ConfigOption<>("Noise Type", Configuration.caveSettings.caverns.flooredCavern.advancedSettings.noiseType)
                .setCategory("general.underground generation.caverns.floored caverns.advanced settings")
                .addToMap(properties)
                .hidden();
    }

    /* ============================== Settings Visible to User ============================== */
    // Dimension-wide cave settings
    public ConfigOption<RegionSize> caveRegionSize;
    public ConfigOption<Float>      caveRegionCustomSize;
    public ConfigOption<Float>      caveSpawnChance;

    // Cubic (Type 1) cave settings
    public ConfigOption<Integer> cubicCaveBottom;
    public ConfigOption<Integer> cubicCaveTop;
    public ConfigOption<Integer> cubicCaveSurfaceCutoffDepth;
    public ConfigOption<Float>   cubicCaveYCompression;
    public ConfigOption<Float>   cubicCaveXZCompression;
    public ConfigOption<Integer> cubicCavePriority;

    // Simplex (Type 2) cave settings
    public ConfigOption<Integer> simplexCaveBottom;
    public ConfigOption<Integer> simplexCaveTop;
    public ConfigOption<Integer> simplexCaveSurfaceCutoffDepth;
    public ConfigOption<Float>   simplexCaveYCompression;
    public ConfigOption<Float>   simplexCaveXZCompression;
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
    public ConfigOption<RegionSize> cavernRegionSize;
    public ConfigOption<Float>      cavernRegionCustomSize;
    public ConfigOption<Float>      cavernSpawnChance;

    // Liquid cavern settings
    public ConfigOption<Integer> liquidCavernBottom;
    public ConfigOption<Integer> liquidCavernTop;
    public ConfigOption<Float>   liquidCavernYCompression;
    public ConfigOption<Float>   liquidCavernXZCompression;
    public ConfigOption<Integer> liquidCavernPriority;

    // Floored cavern settings
    public ConfigOption<Integer>  flooredCavernBottom;
    public ConfigOption<Integer>  flooredCavernTop;
    public ConfigOption<Float>    flooredCavernYCompression;
    public ConfigOption<Float>    flooredCavernXZCompression;
    public ConfigOption<Integer>  flooredCavernPriority;

    // Water region settings
    public ConfigOption<Float>      waterRegionSpawnChance;
    public ConfigOption<RegionSize> waterRegionSize;
    public ConfigOption<Float>      waterRegionCustomSize;

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
    public ConfigOption<Float>               cubicCaveNoiseThreshold;
    public ConfigOption<Integer>             cubicCaveFractalOctaves;
    public ConfigOption<Float>               cubicCaveFractalGain;
    public ConfigOption<Float>               cubicCaveFractalFrequency;
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
    public ConfigOption<Integer>             simplexCaveNumGenerators;
    public ConfigOption<Boolean>             simplexCaveEnableVerticalAdjustment;
    public ConfigOption<Float>               simplexCaveYAdjustF1;
    public ConfigOption<Float>               simplexCaveYAdjustF2;
    public ConfigOption<FastNoise.NoiseType> simplexCaveNoiseType;

    // Liquid cavern settings
    public ConfigOption<Float>               liquidCavernNoiseThreshold;
    public ConfigOption<Integer>             liquidCavernFractalOctaves;
    public ConfigOption<Float>               liquidCavernFractalGain;
    public ConfigOption<Float>               liquidCavernFractalFrequency;
    public ConfigOption<Integer>             liquidCavernNumGenerators;
    public ConfigOption<FastNoise.NoiseType> liquidCavernNoiseType;

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
