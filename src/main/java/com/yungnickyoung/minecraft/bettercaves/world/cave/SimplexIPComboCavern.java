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

import javax.vecmath.Vector3f;
import java.util.List;
import java.util.Random;

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

        int maxSurfaceHeight = BetterCaveUtil.getMaxSurfaceHeight(primer);
        int minSurfaceHeight = BetterCaveUtil.getMinSurfaceHeight(primer);

        int simplexCaveBottom = 20;
//        int simplexCaveTop = minSurfaceHeight + ((maxSurfaceHeight - minSurfaceHeight) / 2);
        Random r = new Random((long) (chunkX + chunkZ));
        int simplexCaveTop = minSurfaceHeight + ((maxSurfaceHeight - minSurfaceHeight) / 4);

        int perlinCavernBottom = 1;
        int perlinCavernTransitionBoundary = 23;
        int perlinCavernTop = 30;

        int easeInDepth = minSurfaceHeight - 5;

        int perlinNumGens = Configuration.invertedPerlinCavern.numGenerators;
        int simplexNumGens = Configuration.simplexFractalCave.numGenerators;

        List<NoiseTuple[][]> perlinNoises = perlinNoiseGen.generateNoise(chunkX, chunkZ, perlinCavernBottom, simplexCaveTop, perlinNumGens);
        List<NoiseTuple[][]> simplexNoises = simplexNoiseGen.generateNoise(chunkX, chunkZ, perlinCavernBottom, simplexCaveTop, simplexNumGens);


        /* Adjust simplex noise values based on horizontal neighbors. This might help prevent cave fracturing.*/
        for (int realY = simplexCaveTop; realY >= perlinCavernBottom; realY--) {
            for (int localX = 0; localX < 16; localX++) {
                for (int localZ = 0; localZ < 16; localZ++) {
                    NoiseTuple sBlockNoise = simplexNoises.get(simplexCaveTop - realY)[localX][localZ];
                    float avgSNoise = 0;

                    for (float noise : sBlockNoise.getNoiseValues())
                        avgSNoise += noise;

                    avgSNoise /= sBlockNoise.size();

                    if (avgSNoise > Configuration.simplexFractalCave.noiseThreshold) {
                        /* Adjust noise values of blocks above to give the player more head room */
                        if (realY < simplexCaveTop) {
                            NoiseTuple tupleAbove = simplexNoises.get(simplexCaveTop - realY - 1)[localX][localZ];
                            for (int i = 0; i < simplexNumGens; i++)
                                tupleAbove.set(i, (.1f * tupleAbove.get(i)) + (.9f * sBlockNoise.get(i)));
                        }

                        if (realY < simplexCaveTop - 1) {
                            NoiseTuple tupleTwoAbove = simplexNoises.get(simplexCaveTop - realY - 2)[localX][localZ];
                            for (int i = 0; i < simplexNumGens; i++)
                                tupleTwoAbove.set(i, (.15f * tupleTwoAbove.get(i)) + (.85f * sBlockNoise.get(i)));
                        }

//                        /* Adjust noise values based on horizontal neighbors. This might help prevent cave fracturing.*/
//                        if (localX > 0) {
//                            NoiseTuple neighbor = simplexNoises.get(simplexCaveTop - realY)[localX - 1][localZ];
//                            for (int i = 0; i < simplexNumGens; i++)
//                                neighbor.set(i, (neighbor.get(i) * .75f) + (sBlockNoise.get(i) * .25f));
//                        }
//
//                        if (localZ > 0) {
//                            NoiseTuple neighbor = simplexNoises.get(simplexCaveTop - realY)[localX][localZ - 1];
//                            for (int i = 0; i < simplexNumGens; i++)
//                                neighbor.set(i, (neighbor.get(i) * .75f) + (sBlockNoise.get(i) * .25f));
//                        }
                    }
                }
            }
        }



        for (int realY = simplexCaveTop; realY >= perlinCavernBottom; realY--) {
            for (int localX = 0; localX < 16; localX++) {
                for (int localZ = 0; localZ < 16; localZ++) {
                    List<Float> pBlockNoise, sBlockNoise;
                    boolean digBlock = false;

                    // Process inverse perlin noise
                    if (realY <= perlinCavernTop) {
                        float pNoise = 1;
                        float noiseThreshold = Configuration.invertedPerlinCavern.noiseThreshold;

                        pBlockNoise = perlinNoises.get(simplexCaveTop - realY)[localX][localZ].getNoiseValues();

                        for (float noise : pBlockNoise)
                            pNoise *= noise;

                        if (realY >= perlinCavernTransitionBoundary) {
                            // Adjust threshold if we're in the transition range to provide smoother transition into simplex caves
                            noiseThreshold *= Math.max((float) (realY - perlinCavernTop) / (perlinCavernTransitionBoundary - perlinCavernTop), .5f);
                        }

                        if (realY >= easeInDepth) {
                            // Close off caverns if we're in ease-in depth range (probably means this is under the ocean)
                            noiseThreshold *= (float) (realY - perlinCavernTop) / (easeInDepth - perlinCavernTop);
                        }

                        if (pNoise < noiseThreshold)
                            digBlock = true;
                    }

                    // Process simplex noise
                    if (realY >= simplexCaveBottom && !digBlock) {
                        float sNoise = 0;
                        float noiseThreshold = Configuration.simplexFractalCave.noiseThreshold;

                        sBlockNoise= simplexNoises.get(simplexCaveTop - realY)[localX][localZ].getNoiseValues();

                        for (float noise : sBlockNoise)
                            sNoise += noise;

                        sNoise /= sBlockNoise.size();

                        if (realY >= easeInDepth) {
                            // Close off caves if we're in ease-in depth range
//                            noiseThreshold *= (1 + Math.max((float) (simplexCaveTop - realY) / (simplexCaveTop- easeInDepth), .5f));
                            noiseThreshold *= (1 + .15f * ((float)(realY - easeInDepth) / (simplexCaveTop - easeInDepth)));
                        }

                        if (sNoise > noiseThreshold)
                            digBlock = true;
                    }

                    if (digBlock) {
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
