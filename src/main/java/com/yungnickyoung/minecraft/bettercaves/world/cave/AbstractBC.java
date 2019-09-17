package com.yungnickyoung.minecraft.bettercaves.world.cave;

import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseTuple;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCaveUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base class for Better Caves caves and caverns
 */
public abstract class AbstractBC {
    private long seed;

    /* ============================== Values determined through config ============================== */
    /* ------------- Ridged Multifractal Params ------------- */
    FastNoise.NoiseType noiseType;
    int fractalOctaves;            // Number of ridged multifractal octaves
    float fractalGain;             // Ridged multifractal gain
    float fractalFreq;             // Ridged multifractal frequency
    int numGens;                   // Number of noise values to generate per iteration (block, sub-chunk, etc)

    /* ----------------- Turbulence Params ----------------- */
    int turbOctaves;               // Number of octaves in turbulence function
    float turbGain;                // Gain of turbulence function
    float turbFreq;                // Frequency of turbulence function
    boolean enableTurbulence;      // Set true to enable turbulence (adds performance overhead, generally not worth it)

    /* -------------- Noise Processing Params -------------- */
    protected double yCompression;  // Vertical cave gen compression
    protected double xzCompression; // Horizontal cave gen compression
    private float yAdjustF1;        // Adjustment value for the block immediately above. Must be between 0 and 1.0
    private float yAdjustF2;        // Adjustment value for the block two blocks above. Must be between 0 and 1.0
    float noiseThreshold;           // Noise threshold for determining whether or not a block gets dug out
    boolean enableYAdjust;          // Set true to perform preprocessing on noise values, adjusting them to increase
                                    // headroom in the y direction. This is generally useful for caves (esp. Simplex),
                                    // but not really necessary for caverns

    BlockState vBlock;              // Block used to represent this cave/cavern type in the debug visualizer

    /**
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
    public AbstractBC(IWorld world, int fOctaves, float fGain, float fFreq, int numGens, float threshold, int tOctaves,
                      float tGain, float tFreq, boolean enableTurbulence, double yComp, double xzComp, boolean yAdj,
                      float yAdjF1, float yAdjF2, BlockState vBlock) {
        this(world.getSeed(), fOctaves, fGain, fFreq, numGens, threshold, tOctaves, tGain, tFreq, enableTurbulence,
                yComp, xzComp, yAdj, yAdjF1, yAdjF2, vBlock);
    }

    /**
     * @param seed the Minecraft World seed
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
    public AbstractBC(long seed, int fOctaves, float fGain, float fFreq, int numGens, float threshold, int tOctaves,
                      float tGain, float tFreq, boolean enableTurbulence, double yComp, double xzComp, boolean yAdj,
                      float yAdjF1, float yAdjF2, BlockState vBlock) {
        this.seed = seed;
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
        this.vBlock = vBlock;    }

    /**
     * Dig out caves for the column of blocks at x-z position (chunkX*16 + localX, chunkZ*16 + localZ).
     * A given block will be calculated based on the noise value and noise threshold of this AbstractBC object.
     * @param chunkX The chunk's x-coordinate
     * @param chunkZ The chunk's z-coordinate
     * @param chunkIn The chunk
     * @param localX the chunk-local x-coordinate of this column of blocks (0 <= localX <= 15)
     * @param localZ the chunk-local z-coordinate of this column of blocks (0 <= localZ <= 15)
     * @param bottomY The bottom y-coordinate to start calculating noise for and potentially dig out
     * @param topY The top y-coordinate to start calculating noise for and potentially dig out
     * @param maxSurfaceHeight This chunk's max surface height. Can be approximated using
     *                         BetterCaveUtil#getMaxSurfaceHeight
     * @param minSurfaceHeight This chunk's min surface height. Can be approximated using
     *                         BetterCaveUtil#getMinSurfaceHeight
     */
    public abstract void generateColumn(int chunkX, int chunkZ, IChunk chunkIn, int localX, int localZ, int bottomY,
                                        int topY, int maxSurfaceHeight, int minSurfaceHeight, int surfaceCutoff, BlockState lavaBlock, boolean flag);

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
     * @param chunkIn The chunk
     * @param lavaBlock The IBlockState to use for lava. If you want regular lava, either pass it in or use the other
     *                  wrapper digBlock function
     * @param chunkX The chunk's x-coordinate
     * @param chunkZ The chunk's z-coordinate
     * @param localX the chunk-local x-coordinate of the block
     * @param localZ the chunk-local z-coordinate of the block
     * @param realY the real Y-coordinate of the block
     */
    protected void digBlock(IChunk chunkIn, BlockState lavaBlock, int chunkX, int chunkZ, int localX, int localZ, int realY) {
        if (!lavaBlock.getFluidState().isTagged(FluidTags.WATER)) {
            // Check for adjacent water blocks to avoid breaking into lakes or oceans
//            if (chunkIn.getBlockState(new BlockPos(localX, realY + 1, localZ)).getFluidState().isTagged(FluidTags.WATER))
//                return;
//            if (localX < 15 && chunkIn.getBlockState(new BlockPos(localX + 1, realY, localZ)).getFluidState().isTagged(FluidTags.WATER))
//                return;
//            if (localX > 0 && chunkIn.getBlockState(new BlockPos(localX - 1, realY, localZ)).getFluidState().isTagged(FluidTags.WATER))
//                return;
//            if (localZ < 15 && chunkIn.getBlockState(new BlockPos(localX, realY, localZ + 1)).getFluidState().isTagged(FluidTags.WATER))
//                return;
//            if (localZ > 0 && chunkIn.getBlockState(new BlockPos(localX, realY, localZ - 1)).getFluidState().isTagged(FluidTags.WATER))
//                return;
        }

        BetterCaveUtil.digBlock(chunkIn, lavaBlock, localX, realY, localZ, chunkX, chunkZ);
    }

    /**
     * Wrapper function for AbstractBC#digBlock with default lava block.
     * Calls util digBlock function if there are no water blocks adjacent, to avoid breaking into oceans and lakes.
     * @param chunkIn The chunk
     * @param chunkX The chunk's x-coordinate
     * @param chunkZ The chunk's z-coordinate
     * @param localX the chunk-local x-coordinate of the block
     * @param localZ the chunk-local z-coordinate of the block
     * @param realY the real Y-coordinate of the block
     */
    protected void digBlock(IChunk chunkIn, int chunkX, int chunkZ, int localX, int localZ, int realY) {
        digBlock(chunkIn, Blocks.LAVA.getDefaultState(), chunkX, chunkZ, localX, localZ, realY);
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
     * DEBUG method for visualizing cave systems. Used as a replacement for AbstractBC#digblock if the
     * debugVisualizer config option is enabled.
     * @param digBlock Whether or not this block should be considered removed (i.e. surpassed the threshold)
     * @param blockState The blockState to set dug out blocks to
     * @param chunkIn Chunk containing the block
     * @param localX Chunk-local x-coordinate of the block
     * @param realY y-coordainte of the block
     * @param localZ Chunk-local z-coordinate of the block
     */
    protected void visualizeDigBlock(boolean digBlock, BlockState blockState, IChunk chunkIn, int localX, int realY, int localZ) {
        BlockPos blockPos = new BlockPos(localX, realY, localZ);
        if (digBlock)
            chunkIn.setBlockState(blockPos, blockState, false);
        else
            chunkIn.setBlockState(blockPos, Blocks.AIR.getDefaultState(), false);
    }

    /* ------------------------- Public Getters -------------------------*/
    public long getSeed() {
        return this.seed;
    }
}