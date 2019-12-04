package com.yungnickyoung.minecraft.bettercaves.config.cave;

import com.yungnickyoung.minecraft.bettercaves.enums.WaterRegionFrequency;
import net.minecraftforge.common.config.Config;

public class ConfigWaterRegions {
    @Config.Name("Enable Water Regions")
    @Config.Comment("Set to true for caves & caverns to have a chance of spawning as their water-based variants, instead" +
            " of having lava")
    @Config.RequiresWorldRestart
    public boolean enableWaterRegions = true;

    @Config.Name("Water Caverns")
    @Config.Comment("Settings for Water Caverns (similar in structure to Lava Caverns).")
    @Config.RequiresWorldRestart
    public WaterCavern waterCavern = new WaterCavern();

    @Config.Name("Water Region Frequency")
    @Config.Comment("Determines how frequently water regions spawn. Only has an effect if Enable Water Regions is true.")
    @Config.RequiresWorldRestart
    public WaterRegionFrequency waterRegionFrequency = WaterRegionFrequency.Normal;

    @Config.Name("Water Region Frequency Custom Value")
    @Config.Comment("Custom value for water region frequency. Only works if Water Region Frequency is set to Custom. 0 = 0% chance of spawning, " +
            "1.0 = 100% chance of spawning. The value may not scale linearly. \nProvided values:\n" +
            "Rare: 0.3\n" +
            "Normal: 0.425\n" +
            "Common: 0.55\n" +
            "VeryCommon: 0.65\n" +
            "Always: 1.0")
    @Config.RangeDouble(min = 0, max = 1)
    @Config.RequiresWorldRestart
    public float customFrequency = 1.0f;

    public static class WaterCavern {
        @Config.Name("Vertical Compression")
        @Config.Comment("Changes height of formations in caverns. Lower value = more open caverns with larger features.")
        @Config.RangeDouble(min = 0, max = 100)
        @Config.RequiresWorldRestart
        public float yCompression = 1.0f;

        @Config.Name("Horizontal Compression")
        @Config.Comment("Changes width of formations in caverns. Lower value = more open caverns with larger features.")
        @Config.RangeDouble(min = 0, max = 100)
        @Config.RequiresWorldRestart
        public float xzCompression = 1.0f;

        @Config.Ignore
        @Config.Name("Noise Threshold")
        @Config.Comment("Threshold for determining which blocks get mined out as part of cave generation. Higher value = less caves.")
        @Config.RangeDouble(min = -1.0, max = 1.0)
        @Config.RequiresWorldRestart
        public float noiseThreshold = .75f;

        @Config.Ignore
        @Config.Name("Fractal Octaves")
        @Config.Comment("The number of octaves used for ridged multi-fractal noise generation.")
        @Config.RequiresWorldRestart
        public int fractalOctaves = 1;

        @Config.Ignore
        @Config.Name("Fractal Gain")
        @Config.Comment("The gain for ridged multi-fractal noise generation.")
        @Config.RequiresWorldRestart
        public float fractalGain = 0.3f;

        @Config.Ignore
        @Config.Name("Fractal Frequency")
        @Config.Comment("The frequency for ridged multi-fractal noise generation.")
        @Config.RequiresWorldRestart
        public float fractalFrequency = 0.03f;

        @Config.Ignore
        @Config.Name("Number of Generators")
        @Config.Comment("The number of noise generation functions used. The intersection of these functions is" +
                "used to calculate a single noise value.")
        @Config.RequiresWorldRestart
        public int numGenerators = 2;
    }
}
