package com.yungnickyoung.minecraft.bettercaves.config.cave;

import net.minecraftforge.common.config.Config;

public class ConfigInvertedPerlinCavern {
    @Config.Name("Noise Threshold")
    @Config.Comment("Threshold for determining which blocks get mined out as part of cave generation. Higher value = less caves.")
    @Config.RangeDouble(min = -1.0, max = 1.0)
    @Config.RequiresWorldRestart
    public float noiseThreshold = .7f;

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
    public int numGenerators = 2;

    @Config.Name("Terrain Smoothing")
    @Config.Comment("Attempts to smooth caves using average computation. Possibly computationally heavy.")
    @Config.RequiresWorldRestart
    public boolean enableSmoothing = false;
}
