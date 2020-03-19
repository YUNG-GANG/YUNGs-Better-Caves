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
        caveRegionCustomSize = new ConfigOption<>("Cave Region Size Custom Value", Configuration.caveSettings.caves.customRegionSize)
                .setCategory("general.cave & cavern generation.caves")
                .addToMap(properties);
        caveSpawnChance = new ConfigOption<>("Cave Spawn Chance", Configuration.caveSettings.caves.caveSpawnChance)
                .setCategory("general.cave & cavern generation.caves")
                .addToMap(properties);

        // Cubic (Type 1) cave settings
        cubicCaveBottom = new ConfigOption<>("Type 1 Cave Minimum Altitude", Configuration.caveSettings.caves.cubicCave.caveBottom)
                .setCategory("general.cave & cavern generation.caves.type 1 caves")
                .addToMap(properties);
        cubicCaveTop = new ConfigOption<>("Type 1 Cave Maximum Altitude", Configuration.caveSettings.caves.cubicCave.caveTop)
                .setCategory("general.cave & cavern generation.caves.type 1 caves")
                .addToMap(properties);
        cubicCaveSurfaceCutoffDepth = new ConfigOption<>("Type 1 Cave Surface Cutoff Depth", Configuration.caveSettings.caves.cubicCave.caveSurfaceCutoff)
                .setCategory("general.cave & cavern generation.caves.type 1 caves")
                .addToMap(properties);
        cubicCaveYCompression = new ConfigOption<>("Compression - Vertical", Configuration.caveSettings.caves.cubicCave.yCompression)
                .setCategory("general.cave & cavern generation.caves.type 1 caves")
                .addToMap(properties);
        cubicCaveXZCompression = new ConfigOption<>("Compression - Horizontal", Configuration.caveSettings.caves.cubicCave.xzCompression)
                .setCategory("general.cave & cavern generation.caves.type 1 caves")
                .addToMap(properties);
        cubicCavePriority = new ConfigOption<>("Type 1 Cave Priority", Configuration.caveSettings.caves.cubicCave.cavePriority)
                .setCategory("general.cave & cavern generation.caves.type 1 caves")
                .addToMap(properties);

        // Simplex (Type 2) cave settings
        simplexCaveBottom = new ConfigOption<>("Type 2 Cave Minimum Altitude", Configuration.caveSettings.caves.simplexCave.caveBottom)
                .setCategory("general.cave & cavern generation.caves.type 2 caves")
                .addToMap(properties);
        simplexCaveTop = new ConfigOption<>("Type 2 Cave Maximum Altitude", Configuration.caveSettings.caves.simplexCave.caveTop)
                .setCategory("general.cave & cavern generation.caves.type 2 caves")
                .addToMap(properties);
        simplexCaveSurfaceCutoffDepth = new ConfigOption<>("Type 2 Cave Surface Cutoff Depth", Configuration.caveSettings.caves.simplexCave.caveSurfaceCutoff)
                .setCategory("general.cave & cavern generation.caves.type 2 caves")
                .addToMap(properties);
        simplexCaveYCompression = new ConfigOption<>("Compression - Vertical", Configuration.caveSettings.caves.simplexCave.yCompression)
                .setCategory("general.cave & cavern generation.caves.type 2 caves")
                .addToMap(properties);
        simplexCaveXZCompression = new ConfigOption<>("Compression - Horizontal", Configuration.caveSettings.caves.simplexCave.xzCompression)
                .setCategory("general.cave & cavern generation.caves.type 2 caves")
                .addToMap(properties);
        simplexCavePriority = new ConfigOption<>("Type 2 Cave Priority", Configuration.caveSettings.caves.simplexCave.cavePriority)
                .setCategory("general.cave & cavern generation.caves.type 1 caves")
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
        cavernRegionCustomSize = new ConfigOption<>("Cavern Region Size Custom Value", Configuration.caveSettings.caverns.customRegionSize)
                .setCategory("general.cave & cavern generation.caverns")
                .addToMap(properties);
        cavernSpawnChance = new ConfigOption<>("Cavern Spawn Chance", Configuration.caveSettings.caverns.cavernSpawnChance)
                .setCategory("general.cave & cavern generation.caverns")
                .addToMap(properties);

        // Liquid cavern settings
        liquidCavernBottom = new ConfigOption<>("Cavern Bottom Altitude", Configuration.caveSettings.caverns.liquidCavern.cavernBottom)
                .setCategory("general.cave & cavern generation.caverns.liquid caverns")
                .addToMap(properties);
        liquidCavernTop = new ConfigOption<>("Cavern Top Altitude", Configuration.caveSettings.caverns.liquidCavern.cavernTop)
                .setCategory("general.cave & cavern generation.caverns.liquid caverns")
                .addToMap(properties);
        liquidCavernYCompression = new ConfigOption<>("Compression - Vertical", Configuration.caveSettings.caverns.liquidCavern.yCompression)
                .setCategory("general.cave & cavern generation.caverns.liquid caverns")
                .addToMap(properties);
        liquidCavernXZCompression = new ConfigOption<>("Compression - Horizontal", Configuration.caveSettings.caverns.liquidCavern.xzCompression)
                .setCategory("general.cave & cavern generation.caverns.liquid caverns")
                .addToMap(properties);
        liquidCavernPriority = new ConfigOption<>("Liquid Cavern Priority", Configuration.caveSettings.caverns.liquidCavern.cavernPriority)
                .setCategory("general.cave & cavern generation.caverns.liquid caverns")
                .addToMap(properties);

        // Floored cavern settings
        flooredCavernBottom = new ConfigOption<>("Cavern Bottom Altitude", Configuration.caveSettings.caverns.flooredCavern.cavernBottom)
                .setCategory("general.cave & cavern generation.caverns.floored caverns")
                .addToMap(properties);
        flooredCavernTop = new ConfigOption<>("Cavern Top Altitude", Configuration.caveSettings.caverns.flooredCavern.cavernTop)
                .setCategory("general.cave & cavern generation.caverns.floored caverns")
                .addToMap(properties);
        flooredCavernYCompression = new ConfigOption<>("Compression - Vertical", Configuration.caveSettings.caverns.flooredCavern.yCompression)
                .setCategory("general.cave & cavern generation.caverns.floored caverns")
                .addToMap(properties);
        flooredCavernXZCompression = new ConfigOption<>("Compression - Horizontal", Configuration.caveSettings.caverns.flooredCavern.xzCompression)
                .setCategory("general.cave & cavern generation.caverns.floored caverns")
                .addToMap(properties);
        flooredCavernPriority = new ConfigOption<>("Floored Cavern Priority", Configuration.caveSettings.caverns.flooredCavern.cavernPriority)
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

        //  Liquid cavern settings
        liquidCavernNoiseThreshold = new ConfigOption<>("Noise Threshold", Configuration.caveSettings.caverns.liquidCavern.noiseThreshold)
                .setCategory("general.cave & cavern generation.caverns.liquid caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        liquidCavernFractalOctaves = new ConfigOption<>("Fractal Octaves", Configuration.caveSettings.caverns.liquidCavern.fractalOctaves)
                .setCategory("general.cave & cavern generation.caverns.liquid caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        liquidCavernFractalGain = new ConfigOption<>("Fractal Gain", Configuration.caveSettings.caverns.liquidCavern.fractalGain)
                .setCategory("general.cave & cavern generation.caverns.liquid caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        liquidCavernFractalFrequency = new ConfigOption<>("Fractal Frequency", Configuration.caveSettings.caverns.liquidCavern.fractalFrequency)
                .setCategory("general.cave & cavern generation.caverns.liquid caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        liquidCavernNumGenerators = new ConfigOption<>("Number of Generators", Configuration.caveSettings.caverns.liquidCavern.numGenerators)
                .setCategory("general.cave & cavern generation.caverns.liquid caverns.advanced settings")
                .addToMap(properties)
                .hidden();
        liquidCavernNoiseType = new ConfigOption<>("Noise Type", Configuration.caveSettings.caverns.liquidCavern.noiseType)
                .setCategory("general.cave & cavern generation.caverns.liquid caverns.advanced settings")
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
    public ConfigOption<Float>      caveRegionCustomSize;
    public ConfigOption<Float>      caveSpawnChance;

    // Cubic (Type 1) cave settings
    public ConfigOption<Integer>       cubicCaveBottom;
    public ConfigOption<Integer>       cubicCaveTop;
    public ConfigOption<Integer>       cubicCaveSurfaceCutoffDepth;
    public ConfigOption<Float>         cubicCaveYCompression;
    public ConfigOption<Float>         cubicCaveXZCompression;
    public ConfigOption<Integer>       cubicCavePriority;

    // Simplex (Type 2) cave settings
    public ConfigOption<Integer>       simplexCaveBottom;
    public ConfigOption<Integer>       simplexCaveTop;
    public ConfigOption<Integer>       simplexCaveSurfaceCutoffDepth;
    public ConfigOption<Float>         simplexCaveYCompression;
    public ConfigOption<Float>         simplexCaveXZCompression;
    public ConfigOption<Integer>       simplexCavePriority;

    // Vanilla cave settings
    public ConfigOption<Boolean> enableVanillaCaves;
    public ConfigOption<Boolean> enableVanillaRavines;

    // Dimension-wide cavern settings
    public ConfigOption<RegionSize> cavernRegionSize;
    public ConfigOption<Float>      cavernRegionCustomSize;
    public ConfigOption<Float>      cavernSpawnChance;

    // Liquid cavern settings
    public ConfigOption<Integer>         liquidCavernBottom;
    public ConfigOption<Integer>         liquidCavernTop;
    public ConfigOption<Float>           liquidCavernYCompression;
    public ConfigOption<Float>           liquidCavernXZCompression;
    public ConfigOption<Integer>         liquidCavernPriority;

    // Floored cavern settings
    public ConfigOption<Integer>         flooredCavernBottom;
    public ConfigOption<Integer>         flooredCavernTop;
    public ConfigOption<Float>           flooredCavernYCompression;
    public ConfigOption<Float>           flooredCavernXZCompression;
    public ConfigOption<Integer>         flooredCavernPriority;

    // Water region settings
    public ConfigOption<Boolean>              enableWaterRegions;
    public ConfigOption<WaterRegionFrequency> waterRegionFrequency;
    public ConfigOption<Float>                waterRegionCustomFrequency;

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
