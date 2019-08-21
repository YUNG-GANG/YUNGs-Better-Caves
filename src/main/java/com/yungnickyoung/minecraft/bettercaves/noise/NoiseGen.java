package com.yungnickyoung.minecraft.bettercaves.noise;

import java.util.List;

public abstract class NoiseGen {
    private long seed;

    public NoiseGen(long seed) {
        this.seed = seed;
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
