package com.yungnickyoung.minecraft.bettercaves.config.dimension;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.enums.CaveFrequency;
import com.yungnickyoung.minecraft.bettercaves.enums.CavernFrequency;
import com.yungnickyoung.minecraft.bettercaves.enums.RegionSize;
import com.yungnickyoung.minecraft.bettercaves.enums.WaterRegionFrequency;

/**
 * Holds all the config values specific to a single dimension.
 * By default, a dimension's values are equivalent to the global configuration found in
 * YUNG's Better Caves.cfg
 */
public class ConfigHolder {
    // Dimension-wide settings
    public ConfigOption<String> lavaBlock = new ConfigOption<>("Lava Block", Configuration.lavaBlock);
    public ConfigOption<String> waterBlock = new ConfigOption<>("Water Block", Configuration.waterblock);
    public ConfigOption<Integer> liquidAltitude = new ConfigOption<>("Liquid Altitude", Configuration.liquidAltitude);

    // Dimension-wide cave settings
    public ConfigOption<RegionSize> caveRegionSize = new ConfigOption<>("Cave Region Size", Configuration.caveSettings.caves.caveRegionSize);
    public ConfigOption<Integer> surfaceCutoff = new ConfigOption<>("Cave Surface Cutoff Depth", Configuration.caveSettings.caves.surfaceCutoff);
    public ConfigOption<Integer> maxCaveAltitude = new ConfigOption<>("Max Cave Altitude", Configuration.caveSettings.caves.maxCaveAltitude);

    // Cubic (Type 1) cave settings
    public ConfigOption<Integer> cubicCaveBottom = new ConfigOption<>("Cave Bottom Altitude", Configuration.caveSettings.caves.cubicCave.caveBottom);
    public ConfigOption<Float> cubicCaveYCompression = new ConfigOption<>("Compression - Vertical", Configuration.caveSettings.caves.cubicCave.yCompression);
    public ConfigOption<Float> cubicCaveXZCompression = new ConfigOption<>("Compression - Horizontal", Configuration.caveSettings.caves.cubicCave.xzCompression);
    public ConfigOption<CaveFrequency> cubicCaveFrequency = new ConfigOption<>("Type 1 Cave Frequency", Configuration.caveSettings.caves.cubicCave.caveFrequency);
    public ConfigOption<Float> cubicCaveCustomFrequency = new ConfigOption<>("Type 1 Cave Frequency Custom Value", Configuration.caveSettings.caves.cubicCave.customFrequency);

    // Simplex (Type 2) cave settings
    public ConfigOption<Integer> simplexCaveBottom = new ConfigOption<>("Cave Bottom Altitude", Configuration.caveSettings.caves.simplexCave.caveBottom);
    public ConfigOption<Float> simplexCaveYCompression = new ConfigOption<>("Compression - Vertical", Configuration.caveSettings.caves.simplexCave.yCompression);
    public ConfigOption<Float> simplexCaveXZCompression = new ConfigOption<>("Compression - Horizontal", Configuration.caveSettings.caves.simplexCave.xzCompression);
    public ConfigOption<CaveFrequency> simplexCaveFrequency = new ConfigOption<>("Type 2 Cave Frequency", Configuration.caveSettings.caves.simplexCave.caveFrequency);
    public ConfigOption<Float> simplexCaveCustomFrequency = new ConfigOption<>("Type 2 Cave Frequency Custom Value", Configuration.caveSettings.caves.simplexCave.customFrequency);

    // Vanilla cave settings
    public ConfigOption<Boolean> enableVanillaCaves = new ConfigOption<>("Enable Vanilla Caves", Configuration.caveSettings.caves.vanillaCave.enableVanillaCaves);
    public ConfigOption<Boolean> enableVanillaRavines = new ConfigOption<>("Enable Ravines", Configuration.caveSettings.caves.vanillaCave.enableVanillaRavines);

    // Dimension-wide cavern settings
    public ConfigOption<RegionSize> cavernRegionSize = new ConfigOption<>("Cavern Region Size", Configuration.caveSettings.caverns.cavernRegionSize);
    public ConfigOption<Boolean> enableBoundarySmoothing = new ConfigOption<>("Enable Smooth Cavern Edges", Configuration.caveSettings.caverns.enableBoundarySmoothing);

    // Lava cavern settings
    public ConfigOption<Integer> lavaCavernBottom = new ConfigOption<>("Cavern Bottom Altitude", Configuration.caveSettings.caverns.lavaCavern.caveBottom);
    public ConfigOption<Integer> lavaCavernTop = new ConfigOption<>("Cavern Top Altitude", Configuration.caveSettings.caverns.lavaCavern.caveTop);
    public ConfigOption<Float> lavaCavernYCompression = new ConfigOption<>("Compression - Vertical", Configuration.caveSettings.caverns.lavaCavern.yCompression);
    public ConfigOption<Float> lavaCavernXZCompression = new ConfigOption<>("Compression - Horizontal", Configuration.caveSettings.caverns.lavaCavern.xzCompression);
    public ConfigOption<CavernFrequency> lavaCavernFrequency = new ConfigOption<>("Lava Cavern Frequency", Configuration.caveSettings.caverns.lavaCavern.caveFrequency);
    public ConfigOption<Float> lavaCavernCustomFrequency = new ConfigOption<>("Lava Cavern Frequency Custom Value", Configuration.caveSettings.caverns.lavaCavern.customFrequency);

    // Floored cavern settings
    public ConfigOption<Integer> flooredCavernBottom = new ConfigOption<>("Cavern Bottom Altitude", Configuration.caveSettings.caverns.flooredCavern.caveBottom);
    public ConfigOption<Integer> flooredCavernTop = new ConfigOption<>("Cavern Top Altitude", Configuration.caveSettings.caverns.flooredCavern.caveTop);
    public ConfigOption<Float> flooredCavernYCompression = new ConfigOption<>("Compression - Vertical", Configuration.caveSettings.caverns.flooredCavern.yCompression);
    public ConfigOption<Float> flooredCavernXZCompression = new ConfigOption<>("Compression - Horizontal", Configuration.caveSettings.caverns.flooredCavern.xzCompression);
    public ConfigOption<CavernFrequency> flooredCavernFrequency = new ConfigOption<>("Floored Cavern Frequency", Configuration.caveSettings.caverns.flooredCavern.caveFrequency);
    public ConfigOption<Float> flooredCavernCustomFrequency = new ConfigOption<>("Floored Cavern Frequency Custom Value", Configuration.caveSettings.caverns.flooredCavern.customFrequency);

    // Water region settings
    public ConfigOption<Boolean> enableWaterRegions = new ConfigOption<>("Enable Water Regions", Configuration.caveSettings.waterRegions.enableWaterRegions);
    public ConfigOption<WaterRegionFrequency> waterRegionFrequency = new ConfigOption<>("Water Region Frequency", Configuration.caveSettings.waterRegions.waterRegionFrequency);
    public ConfigOption<Float> waterRegionCustomFrequency = new ConfigOption<>("Water Region Frequency Custom Value", Configuration.caveSettings.waterRegions.customFrequency);

    // Water cavern settings
    public ConfigOption<Float> waterCavernYCompression = new ConfigOption<>("Compression - Vertical", Configuration.caveSettings.waterRegions.waterCavern.yCompression);
    public ConfigOption<Float> waterCavernXZCompression = new ConfigOption<>("Compression - Horizontal", Configuration.caveSettings.waterRegions.waterCavern.xzCompression);

    // Bedrock settings
    public ConfigOption<Boolean> flattenBedrock = new ConfigOption<>("Flatten Bedrock", Configuration.bedrockSettings.flattenBedrock);
    public ConfigOption<Integer> bedrockWidth = new ConfigOption<>("Bedrock Layer Width", Configuration.bedrockSettings.bedrockWidth);

    // Debug settings
    public ConfigOption<Boolean> debugVisualizer = new ConfigOption<>("Enable DEBUG Visualizer", Configuration.debugsettings.debugVisualizer);

    public static class ConfigOption<T> {
        public String name;
        private T value;

        public ConfigOption(String name, T value) {
            this.name = name;
            this.value = value;
        }

        public T get() {
            return value;
        }
    }
}
