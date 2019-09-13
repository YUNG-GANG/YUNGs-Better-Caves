package com.yungnickyoung.minecraft.bettercaves.config.cave;

import com.yungnickyoung.minecraft.bettercaves.enums.WaterBiomeFrequency;
import net.minecraftforge.common.config.Config;

public class ConfigWaterBiomes {


    @Config.Name("Enable Water Cave/Cavern Biomes")
    @Config.Comment("Set to true for caves/caverns to have a chance of spawning as their water-based variants, instead" +
            " of having lava")
    @Config.RequiresWorldRestart
    public boolean enableWaterBiomes = true;

    @Config.Name("Water Biome Frequency")
    @Config.Comment("Determines how frequently water biomes spawn")
    public WaterBiomeFrequency waterBiomeFrequency = WaterBiomeFrequency.Normal;

    @Config.Name("Water Caverns")
    @Config.Comment("Settings for Water Caverns (similar in structure to Lava Caverns)")
    public WaterCavern waterCavern = new WaterCavern();

    public static class WaterCavern {
        @Config.Name("Vertical Compression")
        @Config.Comment("Changes height of caves. Lower value = taller caves with steeper drops")
        @Config.RangeDouble(min = 0)
        @Config.RequiresWorldRestart
        public float yCompression = 1.0f;

        @Config.Name("Horizontal Compression")
        @Config.Comment("Changes width of caves. Lower value = wider caves")
        @Config.RangeDouble(min = 0)
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
