package com.yungnickyoung.minecraft.bettercaves.world.cave;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseGen;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseTuple;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCaveUtil;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.List;
import java.util.Map;

public class TestCave extends BetterCave {
    private NoiseGen testNoiseGen;

    public TestCave(World world, int fOctaves, float fGain, float fFreq, int numGens, float threshold, int tOctaves,
                    float tGain, float tFreq, boolean enableTurbulence, float yComp, float xzComp, boolean yAdj,
                    float yAdjF1, float yAdjF2) {
        super(world, fOctaves, fGain, fFreq, numGens, threshold, tOctaves, tGain, tFreq, enableTurbulence, yComp,
                xzComp, yAdj, yAdjF1, yAdjF2);

        testNoiseGen = new NoiseGen(
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
                               int topY, int maxSurfaceHeight, int minSurfaceHeight) {
        /* ========================= Import cave variables from the config ========================= */
        // Altitude at which caves start closing off so they aren't all open to the surface
        int transitionBoundary = maxSurfaceHeight - 20;

        /* ========================== Generate noise using test noise type ========================= */
        // The noise for an individual block is represented by a NoiseTuple, which is essentially an n-tuple of
        // floats, where n is equal to the number of generators passed to the function (perlinNumGens or simplexNumGens)
        Map<Integer, NoiseTuple> testNoises =
                testNoiseGen.generateNoiseCol(chunkX, chunkZ, bottomY, topY, this.numGens, localX, localZ);

        // Do some pre-processing on the noises to facilitate better cave generation.
        // See the javadoc for the function for more info.
        if (this.enableYAdjust)
            this.preprocessCaveNoiseCol(testNoises, topY, bottomY, this.numGens);

        /* =============== Dig out caves and caverns in this chunk, based on noise values =============== */
        for (int realY = topY; realY >= bottomY; realY--) {
            List<Float> testNoiseBlock;
            boolean digBlock = false;

            // Process simplex noise
            // Compute a single noise value to represent all the noise values in the NoiseTuple
            float testNoise = 0;
            testNoiseBlock = testNoises.get(realY).getNoiseValues();
            for (float noise : testNoiseBlock)
                testNoise += noise;

            testNoise /= testNoiseBlock.size();

            // Close off caves if we're in ease-in depth range
            float noiseThreshold = this.noiseThreshold;
            if (realY >= transitionBoundary)
                noiseThreshold *= (1 + .3f * ((float)(realY - transitionBoundary) / (topY - transitionBoundary)));

            // Mark block for removal if the noise passes the threshold check
            if (testNoise > noiseThreshold)
                digBlock = true;

            // Dig/remove the block if it passed the threshold check
            if (digBlock) {
                // Check for adjacent water blocks to avoid breaking into lakes or oceans
                if (primer.getBlockState(localX, realY + 1, localZ).getMaterial() == Material.WATER)
                    continue;
                if (localX < 15 && primer.getBlockState(localX + 1, realY, localZ).getMaterial() == Material.WATER)
                    continue;
                if (localX > 0 && primer.getBlockState(localX - 1, realY, localZ).getMaterial() == Material.WATER)
                    continue;
                if (localZ < 15 && primer.getBlockState(localX, realY, localZ + 1).getMaterial() == Material.WATER)
                    continue;
                if (localZ > 0 && primer.getBlockState(localX, realY, localZ - 1).getMaterial() == Material.WATER)
                    continue;

                BetterCaveUtil.digBlock(this.getWorld(), primer, localX, realY, localZ, chunkX, chunkZ);
            }
        }

        /* ============ Post-Processing to remove any singular floating blocks in the ease-in range ============ */
//        IBlockState BlockStateAir = Blocks.AIR.getDefaultState();
//        for (int realY = simplexCaveTransitionBoundary + 1; realY < topY - 1; realY++) {
//            IBlockState currBlock = primer.getBlockState(localX, realY, localZ);
//
//            if (BetterCaveUtil.canReplaceBlock(currBlock, BlockStateAir)
//                    && primer.getBlockState(localX, realY + 1, localZ) == BlockStateAir
//                    && primer.getBlockState(localX, realY - 1, localZ) == BlockStateAir
//            )
//                BetterCaveUtil.digBlock(world, primer, localX, realY, localZ, chunkX, chunkZ);
//        }
    }
}
