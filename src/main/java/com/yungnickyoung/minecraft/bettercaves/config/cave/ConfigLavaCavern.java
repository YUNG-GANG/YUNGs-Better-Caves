package com.yungnickyoung.minecraft.bettercaves.config.cave;

import net.minecraftforge.common.config.Config;

public class ConfigLavaCavern {
    public enum CavernFrequency {
        None, Rare, Normal, Common, VeryCommon
    }

    @Config.Name("Top Generation Altitude")
    @Config.Comment("The top y-coordinate at which Lava Caverns start generating. Note that caverns will attempt " +
            "to close off anyway if this value is greater than the surface y-coordinate.")
    @Config.RequiresWorldRestart
    public int caveTop = 30;

    @Config.Name("Bottom Generation Altitude")
    @Config.Comment("The bottom y-coordinate at which Lava Caverns stop generating.")
    @Config.RequiresWorldRestart
    public int caveBottom = 1;

    @Config.Name("Lava Cavern Frequency")
    @Config.Comment("Determines how frequently Lava Caverns spawn.")
    public CavernFrequency caveFrequency = CavernFrequency.Normal;

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
}
