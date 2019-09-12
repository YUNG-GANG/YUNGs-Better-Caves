package com.yungnickyoung.minecraft.bettercaves.world.cavern;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseGen;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseTuple;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCaveUtil;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.world.BetterCave;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.List;
import java.util.Map;

public class BetterCavernWater extends BetterCave {
    private NoiseGen perlinNoiseGen;

    public BetterCavernWater(World world, int fOctaves, float fGain, float fFreq, int numGens, float threshold,
                            float yComp, float xzComp) {
        super(world, fOctaves, fGain, fFreq, numGens, threshold, yComp, xzComp);

        perlinNoiseGen = new NoiseGen(
                FastNoise.NoiseType.PerlinFractal,
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
        /* ======================== Import cavern variables from the config ======================== */
        // Altitude at which caverns start closing off
        int perlinCavernTransitionBoundary = topY - 7;

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
            if (realY >= perlinCavernTransitionBoundary)
                noiseThreshold *= Math.max((float) (realY - topY) / (perlinCavernTransitionBoundary - topY), .5f);

            // Force close-off caverns if we're in ease-in depth range
            if (realY >= minSurfaceHeight - 5)
                noiseThreshold *= (float) (realY - topY) / (minSurfaceHeight - 5 - topY);

            // Mark block for removal if the noise passes the threshold check
            if (perlinNoise < noiseThreshold)
                digBlock = true;

            // Dig/remove the block if it passed the threshold check, using the visualizer if enabled
            if (Configuration.debugsettings.debugVisualizer)
                visualizeDigBlock(digBlock, Blocks.LAPIS_BLOCK.getDefaultState(), primer, localX, realY, localZ);
            else if (digBlock)
                BetterCaveUtil.digBlock(this.getWorld(), primer, Blocks.WATER.getDefaultState(), localX, realY, localZ, chunkX, chunkZ);

        }
    }
}
