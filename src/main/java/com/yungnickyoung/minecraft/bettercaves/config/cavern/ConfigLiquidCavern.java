package com.yungnickyoung.minecraft.bettercaves.config.cavern;

import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import net.minecraftforge.common.config.Config;

public class ConfigLiquidCavern {
    @Config.Name("Liquid Cavern Minimum Altitude")
    @Config.Comment(
            "The minimum y-coordinate at which Liquid Caverns can generate.\n" +
            "Default: 1")
    @Config.RangeInt(min = 0, max = 255)
    @Config.RequiresWorldRestart
    public int cavernBottom = 1;

    @Config.Name("Liquid Cavern Maximum Altitude")
    @Config.Comment(
            "The maximum y-coordinate at which Liquid Caverns can generate.\n" +
            "    Caverns will attempt to close off anyway if this value is greater than the surface's altitude.\n" +
            "Default: 35")
    @Config.RangeInt(min = 0, max = 255)
    @Config.RequiresWorldRestart
    public int cavernTop = 35;

    @Config.Name("Compression - Vertical")
    @Config.Comment(
            "Stretches caverns vertically. Lower value = more open caverns with larger features.\n" +
            "Default: 1.3")
    @Config.RangeDouble(min = 0, max = 100)
    @Config.RequiresWorldRestart
    public float yCompression = 1.3f;

    @Config.Name("Compression - Horizontal")
    @Config.Comment(
            "Stretches caverns horizontally. Lower value = more open caverns with larger features.\n" +
            "Default: 0.7")
    @Config.RangeDouble(min = 0, max = 100)
    @Config.RequiresWorldRestart
    public float xzCompression = 0.7f;

    @Config.Name("Liquid Cavern Priority")
    @Config.Comment(
            "Determines how frequently Liquid Caverns spawn. 0 = will not spawn at all.\n" +
            "Default: 10")
    @Config.RangeInt(min = 0, max = 10)
    @Config.RequiresWorldRestart
    public int cavernPriority = 10;

    @Config.Ignore
    @Config.Name("Advanced Settings")
    @Config.Comment("Don't mess with these if you don't know what you're doing.")
    public Advanced advancedSettings = new Advanced();

    public class Advanced {
        @Config.Name("Noise Threshold")
        @Config.Comment(
                "Noise threshold for determining which blocks get mined out as part of cavern generation\n" +
                "    Blocks with generated noise values lower than this threshold will be dug out.\n" +
                "Default: 0.6")
        @Config.RangeDouble(min = -1.0, max = 1.0)
        @Config.RequiresWorldRestart
        public float noiseThreshold = .6f;

        @Config.Name("Fractal Octaves")
        @Config.Comment("The number of octaves used for ridged multi-fractal noise generation.\n" +
                "Default: 1")
        @Config.RequiresWorldRestart
        public int fractalOctaves = 1;

        @Config.Name("Fractal Gain")
        @Config.Comment("The gain for successive octaves of ridged multi-fractal noise generation.\n" +
                "Default: 0.3")
        @Config.RequiresWorldRestart
        public float fractalGain = 0.3f;

        @Config.Name("Fractal Frequency")
        @Config.Comment(
                "The frequency for ridged multi-fractal noise generation.\n" +
                "    This determines how spread out or tightly knit the formations in caverns are.\n" +
                "Default: 0.03")
        @Config.RequiresWorldRestart
        public float fractalFrequency = 0.03f;

        @Config.Name("Number of Generators")
        @Config.Comment(
                "The number of noise generation functions used.\n" +
                "    The intersection of these functions is used to calculate a single noise value.\n" +
                "    Increasing this may decrease performance.\n" +
                "Default: 2")
        @Config.RequiresWorldRestart
        public int numGenerators = 2;

        @Config.Name("Noise Type")
        @Config.Comment(
                "Type of noise to use for this cavern. \n" +
                "Default: SimplexFractal")
        public FastNoise.NoiseType noiseType = FastNoise.NoiseType.SimplexFractal;
    }
}
