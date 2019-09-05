package com.yungnickyoung.minecraft.bettercaves.world.cavern;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseGen;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseTuple;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCaveUtil;
import com.yungnickyoung.minecraft.bettercaves.util.FastNoise;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.List;
import java.util.Map;

public class BetterCavernFloored extends BetterCavern {
    private NoiseGen perlinNoiseGen;

    public BetterCavernFloored(World world, int fOctaves, float fGain, float fFreq, int numGens, float threshold,
                               float yComp, float xzComp) {
        super(world, fOctaves, fGain, fFreq, numGens, threshold, yComp, xzComp);

        perlinNoiseGen = new NoiseGen(
                FastNoise.NoiseType.PerlinFractal,
                world,
                this.fractalOctaves,
                this.fractalGain,
                this.fractalFreq,
                0,
                0,
                0,
                false,
                this.yCompression,
                this.xzCompression
        );
    }

    @Override
    public void generateColumn(int chunkX, int chunkZ, ChunkPrimer primer, int localX, int localZ, int bottomY,
                               int topY, int maxSurfaceHeight, int minSurfaceHeight) {
        /* ======================== Import cavern variables from the config ======================== */
        // Altitude at which caverns start closing off on the top
        int perlinCavernTopTransitionBoundary = topY - 7;

        // Altitude at which caverns start closing off on the bottom
        int perlinCavernBottomTransitionBoundary = (bottomY <= 10) ? Configuration.lavaDepth + 4 : bottomY + 7;


        /* ========== Generate noise for caverns (using Perlin noise) ========== */
        // The noise for an individual block is represented by a NoiseTuple, which is essentially an n-tuple of
        // floats, where n is equal to the number of generators passed to the function
        Map<Integer, NoiseTuple> perlinNoises =
                perlinNoiseGen.generateNoiseCol(chunkX, chunkZ, bottomY, topY, this.numGens, localX, localZ);

        /* =============== Dig out caves and caverns in this chunk, based on noise values =============== */
        for (int realY = topY; realY >= bottomY; realY--) {
            List<Float> perlinNoiseBlock;
            boolean digBlock = false;

            // Compute a single noise value to represent all the noise values in the NoiseTuple
            float perlinNoise = 1;
            perlinNoiseBlock = perlinNoises.get(realY).getNoiseValues();
            for (float noise : perlinNoiseBlock)
                perlinNoise *= noise;

            // Adjust threshold if we're in the transition range to provide smoother transition into ceiling
            float noiseThreshold = this.noiseThreshold;
            if (realY >= perlinCavernTopTransitionBoundary)
                noiseThreshold *= Math.max((float) (realY - topY) / (perlinCavernTopTransitionBoundary - topY), .5f);

            // Force close-off caverns if we're in ease-in depth range
            if (realY >= minSurfaceHeight - 5)
                noiseThreshold *= (float) (realY - topY) / (minSurfaceHeight - 5 - topY);

            // Close off caverns at the bottom to provide floors for the player to walk on
            if (realY <= perlinCavernBottomTransitionBoundary)
                noiseThreshold *= Math.max((float) (realY - bottomY) / (perlinCavernBottomTransitionBoundary - bottomY), .5f);

            // Mark block for removal if the noise passes the threshold check
            if (perlinNoise < noiseThreshold)
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
    }
}
