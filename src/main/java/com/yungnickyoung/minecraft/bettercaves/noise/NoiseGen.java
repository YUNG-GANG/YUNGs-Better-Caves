package com.yungnickyoung.minecraft.bettercaves.noise;

import java.util.List;

public abstract class NoiseGen {
    private long seed;

    int fractalOctaves;
    float fractalGain;
    float fractalFrequency;

    int turbulenceOctaves;
    float turbulenceGain;
    float turbulenceFrequency;

    boolean enableTurbulence;
    boolean enableSmoothing;

    public NoiseGen(long seed, int fOctaves, float fGain, float fFreq, int tOctaves, float tGain, float tFreq, boolean useTurb, boolean useSmooth) {
        this.seed = seed;

        this.fractalOctaves = fOctaves;
        this.fractalGain = fGain;
        this.fractalFrequency = fFreq;

        this.turbulenceOctaves = tOctaves;
        this.turbulenceGain = tGain;
        this.turbulenceFrequency = tFreq;

        this.enableTurbulence = useTurb;
        this.enableSmoothing = useSmooth;
    }

    /**
     * Generates 16 x 16 array of noise for each y-value from minHeight to maxHeight, inclusive.
     * @param chunkX The chunk's x-coordinate
     * @param chunkZ The chunk's y-coordinate
     * @param minHeight Minimum y-coordinate to generate noise for
     * @param maxHeight Maximum y-coordinate to generate noise for
     * @param numGenerators Number of noise generation functions to use when calculating noise. This is the number
     *                      of noise values a single NoiseTuple will contain in the return value.
     * @return A list of 16 x 16 arrays of NoiseTuples, each containing n = numGenerators noise values.
     */
    public abstract List<NoiseTuple[][]> generateNoise(int chunkX, int chunkZ, int minHeight, int maxHeight, int numGenerators);

    public void setSeed(long s) {
        seed = s;
    }

    public long getSeed() {
        return seed;
    }
}
