package com.yungnickyoung.minecraft.bettercaves.config;

import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import net.minecraftforge.common.config.Config;

public class ConfigTest {
    @Config.Name("Noise Type")
    @Config.Comment("The type of noise to test with.")
    @Config.RequiresWorldRestart
    public FastNoise.NoiseType testnoiseType = FastNoise.NoiseType.CubicFractal;

    @Config.Name("Cave Bottom Altitude")
    @Config.Comment("The minimum y-coordinate at which caves start generating. Default: 20")
    @Config.RequiresWorldRestart
    public int caveBottom = 13;

    @Config.Name("Vertical Compression")
    @Config.Comment("Changes height of caves. Lower value = taller caves with steeper drops.")
    @Config.RangeDouble(min = 0)
    @Config.RequiresWorldRestart
    public float yCompression = 1.0f;

    @Config.Name("Horizontal Compression")
    @Config.Comment("Changes width of caves. Lower value = wider caves.")
    @Config.RangeDouble(min = 0)
    @Config.RequiresWorldRestart
    public float xzCompression = 1.0f;

    @Config.Name("Noise Threshold")
    @Config.Comment("Threshold for determining which blocks get mined out as part of cave generation. Higher value" +
            " = less caves.")
    @Config.RangeDouble(min = -1.0, max = 1.0)
    @Config.RequiresWorldRestart
    public float noiseThreshold = .7f;

    @Config.Name("Fractal Octaves")
    @Config.Comment("The number of octaves used for ridged multi-fractal noise generation.")
    @Config.RequiresWorldRestart
    public int fractalOctaves = 1;

    @Config.Name("Fractal Gain")
    @Config.Comment("The gain for ridged multi-fractal noise generation.")
    @Config.RequiresWorldRestart
    public float fractalGain = 0.3f;

    @Config.Name("Fractal Frequency")
    @Config.Comment("The frequency for ridged multi-fractal noise generation. This determines how spread out or" +
            " tightly knit cave systems are.")
    @Config.RequiresWorldRestart
    public float fractalFrequency = 0.03f;

    @Config.Name("Use Turbulence")
    @Config.Comment("Enable to apply turbulence. Turbulence will add some more random variation to cave " +
            "structure. Will negatively impact performance and probably not do much anyway.")
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

    @Config.Name("Number of Generators")
    @Config.Comment("The number of noise generation functions used. The intersection of these functions is" +
            "used to calculate a single noise value.")
    @Config.RequiresWorldRestart
    public int numGenerators = 9;

    @Config.Name("Enable y-adjustment")
    @Config.Comment("Enable y-adjustment, giving players more headroom in caves.")
    public boolean yAdjust = true;

    @Config.Name("y-adjustment Variable 1")
    @Config.Comment("Factor affecting the block immediately above a given block.")
    @Config.RangeDouble(min = 0, max = 1f)
    public float yAdjustF1 = .9f;

    @Config.Name("y-adjustment Variable 2")
    @Config.Comment("Factor affecting the block two blocks above a given block.")
    @Config.RangeDouble(min = 0, max = 1f)
    public float yAdjustF2 = .6f;
}
