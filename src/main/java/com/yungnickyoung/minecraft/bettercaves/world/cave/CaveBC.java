package com.yungnickyoung.minecraft.bettercaves.world.cave;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.enums.CaveType;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseGen;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseTuple;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCaveUtil;
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
public class CaveBC extends AbstractBC {
    private NoiseGen noiseGen;

    public CaveBC(World world, CaveType caveType, int fOctaves, float fGain, float fFreq, int numGens, float threshold, int tOctaves,
                  float tGain, float tFreq, boolean enableTurbulence, float yComp, float xzComp, boolean yAdj,
                  float yAdjF1, float yAdjF2, IBlockState vBlock) {
        super(world, fOctaves, fGain, fFreq, numGens, threshold, tOctaves, tGain, tFreq, enableTurbulence, yComp,
                xzComp, yAdj, yAdjF1, yAdjF2, vBlock);

        // Determine noise to use based on cave type
        switch (caveType) {
            case CUBIC:
                this.noiseType = FastNoise.NoiseType.CubicFractal;
                break;
            default:
            case SIMPLEX:
                this.noiseType = FastNoise.NoiseType.SimplexFractal;
                break;
        }

        noiseGen = new NoiseGen(
                this.noiseType,
                world,
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
                               int topY, int maxSurfaceHeight, int minSurfaceHeight, int surfaceCutoff) {
        // Altitude at which caves start closing off so they aren't all open to the surface
        int transitionBoundary = maxSurfaceHeight - surfaceCutoff;

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
            if (Configuration.debugsettings.debugVisualizer)
                visualizeDigBlock(digBlock, this.vBlock, primer, localX, realY, localZ);
            else if (digBlock)
                this.digBlock(primer, chunkX, chunkZ, localX, localZ, realY);
        }

        /* ============ Post-Processing to remove any singular floating blocks in the ease-in range ============ */
        IBlockState BlockStateAir = Blocks.AIR.getDefaultState();
        for (int realY = transitionBoundary + 1; realY < topY; realY++) {
            IBlockState currBlock = primer.getBlockState(localX, realY, localZ);

            if (BetterCaveUtil.canReplaceBlock(currBlock, BlockStateAir)
                    && primer.getBlockState(localX, realY + 1, localZ) == BlockStateAir
                    && primer.getBlockState(localX, realY - 1, localZ) == BlockStateAir
            )
                this.digBlock(primer, chunkX, chunkZ, localX, localZ, realY);
        }
    }
}