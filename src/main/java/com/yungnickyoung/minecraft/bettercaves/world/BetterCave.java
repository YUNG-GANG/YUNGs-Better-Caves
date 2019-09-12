package com.yungnickyoung.minecraft.bettercaves.world;

import com.yungnickyoung.minecraft.bettercaves.noise.NoiseTuple;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCaveUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.HashMap;
import java.util.Map;

public abstract class BetterCave {
    private World world;
    private long seed;

    /* ============================== Values passed in through config ============================== */
    /* ------------- Ridged Multifractal Params ------------- */
    protected int fractalOctaves;            // Number of ridged multifractal octaves
    protected float fractalGain;             // Ridged multifractal gain
    protected float fractalFreq;             // Ridged multifractal frequency
    protected int numGens;                   // Number of noise values to generate per iteration (block, sub-chunk, etc)

    /* ----------------- Turbulence Params ----------------- */
    protected int turbOctaves;               // Number of octaves in turbulence function
    protected float turbGain;                // Gain of turbulence function
    protected float turbFreq;                // Frequency of turbulence function
    protected boolean enableTurbulence;      // Set true to enable turbulence (adds performance overhead, generally not worth it)

    /* -------------- Noise Processing Params -------------- */
    protected float yCompression;            // Vertical cave gen compression
    protected float xzCompression;           // Horizontal cave gen compression
    private float yAdjustF1;                 // Adjustment value for the block immediately above. Must be between 0 and 1.0
    private float yAdjustF2;                 // Adjustment value for the block two blocks above. Must be between 0 and 1.0
    protected float noiseThreshold;          // Noise threshold for determining whether or not a block gets dug out
    protected boolean enableYAdjust;         // Set true to perform preprocessing on noise values, adjusting them to increase
                                             // headroom in the y direction. This is generally useful for caves (esp. Simplex),
                                             // but not really necessary for caverns

    /**
     * Primary constructor, used for caves. See other constructor for caverns.
     * @param world the Minecraft World
     * @param fOctaves Number of fractal octaves to use in ridged multifractal noise generation
     * @param fGain Amount of gain to use in ridged multifractal noise generation
     * @param fFreq Frequency to use in ridged multifractal noise generation
     * @param numGens Number of noise values to calculate for a given block
     * @param threshold Noise threshold to determine whether or not a given block will be dug out
     * @param tOctaves Number of octaves in turbulence function
     * @param tGain Gain of turbulence function
     * @param tFreq Frequency of turbulence function
     * @param enableTurbulence Whether or not to enable turbulence (adds performance overhead, generally not worth it).
     *                         If set to false then other turbulence params don't matter.
     * @param yComp Vertical cave gen compression. Use 1.0 for default generation
     * @param xzComp Horizontal cave gen compression. Use 1.0 for default generation
     * @param yAdj Whether or not to adjust/increase the height of caves.
     * @param yAdjF1 Adjustment value for the block immediately above. Must be between 0 and 1.0
     * @param yAdjF2 Adjustment value for the block two blocks above. Must be between 0 and 1.0
     */
    public BetterCave(World world, int fOctaves, float fGain, float fFreq, int numGens, float threshold, int tOctaves,
                      float tGain, float tFreq, boolean enableTurbulence, float yComp, float xzComp, boolean yAdj,
                      float yAdjF1, float yAdjF2) {
        this.world = world;
        this.seed = world.getSeed();
        this.fractalOctaves = fOctaves;
        this.fractalGain = fGain;
        this.fractalFreq = fFreq;
        this.numGens = numGens;
        this.noiseThreshold = threshold;
        this.turbOctaves = tOctaves;
        this.turbGain = tGain;
        this.turbFreq = tFreq;
        this.enableTurbulence = enableTurbulence;
        this.yCompression = yComp;
        this.xzCompression = xzComp;
        this.enableYAdjust = yAdj;
        this.yAdjustF1 = yAdjF1;
        this.yAdjustF2 = yAdjF2;
    }

    /**
     * Constructor for caverns.
     * Disables turbulence and sets all turbulence-related fields to zero (shouldn't matter anyway).
     * Disables vertical adjustment and sets all y-adjust related fields to one (shouldn't matter anyway).
     * @param world the Minecraft World
     * @param fOctaves Number of fractal octaves to use in ridged multifractal noise generation
     * @param fGain Amount of gain to use in ridged multifractal noise generation
     * @param fFreq Frequency to use in ridged multifractal noise generation
     * @param numGens Number of noise values to calculate for a given block
     * @param threshold Noise threshold to determine whether or not a given block will be dug out
     * @param yComp Vertical cave gen compression. Use 1.0 for default generation
     * @param xzComp Horizontal cave gen compression. Use 1.0 for default generation
     */
    public BetterCave(World world, int fOctaves, float fGain, float fFreq, int numGens, float threshold, float yComp,
                        float xzComp) {
        this(world, fOctaves, fGain, fFreq, numGens, threshold, 0, 0, 0, false,
                yComp, xzComp, false, 1, 1);
    }

    /**
     * Dig out caves for the column of blocks at x-z position (chunkX*16 + localX, chunkZ*16 + localZ).
     * A given block will be calculated based on the noise value and noise threshold of this BetterCave object.
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
                      int topY, int maxSurfaceHeight, int minSurfaceHeight, int surfaceCutoff);

    /**
     * Preprocessing performed on a column of noise to adjust its values before comparing them to the threshold.
     * This function adjusts the noise value of blocks based on the noise values of blocks below.
     * This has the effect of raising the ceilings of caves, giving the player more headroom.
     * Big shoutouts to the guys behind Worley's Caves for this great idea.
     * @param noises The column of noises as a map, mapping the y-coordinate of a block to its NoiseTuple
     * @param topY Top y-coordinate of the noise column
     * @param bottomY Bottom y-coordinate of the noise column
     * @param thresholds Map of y-coordinates to noise thresholds. This is the output of the generateThresholds method.
     * @param numGens Number of noise values to create per block. This is equal to the number of floats held
     *                in each NoiseTuple for each block in the noise column.
     */
    protected void preprocessCaveNoiseCol(Map<Integer, NoiseTuple> noises, int topY, int bottomY, Map<Integer, Float> thresholds, int numGens) {
        /* Adjust simplex noise values based on blocks above in order to give the player more headroom */
        for (int realY = topY; realY >= bottomY; realY--) {
            NoiseTuple noiseBlock = noises.get(realY);
            float threshold = thresholds.get(realY);

            boolean valid = true;
            for (float noise : noiseBlock.getNoiseValues()) {
                if (noise < threshold) {
                    valid = false;
                    break;
                }
            }

            if (valid) {
                /* Adjust noise values of blocks above to give the player more head room */
                float f1 = this.yAdjustF1;
                float f2 = this.yAdjustF2;

                if (realY < topY) {
                    NoiseTuple tupleAbove = noises.get(realY + 1);
                    for (int i = 0; i < numGens; i++)
                        tupleAbove.set(i, ((1 - f1) * tupleAbove.get(i)) + (f1 * noiseBlock.get(i)));
                }

                if (realY < topY - 1) {
                    NoiseTuple tupleTwoAbove = noises.get(realY + 2);
                    for (int i = 0; i < numGens; i++)
                        tupleTwoAbove.set(i, ((1 - f2) * tupleTwoAbove.get(i)) + (f2 * noiseBlock.get(i)));
                }
            }
        }
    }

    /**
     * Calls util digBlock function if there are no water blocks adjacent, to avoid breaking into oceans and lakes.
     * @param primer The ChunkPrimer for this chunk
     * @param lavaBlock The IBlockState to use for lava. If you want regular lava, either pass it in or use the other
     *                  wrapper digBlock function
     * @param chunkX The chunk's x-coordinate
     * @param chunkZ The chunk's z-coordinate
     * @param localX the chunk-local x-coordinate of the block
     * @param localZ the chunk-local z-coordinate of the block
     * @param realY the real Y-coordinate of the block
     */
    protected void digBlock(ChunkPrimer primer, IBlockState lavaBlock, int chunkX, int chunkZ, int localX, int localZ, int realY) {
        // Check for adjacent water blocks to avoid breaking into lakes or oceans
        if (primer.getBlockState(localX, realY + 1, localZ).getMaterial() == Material.WATER)
            return;
        if (localX < 15 && primer.getBlockState(localX + 1, realY, localZ).getMaterial() == Material.WATER)
            return;
        if (localX > 0 && primer.getBlockState(localX - 1, realY, localZ).getMaterial() == Material.WATER)
            return;
        if (localZ < 15 && primer.getBlockState(localX, realY, localZ + 1).getMaterial() == Material.WATER)
            return;
        if (localZ > 0 && primer.getBlockState(localX, realY, localZ - 1).getMaterial() == Material.WATER)
            return;

        BetterCaveUtil.digBlock(this.getWorld(), primer, lavaBlock, localX, realY, localZ, chunkX, chunkZ);
    }

    /**
     * Wrapper function for BetterCave#digBlock with default lava block.
     * Calls util digBlock function if there are no water blocks adjacent, to avoid breaking into oceans and lakes.
     * @param primer The ChunkPrimer for this chunk
     * @param chunkX The chunk's x-coordinate
     * @param chunkZ The chunk's z-coordinate
     * @param localX the chunk-local x-coordinate of the block
     * @param localZ the chunk-local z-coordinate of the block
     * @param realY the real Y-coordinate of the block
     */
    protected void digBlock(ChunkPrimer primer, int chunkX, int chunkZ, int localX, int localZ, int realY) {
        digBlock(primer, Blocks.LAVA.getDefaultState(), chunkX, chunkZ, localX, localZ, realY);
    }

    /**
     * Generate a map of y-coordinates to thresholds for a column of blocks.
     * This is useful because the threshold will decrease near the surface, and it is useful (and more accurate)
     * to have a precomputed threshold value when doing y-adjustments for caves.
     * @param topY Top y-coordinate of the column
     * @param bottomY Bottom y-coordinate of the column
     * @param transitionBoundary The y-coordinate at which the caves start to close off
     * @return Map of y-coordinates to noise thresholds
     */
    protected Map<Integer, Float> generateThresholds(int topY, int bottomY, int transitionBoundary) {
        Map<Integer, Float> thresholds = new HashMap<>();
        for (int realY = bottomY; realY <= topY; realY++) {
            float noiseThreshold = this.noiseThreshold;
            if (realY >= transitionBoundary)
                noiseThreshold *= (1 + .3f * ((float)(realY - transitionBoundary) / (topY - transitionBoundary)));
            thresholds.put(realY, noiseThreshold);
        }

        return thresholds;
    }

    /**
     * DEBUG method for visualizing cave systems. Used as a replacement for BetterCave#digblock if the
     * debugVisualizer config option is enabled.
     * @param digBlock Whether or not this block should be considered removed (i.e. surpassed the threshold)
     * @param blockState The blockState to set dug out blocks to
     * @param primer Chunk containing the block
     * @param localX Chunk-local x-coordinate of the block
     * @param realY y-coordainte of the block
     * @param localZ Chunk-local z-coordinate of the block
     */
    protected void visualizeDigBlock(boolean digBlock, IBlockState blockState, ChunkPrimer primer, int localX, int realY, int localZ) {
        if (digBlock)
            primer.setBlockState(localX, realY, localZ, blockState);
        else
            primer.setBlockState(localX, realY, localZ, Blocks.AIR.getDefaultState());
    }

    /* ------------------------- Public Getters -------------------------*/
    public World getWorld() {
        return this.world;
    }

    public long getSeed() {
        return this.seed;
    }
}
