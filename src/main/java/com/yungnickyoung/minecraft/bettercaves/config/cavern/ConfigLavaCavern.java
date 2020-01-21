package com.yungnickyoung.minecraft.bettercaves.config.cavern;

import com.yungnickyoung.minecraft.bettercaves.enums.CavernFrequency;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import net.minecraftforge.common.config.Config;

public class ConfigLavaCavern {
    @Config.Name("Cavern Top Altitude")
    @Config.Comment("The top cutoff y-coordinate of Lava Caverns. Note that caverns will attempt " +
            "to close off anyway if this value is greater than the surface y-coordinate.")
    @Config.RangeInt(min = 0, max = 255)
    @Config.RequiresWorldRestart
    public int caveTop = 30;

    @Config.Name("Cavern Bottom Altitude")
    @Config.Comment("The bottom cutoff y-coordinate at which Lava Caverns stop generating.")
    @Config.RangeInt(min = 0, max = 255)
    @Config.RequiresWorldRestart
    public int caveBottom = 1;

    @Config.Name("Lava Cavern Frequency")
    @Config.Comment("Determines how frequently Lava Caverns spawn.")
    @Config.RequiresWorldRestart
    public CavernFrequency caveFrequency = CavernFrequency.Normal;

    @Config.Name("Lava Cavern Frequency Custom Value")
    @Config.Comment("Custom value for cavern frequency. Only works if Lava Cavern Frequency is set to Custom. 0 = 0% chance of spawning, " +
            "1.0 = 50% chance of spawning (which is the max value). The value does not scale linearly. \nProvided values:\n" +
            "None: 0\n" +
            "Rare: 0.2\n" +
            "Normal: 0.6\n" +
            "Common: 0.7\n" +
            "VeryCommon: 0.9")
    @Config.RangeDouble(min = 0, max = 1)
    @Config.RequiresWorldRestart
    public float customFrequency = 1.0f;

    @Config.Name("Compression - Vertical")
    @Config.Comment("Changes height of formations in caverns. Lower value = more open caverns with larger features.")
    @Config.RangeDouble(min = 0, max = 100)
    @Config.RequiresWorldRestart
    public float yCompression = 1.0f;

    @Config.Name("Compression - Horizontal")
    @Config.Comment("Changes width of formations in caverns. Lower value = more open caverns with larger features.")
    @Config.RangeDouble(min = 0, max = 100)
    @Config.RequiresWorldRestart
    public float xzCompression = 1.0f;

    @Config.Ignore
    @Config.Name("Noise Threshold")
    @Config.Comment("Threshold for determining which blocks get mined out as part of cave generation. Higher value = less caves.")
    @Config.RangeDouble(min = -1.0, max = 1.0)
    @Config.RequiresWorldRestart
    public float noiseThreshold = .7f;

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

    @Config.Ignore
    @Config.Name("Noise Type")
    @Config.Comment("Type of noise to use for this cave. \nAccepted values:\n" +
            "Value\n" +
            "ValueFractal\n" +
            "Perlin\n" +
            "PerlinFractal\n" +
            "Simplex\n" +
            "SimplexFractal\n" +
            "Cellular\n" +
            "WhiteNoise\n" +
            "Cubic\n" +
            "CubicFractal")
    public FastNoise.NoiseType noiseType = FastNoise.NoiseType.PerlinFractal;
}
