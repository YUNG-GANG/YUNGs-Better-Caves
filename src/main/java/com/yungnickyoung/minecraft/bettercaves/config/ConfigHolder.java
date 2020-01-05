package com.yungnickyoung.minecraft.bettercaves.config;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.enums.CaveFrequency;
import com.yungnickyoung.minecraft.bettercaves.enums.CavernFrequency;
import com.yungnickyoung.minecraft.bettercaves.enums.RegionSize;
import com.yungnickyoung.minecraft.bettercaves.enums.WaterRegionFrequency;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Holds all the config values specific to a single dimension.
 * By default, a dimension's values are equivalent to the global configuration found in
 * YUNG's Better Caves.cfg
 */
public class ConfigHolder {
    public Map<String, ConfigOption<?>> properties = new HashMap<>();

    public ConfigHolder() {
        // Dimension-wide settings
        lavaBlock = new ConfigOption<>("Lava Block", Configuration.lavaBlock)
                .setCategory("general")
                .addToMap(properties);
        waterBlock = new ConfigOption<>("Water Block", Configuration.waterblock)
                .setCategory("general")
                .addToMap(properties);
        liquidAltitude = new ConfigOption<>("Liquid Altitude", Configuration.liquidAltitude)
                .setCategory("general")
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
        enableBoundarySmoothing = new ConfigOption<>("Enable Smooth Cavern Edges", Configuration.caveSettings.caverns.enableBoundarySmoothing)
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
    }

    /* ============================== Settings Visible to User ============================== */
    // Dimension-wide settings
    public ConfigOption<String> lavaBlock;
    public ConfigOption<String> waterBlock;
    public ConfigOption<Integer> liquidAltitude;

    // Dimension-wide cave settings
    public ConfigOption<RegionSize> caveRegionSize;
    public ConfigOption<Integer> surfaceCutoff;
    public ConfigOption<Integer> maxCaveAltitude;

    // Cubic (Type 1) cave settings
    public ConfigOption<Integer> cubicCaveBottom;
    public ConfigOption<Float> cubicCaveYCompression;
    public ConfigOption<Float> cubicCaveXZCompression;
    public ConfigOption<CaveFrequency> cubicCaveFrequency;
    public ConfigOption<Float> cubicCaveCustomFrequency;

    // Simplex (Type 2) cave settings
    public ConfigOption<Integer> simplexCaveBottom;
    public ConfigOption<Float> simplexCaveYCompression;
    public ConfigOption<Float> simplexCaveXZCompression;
    public ConfigOption<CaveFrequency> simplexCaveFrequency;
    public ConfigOption<Float> simplexCaveCustomFrequency;

    // Vanilla cave settings
    public ConfigOption<Boolean> enableVanillaCaves;
    public ConfigOption<Boolean> enableVanillaRavines;

    // Dimension-wide cavern settings
    public ConfigOption<RegionSize> cavernRegionSize;
    public ConfigOption<Boolean> enableBoundarySmoothing;

    // Lava cavern settings
    public ConfigOption<Integer> lavaCavernBottom;
    public ConfigOption<Integer> lavaCavernTop;
    public ConfigOption<Float> lavaCavernYCompression;
    public ConfigOption<Float> lavaCavernXZCompression;
    public ConfigOption<CavernFrequency> lavaCavernFrequency;
    public ConfigOption<Float> lavaCavernCustomFrequency;

    // Floored cavern settings
    public ConfigOption<Integer> flooredCavernBottom;
    public ConfigOption<Integer> flooredCavernTop;
    public ConfigOption<Float> flooredCavernYCompression;
    public ConfigOption<Float> flooredCavernXZCompression;
    public ConfigOption<CavernFrequency> flooredCavernFrequency;
    public ConfigOption<Float> flooredCavernCustomFrequency;

    // Water region settings
    public ConfigOption<Boolean> enableWaterRegions;
    public ConfigOption<WaterRegionFrequency> waterRegionFrequency;
    public ConfigOption<Float> waterRegionCustomFrequency;

    // Water cavern settings
    public ConfigOption<Float> waterCavernYCompression;
    public ConfigOption<Float> waterCavernXZCompression;

    // Bedrock settings
    public ConfigOption<Boolean> flattenBedrock;
    public ConfigOption<Integer> bedrockWidth;

    // Debug settings
    public ConfigOption<Boolean> debugVisualizer;

    /* ============================== Settings Hidden from User ============================== */
    // These are settings that I hide from users because they are very important to cave gen and sensitive to change.
    // I decided to place them here along with other config options in case I decide to make them visible
    // to the user in the future.

    // Cubic (Type 1) cave settings
    public ConfigOption<Float> cubicCaveNoiseThreshold = new ConfigOption<>("Noise Threshold", Configuration.caveSettings.caves.cubicCave.noiseThreshold)
            .hidden();
    public ConfigOption<Integer> cubicCaveFractalOctaves = new ConfigOption<>("Fractal Octaves", Configuration.caveSettings.caves.cubicCave.fractalOctaves)
            .hidden();
    public ConfigOption<Float> cubicCaveFractalGain = new ConfigOption<>("Fractal Gain", Configuration.caveSettings.caves.cubicCave.fractalGain)
            .hidden();
    public ConfigOption<Float> cubicCaveFractalFrequency = new ConfigOption<>("Fractal Frequency", Configuration.caveSettings.caves.cubicCave.fractalFrequency)
            .hidden();
    public ConfigOption<Boolean> cubicCaveEnableTurbulence = new ConfigOption<>("Use Turbulence", Configuration.caveSettings.caves.cubicCave.enableTurbulence)
            .hidden();
    public ConfigOption<Integer> cubicCaveTurbulenceOctaves = new ConfigOption<>("Turbulence Octaves", Configuration.caveSettings.caves.cubicCave.turbulenceOctaves)
            .hidden();
    public ConfigOption<Float> cubicCaveTurbulenceGain = new ConfigOption<>("Turbulence Gain", Configuration.caveSettings.caves.cubicCave.turbulenceGain)
            .hidden();
    public ConfigOption<Float> cubicCaveTurbulenceFrequency = new ConfigOption<>("Turbulence Frequency", Configuration.caveSettings.caves.cubicCave.turbulenceFrequency)
            .hidden();
    public ConfigOption<Integer> cubicCaveNumGenerators = new ConfigOption<>("Number of Generators", Configuration.caveSettings.caves.cubicCave.numGenerators)
            .hidden();
    public ConfigOption<Boolean> cubicCaveEnableVerticalAdjustment = new ConfigOption<>("Enable y-adjustment", Configuration.caveSettings.caves.cubicCave.yAdjust)
            .hidden();
    public ConfigOption<Float> cubicCaveYAdjustF1 = new ConfigOption<>("y-adjustment Variable 1", Configuration.caveSettings.caves.cubicCave.yAdjustF1)
            .hidden();
    public ConfigOption<Float> cubicCaveYAdjustF2= new ConfigOption<>("y-adjustment Variable 2", Configuration.caveSettings.caves.cubicCave.yAdjustF2)
            .hidden();

    // Simplex (Type 2) cave settings
    public ConfigOption<Float> simplexCaveNoiseThreshold = new ConfigOption<>("Noise Threshold", Configuration.caveSettings.caves.simplexCave.noiseThreshold)
            .hidden();
    public ConfigOption<Integer> simplexCaveFractalOctaves = new ConfigOption<>("Fractal Octaves", Configuration.caveSettings.caves.simplexCave.fractalOctaves)
            .hidden();
    public ConfigOption<Float> simplexCaveFractalGain = new ConfigOption<>("Fractal Gain", Configuration.caveSettings.caves.simplexCave.fractalGain)
            .hidden();
    public ConfigOption<Float> simplexCaveFractalFrequency = new ConfigOption<>("Fractal Frequency", Configuration.caveSettings.caves.simplexCave.fractalFrequency)
            .hidden();
    public ConfigOption<Boolean> simplexCaveEnableTurbulence = new ConfigOption<>("Use Turbulence", Configuration.caveSettings.caves.simplexCave.enableTurbulence)
            .hidden();
    public ConfigOption<Integer> simplexCaveTurbulenceOctaves = new ConfigOption<>("Turbulence Octaves", Configuration.caveSettings.caves.simplexCave.turbulenceOctaves)
            .hidden();
    public ConfigOption<Float> simplexCaveTurbulenceGain = new ConfigOption<>("Turbulence Gain", Configuration.caveSettings.caves.simplexCave.turbulenceGain)
            .hidden();
    public ConfigOption<Float> simplexCaveTurbulenceFrequency = new ConfigOption<>("Turbulence Frequency", Configuration.caveSettings.caves.simplexCave.turbulenceFrequency)
            .hidden();
    public ConfigOption<Integer> simplexCaveNumGenerators = new ConfigOption<>("Number of Generators", Configuration.caveSettings.caves.simplexCave.numGenerators)
            .hidden();
    public ConfigOption<Boolean> simplexCaveEnableVerticalAdjustment = new ConfigOption<>("Enable y-adjustment", Configuration.caveSettings.caves.simplexCave.yAdjust)
            .hidden();
    public ConfigOption<Float> simplexCaveYAdjustF1 = new ConfigOption<>("y-adjustment Variable 1", Configuration.caveSettings.caves.simplexCave.yAdjustF1)
            .hidden();
    public ConfigOption<Float> simplexCaveYAdjustF2= new ConfigOption<>("y-adjustment Variable 2", Configuration.caveSettings.caves.simplexCave.yAdjustF2)
            .hidden();

    // Water cavern settings
    public ConfigOption<Float> waterCavernNoiseThreshold = new ConfigOption<>("Noise Threshold", Configuration.caveSettings.waterRegions.waterCavern.noiseThreshold)
            .hidden();
    public ConfigOption<Integer> waterCavernFractalOctaves = new ConfigOption<>("Fractal Octaves", Configuration.caveSettings.waterRegions.waterCavern.fractalOctaves)
            .hidden();
    public ConfigOption<Float> waterCavernFractalGain = new ConfigOption<>("Fractal Gain", Configuration.caveSettings.waterRegions.waterCavern.fractalGain)
            .hidden();
    public ConfigOption<Float> waterCavernFractalFrequency = new ConfigOption<>("Fractal Frequency", Configuration.caveSettings.waterRegions.waterCavern.fractalFrequency)
            .hidden();
    public ConfigOption<Integer> waterCavernNumGenerators = new ConfigOption<>("Number of Generators", Configuration.caveSettings.waterRegions.waterCavern.numGenerators)
            .hidden();

    // Lava cavern settings
    public ConfigOption<Float> lavaCavernNoiseThreshold = new ConfigOption<>("Noise Threshold", Configuration.caveSettings.caverns.lavaCavern.noiseThreshold)
            .hidden();
    public ConfigOption<Integer> lavaCavernFractalOctaves = new ConfigOption<>("Fractal Octaves", Configuration.caveSettings.caverns.lavaCavern.fractalOctaves)
            .hidden();
    public ConfigOption<Float> lavaCavernFractalGain = new ConfigOption<>("Fractal Gain", Configuration.caveSettings.caverns.lavaCavern.fractalGain)
            .hidden();
    public ConfigOption<Float> lavaCavernFractalFrequency = new ConfigOption<>("Fractal Frequency", Configuration.caveSettings.caverns.lavaCavern.fractalFrequency)
            .hidden();
    public ConfigOption<Integer> lavaCavernNumGenerators = new ConfigOption<>("Number of Generators", Configuration.caveSettings.caverns.lavaCavern.numGenerators)
            .hidden();

    // Floored cavern settings
    public ConfigOption<Float> flooredCavernNoiseThreshold = new ConfigOption<>("Noise Threshold", Configuration.caveSettings.caverns.flooredCavern.noiseThreshold)
            .hidden();
    public ConfigOption<Integer> flooredCavernFractalOctaves = new ConfigOption<>("Fractal Octaves", Configuration.caveSettings.caverns.flooredCavern.fractalOctaves)
            .hidden();
    public ConfigOption<Float> flooredCavernFractalGain = new ConfigOption<>("Fractal Gain", Configuration.caveSettings.caverns.flooredCavern.fractalGain)
            .hidden();
    public ConfigOption<Float> flooredCavernFractalFrequency = new ConfigOption<>("Fractal Frequency", Configuration.caveSettings.caverns.flooredCavern.fractalFrequency)
            .hidden();
    public ConfigOption<Integer> flooredCavernNumGenerators = new ConfigOption<>("Number of Generators", Configuration.caveSettings.caverns.flooredCavern.numGenerators)
            .hidden();

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
