package com.yungnickyoung.minecraft.bettercaves.config.cave;

import com.yungnickyoung.minecraft.bettercaves.enums.CaveFrequency;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import net.minecraftforge.common.config.Config;

public class ConfigSimplexCave {
    @Config.Name("Cave Bottom Altitude")
    @Config.Comment("The minimum y-coordinate at which caves start generating.")
    @Config.RangeInt(min = 0, max = 255)
    @Config.RequiresWorldRestart
    public int caveBottom = 1;

    @Config.Name("Compression - Vertical")
    @Config.Comment("Changes height of caves. Lower value = taller caves with steeper drops.")
    @Config.RangeDouble(min = 0, max = 100)
    @Config.RequiresWorldRestart
    public float yCompression = 2.2f;

    @Config.Name("Compression - Horizontal")
    @Config.Comment("Changes width of caves. Lower value = wider caves.")
    @Config.RangeDouble(min = 0, max = 100)
    @Config.RequiresWorldRestart
    public float xzCompression = 0.9f;

    @Config.Name("Type 2 Cave Frequency")
    @Config.Comment("Determines how frequently Type 2 Caves spawn. If this is anything but VeryCommon (the default), vanilla caves will " +
            "replace some of the areas where Type 2 Caves would have generated. However, if vanilla caves are disabled, " +
            "then these areas will instead not have any caves at all.")
    @Config.RequiresWorldRestart
    public CaveFrequency caveFrequency = CaveFrequency.VeryCommon;

    @Config.Name("Type 2 Cave Frequency Custom Value")
    @Config.Comment("Custom value for cave frequency. Only works if Type 2 Cave Frequency is set to Custom. 0 = 0% chance of spawning, " +
            "1.0 = 50% chance of spawning (which is the max value). The value may not scale linearly. \nProvided values:\n" +
            "None: 0\n" +
            "Rare: 0.4\n" +
            "Common: 0.8\n" +
            "VeryCommon: 1.0")
    @Config.RangeDouble(min = 0, max = 1)
    @Config.RequiresWorldRestart
    public float customFrequency = 1.0f;

    @Config.Ignore
    @Config.Name("Noise Threshold")
    @Config.Comment("Threshold for determining which blocks get mined out as part of cave generation. Higher value" +
            " = less caves.")
    @Config.RangeDouble(min = -1.0, max = 1.0)
    @Config.RequiresWorldRestart
    public float noiseThreshold = .64f;

    @Config.Ignore
    @Config.Name("Fractal Octaves")
    @Config.Comment("The number of octaves used for ridged multi-fractal noise generation.")
    @Config.RequiresWorldRestart
    public int fractalOctaves = 2;

    @Config.Ignore
    @Config.Name("Fractal Gain")
    @Config.Comment("The gain for ridged multi-fractal noise generation.")
    @Config.RequiresWorldRestart
    public float fractalGain = 0.3f;

    @Config.Ignore
    @Config.Name("Fractal Frequency")
    @Config.Comment("The frequency for ridged multi-fractal noise generation. This determines how spread out or" +
            " tightly knit cave systems are.")
    @Config.RequiresWorldRestart
    public float fractalFrequency = 0.03f;

    @Config.Ignore
    @Config.Name("Number of Generators")
    @Config.Comment("The number of noise generation functions used. The intersection of these functions is" +
            "used to calculate a single noise value.")
    @Config.RequiresWorldRestart
    public int numGenerators = 2;

    @Config.Ignore
    @Config.Name("Enable y-adjustment")
    @Config.Comment("Enable y-adjustment, giving players more headroom in caves.")
    public boolean yAdjust = true;

    @Config.Ignore
    @Config.Name("y-adjustment Variable 1")
    @Config.Comment("Factor affecting the block immediately above a given block.")
    @Config.RangeDouble(min = 0, max = 1f)
    public float yAdjustF1 = .95f;

    @Config.Ignore
    @Config.Name("y-adjustment Variable 2")
    @Config.Comment("Factor affecting the block two blocks above a given block.")
    @Config.RangeDouble(min = 0, max = 1f)
    public float yAdjustF2 = .9f;

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
    public FastNoise.NoiseType noiseType = FastNoise.NoiseType.SimplexFractal;
}
