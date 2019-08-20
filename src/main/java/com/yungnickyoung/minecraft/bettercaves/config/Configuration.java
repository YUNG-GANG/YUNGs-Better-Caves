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
        public float noiseThreshold = .6f;

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
        public float fractalFrequency = 0.04f;

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

    public static class CellularCave {
        @Config.Name("Noise Threshold")
        @Config.Comment("Threshold for determining which blocks get mined out as part of cave generation. Higher value = less caves.")
        @Config.RangeDouble(min = -1.0, max = 1.0)
        @Config.RequiresWorldRestart
        public float noiseThreshold = -.77f;

        @Config.Name("Top Threshold")
        @Config.Comment("Threshold for determining which blocks get mined out as part of cave generation. Higher value = less caves.")
        @Config.RangeDouble(min = -1.0, max = 1.0)
        @Config.RequiresWorldRestart
        public float topThreshold = -.55f;

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
        public float fractalFrequency = 0.03f;

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

    public static class PerlinFractalCave {
        @Config.Name("Noise Threshold")
        @Config.Comment("Threshold for determining which blocks get mined out as part of cave generation. Higher value = less caves.")
        @Config.RangeDouble(min = -1.0, max = 1.0)
        @Config.RequiresWorldRestart
        public float noiseThreshold = .7f;

        @Config.Name("Top Threshold")
        @Config.Comment("Threshold for determining which blocks get mined out as part of cave generation. Higher value = less caves.")
        @Config.RequiresWorldRestart
        public float topThreshold = 1f;

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
        public float fractalFrequency = 0.03f;

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

        @Config.Name("Apply y-noise adjustment")
        @Config.Comment("Whether or not to adjust noise of blocks above to make caves taller")
        @Config.RequiresWorldRestart
        public boolean yNoiseAdjustment = false;
    }
}
