package com.yungnickyoung.minecraft.bettercaves.world.cave;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCaveUtil;
import com.yungnickyoung.minecraft.bettercaves.util.FastNoise;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import javax.vecmath.Vector3f;
import java.util.Random;

/**
 * Generates large cavernous caves of uniform size, i.e. not depending on depth
 */
public class ValueFractalCave extends BetterCave {
    public ValueFractalCave(World world) {
        super(world);

        noiseGenerator1.SetNoiseType(FastNoise.NoiseType.ValueFractal);
        noiseGenerator1.SetFractalOctaves(Configuration.caveSettings.valueFractalCave.fractalOctaves);
        noiseGenerator1.SetFractalGain(Configuration.caveSettings.valueFractalCave.fractalGain);
        noiseGenerator1.SetFrequency(Configuration.caveSettings.valueFractalCave.fractalFrequency);

        noiseGenerator2.SetNoiseType(FastNoise.NoiseType.ValueFractal);
        noiseGenerator2.SetFractalOctaves(Configuration.caveSettings.valueFractalCave.fractalOctaves);
        noiseGenerator2.SetFractalGain(Configuration.caveSettings.valueFractalCave.fractalGain);
        noiseGenerator2.SetFrequency(Configuration.caveSettings.valueFractalCave.fractalFrequency);

        turbulence.SetFractalOctaves(Configuration.caveSettings.valueFractalCave.turbulenceOctaves);
        turbulence.SetFractalGain(Configuration.caveSettings.valueFractalCave.turbulenceGain);
        turbulence.SetFrequency(Configuration.caveSettings.valueFractalCave.turbulenceFrequency);


        noiseGenerator3.SetFractalType(FastNoise.FractalType.RigidMulti);
        noiseGenerator3.SetSeed((int)(world.getSeed()) + 2);
        noiseGenerator3.SetNoiseType(FastNoise.NoiseType.ValueFractal);
        noiseGenerator3.SetFractalOctaves(Configuration.caveSettings.valueFractalCave.fractalOctaves);
        noiseGenerator3.SetFractalGain(Configuration.caveSettings.valueFractalCave.fractalGain);
        noiseGenerator3.SetFrequency(Configuration.caveSettings.valueFractalCave.fractalFrequency);

        noiseGenerator4.SetFractalType(FastNoise.FractalType.RigidMulti);
        noiseGenerator4.SetSeed((int)(world.getSeed()) + 3);
        noiseGenerator4.SetNoiseType(FastNoise.NoiseType.ValueFractal);
        noiseGenerator4.SetFractalOctaves(Configuration.caveSettings.valueFractalCave.fractalOctaves);
        noiseGenerator4.SetFractalGain(Configuration.caveSettings.valueFractalCave.fractalGain);
        noiseGenerator4.SetFrequency(Configuration.caveSettings.valueFractalCave.fractalFrequency);

        noiseGenerator5.SetFractalType(FastNoise.FractalType.RigidMulti);
        noiseGenerator5.SetSeed((int)(world.getSeed()) + 4);
        noiseGenerator5.SetNoiseType(FastNoise.NoiseType.ValueFractal);
        noiseGenerator5.SetFractalOctaves(Configuration.caveSettings.valueFractalCave.fractalOctaves);
        noiseGenerator5.SetFractalGain(Configuration.caveSettings.valueFractalCave.fractalGain);
        noiseGenerator5.SetFrequency(Configuration.caveSettings.valueFractalCave.fractalFrequency);

        r = new Random(world.getSeed());
    }

    private FastNoise noiseGenerator3 = new FastNoise();
    private FastNoise noiseGenerator4 = new FastNoise();
    private FastNoise noiseGenerator5 = new FastNoise();

    private Random r;

    private NoiseTriple[][][] createNoise(int chunkX, int chunkZ, int maxHeight) {
        NoiseTriple[][][] noises = new NoiseTriple[16][maxHeight][16];

        for (int localX = 0; localX < 16; localX++) {
            int realX = localX + 16 * chunkX;

            for (int localZ = 0; localZ < 16; localZ++) {
                int realZ = localZ + 16 * chunkZ;

                for (int realY = maxHeight - 1; realY >= 0; realY--) {
                    Vector3f f = new Vector3f(realX, realY, realZ);

                    // Use turbulence function to apply gradient perturbation, if enabled
                    if (Configuration.caveSettings.valueFractalCave.enableTurbulence)
                        turbulence.GradientPerturbFractal(f);

                    float noise1 = noiseGenerator1.GetNoise(f.x, f.y, f.z);
                    float noise2 = noiseGenerator2.GetNoise(f.x, f.y, f.z);
                    float noise3 = noiseGenerator3.GetNoise(f.x, f.y, f.z);
                    float noise4 = noiseGenerator4.GetNoise(f.x, f.y, f.z);
                    float noise5 = noiseGenerator5.GetNoise(f.x, f.y, f.z);

                    NoiseTriple aboveTriple, twoAboveTriple;

                    if (realY < maxHeight - 1) {
                        aboveTriple = noises[localX][realY + 1][localZ];
                        if (noise1 * noise2 * noise3 * noise4 * noise5 > aboveTriple.n1 * aboveTriple.n2 * aboveTriple.n3 * aboveTriple.n4 * aboveTriple.n5) {
                            aboveTriple.n1 = (.2f * aboveTriple.n1) + (.8f * noise1);
                            aboveTriple.n2 = (.2f * aboveTriple.n2) + (.8f * noise2);
                            aboveTriple.n3 = (.2f * aboveTriple.n3) + (.8f * noise3);
                            aboveTriple.n4 = (.2f * aboveTriple.n4) + (.8f * noise4);
                            aboveTriple.n5 = (.2f * aboveTriple.n5) + (.8f * noise5);
                        }
                    }

                    if (realY < maxHeight - 2) {
                        twoAboveTriple = noises[localX][realY + 2][localZ];
                        if (noise1 * noise2 * noise3 * noise4 * noise5 > twoAboveTriple.n1 * twoAboveTriple.n2 * twoAboveTriple.n3 * twoAboveTriple.n4 * twoAboveTriple.n5) {
                            twoAboveTriple.n1 = (.65f * twoAboveTriple.n1) + (.35f * noise1);
                            twoAboveTriple.n2 = (.65f * twoAboveTriple.n2) + (.35f * noise2);
                            twoAboveTriple.n3 = (.65f * twoAboveTriple.n3) + (.35f * noise3);
                            twoAboveTriple.n4 = (.65f * twoAboveTriple.n4) + (.35f * noise4);
                            twoAboveTriple.n5 = (.65f * twoAboveTriple.n5) + (.35f * noise5);
                        }
                    }

                    noises[localX][realY][localZ] = new NoiseTriple(noise1, noise2, noise3, noise4, noise5); // note that y indices are from 0 to maxSurfaceHeight - 1
                }
            }
        }
        return noises;
    }

    @Override
    public void generate(int chunkX, int chunkZ, ChunkPrimer primer) {
        if (Settings.DEBUG_WORLD_GEN) {
            debugGenerate(chunkX, chunkZ, primer);
            return;
        }

        NoiseTriple[][][] noises = createNoise(chunkX, chunkZ, 64);

        float adjustment = (r.nextFloat() % .05f) - .025f;
        float adjustedThreshold = Configuration.caveSettings.valueFractalCave.noiseThreshold + adjustment;

        for (int localX = 0; localX < 16; localX++) {
            int realX = localX + 16*chunkX;

            for (int localZ = 0; localZ < 16; localZ++) {
                int realZ = localZ + 16*chunkZ;

                for (int realY = 64; realY > 0; realY--) {
                    float noise1 = noises[localX][realY - 1][localZ].n1;
                    float noise2 = noises[localX][realY - 1][localZ].n2;
                    float noise3 = noises[localX][realY - 1][localZ].n3;
                    float noise4 = noises[localX][realY - 1][localZ].n4;
                    float noise5 = noises[localX][realY - 1][localZ].n5;

                    int state1 = (noise1 > adjustedThreshold) ? 1 : 0;
                    int state2 = (noise2 > adjustedThreshold) ? 1 : 0;
                    int state3 = (noise3 > adjustedThreshold) ? 1 : 0;
                    int state4 = (noise4 > adjustedThreshold) ? 1 : 0;
                    int state5 = (noise5 > adjustedThreshold) ? 1 : 0;

                    int state = state1 * state2 * state3 * state4 * state5;

                    if (state == 1) {
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

                        BetterCaveUtil.digBlock(world, primer, localX, realY, localZ, chunkX, chunkZ);
                    }

                    /*
                    if (Settings.DEBUG_LOG_ENABLED) {
                        avgNoise = ((numChunksGenerated * avgNoise) + noise) / (numChunksGenerated + 1);

                        if (noise > maxNoise) maxNoise = noise;
                        if (noise < minNoise) minNoise = noise;

                        numChunksGenerated++;

                        if (numChunksGenerated == CHUNKS_PER_REPORT) {
                            Settings.LOGGER.info(CHUNKS_PER_REPORT + " Chunks Generated Report");

                            Settings.LOGGER.info("--> Noise");
                            Settings.LOGGER.info("  > Average: {}", avgNoise);
                            Settings.LOGGER.info("  > Max: {}", maxNoise);
                            Settings.LOGGER.info("  > Min: {}", minNoise);

                            // Reset vals
                            numChunksGenerated = 0;

                            avgNoise = 0;
                            maxNoise = -10;
                            minNoise = 10;
                        }
                    }*/
                }
            }
        }
    }

    private static class NoiseTriple {
        public float n1, n2, n3, n4, n5;
        public NoiseTriple(float n1, float n2, float n3, float n4, float n5) {
            this.n1 = n1;
            this.n2 = n2;
            this.n3 = n3;
            this.n4 = n4;
            this.n5 = n5;
        }
    }

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

                        int state1 = (noise1 > Configuration.caveSettings.valueFractalCave.noiseThreshold) ? 1 : 0;
                        int state2 = (noise2 > Configuration.caveSettings.valueFractalCave.noiseThreshold) ? 1 : 0;
                        int state3 = (noise3 > Configuration.caveSettings.valueFractalCave.noiseThreshold) ? 1 : 0;

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
    }
}
