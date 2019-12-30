package com.yungnickyoung.minecraft.bettercaves.world.cave;

import com.yungnickyoung.minecraft.bettercaves.config.dimension.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.enums.CaveType;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseGen;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseTuple;
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
    private NoiseGen noiseGen;

    private CaveCarver(final CaveCarverBuilder builder) {
        super(builder);
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

    @Override
    public void generateColumn(int chunkX, int chunkZ, ChunkPrimer primer, int localX, int localZ, int bottomY,
                               int topY, int maxSurfaceHeight, int minSurfaceHeight, int surfaceCutoff, IBlockState lavaBlock) {
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
        Map<Integer, NoiseTuple> noises =
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
                this.digBlock(primer, lavaBlock, chunkX, chunkZ, localX, localZ, realY);
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
                this.digBlock(primer, lavaBlock, chunkX, chunkZ, localX, localZ, realY);
        }
    }

    public static class CaveCarverBuilder extends UndergroundCarverBuilder {
        public CaveCarverBuilder(World world) {
            super(world);
        }

        @Override
        public UndergroundCarver build() {
            return new CaveCarver(this);
        }

        public CaveCarverBuilder ofTypeFromConfig(CaveType caveType, ConfigHolder config) {
            switch (caveType) {
                case CUBIC:
                    this.noiseType = FastNoise.NoiseType.CubicFractal;
                    this.fractalOctaves = config.cubicCaveFractalOctaves.get();
                    this.fractalGain = config.cubicCaveFractalGain.get();
                    this.fractalFreq = config.cubicCaveFractalFrequency.get();
                    this.numGens = config.cubicCaveNumGenerators.get();
                    this.turbOctaves = config.cubicCaveTurbulenceOctaves.get();
                    this.turbGain = config.cubicCaveTurbulenceGain.get();
                    this.turbFreq = config.cubicCaveTurbulenceFrequency.get();
                    this.enableTurbulence = config.cubicCaveEnableTurbulence.get();
                    this.yCompression = config.cubicCaveYCompression.get();
                    this.xzCompression = config.cubicCaveXZCompression.get();
                    this.yAdjustF1 = config.cubicCaveYAdjustF1.get();
                    this.yAdjustF2 = config.cubicCaveYAdjustF2.get();
                    this.noiseThreshold = config.cubicCaveNoiseThreshold.get();
                    this.enableYAdjust = config.cubicCaveEnableVerticalAdjustment.get();
                    break;
                case SIMPLEX:
                    this.noiseType = FastNoise.NoiseType.SimplexFractal;
                    this.fractalOctaves = config.simplexCaveFractalOctaves.get();
                    this.fractalGain = config.simplexCaveFractalGain.get();
                    this.fractalFreq = config.simplexCaveFractalFrequency.get();
                    this.numGens = config.simplexCaveNumGenerators.get();
                    this.turbOctaves = config.simplexCaveTurbulenceOctaves.get();
                    this.turbGain = config.simplexCaveTurbulenceGain.get();
                    this.turbFreq = config.simplexCaveTurbulenceFrequency.get();
                    this.enableTurbulence = config.simplexCaveEnableTurbulence.get();
                    this.yCompression = config.simplexCaveYCompression.get();
                    this.xzCompression = config.simplexCaveXZCompression.get();
                    this.yAdjustF1 = config.simplexCaveYAdjustF1.get();
                    this.yAdjustF2 = config.simplexCaveYAdjustF2.get();
                    this.noiseThreshold = config.simplexCaveNoiseThreshold.get();
                    this.enableYAdjust = config.simplexCaveEnableVerticalAdjustment.get();
                    break;
            }
            return this;
        }
    }
}