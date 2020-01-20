package com.yungnickyoung.minecraft.bettercaves.world.cave;

import com.yungnickyoung.minecraft.bettercaves.config.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.enums.CavernType;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseColumn;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseGen;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.List;

public class CavernCarver extends UndergroundCarver {
    private CavernType cavernType;

    public CavernCarver(final CavernCarverBuilder builder) {
        super(builder);
        cavernType = builder.cavernType;
        noiseGen = new NoiseGen(
                this.noiseType,
                this.world,
                this.fractalOctaves,
                this.fractalGain,
                this.fractalFreq,
                this.numGens,
                this.turbOctaves,
                this.turbGain,
                this.turbFreq,
                this.enableTurbulence,
                this.yCompression,
                this.xzCompression
        );
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
     */@Override
    public void generateColumn(ChunkPrimer primer, BlockPos colPos, int bottomY,
                               int topY, int maxSurfaceHeight, int minSurfaceHeight,
                               IBlockState liquidBlock, float smoothAmp) {
        int localX = BetterCavesUtil.getLocal(colPos.getX() % 16);
        int localZ = BetterCavesUtil.getLocal(colPos.getZ() % 16);

        // Validate vars
        if (localX < 0 || localX > 15)
            return;
        if (localZ < 0 || localZ > 15)
            return;
        if (bottomY < 0)
            return;
        if (topY > 255)
            return;

        // Altitude at which caverns start closing off on the top
        int topTransitionBoundary = topY - 7;

        // Validate transition boundary
        if (topTransitionBoundary < 1)
            topTransitionBoundary = 1;

        // Altitude at which caverns start closing off on the bottom to create "floors"
        int bottomTransitionBoundary = 0;
        if (cavernType == CavernType.FLOORED)
            bottomTransitionBoundary = (bottomY <= 10) ? liquidAltitude + 4 : bottomY + 7;
        else if (cavernType == CavernType.WATER)
            bottomTransitionBoundary = bottomY + 3;

        // Generate noise for caverns.
        // The noise for an individual block is represented by a NoiseTuple, which is essentially an n-tuple of
        // floats, where n is equal to the number of generators passed to the function
        NoiseColumn noises =
                noiseGen.generateNoiseColumn(colPos, bottomY, topY);

        /* =============== Dig out caves and caverns in this chunk, based on noise values =============== */
        for (int y = topY; y >= bottomY; y--) {
            List<Float> noiseBlock;
            boolean digBlock = false;

            // Compute a single noise value to represent all the noise values in the NoiseTuple
            float noise = 1;
            noiseBlock = noises.get(y).getNoiseValues();
            for (float n : noiseBlock)
                noise *= n;

            // Adjust threshold if we're in the transition range to provide smoother transition into ceiling
            float noiseThreshold = this.noiseThreshold;
            if (y >= topTransitionBoundary)
                noiseThreshold *= Math.max((float) (y - topY) / (topTransitionBoundary - topY), .5f);

            // Force close-off caverns if we're in ease-in depth range
            if (y >= minSurfaceHeight - 5)
                noiseThreshold *= (float) (y - topY) / (minSurfaceHeight - 5 - topY);

            // For floored caverns, close off caverns at the bottom to provide floors for the player to walk on
            if ((this.cavernType == CavernType.FLOORED || this.cavernType == CavernType.WATER) && y <= bottomTransitionBoundary)
                noiseThreshold *= Math.max((float) (y - bottomY) / (bottomTransitionBoundary - bottomY), .5f);

            // Adjust threshold along region borders to create smooth transition
            if (smoothAmp < 1)
                noiseThreshold *= smoothAmp;

            // Mark block for removal if the noise passes the threshold check
            if (noise < noiseThreshold)
                digBlock = true;

            BlockPos blockPos = new BlockPos(colPos.getX(), y, colPos.getZ());

            // Consider digging out the block if it passed the threshold check, using the debug visualizer if enabled
            if (this.enableDebugVisualizer)
                visualizeDigBlock(primer, blockPos, digBlock, this.debugBlock);
            else if (digBlock) {
                this.digBlock(primer, blockPos, liquidBlock, liquidAltitude);
            }
        }
    }

    @Override
    public void generateColumnWithNoise(ChunkPrimer primer, BlockPos colPos, int bottomY,
                                        int topY, int maxSurfaceHeight, int minSurfaceHeight,
                                        IBlockState liquidBlock, float smoothAmp, NoiseColumn noises) {
        int localX = BetterCavesUtil.getLocal(colPos.getX());
        int localZ = BetterCavesUtil.getLocal(colPos.getZ());

        // Validate vars
        if (localX < 0 || localX > 15)
            return;
        if (localZ < 0 || localZ > 15)
            return;
        if (bottomY < 0)
            return;
        if (topY > 255)
            return;

        // Altitude at which caverns start closing off on the top
        int topTransitionBoundary = topY - 7;

        // Validate transition boundary
        if (topTransitionBoundary < 1)
            topTransitionBoundary = 1;

        // Altitude at which caverns start closing off on the bottom to create "floors"
        int bottomTransitionBoundary = 0;
        if (cavernType == CavernType.FLOORED)
            bottomTransitionBoundary = (bottomY <= 10) ? liquidAltitude + 4 : bottomY + 7;
        else if (cavernType == CavernType.WATER)
            bottomTransitionBoundary = bottomY + 3;

        /* =============== Dig out caves and caverns in this chunk, based on noise values =============== */
        for (int y = topY; y >= bottomY; y--) {
            List<Float> noiseBlock;
            boolean digBlock = false;

            // Compute a single noise value to represent all the noise values in the NoiseTuple
            float noise = 1;
            noiseBlock = noises.get(y).getNoiseValues();
            for (float n : noiseBlock)
                noise *= n;

            // Adjust threshold if we're in the transition range to provide smoother transition into ceiling
            float noiseThreshold = this.noiseThreshold;
            if (y >= topTransitionBoundary)
                noiseThreshold *= Math.max((float) (y - topY) / (topTransitionBoundary - topY), .5f);

            // Force close-off caverns if we're in ease-in depth range
            if (y >= minSurfaceHeight - 5)
                noiseThreshold *= (float) (y - topY) / (minSurfaceHeight - 5 - topY);

            // For floored caverns, close off caverns at the bottom to provide floors for the player to walk on
            if ((this.cavernType == CavernType.FLOORED || this.cavernType == CavernType.WATER) && y <= bottomTransitionBoundary)
                noiseThreshold *= Math.max((float) (y - bottomY) / (bottomTransitionBoundary - bottomY), .5f);

            // Adjust threshold along region borders to create smooth transition
            if (smoothAmp < 1)
                noiseThreshold *= smoothAmp;

            // Mark block for removal if the noise passes the threshold check
            if (noise < noiseThreshold)
                digBlock = true;

            BlockPos blockPos = new BlockPos(colPos.getX(), y, colPos.getZ());

            // Consider digging out the block if it passed the threshold check, using the debug visualizer if enabled
            if (this.enableDebugVisualizer)
                visualizeDigBlock(primer, blockPos, digBlock, this.debugBlock);
            else if (digBlock) {
                this.digBlock(primer, blockPos, liquidBlock, liquidAltitude);
            }
        }
    }

    public void generateColumnWithNoise(ChunkPrimer primer, BlockPos colPos, int bottomY,
                                        int topY, int maxSurfaceHeight, int minSurfaceHeight,
                                        IBlockState liquidBlock, NoiseColumn noises) {
         generateColumnWithNoise(primer, colPos, bottomY, topY, maxSurfaceHeight, minSurfaceHeight, liquidBlock, 1, noises);
    }

    @Override
    public void generateColumn(ChunkPrimer primer, BlockPos colPos, int bottomY, int topY, int maxSurfaceHeight, int minSurfaceHeight, IBlockState liquidBlock) {
        generateColumn(primer, colPos, bottomY, topY, maxSurfaceHeight, minSurfaceHeight, liquidBlock, 1);
    }

    /**
     * Builder class for CavernCarver.
     * Fields may be built individually or loaded in bulk via the {@code ofTypeFromCarver} method
     */
    public static class CavernCarverBuilder extends UndergroundCarverBuilder {
        CavernType cavernType;

        public CavernCarverBuilder(World world) {
            super(world);
        }

        @Override
        public UndergroundCarver build() {
            return new CavernCarver(this);
        }

        /**
         * Helps build a CavernCarver from a ConfigHolder based on its CavernType
         * @param cavernType the CavernType of this CavernCarver
         * @param config the config
         */
        public CavernCarverBuilder ofTypeFromConfig(CavernType cavernType, ConfigHolder config) {
            this.liquidAltitude = config.liquidAltitude.get();
            this.enableDebugVisualizer = config.debugVisualizer.get();
            this.cavernType = cavernType;
            switch (cavernType) {
                case LAVA:
                    this.noiseType = FastNoise.NoiseType.PerlinFractal;
                    this.noiseThreshold = config.lavaCavernNoiseThreshold.get();
                    this.fractalOctaves = config.lavaCavernFractalOctaves.get();
                    this.fractalGain = config.lavaCavernFractalGain.get();
                    this.fractalFreq = config.lavaCavernFractalFrequency.get();
                    this.numGens = config.lavaCavernNumGenerators.get();
                    this.yCompression = config.lavaCavernYCompression.get();
                    this.xzCompression = config.lavaCavernXZCompression.get();
                    break;
                case FLOORED:
                    this.noiseType = FastNoise.NoiseType.PerlinFractal;
                    this.noiseThreshold = config.flooredCavernNoiseThreshold.get();
                    this.fractalOctaves = config.flooredCavernFractalOctaves.get();
                    this.fractalGain = config.flooredCavernFractalGain.get();
                    this.fractalFreq = config.flooredCavernFractalFrequency.get();
                    this.numGens = config.flooredCavernNumGenerators.get();
                    this.yCompression = config.flooredCavernYCompression.get();
                    this.xzCompression = config.flooredCavernXZCompression.get();
                    break;
                case WATER:
                    this.noiseType = FastNoise.NoiseType.PerlinFractal;
                    this.noiseThreshold = config.waterCavernNoiseThreshold.get();
                    this.fractalOctaves = config.waterCavernFractalOctaves.get();
                    this.fractalGain = config.waterCavernFractalGain.get();
                    this.fractalFreq = config.waterCavernFractalFrequency.get();
                    this.numGens = config.waterCavernNumGenerators.get();
                    this.yCompression = config.waterCavernYCompression.get();
                    this.xzCompression = config.waterCavernXZCompression.get();
                    break;
            }
            return this;
        }
    }
}
