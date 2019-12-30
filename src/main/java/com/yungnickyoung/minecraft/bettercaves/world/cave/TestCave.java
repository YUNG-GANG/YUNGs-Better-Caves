package com.yungnickyoung.minecraft.bettercaves.world.cave;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseGen;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseTuple;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.List;
import java.util.Map;

public class TestCave extends UndergroundCarver {
    private NoiseGen noiseGen;

    public TestCave(World world, int fOctaves, float fGain, float fFreq, int numGens, float threshold, int tOctaves,
                    float tGain, float tFreq, boolean enableTurbulence, float yComp, float xzComp, boolean yAdj,
                    float yAdjF1, float yAdjF2) {
        super(world, fOctaves, fGain, fFreq, numGens, threshold, tOctaves, tGain, tFreq, enableTurbulence, yComp,
                xzComp, yAdj, yAdjF1, yAdjF2, Blocks.DIRT.getDefaultState());

        noiseGen = new NoiseGen(
                Configuration.testSettings.testnoiseType,
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
                               int topY, int maxSurfaceHeight, int minSurfaceHeight, int surfaceCutoff, IBlockState lavaBlock) {
        // Altitude at which caves start closing off so they aren't all open to the surface
        int transitionBoundary = maxSurfaceHeight - surfaceCutoff;

        // Generate noise for caves.
        // The noise for an individual block is represented by a NoiseTuple, which is essentially an n-tuple of
        // floats, where n is equal to the number of generators passed to the function
        Map<Integer, NoiseTuple> noises =
                noiseGen.generateNoiseCol(chunkX, chunkZ, bottomY, topY, this.numGens, localX, localZ);

        Map<Integer, Float> thresholds = generateThresholds(topY, bottomY, transitionBoundary);

        // Do some pre-processing on the noises to facilitate better cave generation.
        // Basically this makes caves taller to give players more headroom.
        // See the javadoc for the function for more info.
        if (this.enableYAdjust)
            this.preprocessCaveNoiseCol(noises, topY, bottomY, thresholds, this.numGens);

        /* =============== Dig out caves and caverns in this column, based on noise values =============== */
        for (int realY = topY; realY >= bottomY; realY--) {
            List<Float> noiseBlock = noises.get(realY).getNoiseValues();
            boolean digBlock = true;
//            boolean digBlock = false;

//            // Process noise
//            float testNoise = 0;
//            for (float noise : testNoiseBlock)
//                testNoise += noise;
//
//            testNoise /= testNoiseBlock.size();

            for (float noise : noiseBlock) {
                if (noise < thresholds.get(realY)) {
                    digBlock = false;
                    break;
                }
            }
//
//            // Mark block for removal if the noise passes the threshold check
//            if (testNoise > thresholds.get(realY))
//                digBlock = true;

            // Consider digging out the block if it passed the threshold check, using the debug visualizer if enabled
            if (Configuration.debugsettings.debugVisualizer)
                visualizeDigBlock(digBlock, this.debugBlock, primer, localX, realY, localZ);
            else if (digBlock)
                this.digBlock(primer, chunkX, chunkZ, localX, localZ, realY);
        }

        /* ============ Post-Processing to remove any singular floating blocks in the ease-in range ============ */
//        IBlockState BlockStateAir = Blocks.AIR.getDefaultState();
//        for (int realY = simplexCaveTransitionBoundary + 1; realY < topY; realY++) {
//            IBlockState currBlock = primer.getBlockState(localX, realY, localZ);
//
//            if (BetterCavesUtil.canReplaceBlock(currBlock, BlockStateAir)
//                    && primer.getBlockState(localX, realY + 1, localZ) == BlockStateAir
//                    && primer.getBlockState(localX, realY - 1, localZ) == BlockStateAir
//            )
//                this.digBlock(primer, chunkX, chunkZ, localX, localZ, realY);
//        }
    }
}
