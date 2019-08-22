package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.common.config.Config;

@Config(modid = Settings.MOD_ID, name = Settings.NAME)
public class Configuration {

    @Config.Name("Giant Cavern Settings")
    @Config.Comment("Parameters used in the generation of large cavernous caves")
    public static Cavern cavern = new Cavern();

    @Config.Name("Dynamic Cavern Settings")
    @Config.Comment("Parameters used in the generation of the dynamic variation of large cavernous caves")
    public static DynamicCavern dynamicCavern = new DynamicCavern();

    @Config.Name("Value Fractal Cave Settings")
    @Config.Comment("Parameters used in the generation of the value fractal caves")
    public static ValueFractalCave valueFractalCave = new ValueFractalCave();

    @Config.Name("Simplex Fractal Cave Settings")
    @Config.Comment("Parameters used in the generation of the simplex fractal caves")
    public static SimplexFractalCave simplexFractalCave = new SimplexFractalCave();

    @Config.Name("Cellular Cave Settings")
    @Config.Comment("Parameters used in the generation of the cellular caves")
    public static CellularCave cellularCave = new CellularCave();

    @Config.Name("Perlin Cave Settings")
    @Config.Comment("Parameters used in the generation of the cellular caves")
    public static PerlinFractalCave perlinFractalCave = new PerlinFractalCave();

    @Config.Name("Lava Depth")
    @Config.Comment("The y-coordinate below which lava replaces air. Default 10")
    @Config.RangeInt(min = 1)
    @Config.RequiresWorldRestart
    public static int lavaDepth = 10;

    @Config.Name("Bedrock Generation")
    @Config.Comment("Configure how bedrock spawns at the bottom of the map.")
    public static BedrockSettings bedrockSettings = new BedrockSettings();

    public static class Cavern {
        @Config.Name("Noise Threshold")
        @Config.Comment("Threshold for determining which blocks get mined out as part of cave generation. Higher value = less caves.")
        @Config.RangeDouble(min = -1.0, max = 1.0)
        @Config.RequiresWorldRestart
        public float noiseThreshold = .7f;

        @Config.Name("Use Turbulence")
        @Config.Comment("Enable to apply turbulence")
        @Config.RequiresWorldRestart
        public boolean enableTurbulence = true;

        @Config.Name("Fractal Octaves")
        @Config.Comment("The number of octaves used for ridged multi-fractal noise generation.")
        @Config.RequiresWorldRestart
        public int fractalOctaves = 1;

        @Config.Name("Fractal Gain")
        @Config.Comment("The gain for ridged multi-fractal noise generation.")
        @Config.RequiresWorldRestart
        public float fractalGain = 0.3f;

        @Config.Name("Fractal Frequency")
        @Config.Comment("The frequency for ridged multi-fractal noise generation.")
        @Config.RequiresWorldRestart
        public float fractalFrequency = 0.035f;

        @Config.Name("Turbulence Octaves")
        @Config.Comment("The number of octaves used for the fBM turbulence function.")
        @Config.RequiresWorldRestart
        public int turbulenceOctaves = 3;

        @Config.Name("Turbulence Gain")
        @Config.Comment("The gain for the fBM turbulence function.")
        @Config.RequiresWorldRestart
        public float turbulenceGain = 30f;

        @Config.Name("Turbulence Frequency")
        @Config.Comment("The frequency for the fBM turbulence function.")
        @Config.RequiresWorldRestart
        public float turbulenceFrequency = 0.03f;
    }

    public static class DynamicCavern {
        @Config.Name("Depth Power")
        @Config.Comment("Determines how much depth affects the size of caves. The higher the value, the more caves increase in size as the y-coordinate decreases.")
        @Config.RequiresWorldRestart
        public float depthPower = .2f;
    }

    public static class ValueFractalCave {
        @Config.Name("Noise Threshold")
        @Config.Comment("Threshold for determining which blocks get mined out as part of cave generation. Higher value = less caves.")
        @Config.RangeDouble(min = -1.0, max = 1.0)
        @Config.RequiresWorldRestart
        public float noiseThreshold = .675f;

        @Config.Name("Use Turbulence")
        @Config.Comment("Enable to apply turbulence")
        @Config.RequiresWorldRestart
        public boolean enableTurbulence = false;

        @Config.Name("Fractal Octaves")
        @Config.Comment("The number of octaves used for ridged multi-fractal noise generation.")
        @Config.RequiresWorldRestart
        public int fractalOctaves = 1;

        @Config.Name("Fractal Gain")
        @Config.Comment("The gain for ridged multi-fractal noise generation.")
        @Config.RequiresWorldRestart
        public float fractalGain = 0.3f;

        @Config.Name("Fractal Frequency")
        @Config.Comment("The frequency for ridged multi-fractal noise generation.")
        @Config.RequiresWorldRestart
        public float fractalFrequency = 0.07f;

        @Config.Name("Turbulence Octaves")
        @Config.Comment("The number of octaves used for the fBM turbulence function.")
        @Config.RequiresWorldRestart
        public int turbulenceOctaves = 3;

        @Config.Name("Turbulence Gain")
        @Config.Comment("The gain for the fBM turbulence function.")
        @Config.RequiresWorldRestart
        public float turbulenceGain = 30f;

        @Config.Name("Turbulence Frequency")
        @Config.Comment("The frequency for the fBM turbulence function.")
        @Config.RequiresWorldRestart
        public float turbulenceFrequency = 0.03f;
    }

    public static class SimplexFractalCave {
        @Config.Name("Noise Threshold")
        @Config.Comment("Threshold for determining which blocks get mined out as part of cave generation. Higher value = less caves.")
        @Config.RangeDouble(min = -1.0, max = 1.0)
        @Config.RequiresWorldRestart
        public float noiseThreshold = .77f;

        @Config.Name("Fractal Octaves")
        @Config.Comment("The number of octaves used for ridged multi-fractal noise generation.")
        @Config.RequiresWorldRestart
        public int fractalOctaves = 1;

        @Config.Name("Fractal Gain")
        @Config.Comment("The gain for ridged multi-fractal noise generation.")
        @Config.RequiresWorldRestart
        public float fractalGain = 0.3f;

        @Config.Name("Fractal Frequency")
        @Config.Comment("The frequency for ridged multi-fractal noise generation.")
        @Config.RequiresWorldRestart
        public float fractalFrequency = 0.04f;

        @Config.Name("Use Turbulence")
        @Config.Comment("Enable to apply turbulence")
        @Config.RequiresWorldRestart
        public boolean enableTurbulence = false;

        @Config.Name("Turbulence Octaves")
        @Config.Comment("The number of octaves used for the fBM turbulence function.")
        @Config.RequiresWorldRestart
        public int turbulenceOctaves = 3;

        @Config.Name("Turbulence Gain")
        @Config.Comment("The gain for the fBM turbulence function.")
        @Config.RequiresWorldRestart
        public float turbulenceGain = 45f;

        @Config.Name("Turbulence Frequency")
        @Config.Comment("The frequency for the fBM turbulence function.")
        @Config.RequiresWorldRestart
        public float turbulenceFrequency = 0.01f;

        @Config.Name("Max Height")
        @Config.Comment("The max y-coordinate for cave generation.")
        @Config.RequiresWorldRestart
        public int maxHeight = 64;

        @Config.Name("Min Height")
        @Config.Comment("The min y-coordinate for cave generation.")
        @Config.RequiresWorldRestart
        public int minHeight = 1;

        @Config.Name("Number of Generators")
        @Config.Comment("The number of noise generation functions used. The intersection of these functions is" +
                "used to calculate a single noise value.")
        @Config.RequiresWorldRestart
        public int numGenerators = 8;

        @Config.Name("Terrain Smoothing")
        @Config.Comment("Attempts to smooth caves using average computation. Possibly computationally heavy.")
        @Config.RequiresWorldRestart
        public boolean enableSmoothing = true;
    }

    public static class CellularCave {
        @Config.Name("Noise Threshold")
        @Config.Comment("Threshold for determining which blocks get mined out as part of cave generation. Higher value = less caves.")
        @Config.RangeDouble(min = -1.0, max = 1.0)
        @Config.RequiresWorldRestart
        public float noiseThreshold = -.79f;

        @Config.Name("Top Threshold")
        @Config.Comment("Threshold for determining which blocks get mined out as part of cave generation. Higher value = less caves.")
        @Config.RangeDouble(min = -1.0, max = 1.0)
        @Config.RequiresWorldRestart
        public float topThreshold = -.57f;

        @Config.Name("Use Turbulence")
        @Config.Comment("Enable to apply turbulence")
        @Config.RequiresWorldRestart
        public boolean enableTurbulence = true;

        @Config.Name("Fractal Octaves")
        @Config.Comment("The number of octaves used for ridged multi-fractal noise generation.")
        @Config.RequiresWorldRestart
        public int fractalOctaves = 1;

        @Config.Name("Fractal Gain")
        @Config.Comment("The gain for ridged multi-fractal noise generation.")
        @Config.RequiresWorldRestart
        public float fractalGain = 0.3f;

        @Config.Name("Fractal Frequency")
        @Config.Comment("The frequency for ridged multi-fractal noise generation.")
        @Config.RequiresWorldRestart
        public float fractalFrequency = 0.02f;

        @Config.Name("Turbulence Octaves")
        @Config.Comment("The number of octaves used for the fBM turbulence function.")
        @Config.RequiresWorldRestart
        public int turbulenceOctaves = 5;

        @Config.Name("Turbulence Gain")
        @Config.Comment("The gain for the fBM turbulence function.")
        @Config.RequiresWorldRestart
        public float turbulenceGain = 30f;

        @Config.Name("Turbulence Frequency")
        @Config.Comment("The frequency for the fBM turbulence function.")
        @Config.RequiresWorldRestart
        public float turbulenceFrequency = 0.005f;
    }

    public static class PerlinFractalCave {
        @Config.Name("Noise Threshold")
        @Config.Comment("Threshold for determining which blocks get mined out as part of cave generation. Higher value = less caves.")
        @Config.RangeDouble(min = -1.0, max = 1.0)
        @Config.RequiresWorldRestart
        public float noiseThreshold = .78f;

        @Config.Name("Use Turbulence")
        @Config.Comment("Enable to apply turbulence")
        @Config.RequiresWorldRestart
        public boolean enableTurbulence = true;

        @Config.Name("Fractal Octaves")
        @Config.Comment("The number of octaves used for ridged multi-fractal noise generation.")
        @Config.RequiresWorldRestart
        public int fractalOctaves = 1;

        @Config.Name("Fractal Gain")
        @Config.Comment("The gain for ridged multi-fractal noise generation.")
        @Config.RequiresWorldRestart
        public float fractalGain = 0.3f;

        @Config.Name("Fractal Frequency")
        @Config.Comment("The frequency for ridged multi-fractal noise generation.")
        @Config.RequiresWorldRestart
        public float fractalFrequency = 0.05f;

        @Config.Name("Turbulence Octaves")
        @Config.Comment("The number of octaves used for the fBM turbulence function.")
        @Config.RequiresWorldRestart
        public int turbulenceOctaves = 3;

        @Config.Name("Turbulence Gain")
        @Config.Comment("The gain for the fBM turbulence function.")
        @Config.RequiresWorldRestart
        public float turbulenceGain = 30f;

        @Config.Name("Turbulence Frequency")
        @Config.Comment("The frequency for the fBM turbulence function.")
        @Config.RequiresWorldRestart
        public float turbulenceFrequency = 0.015f;

        @Config.Name("Apply y-noise adjustment")
        @Config.Comment("Whether or not to adjust noise of blocks above to make caves taller")
        @Config.RequiresWorldRestart
        public boolean yNoiseAdjustment = true;
    }

    public static class BedrockSettings {
        public Overworld overworld = new Overworld();
        public Nether nether = new Nether();

        public static class Overworld {
            @Config.Name("Flatten Bedrock")
            @Config.Comment("Set this to true to replace the usual bedrock generation pattern with flat layers.")
            @Config.RequiresWorldRestart
            public boolean flattenBedrock = true;

            @Config.Name("Bedrock Layer Width")
            @Config.Comment("The width of the bedrock layer. Only works if Flatten Bedrock is true.")
            @Config.RequiresWorldRestart
            @Config.SlidingOption
            @Config.RangeInt(min = 1, max = 256)
            public int bedrockWidth = 1;
        }

        public static class Nether {
            @Config.Name("Flatten Bedrock")
            @Config.Comment("Set this to true to replace the usual bedrock generation pattern with flat layers.")
            @Config.RequiresWorldRestart
            public boolean flattenBedrock = true;

            @Config.Name("Bedrock Layer Width - Bottom")
            @Config.Comment("The width of the bedrock layer at the bottom of the nether. Only works if Flatten Bedrock is true.")
            @Config.RequiresWorldRestart
            @Config.SlidingOption
            @Config.RangeInt(min = 1, max = 64)
            public int bedrockWidthBottom = 1;

            @Config.Name("Bedrock Layer Width - Top")
            @Config.Comment("The width of the bedrock layer at the top of the nether. Only works if Flatten Bedrock is true.")
            @Config.RequiresWorldRestart
            @Config.SlidingOption
            @Config.RangeInt(min = 1, max = 64)
            public int bedrockWidthTop = 1;
        }
    }
}
