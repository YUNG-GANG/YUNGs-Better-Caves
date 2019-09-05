package com.yungnickyoung.minecraft.bettercaves.world.cavern;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public abstract class BetterCavern {
    private World world;
    private long seed;

    /* ============================== Values passed in through config ============================== */
    /* ------------- Ridged Multifractal Params ------------- */
    int fractalOctaves;            // Number of ridged multifractal octaves
    float fractalGain;             // Ridged multifractal gain
    float fractalFreq;             // Ridged multifractal frequency
    int numGens;                   // Number of noise values to generate per iteration (block, sub-chunk, etc)

    /* -------------- Noise Processing Params -------------- */
    float yCompression;            // Vertical cave gen compression
    float xzCompression;           // Horizontal cave gen compression
    float noiseThreshold;          // Noise threshold for determining whether or not a block gets dug out

    /**
     *
     * @param world the Minecraft World
     * @param fOctaves Number of fractal octaves to use in ridged multifractal noise generation
     * @param fGain Amount of gain to use in ridged multifractal noise generation
     * @param fFreq Frequency to use in ridged multifractal noise generation
     * @param numGens Number of noise values to calculate for a given block
     * @param threshold Noise threshold to determine whether or not a given block will be dug out
     * @param yComp Vertical cave gen compression. Use 1.0 for default generation
     * @param xzComp Horizontal cave gen compression. Use 1.0 for default generation
     */
    public BetterCavern(World world, int fOctaves, float fGain, float fFreq, int numGens, float threshold, float yComp,
                      float xzComp) {
        this.world = world;
        this.seed = world.getSeed();
        this.fractalOctaves = fOctaves;
        this.fractalGain = fGain;
        this.fractalFreq = fFreq;
        this.numGens = numGens;
        this.noiseThreshold = threshold;
        this.yCompression = yComp;
        this.xzCompression = xzComp;
    }

    public World getWorld() {
        return this.world;
    }

    public long getSeed() {
        return this.seed;
    }

    /**
     * Dig out caverns for the column of blocks at x-z position (chunkX*16 + localX, chunkZ*16 + localZ).
     * A given block will be calculated based on the noise value and noise threshold of this BetterCavern object.
     * All of these params, including the noise function params, are attributes of this object.
     * @param chunkX The chunk's x-coordinate
     * @param chunkZ The chunk's z-coordinate
     * @param primer The ChunkPrimer for this chunk
     * @param localX the chunk-local x-coordinate of this column of blocks (0 <= localX <= 15)
     * @param localZ the chunk-local z-coordinate of this column of blocks (0 <= localZ <= 15)
     * @param bottomY The bottom y-coordinate to start calculating noise for and potentially dig out
     * @param topY The top y-coordinate to start calculating noise for and potentially dig out
     * @param maxSurfaceHeight This chunk's max surface height. Can be approximated using
     *                         BetterCaveUtil#getMaxSurfaceHeight
     * @param minSurfaceHeight This chunk's min surface height. Can be approximated using
     *                         BetterCaveUtil#getMinSurfaceHeight
     */
    public abstract void generateColumn(int chunkX, int chunkZ, ChunkPrimer primer, int localX, int localZ, int bottomY,
                                        int topY, int maxSurfaceHeight, int minSurfaceHeight);
}
