package com.yungnickyoung.minecraft.bettercaves.world.cave;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseGen;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseTuple;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCaveUtil;
import com.yungnickyoung.minecraft.bettercaves.util.FastNoise;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.List;
import java.util.Map;

public class OGSimplexCave {
    private World world;
    private long seed;
    private NoiseGen simplexNoiseGen;

    public OGSimplexCave(World world) {
        this.world = world;
        this.seed = world.getSeed();

        simplexNoiseGen = new NoiseGen(
                FastNoise.NoiseType.SimplexFractal,
                world,
                Configuration.caveSettings.simplexFractalCave.fractalOctaves,
                Configuration.caveSettings.simplexFractalCave.fractalGain,
                Configuration.caveSettings.simplexFractalCave.fractalFrequency,
                Configuration.caveSettings.simplexFractalCave.turbulenceOctaves,
                Configuration.caveSettings.simplexFractalCave.turbulenceGain,
                Configuration.caveSettings.simplexFractalCave.turbulenceFrequency,
                Configuration.caveSettings.simplexFractalCave.enableTurbulence,
                Configuration.caveSettings.simplexFractalCave.yCompression,
                Configuration.caveSettings.simplexFractalCave.xzCompression
        );
    }

    public void generateColumn(int chunkX, int chunkZ, ChunkPrimer primer, int localX, int localZ, int bottomY,
                               int topY, int maxSurfaceHeight, int minSurfaceHeight) {
        /* ========================= Import cave variables from the config ========================= */
        // Altitude at which caves start closing off so they aren't all open to the surface
        int simplexCaveTransitionBoundary = maxSurfaceHeight - 20;

        // Number of noise values to calculate. Their intersection will be taken to determine a single noise value
        int simplexNumGens = Configuration.caveSettings.simplexFractalCave.numGenerators;

        /* ========== Generate noise for caves (using Simplex noise) and caverns (using Perlin noise) ========== */
        // The noise for an individual block is represented by a NoiseTuple, which is essentially an n-tuple of
        // floats, where n is equal to the number of generators passed to the function (perlinNumGens or simplexNumGens)
        Map<Integer, NoiseTuple> simplexNoises =
                simplexNoiseGen.generateNoiseCol(chunkX, chunkZ, bottomY, topY, simplexNumGens, localX, localZ);

        // Do some pre-processing on the simplex noises to facilitate better cave generation.
        // See the javadoc for the function for more info.
        preprocessCaveNoiseCol(simplexNoises, topY, bottomY, simplexNumGens);

        /* =============== Dig out caves and caverns in this chunk, based on noise values =============== */
        for (int realY = topY; realY >= bottomY; realY--) {
            List<Float> simplexNoiseBlock;
            boolean digBlock = false;

            // Process simplex noise
            // Compute a single noise value to represent all the noise values in the NoiseTuple
            float simplexNoise = 0;
            simplexNoiseBlock = simplexNoises.get(realY).getNoiseValues();
            for (float noise : simplexNoiseBlock)
                simplexNoise += noise;

            simplexNoise /= simplexNoiseBlock.size();

            // Close off caves if we're in ease-in depth range
            float noiseThreshold = Configuration.caveSettings.simplexFractalCave.noiseThreshold;
            if (realY >= simplexCaveTransitionBoundary)
                noiseThreshold *= (1 + .3f * ((float)(realY - simplexCaveTransitionBoundary) / (topY - simplexCaveTransitionBoundary)));

            // Mark block for removal if the noise passes the threshold check
            if (simplexNoise > noiseThreshold)
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

                BetterCaveUtil.digBlock(world, primer, localX, realY, localZ, chunkX, chunkZ);
            }
        }

        /* ============ Post-Processing to remove any singular floating blocks in the ease-in range ============ */
        IBlockState BlockStateAir = Blocks.AIR.getDefaultState();
        for (int realY = simplexCaveTransitionBoundary + 1; realY < topY - 1; realY++) {
            IBlockState currBlock = primer.getBlockState(localX, realY, localZ);

            if (BetterCaveUtil.canReplaceBlock(currBlock, BlockStateAir)
                    && primer.getBlockState(localX, realY + 1, localZ) == BlockStateAir
                    && primer.getBlockState(localX, realY - 1, localZ) == BlockStateAir
            )
                BetterCaveUtil.digBlock(world, primer, localX, realY, localZ, chunkX, chunkZ);
        }
    }

    private Map<Integer, NoiseTuple> preprocessCaveNoiseCol(Map<Integer, NoiseTuple> noises, int topY, int bottomY, int numGens) {
        /* Adjust simplex noise values based on blocks above in order to give the player more headroom */
        for (int realY = topY; realY >= bottomY; realY--) {
            NoiseTuple sBlockNoise = noises.get(realY);
            float avgSNoise = 0;

            for (float noise : sBlockNoise.getNoiseValues())
                avgSNoise += noise;

            avgSNoise /= sBlockNoise.size();

            if (avgSNoise > Configuration.caveSettings.simplexFractalCave.noiseThreshold) {
                /* Adjust noise values of blocks above to give the player more head room */
                if (realY < topY) {
                    NoiseTuple tupleAbove = noises.get(realY + 1);
                    for (int i = 0; i < numGens; i++)
                        tupleAbove.set(i, (.1f * tupleAbove.get(i)) + (.9f * sBlockNoise.get(i)));
                }

                if (realY < topY - 1) {
                    NoiseTuple tupleTwoAbove = noises.get(realY + 2);
                    for (int i = 0; i < numGens; i++)
                        tupleTwoAbove.set(i, (.15f * tupleTwoAbove.get(i)) + (.85f * sBlockNoise.get(i)));
                }
            }
        }
        return noises;
    }
}