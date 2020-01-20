package com.yungnickyoung.minecraft.bettercaves.world.cave;

import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseColumn;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseGen;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseTuple;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtil;
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
    public UndergroundCarverBuilder builder;
    protected World world;
    protected long seed;
    public NoiseGen noiseGen;

    /* ============================== Values determined through config ============================== */
    /* ------------- Ridged Multifractal Params ------------- */
    FastNoise.NoiseType noiseType;
    protected int       fractalOctaves;            // Number of ridged multifractal octaves
    protected float     fractalGain;               // Ridged multifractal gain
    protected float     fractalFreq;               // Ridged multifractal frequency
    protected int       numGens;                   // Number of noise values to generate per iteration (block, sub-chunk, etc)

    /* ----------------- Turbulence Params ----------------- */
    protected int       turbOctaves;               // Number of octaves in turbulence function
    protected float     turbGain;                  // Gain of turbulence function
    protected float     turbFreq;                  // Frequency of turbulence function
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
    protected UndergroundCarver(final UndergroundCarverBuilder builder) {
        this.world = builder.world;
        this.seed = builder.seed;
        this.noiseType = builder.noiseType;
        this.fractalOctaves = builder.fractalOctaves;
        this.fractalGain = builder.fractalGain;
        this.fractalFreq = builder.fractalFreq;
        this.numGens = builder.numGens;
        this.noiseThreshold = builder.noiseThreshold;
        this.turbOctaves = builder.turbOctaves;
        this.turbGain = builder.turbGain;
        this.turbFreq = builder.turbFreq;
        this.enableTurbulence = builder.enableTurbulence;
        this.yCompression = builder.yCompression;
        this.xzCompression = builder.xzCompression;
        this.enableYAdjust = builder.enableYAdjust;
        this.yAdjustF1 = builder.yAdjustF1;
        this.yAdjustF2 = builder.yAdjustF2;
        this.liquidAltitude = builder.liquidAltitude;
        this.debugBlock = builder.debugBlock;
        this.enableDebugVisualizer = builder.enableDebugVisualizer;
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
    protected void preprocessCaveNoiseCol(NoiseColumn noises, int topY, int bottomY, Map<Integer, Float> thresholds, int numGens) {
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

    protected void preprocessCaveNoiseSubChunk(Map<Integer, NoiseTuple[][]> noiseCube, int topY, int bottomY, Map<Integer, Float> thresholds, int numGens) {
        /* Adjust simplex noise values based on blocks above in order to give the player more headroom */
        for (int realY = topY; realY >= bottomY; realY--) {
            float threshold = thresholds.get(realY);
            NoiseTuple[][] layer = noiseCube.get(realY);
            NoiseTuple[][] layerAbove = noiseCube.get(realY + 1);
            NoiseTuple[][] layerTwoAbove = noiseCube.get(realY + 2);

            for (int localX = 0; localX < layer.length; localX++) {
                for (int localZ = 0; localZ < layer[0].length; localZ++) {
                    NoiseTuple noiseBlock = layer[localX][localZ];

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
                            NoiseTuple tupleAbove = layerAbove[localX][localZ];
                            for (int i = 0; i < numGens; i++)
                                tupleAbove.set(i, ((1 - f1) * tupleAbove.get(i)) + (f1 * noiseBlock.get(i)));
                        }

                        if (realY < topY - 1) {
                            NoiseTuple tupleTwoAbove = layerTwoAbove[localX][localZ];
                            for (int i = 0; i < numGens; i++)
                                tupleTwoAbove.set(i, ((1 - f2) * tupleTwoAbove.get(i)) + (f2 * noiseBlock.get(i)));
                        }
                    }
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
    protected void digBlock(ChunkPrimer primer, BlockPos blockPos, IBlockState liquidBlock, int liquidAltitude) {
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
     * DEBUG method for visualizing cave systems. Used as a replacement for the {@code digBlock} method if the
     * debugVisualizer config option is enabled.
     * @param digBlock Whether or not this block should be considered removed (i.e. surpassed the threshold)
     * @param blockState The blockState to set dug out blocks to
     * @param primer Chunk containing the block
     */
    protected void visualizeDigBlock(ChunkPrimer primer, BlockPos blockPos, boolean digBlock, IBlockState blockState) {
        int x = BetterCavesUtil.getLocal(blockPos.getX());
        int y = blockPos.getY();
        int z = BetterCavesUtil.getLocal(blockPos.getZ());

        if (digBlock)
            primer.setBlockState(x, y, z, blockState);
        else
            primer.setBlockState(x, y, z, Blocks.AIR.getDefaultState());
    }

    public void generateColumn(ChunkPrimer primer, BlockPos colPos, int bottomY,
                               int topY, int maxSurfaceHeight, int minSurfaceHeight, IBlockState liquidBlock) {
    }

    public void generateColumn(ChunkPrimer primer, BlockPos colPos, int bottomY,
                               int topY, int maxSurfaceHeight, int minSurfaceHeight, IBlockState liquidBlock,
                               float smoothAmp) {
    }

    public void generateColumnWithNoise(ChunkPrimer primer, BlockPos colPos, int bottomY,
                                        int topY, int maxSurfaceHeight, int minSurfaceHeight, IBlockState liquidBlock, NoiseColumn noises) {

    }

    public void generateColumnWithNoise(ChunkPrimer primer, BlockPos colPos, int bottomY,
                                        int topY, int maxSurfaceHeight, int minSurfaceHeight, IBlockState liquidBlock,
                                        float smoothAmp, NoiseColumn noises) {

    }

    /* ------------------------- Public Getters -------------------------*/
    public World getWorld() {
        return this.world;
    }

    public long getSeed() {
        return this.seed;
    }

    /**
     * Builder class for UndergroundCarver.
     */
    public static class UndergroundCarverBuilder {
        protected World world;
        protected long seed;
        protected FastNoise.NoiseType noiseType;

        /* ------------------- Fractal Params ------------------ */
        protected int fractalOctaves;
        protected float fractalGain;
        protected float fractalFreq;
        protected int numGens;

        /* ----------------- Turbulence Params ----------------- */
        protected int turbOctaves;
        protected float turbGain;
        protected float turbFreq;
        protected boolean enableTurbulence = false;

        /* -------------- Noise Processing Params -------------- */
        protected float yCompression;
        protected float xzCompression;
        protected float yAdjustF1;
        protected float yAdjustF2;
        protected float noiseThreshold;
        protected boolean enableYAdjust;

        /* ------------------ Worldgen Params ------------------ */
        protected int liquidAltitude;

        /* -------------------- Debug Params ------------------- */
        protected IBlockState debugBlock;
        protected boolean enableDebugVisualizer = false;

        public UndergroundCarverBuilder(World world) {
            this.world = world;
            this.seed = world.getSeed();
        }

        /**
         * @param noiseType The type of noise this carver will use
         */
        public UndergroundCarverBuilder noiseType(FastNoise.NoiseType noiseType) {
            this.noiseType = noiseType;
            return this;
        }

        /**
         * @param fractalOctaves Number of fractal octaves to use in ridged multifractal noise generation
         */
        public UndergroundCarverBuilder fractalOctaves(int fractalOctaves) {
            this.fractalOctaves = fractalOctaves;
            return this;
        }

        /**
         * @param fractalGain Amount of gain to use in ridged multifractal noise generation
         */
        public UndergroundCarverBuilder fractalGain(float fractalGain) {
            this.fractalGain = fractalGain;
            return this;
        }

        /**
         * @param fractalFreq Frequency to use in ridged multifractal noise generation
         */
        public UndergroundCarverBuilder fractalFrequency(float fractalFreq) {
            this.fractalFreq = fractalFreq;
            return this;
        }

        /**
         * @param numGens Number of noise values to calculate for a given block
         */
        public UndergroundCarverBuilder numberOfGenerators(int numGens) {
            this.numGens = numGens;
            return this;
        }

        /**
         * @param turbOctaves Number of octaves in turbulence function
         */
        public UndergroundCarverBuilder turbulenceOctaves(int turbOctaves) {
            this.turbOctaves = turbOctaves;
            return this;
        }

        /**
         * @param turbGain Gain of turbulence function
         */
        public UndergroundCarverBuilder turbulenceGain(float turbGain) {
            this.turbGain = turbGain;
            return this;
        }

        /**
         * @param turbFreq Frequency of turbulence function
         */
        public UndergroundCarverBuilder turbulenceFrequency(float turbFreq) {
            this.turbFreq = turbFreq;
            return this;
        }

        /**
         * Enable turbulence (adds performance overhead, generally not worth it).
         * If not enabled then other turbulence parameters don't matter and are not used.
         */
        public UndergroundCarverBuilder enableTurbulence(boolean enableTurbulence) {
            this.enableTurbulence = enableTurbulence;
            return this;
        }

        /**
         * @param yCompression Vertical cave gen compression. Use 1.0 for default generation
         */
        public UndergroundCarverBuilder verticalCompression(float yCompression) {
            this.yCompression = yCompression;
            return this;
        }

        /**
         * @param xzCompression Horizontal cave gen compression. Use 1.0 for default generation
         */
        public UndergroundCarverBuilder horizontalCompression(float xzCompression) {
            this.xzCompression = xzCompression;
            return this;
        }

        /**
         * @param yAdjustF1 Adjustment value for the block immediately above. Must be between 0 and 1.0
         */
        public UndergroundCarverBuilder verticalAdjuster1(float yAdjustF1) {
            this.yAdjustF1 = yAdjustF1;
            return this;
        }

        /**
         * @param yAdjustF2 Adjustment value for the block two blocks above. Must be between 0 and 1.0
         */
        public UndergroundCarverBuilder verticalAdjuster2(float yAdjustF2) {
            this.yAdjustF2 = yAdjustF2;
            return this;
        }

        /**
         * @param enableYAdjust Whether or not to adjust/increase the height of caves.
         */
        public UndergroundCarverBuilder enableVerticalAdjustment(boolean enableYAdjust) {
            this.enableYAdjust = enableYAdjust;
            return this;
        }

        /**
         * @param noiseThreshold Noise threshold to determine whether or not a given block will be dug out
         */
        public UndergroundCarverBuilder noiseThreshold(float noiseThreshold) {
            this.noiseThreshold = noiseThreshold;
            return this;
        }

        /**
         * @param vBlock Block used for this cave type in the debug visualizer
         */
        public UndergroundCarverBuilder debugVisualizerBlock(IBlockState vBlock) {
            this.debugBlock = vBlock;
            return this;
        }

        /**
         * @param liquidAltitude altitude at and below which air is replaced with liquid
         */
        public UndergroundCarverBuilder liquidAltitude(int liquidAltitude) {
            this.liquidAltitude = liquidAltitude;
            return this;
        }

        /**
         * Enable the debug visualizer
         */
        public UndergroundCarverBuilder enableDebugVisualizer(boolean enableDebugVisualizer) {
            this.enableDebugVisualizer = enableDebugVisualizer;
            return this;
        }

        public UndergroundCarver build() {
            return new UndergroundCarver(this);
        }
    }
}
