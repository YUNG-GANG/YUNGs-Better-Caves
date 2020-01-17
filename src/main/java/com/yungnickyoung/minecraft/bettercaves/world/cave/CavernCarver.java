package com.yungnickyoung.minecraft.bettercaves.world.cave;

import com.yungnickyoung.minecraft.bettercaves.config.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.enums.CavernType;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseGen;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseTuple;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.List;
import java.util.Map;

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
                this.turbOctaves,
                this.turbGain,
                this.turbFreq,
                this.enableTurbulence,
                this.yCompression,
                this.xzCompression
        );
    }

    /**
     * Dig out caverns for the column of blocks at x-z position (chunkX*16 + localX, chunkZ*16 + localZ).
     * A given block will be calculated based on the noise value and noise threshold of this UndergroundCarver object.
     * @param chunkX The chunk's x-coordinate
     * @param chunkZ The chunk's z-coordinate
     * @param primer The ChunkPrimer for this chunk
     * @param localX The chunk-local x-coordinate of this column of blocks (0 <= localX <= 15)
     * @param localZ The chunk-local z-coordinate of this column of blocks (0 <= localZ <= 15)
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
    public void generateColumn(int chunkX, int chunkZ, ChunkPrimer primer, int localX, int localZ, int bottomY,
                               int topY, int maxSurfaceHeight, int minSurfaceHeight,
                               IBlockState liquidBlock, float smoothAmp) {
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
        Map<Integer, NoiseTuple> noises =
                noiseGen.generateNoiseCol(chunkX, chunkZ, bottomY, topY, this.numGens, localX, localZ);

        /* =============== Dig out caves and caverns in this chunk, based on noise values =============== */
        for (int realY = topY; realY >= bottomY; realY--) {
            List<Float> noiseBlock;
            boolean digBlock = false;

            // Compute a single noise value to represent all the noise values in the NoiseTuple
            float noise = 1;
            noiseBlock = noises.get(realY).getNoiseValues();
            for (float n : noiseBlock)
                noise *= n;

            // Adjust threshold if we're in the transition range to provide smoother transition into ceiling
            float noiseThreshold = this.noiseThreshold;
            if (realY >= topTransitionBoundary)
                noiseThreshold *= Math.max((float) (realY - topY) / (topTransitionBoundary - topY), .5f);

            // Force close-off caverns if we're in ease-in depth range
            if (realY >= minSurfaceHeight - 5)
                noiseThreshold *= (float) (realY - topY) / (minSurfaceHeight - 5 - topY);

            // For floored caverns, close off caverns at the bottom to provide floors for the player to walk on
            if ((this.cavernType == CavernType.FLOORED || this.cavernType == CavernType.WATER) && realY <= bottomTransitionBoundary)
                noiseThreshold *= Math.max((float) (realY - bottomY) / (bottomTransitionBoundary - bottomY), .5f);

            // Adjust threshold along region borders to create smooth transition
            if (smoothAmp < 1)
                noiseThreshold *= smoothAmp;

            // Mark block for removal if the noise passes the threshold check
            if (noise < noiseThreshold)
                digBlock = true;

            // Consider digging out the block if it passed the threshold check, using the debug visualizer if enabled
            if (this.enableDebugVisualizer)
                visualizeDigBlock(digBlock, this.debugBlock, primer, localX, realY, localZ);
            else if (digBlock) {
//            if (this.cavernType == CavernType.WATER) {
//                // Make sure we replace any lava possibly generated from caves with water to avoid having lava under the water
//                if (primer.getBlockState(localX, realY, localZ).getMaterial() == Material.LAVA && realY <= liquidAltitude)
//                    primer.setBlockState(localX, realY, localZ, Blocks.WATER.getDefaultState());
//                else
//                    this.digBlock(primer, liquidBlock, liquidAltitude, chunkX, chunkZ, localX, localZ, realY);
//            } else
                this.digBlock(primer, liquidBlock, liquidAltitude, chunkX, chunkZ, localX, localZ, realY);
            }
        }
    }

    @Override
    public void generateColumn(int chunkX, int chunkZ, ChunkPrimer primer, int localX, int localZ, int bottomY, int topY, int maxSurfaceHeight, int minSurfaceHeight, IBlockState liquidBlock) {
        generateColumn(chunkX, chunkZ, primer, localX, localZ, bottomY, topY, maxSurfaceHeight, minSurfaceHeight, liquidBlock, 1);
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
