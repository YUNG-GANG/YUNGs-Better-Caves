package com.yungnickyoung.minecraft.bettercaves.world.cave;

import com.yungnickyoung.minecraft.bettercaves.config.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.enums.CaveType;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseColumn;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseGen;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtil;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.List;
import java.util.Map;

/**
 * Class for generation of Better Caves caves.
 */
public class CaveCarver extends UndergroundCarver {
    private int surfaceCutoff;

    private CaveCarver(final CaveCarverBuilder builder) {
        super(builder);
        surfaceCutoff = builder.surfaceCutoff;
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
     * Dig out caves for the column of blocks at x-z position (chunkX*16 + localX, chunkZ*16 + localZ).
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
     *                         BetterCavesUtil#getMinSurfaceAltitudeChunk or BetterCavesUtil#getMinSurfaceAltitudeSubChunk
     * @param liquidBlock Block to use for liquid, e.g. lava
     */
    @Override
    public void generateColumn(int chunkX, int chunkZ, ChunkPrimer primer, int localX, int localZ, int bottomY,
                               int topY, int maxSurfaceHeight, int minSurfaceHeight, IBlockState liquidBlock) {
        // Validate vars
        if (localX < 0 || localX > 15)
            return;
        if (localZ < 0 || localZ > 15)
            return;
        if (bottomY < 0)
            return;
        if (topY > 255)
            return;

        // Altitude at which caves start closing off so they aren't all open to the surface
        int transitionBoundary = maxSurfaceHeight - surfaceCutoff;

        // Validate transition boundary
        if (transitionBoundary < 1)
            transitionBoundary = 1;

        // Generate noise for caves.
        // The noise for an individual block is represented by a NoiseTuple, which is essentially an n-tuple of
        // floats, where n is equal to the number of generators passed to the function
        NoiseColumn noises =
                noiseGen.generateNoiseCol(chunkX, chunkZ, bottomY, topY, this.numGens, localX, localZ);

        // Pre-compute thresholds to ensure accuracy during pre-processing
        Map<Integer, Float> thresholds = generateThresholds(topY, bottomY, transitionBoundary);

        // Do some pre-processing on the noises to facilitate better cave generation.
        // Basically this makes caves taller to give players more headroom.
        // See the javadoc for the function for more info.
        if (this.enableYAdjust)
            preprocessCaveNoiseCol(noises, topY, bottomY, thresholds, this.numGens);

        /* =============== Dig out caves and caverns in this column, based on noise values =============== */
        for (int realY = topY; realY >= bottomY; realY--) {
            List<Float> noiseBlock = noises.get(realY).getNoiseValues();
            boolean digBlock = true;

            for (float noise : noiseBlock) {
                if (noise < thresholds.get(realY)) {
                    digBlock = false;
                    break;
                }
            }

            // Consider digging out the block if it passed the threshold check, using the debug visualizer if enabled
            if (this.enableDebugVisualizer)
                visualizeDigBlock(digBlock, this.debugBlock, primer, localX, realY, localZ);
            else if (digBlock)
                this.digBlock(primer, liquidBlock, liquidAltitude, chunkX, chunkZ, localX, localZ, realY);
        }

        /* ============ Post-Processing to remove any singular floating blocks in the ease-in range ============ */
        IBlockState BlockStateAir = Blocks.AIR.getDefaultState();
        for (int realY = transitionBoundary + 1; realY < topY; realY++) {
            if (realY < 1 || realY > 255)
                break;

            IBlockState currBlock = primer.getBlockState(localX, realY, localZ);

            if (BetterCavesUtil.canReplaceBlock(currBlock, BlockStateAir)
                    && primer.getBlockState(localX, realY + 1, localZ) == BlockStateAir
                    && primer.getBlockState(localX, realY - 1, localZ) == BlockStateAir
            )
                this.digBlock(primer, liquidBlock, liquidAltitude, chunkX, chunkZ, localX, localZ, realY);
        }
    }

    @Override
    public void generateColumnWithNoise(int chunkX, int chunkZ, ChunkPrimer primer, int localX, int localZ, int bottomY,
                               int topY, int maxSurfaceHeight, int minSurfaceHeight, IBlockState liquidBlock, NoiseColumn noises) {
        // Validate vars
        if (localX < 0 || localX > 15)
            return;
        if (localZ < 0 || localZ > 15)
            return;
        if (bottomY < 0 || bottomY > 255)
            return;
        if (topY < 0 || topY > 255)
            return;

        // Altitude at which caves start closing off so they aren't all open to the surface
        int transitionBoundary = maxSurfaceHeight - surfaceCutoff;

        // Validate transition boundary
        if (transitionBoundary < 1)
            transitionBoundary = 1;

        // Pre-compute thresholds to ensure accuracy during pre-processing
        Map<Integer, Float> thresholds = generateThresholds(topY, bottomY, transitionBoundary);

        // Do some pre-processing on the noises to facilitate better cave generation.
        // Basically this makes caves taller to give players more headroom.
        // See the javadoc for the function for more info.
        if (this.enableYAdjust)
            preprocessCaveNoiseCol(noises, topY, bottomY, thresholds, this.numGens);

        /* =============== Dig out caves and caverns in this column, based on noise values =============== */
        for (int realY = topY; realY >= bottomY; realY--) {
            List<Float> noiseBlock = noises.get(realY).getNoiseValues();
            boolean digBlock = true;

            for (float noise : noiseBlock) {
                if (noise < thresholds.get(realY)) {
                    digBlock = false;
                    break;
                }
            }

            // Consider digging out the block if it passed the threshold check, using the debug visualizer if enabled
            if (this.enableDebugVisualizer)
                visualizeDigBlock(digBlock, this.debugBlock, primer, localX, realY, localZ);
            else if (digBlock)
                this.digBlock(primer, liquidBlock, liquidAltitude, chunkX, chunkZ, localX, localZ, realY);
        }

        /* ============ Post-Processing to remove any singular floating blocks in the ease-in range ============ */
        IBlockState BlockStateAir = Blocks.AIR.getDefaultState();
        for (int realY = transitionBoundary + 1; realY < topY; realY++) {
            if (realY < 1 || realY > 255)
                break;

            IBlockState currBlock = primer.getBlockState(localX, realY, localZ);

            if (BetterCavesUtil.canReplaceBlock(currBlock, BlockStateAir)
                    && primer.getBlockState(localX, realY + 1, localZ) == BlockStateAir
                    && primer.getBlockState(localX, realY - 1, localZ) == BlockStateAir
            )
                this.digBlock(primer, liquidBlock, liquidAltitude, chunkX, chunkZ, localX, localZ, realY);
        }
    }

    /**
     * Builder class for CaveCarver.
     * Fields may be built individually or loaded in bulk via the {@code ofTypeFromCarver} method
     */
    public static class CaveCarverBuilder extends UndergroundCarverBuilder {
        int surfaceCutoff;

        public CaveCarverBuilder(World world) {
            super(world);
        }

        @Override
        public UndergroundCarver build() {
            return new CaveCarver(this);
        }

        /**
         * Helps build a CaveCarver from a ConfigHolder based on its CaveType
         * @param caveType the CaveType of this CaveCarver
         * @param config the config
         */
        public CaveCarverBuilder ofTypeFromConfig(CaveType caveType, ConfigHolder config) {
            this.liquidAltitude = config.liquidAltitude.get();
            this.enableDebugVisualizer = config.debugVisualizer.get();
            this.surfaceCutoff = config.surfaceCutoff.get();
            switch (caveType) {
                case CUBIC:
                    this.noiseType = FastNoise.NoiseType.CubicFractal;
                    this.noiseThreshold = config.cubicCaveNoiseThreshold.get();
                    this.fractalOctaves = config.cubicCaveFractalOctaves.get();
                    this.fractalGain = config.cubicCaveFractalGain.get();
                    this.fractalFreq = config.cubicCaveFractalFrequency.get();
                    this.enableTurbulence = config.cubicCaveEnableTurbulence.get();
                    this.turbOctaves = config.cubicCaveTurbulenceOctaves.get();
                    this.turbGain = config.cubicCaveTurbulenceGain.get();
                    this.turbFreq = config.cubicCaveTurbulenceFrequency.get();
                    this.numGens = config.cubicCaveNumGenerators.get();
                    this.enableYAdjust = config.cubicCaveEnableVerticalAdjustment.get();
                    this.yAdjustF1 = config.cubicCaveYAdjustF1.get();
                    this.yAdjustF2 = config.cubicCaveYAdjustF2.get();
                    this.xzCompression = config.cubicCaveXZCompression.get();
                    this.yCompression = config.cubicCaveYCompression.get();
                    break;
                case SIMPLEX:
                    this.noiseType = FastNoise.NoiseType.SimplexFractal;
                    this.noiseThreshold = config.simplexCaveNoiseThreshold.get();
                    this.fractalOctaves = config.simplexCaveFractalOctaves.get();
                    this.fractalGain = config.simplexCaveFractalGain.get();
                    this.fractalFreq = config.simplexCaveFractalFrequency.get();
                    this.enableTurbulence = config.simplexCaveEnableTurbulence.get();
                    this.turbOctaves = config.simplexCaveTurbulenceOctaves.get();
                    this.turbGain = config.simplexCaveTurbulenceGain.get();
                    this.turbFreq = config.simplexCaveTurbulenceFrequency.get();
                    this.numGens = config.simplexCaveNumGenerators.get();
                    this.enableYAdjust = config.simplexCaveEnableVerticalAdjustment.get();
                    this.yAdjustF1 = config.simplexCaveYAdjustF1.get();
                    this.yAdjustF2 = config.simplexCaveYAdjustF2.get();
                    this.xzCompression = config.simplexCaveXZCompression.get();
                    this.yCompression = config.simplexCaveYCompression.get();
                    break;
            }
            return this;
        }
    }
}