package com.yungnickyoung.minecraft.bettercaves.world.cave;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCaveUtil;
import com.yungnickyoung.minecraft.bettercaves.util.FastNoise;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import javax.vecmath.Vector3f;

/**
 * Generates large cavernous caves of dynamic size, meaning the caves get larger the lower the y-coordinate.
 */
public class ReverseDynamicCavern extends BetterCave {
    public ReverseDynamicCavern(World world) {
        super(world);

        noiseGenerator1.SetNoiseType(FastNoise.NoiseType.PerlinFractal);
        noiseGenerator1.SetFractalOctaves(Configuration.cavern.fractalOctaves);
        noiseGenerator1.SetFractalGain(Configuration.cavern.fractalGain);
        noiseGenerator1.SetFrequency(Configuration.cavern.fractalFrequency);

        noiseGenerator2.SetNoiseType(FastNoise.NoiseType.PerlinFractal);
        noiseGenerator2.SetFractalOctaves(Configuration.cavern.fractalOctaves);
        noiseGenerator2.SetFractalGain(Configuration.cavern.fractalGain);
        noiseGenerator2.SetFrequency(Configuration.cavern.fractalFrequency);

        turbulence.SetFractalOctaves(Configuration.cavern.turbulenceOctaves);
        turbulence.SetFractalGain(Configuration.cavern.turbulenceGain);
        turbulence.SetFrequency(Configuration.cavern.turbulenceFrequency);
    }

    @Override
    public void generate(int chunkX, int chunkZ, ChunkPrimer primer) {
        if (Settings.DEBUG_WORLD_GEN) {
            debugGenerate(chunkX, chunkZ, primer);
            return;
        }

        for (int localX = 0; localX < 16; localX++) {
            int realX = localX + 16*chunkX;

            for (int localZ = 0; localZ < 16; localZ++) {
                int realZ = localZ + 16*chunkZ;

                for (int realY = 64; realY > 0; realY--) {
                    Vector3f f = new Vector3f(realX, realY, realZ);

                    // Use turbulence function to apply gradient perturbation, if enabled
                    if (Configuration.cavern.enableTurbulence)
                        turbulence.GradientPerturbFractal(f);

                    float noise1 = noiseGenerator1.GetNoise(f.x, f.y, f.z);
                    float noise2 = noiseGenerator2.GetNoise(f.x, f.y, f.z);

                    /*
                     * Multiply noise to get intersection of the two multi-fractals.
                     * This is necessary when operating in three dimensions. A single multi-fractal would yield
                     * shell-like slices carved out of the terrain, rather than tunnels.
                     */
                    float noise = noise1 * noise2;
                    float heightAdjustedThreshold;

//                    if (realY < 32)
//                        heightAdjustedThreshold = Configuration.cavern.noiseThreshold;
//                    else {
//                        float heightAdjuster = realY * (1f / 64f);
//                        heightAdjustedThreshold = Configuration.cavern.noiseThreshold - (Configuration.dynamicCavern.depthPower * heightAdjuster);
//                    }

                    // The following is kinda decent with a depthPower of .4
                    float heightAdjuster = realY * (1f / 64f);
                    heightAdjustedThreshold = Configuration.cavern.noiseThreshold - (Configuration.dynamicCavern.depthPower * heightAdjuster);


                    if (noise < heightAdjustedThreshold) {
                        IBlockState currentBlockState = primer.getBlockState(localX, realY, localZ);
                        IBlockState aboveBlockState = primer.getBlockState(localX, realY + 1, localZ);
                        boolean foundTopBlock = BetterCaveUtil.isTopBlock(world, primer, localX, realY, localZ, chunkX, chunkZ);
                        BetterCaveUtil.digBlock(world, primer, localX, realY, localZ, chunkX, chunkZ, foundTopBlock, currentBlockState, aboveBlockState);
                    }

                    /*
                    float noise = noise1 * noise2;
                    float heightAdjustedThreshold;

                    if (realY < 32) {
                        heightAdjustedThreshold = Configuration.cavern.noiseThreshold;
                        if (noise < heightAdjustedThreshold) {
                            IBlockState currentBlockState = primer.getBlockState(localX, realY, localZ);
                            IBlockState aboveBlockState = primer.getBlockState(localX, realY + 1, localZ);
                            boolean foundTopBlock = BetterCaveUtil.isTopBlock(world, primer, localX, realY, localZ, chunkX, chunkZ);
                            BetterCaveUtil.digBlock(world, primer, localX, realY, localZ, chunkX, chunkZ, foundTopBlock, currentBlockState, aboveBlockState);
                        }
                    } else {
                        float heightAdjuster = realY * (1f / 64f);
                        heightAdjustedThreshold = Configuration.cavern.noiseThreshold + (Configuration.dynamicCavern.depthPower * heightAdjuster);
                        if (noise > heightAdjustedThreshold) {
                            IBlockState currentBlockState = primer.getBlockState(localX, realY, localZ);
                            IBlockState aboveBlockState = primer.getBlockState(localX, realY + 1, localZ);
                            boolean foundTopBlock = BetterCaveUtil.isTopBlock(world, primer, localX, realY, localZ, chunkX, chunkZ);
                            BetterCaveUtil.digBlock(world, primer, localX, realY, localZ, chunkX, chunkZ, foundTopBlock, currentBlockState, aboveBlockState);
                        }
                    }*/

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
                    }
                }
            }
        }
    }

    private void debugGenerate(int chunkX, int chunkZ, ChunkPrimer primer) {
        for (int localX = 0; localX < 16; localX++) {
            int realX = localX + 16*chunkX;

            for (int localZ = 0; localZ < 16; localZ++) {
                int realZ = localZ + 16*chunkZ;

                for (int realY = 128; realY > 0; realY--) {
                    if (realX < 0) {
                        primer.setBlockState(localX, realY, localZ, Blocks.AIR.getDefaultState());
                    } else {
                        Vector3f f = new Vector3f(realX, realY, realZ);

                        // Use turbulence function to apply gradient perturbation, if enabled
                        if (Configuration.cavern.enableTurbulence)
                            turbulence.GradientPerturbFractal(f);

                        float noise1 = noiseGenerator1.GetNoise(f.x, f.y, f.z);
                        float noise2 = noiseGenerator2.GetNoise(f.x, f.y, f.z);

                        /*
                         * Multiply noise to get intersection of the two multi-fractals.
                         * This is necessary when operating in three dimensions. A single multi-fractal would yield
                         * shell-like slices carved out of the terrain, rather than tunnels.
                         */
                        float noise = noise1 * noise2;

                        float heightAdjustedThreshold;
                        if (realY < 32)
                            heightAdjustedThreshold = Configuration.cavern.noiseThreshold;
                        else {
                            float heightAdjuster = realY * (1f / 64f);
                            heightAdjustedThreshold = Configuration.cavern.noiseThreshold - (Configuration.dynamicCavern.depthPower * heightAdjuster);
                        }

                        if (noise < heightAdjustedThreshold) {
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
