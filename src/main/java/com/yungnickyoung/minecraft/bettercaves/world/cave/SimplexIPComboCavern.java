package com.yungnickyoung.minecraft.bettercaves.world.cave;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseTuple;
import com.yungnickyoung.minecraft.bettercaves.noise.PerlinNoiseGen;
import com.yungnickyoung.minecraft.bettercaves.noise.SimplexNoiseGen;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCaveUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.List;

public class SimplexIPComboCavern extends BetterCave {
    private SimplexNoiseGen simplexNoiseGen;
    private PerlinNoiseGen perlinNoiseGen;

    public SimplexIPComboCavern(World world) {
        super(world);
        simplexNoiseGen = new SimplexNoiseGen(
                world,
                Configuration.simplexFractalCave.fractalOctaves,
                Configuration.simplexFractalCave.fractalGain,
                Configuration.simplexFractalCave.fractalFrequency,
                Configuration.simplexFractalCave.turbulenceOctaves,
                Configuration.simplexFractalCave.turbulenceGain,
                Configuration.simplexFractalCave.turbulenceFrequency,
                Configuration.simplexFractalCave.enableTurbulence,
                Configuration.simplexFractalCave.enableSmoothing
        );

        perlinNoiseGen = new PerlinNoiseGen(
                world,
                Configuration.invertedPerlinCavern.fractalOctaves,
                Configuration.invertedPerlinCavern.fractalGain,
                Configuration.invertedPerlinCavern.fractalFrequency,
                Configuration.invertedPerlinCavern.turbulenceOctaves,
                Configuration.invertedPerlinCavern.turbulenceGain,
                Configuration.invertedPerlinCavern.turbulenceFrequency,
                Configuration.invertedPerlinCavern.enableTurbulence,
                Configuration.invertedPerlinCavern.enableSmoothing
        );
    }

    @Override
    public void generate(int chunkX, int chunkZ, ChunkPrimer primer) {
//        if (Settings.DEBUG_WORLD_GEN) {
//            debugGenerate(chunkX, chunkZ, primer);
//            return;
//        }

//        int maxHeight = Configuration.simplexFractalCave.maxHeight;
//        int minHeight = Configuration.simplexFractalCave.minHeight;

        int perlinCavernBottom = 1;
        int perlinCavernTransitionBoundary = 25;
        int perlinCavernTop = 35;

        int simplexCaveBottom = 30;
        int simplexCaveTransitionBoundary = 45;
        int simplexCaveTop = 60;

        int perlinNumGens = Configuration.invertedPerlinCavern.numGenerators;
        int simplexNumGens = Configuration.simplexFractalCave.numGenerators;

        List<NoiseTuple[][]> perlinNoises = perlinNoiseGen.generateNoise(chunkX, chunkZ, perlinCavernBottom, simplexCaveTop, perlinNumGens);
        List<NoiseTuple[][]> simplexNoises = simplexNoiseGen.generateNoise(chunkX, chunkZ, perlinCavernBottom, simplexCaveTop, simplexNumGens);

        for (int realY = simplexCaveTop; realY >= perlinCavernBottom; realY--) {
            for (int localX = 0; localX < 16; localX++) {
                for (int localZ = 0; localZ < 16; localZ++) {
                    List<Float> pBlockNoise, sBlockNoise;
                    boolean pdigBlock = true;
                    boolean sdigBlock = true;

                    // Process inverse perlin noise
                    if (realY <= perlinCavernTop) {
                        float pNoise = 1;
                        float noiseThreshold = Configuration.invertedPerlinCavern.noiseThreshold;

                        pBlockNoise = perlinNoises.get(simplexCaveTop - realY)[localX][localZ].getNoiseValues();

                        for (float noise : pBlockNoise)
                            pNoise *= noise;

                        // Adjust threshold if we're in the transition range
                        if (realY >= perlinCavernTransitionBoundary)
                            noiseThreshold *= Math.max((float)(realY - perlinCavernTop) / (perlinCavernTransitionBoundary - perlinCavernTop), .7f);

                        if (pNoise > noiseThreshold)
                            pdigBlock = false;
                    }

                    // Process simplex noise
                    if (realY >= simplexCaveBottom) {
                        float sNoise = 0;
                        sBlockNoise= simplexNoises.get(simplexCaveTop - realY)[localX][localZ].getNoiseValues();

                        for (float noise : sBlockNoise)
                            sNoise += noise;

                        sNoise /= sBlockNoise.size();

                        if (sNoise < Configuration.simplexFractalCave.noiseThreshold)
                            sdigBlock = false;
                    }

                    if (pdigBlock && sdigBlock) {
                        IBlockState blockState = primer.getBlockState(localX, realY, localZ);
                        IBlockState blockStateAbove = primer.getBlockState(localX, realY + 1, localZ);
                        boolean foundTopBlock = BetterCaveUtil.isTopBlock(world, primer, localX, realY, localZ, chunkX, chunkZ);

                        if (blockStateAbove.getMaterial() == Material.WATER)
                            continue;
                        if (localX < 15 && primer.getBlockState(localX + 1, realY, localZ).getMaterial() == Material.WATER)
                            continue;
                        if (localX > 0 && primer.getBlockState(localX - 1, realY, localZ).getMaterial() == Material.WATER)
                            continue;
                        if (localZ < 15 && primer.getBlockState(localX, realY, localZ + 1).getMaterial() == Material.WATER)
                            continue;
                        if (localZ > 0 && primer.getBlockState(localX, realY, localZ - 1).getMaterial() == Material.WATER)
                            continue;

                        boolean lava = true;

                        BetterCaveUtil.digBlock(world, primer, localX, realY, localZ, chunkX, chunkZ, foundTopBlock, blockState, blockStateAbove, lava);
                    }
                }
            }
        }
    }

    /*
    private void debugGenerate(int chunkX, int chunkZ, ChunkPrimer primer) {
        NoiseTriple[][][] noises = createNoise(chunkX, chunkZ, 128);

        for (int localX = 0; localX < 16; localX++) {
            int realX = localX + 16*chunkX;

            for (int localZ = 0; localZ < 16; localZ++) {

                for (int realY = 128; realY > 0; realY--) {
                    if (realX < 0) {
                        primer.setBlockState(localX, realY, localZ, Blocks.AIR.getDefaultState());
                    } else {
                        float noise1 = noises[localX][realY - 1][localZ].n1;
                        float noise2 = noises[localX][realY - 1][localZ].n2;
                        float noise3 = noises[localX][realY - 1][localZ].n3;

                        int state1 = (noise1 > Configuration.simplexFractalCave.noiseThreshold) ? 1 : 0;
                        int state2 = (noise2 > Configuration.simplexFractalCave.noiseThreshold) ? 1 : 0;
                        int state3 = (noise3 > Configuration.simplexFractalCave.noiseThreshold) ? 1 : 0;

                        int state = state1 * state2 * state3;

                        if (state == 1) {
                            primer.setBlockState(localX, realY, localZ, Blocks.QUARTZ_BLOCK.getDefaultState());
                        } else {
                            primer.setBlockState(localX, realY, localZ, Blocks.AIR.getDefaultState());
                        }
                    }
                }
            }
        }
    }*/
}
