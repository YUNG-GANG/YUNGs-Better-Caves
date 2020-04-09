package com.yungnickyoung.minecraft.bettercaves.config.cave;

import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import net.minecraftforge.common.config.Config;

public class ConfigSimplexCave {
    @Config.Name("Type 2 Cave Minimum Altitude")
    @Config.Comment(
            "The minimum y-coordinate at which type 2 caves can generate.\n" +
            "Default: 1")
    @Config.RangeInt(min = 0, max = 255)
    @Config.RequiresWorldRestart
    public int caveBottom = 1;

    @Config.Name("Type 2 Cave Maximum Altitude")
    @Config.Comment(
            "The maximum y-coordinate at which type 2 caves can generate.\n" +
            "Default: 80")
    @Config.RangeInt(min = 0, max = 255)
    @Config.RequiresWorldRestart
    public int caveTop = 80;

    @Config.Name("Type 2 Cave Surface Cutoff Depth")
    @Config.Comment(
            "The depth from a given point on the surface at which type 2 caves start to close off.\n" +
            "    Will use the Max Cave Altitude instead of surface height if it is lower.\n" +
            "    Will use the Max Cave Altitude no matter what if Override Surface Detection is enabled.\n" +
            "Default: 15 (recommended)")
    @Config.RangeInt(min = 0, max = 255)
    @Config.RequiresWorldRestart
    public int caveSurfaceCutoff = 15;

    @Config.Name("Compression - Vertical")
    @Config.Comment(
            "Stretches caves vertically. Lower value = taller caves with steeper drops.\n" +
            "Default: 2.2 (recommended)")
    @Config.RangeDouble(min = 0, max = 100)
    @Config.RequiresWorldRestart
    public float yCompression = 2.2f;

    @Config.Name("Compression - Horizontal")
    @Config.Comment(
            "Stretches caves horizontally. Lower value = wider caves.\n" +
            "Default: 0.9 (recommended)")
    @Config.RangeDouble(min = 0, max = 100)
    @Config.RequiresWorldRestart
    public float xzCompression = 0.9f;

    @Config.Name("Type 2 Cave Priority")
    @Config.Comment(
            "Determines how frequently Type 2 Caves spawn. 0 = will not spawn at all.\n" +
            "Default: 5")
    @Config.RangeInt(min = 0, max = 10)
    @Config.RequiresWorldRestart
    public int cavePriority = 5;

    @Config.Ignore
    @Config.Name("Advanced Settings")
    @Config.Comment("Don't mess with these if you don't know what you're doing.")
    public Advanced advancedSettings = new Advanced();

    public class Advanced {
        @Config.Name("Noise Threshold")
        @Config.Comment(
                "Noise threshold for determining which blocks get mined out as part of cave generation\n" +
                "    Blocks with generated noise values greater than this threshold will be dug out.\n" +
                "Default: 0.64")
        @Config.RangeDouble(min = -1.0, max = 1.0)
        @Config.RequiresWorldRestart
        public float noiseThreshold = .82f;

        @Config.Name("Fractal Octaves")
        @Config.Comment(
                "The number of octaves used for ridged multi-fractal noise generation.\n" +
                "Default: 2")
        @Config.RequiresWorldRestart
        public int fractalOctaves = 1;

        @Config.Name("Fractal Gain")
        @Config.Comment(
                "The gain for successive octaves of ridged multi-fractal noise generation.\n" +
                "Default: 0.3")
        @Config.RequiresWorldRestart
        public float fractalGain = 0.3f;

        @Config.Name("Fractal Frequency")
        @Config.Comment(
                "The frequency for ridged multi-fractal noise generation.\n" +
                "    This determines how spread out or tightly knit cave systems are.\n" +
                "Default: 0.03")
        @Config.RequiresWorldRestart
        public float fractalFrequency = 0.025f;

        @Config.Name("Number of Generators")
        @Config.Comment(
                "The number of noise generation functions used.\n" +
                "    The intersection of these functions is used to calculate a single noise value.\n" +
                "    Increasing this may decrease performance.\n" +
                "Default: 2")
        @Config.RequiresWorldRestart
        public int numGenerators = 2;

        @Config.Name("Enable y-adjustment")
        @Config.Comment(
                "Enable y-adjustment, giving players more headroom in caves.\n" +
                "Default: true")
        public boolean yAdjust = true;

        @Config.Name("y-adjustment Variable 1")
        @Config.Comment(
                "Adjustment factor affecting the block immediately above a given block.\n" +
                "    Higher value will tend to increase the headroom in caves.\n" +
                "Default: 0.95")
        @Config.RangeDouble(min = 0, max = 1f)
        public float yAdjustF1 = .95f;

        @Config.Name("y-adjustment Variable 2")
        @Config.Comment(
                "Adjustment factor affecting the block two blocks above a given block.\n" +
                "    Higher value will tend to increase the headroom in caves.\n" +
                "Default: 0.9")
        @Config.RangeDouble(min = 0, max = 1f)
        public float yAdjustF2 = .5f;

        @Config.Name("Noise Type")
        @Config.Comment("This value is currently unused for Type 2 caves. ")
        public FastNoise.NoiseType noiseType = FastNoise.NoiseType.SimplexFractal;
    }
}
