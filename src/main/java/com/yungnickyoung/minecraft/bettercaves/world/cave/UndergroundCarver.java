package com.yungnickyoung.minecraft.bettercaves.world.cave;

import com.yungnickyoung.minecraft.bettercaves.noise.*;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtil;
import com.yungnickyoung.minecraft.bettercaves.world.cave.builder.UndergroundCarverBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for CaveCarver and CavernCarver
 */
public class UndergroundCarver {
    protected World world;
    protected long seed;
    public NoiseGen noiseGen;

    /* ============================== Values determined through config ============================== */
    /* ------------- Ridged Multifractal Params ------------- */
    NoiseSettings       noiseSettings;
    protected int       numGens;                   // Number of noise values to generate per iteration (block, sub-chunk, etc)

    /* ----------------- Turbulence Params ----------------- */
    NoiseSettings       turbulenceSettings;
    protected boolean   enableTurbulence;          // Set true to enable turbulence (adds performance overhead, generally not worth it)

    /* -------------- Noise Processing Params -------------- */
    protected float     yCompression;              // Vertical cave gen compression
    protected float     xzCompression;             // Horizontal cave gen compression
    protected float     yAdjustF1;                 // Adjustment value for the block immediately above. Must be between 0 and 1.0
    protected float     yAdjustF2;                 // Adjustment value for the block two blocks above. Must be between 0 and 1.0
    protected float     noiseThreshold;            // Noise threshold for determining whether or not a block gets dug out
    protected boolean   enableYAdjust;             // Set true to perform pre-processing on noise values, adjusting them to increase ...
                                                   // ... headroom in the y direction. This is generally useful for caves ...
                                                   // ... (esp. Simplex), but not really necessary for caverns

    /* ------------------ Worldgen Params ------------------ */
    protected int       liquidAltitude;

    /* -------------------- Debug Params ------------------- */
    protected IBlockState debugBlock;             // Block used to represent this cave/cavern type in the debug visualizer
    protected boolean     enableDebugVisualizer;  // Set true to enable debug visualization for this carver

    /**
     * Creates and UndergroundCarver from an UndergroundCarverBuilder.
     * The builder should build all possible fields.
     */
    public UndergroundCarver(final UndergroundCarverBuilder builder) {
        this.world = builder.getWorld();
        this.seed = builder.getSeed();
        this.noiseSettings = new NoiseSettings()
                .setNoiseType(builder.getNoiseType())
                .setFractalType(FastNoise.FractalType.RigidMulti)
                .setOctaves(builder.getFractalOctaves())
                .setGain(builder.getFractalGain())
                .setFrequency(builder.getFractalFreq());
        this.turbulenceSettings = new NoiseSettings()
                .setNoiseType(FastNoise.NoiseType.SimplexFractal)
                .setFractalType(FastNoise.FractalType.FBM)
                .setOctaves(builder.getTurbOctaves())
                .setGain(builder.getTurbGain())
                .setFrequency(builder.getTurbFreq());
        this.numGens = builder.getNumGens();
        this.noiseThreshold = builder.getNoiseThreshold();
        this.enableTurbulence = builder.isEnableTurbulence();
        this.yCompression = builder.getyCompression();
        this.xzCompression = builder.getXzCompression();
        this.enableYAdjust = builder.isEnableYAdjust();
        this.yAdjustF1 = builder.getyAdjustF1();
        this.yAdjustF2 = builder.getyAdjustF2();
        this.liquidAltitude = builder.getLiquidAltitude();
        this.debugBlock = builder.getDebugBlock();
        this.enableDebugVisualizer = builder.isEnableDebugVisualizer();
    }

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
    public void preprocessCaveNoiseCol(NoiseColumn noises, int topY, int bottomY, Map<Integer, Float> thresholds, int numGens) {
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
     * @param blockPos Block position
     * @param liquidBlock The IBlockState to use for liquid, e.g. lava
     * @param liquidAltitude the altitude at and below which air is replaced with liquidBlock
     */
    public void digBlock(ChunkPrimer primer, BlockPos blockPos, IBlockState liquidBlock, int liquidAltitude) {
        int x = BetterCavesUtil.getLocal(blockPos.getX());
        int y = blockPos.getY();
        int z = BetterCavesUtil.getLocal(blockPos.getZ());

        if (liquidBlock.getMaterial() != Material.WATER) {
            // Check for adjacent water blocks to avoid breaking into lakes or oceans
            if (primer.getBlockState(x, y + 1, z).getMaterial() == Material.WATER)
                return;
            if (x < 15 && primer.getBlockState(x + 1, y, z).getMaterial() == Material.WATER)
                return;
            if (x > 0 && primer.getBlockState(x - 1, y, z).getMaterial() == Material.WATER)
                return;
            if (z < 15 && primer.getBlockState(x, y, z + 1).getMaterial() == Material.WATER)
                return;
            if (z > 0 && primer.getBlockState(x, y, z - 1).getMaterial() == Material.WATER)
                return;
        }

        BetterCavesUtil.digBlock(this.getWorld(), primer, blockPos, liquidBlock, liquidAltitude);
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
    public Map<Integer, Float> generateThresholds(int topY, int bottomY, int transitionBoundary) {
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
     * DEBUG method for visualizing cave systems. Used as a replacement for the {@code digBlock} method if the
     * debugVisualizer config option is enabled.
     * @param digBlock Whether or not this block should be considered removed (i.e. surpassed the threshold)
     * @param blockState The blockState to set dug out blocks to
     * @param primer Chunk containing the block
     */
    public void visualizeDigBlock(ChunkPrimer primer, BlockPos blockPos, boolean digBlock, IBlockState blockState) {
        int x = BetterCavesUtil.getLocal(blockPos.getX());
        int y = blockPos.getY();
        int z = BetterCavesUtil.getLocal(blockPos.getZ());

        if (digBlock)
            primer.setBlockState(x, y, z, blockState);
        else
            primer.setBlockState(x, y, z, Blocks.AIR.getDefaultState());
    }

    /**
     * Dig out caverns for the column of blocks containing blockPos.
     * @param primer The ChunkPrimer for this chunk
     * @param colPos Position of any block in the column. Only the x and z coordinates are used.
     * @param bottomY The bottom y-coordinate to start calculating noise for and potentially dig out
     * @param topY The top y-coordinate to start calculating noise for and potentially dig out
     * @param maxSurfaceHeight This column's max surface height. Can be approximated using
     *                         BetterCavesUtil#getMaxSurfaceAltitudeChunk or BetterCavesUtil#getMaxSurfaceAltitudeSubChunk
     * @param minSurfaceHeight This chunk's min surface height. Can be approximated using
     *                         BetterCavesUtil#getMinSurfaceAltitudeChunk or BetterCavesUtil#getMinSurfaceHeightSubChunk
     * @param liquidBlock Block to use for liquid, e.g. lava
     * @param noises NoiseColumn for the blocks at the given colPos between bottomY and topY (inclusive)
     */
    public void generateColumnWithNoise(ChunkPrimer primer, BlockPos colPos, int bottomY,
                                        int topY, int maxSurfaceHeight, int minSurfaceHeight, IBlockState liquidBlock, NoiseColumn noises, boolean liquidBuffer) {
        generateColumnWithNoise(primer, colPos, bottomY, topY, maxSurfaceHeight, minSurfaceHeight, liquidBlock, 1, noises, liquidBuffer);
    }

    /**
     * Dig out caverns for the column of blocks containing blockPos.
     * @param primer The ChunkPrimer for this chunk
     * @param colPos Position of any block in the column. Only the x and z coordinates are used.
     * @param bottomY The bottom y-coordinate to start calculating noise for and potentially dig out
     * @param topY The top y-coordinate to start calculating noise for and potentially dig out
     * @param maxSurfaceHeight This column's max surface height. Can be approximated using
     *                         BetterCavesUtil#getMaxSurfaceAltitudeChunk or BetterCavesUtil#getMaxSurfaceAltitudeSubChunk
     * @param minSurfaceHeight This chunk's min surface height. Can be approximated using
     *                         BetterCavesUtil#getMinSurfaceAltitudeChunk or BetterCavesUtil#getMinSurfaceHeightSubChunk
     * @param liquidBlock Block to use for liquid, e.g. lava
     * @param smoothAmp Amplitude of smoothing power along edges of caverns. Between 0 and 1. Higher = smoother cavern
     *                  edges, but more costly to performance
     * @param noises NoiseColumn for the blocks at the given colPos between bottomY and topY (inclusive)
     */
    public void generateColumnWithNoise(ChunkPrimer primer, BlockPos colPos, int bottomY,
                                        int topY, int maxSurfaceHeight, int minSurfaceHeight, IBlockState liquidBlock,
                                        float smoothAmp, NoiseColumn noises, boolean liquidBuffer) {
    }

    /* ------------------------- Public Getters -------------------------*/
    public World getWorld() {
        return this.world;
    }

    public long getSeed() {
        return this.seed;
    }
}
