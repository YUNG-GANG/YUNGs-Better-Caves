package com.yungnickyoung.minecraft.bettercaves.world.cave;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCaveUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import javax.vecmath.Vector3f;
import java.util.Random;

/**
 * Generates large cavernous caves of uniform size, i.e. not depending on depth
 */
public class ReverseCavern extends BetterCave {

    private Random r;

    public ReverseCavern(World world) {
        super(world);

        noiseGenerator1.SetFractalOctaves(Configuration.cavern.fractalOctaves);
        noiseGenerator1.SetFractalGain(Configuration.cavern.fractalGain);
        noiseGenerator1.SetFrequency(Configuration.cavern.fractalFrequency);

        noiseGenerator2.SetFractalOctaves(Configuration.cavern.fractalOctaves);
        noiseGenerator2.SetFractalGain(Configuration.cavern.fractalGain);
        noiseGenerator2.SetFrequency(Configuration.cavern.fractalFrequency);

        turbulence.SetFractalOctaves(Configuration.cavern.turbulenceOctaves);
        turbulence.SetFractalGain(Configuration.cavern.turbulenceGain);
        turbulence.SetFrequency(Configuration.cavern.turbulenceFrequency);

        r = new Random(world.getSeed());
    }

    @Override
    public void generate(int chunkX, int chunkZ, ChunkPrimer primer) {
        if (Settings.DEBUG_WORLD_GEN) {
            debugGenerate(chunkX, chunkZ, primer);
            return;
        }

        int maxSurfaceHeight = BetterCaveUtil.getMaxSurfaceHeight(primer);
        int minSurfaceHeight = BetterCaveUtil.getMinSurfaceHeight(primer);

        int maxGenHeight = maxSurfaceHeight - ((maxSurfaceHeight - minSurfaceHeight) / 2);

//        int easeInDepth = (int)(maxSurfaceHeight * .75); // begin closing off caves when x% down from the surface
        int easeInDepth = (int)(minSurfaceHeight * .85); // begin closing off caves when x% down from the surface

        net.minecraft.world.biome.Biome biome = world.getBiome(new BlockPos(7 + chunkX*16, 60, 7 + chunkZ*16));
//        Settings.LOGGER.info(biome.getBiomeName() + " " + maxSurfaceHeight + " " + minSurfaceHeight + " " + easeInDepth);

        int rand = r.nextInt(10);

        /* CALCULATE INITIAL NOISE VALUES */
        float[][][] noises = new float[16][128][16];

        for (int localX = 0; localX < 16; localX++) {
            int realX = localX + 16 * chunkX;

            for (int localZ = 0; localZ < 16; localZ++) {
                int realZ = localZ + 16 * chunkZ;

                for (int realY = 128; realY > 0; realY--) {
                    Vector3f f = new Vector3f(realX, realY, realZ);

                    // Use turbulence function to apply gradient perturbation, if enabled
                    if (Configuration.cavern.enableTurbulence)
                        turbulence.GradientPerturbFractal(f);

                    float noise1 = noiseGenerator1.GetNoise(f.x, f.y, f.z);
                    float noise2 = noiseGenerator2.GetNoise(f.x, f.y, f.z);
                    float noise = noise1 * noise2;  /* Multiply noise to get intersection of the two multi-fractals.
                                                     * This is necessary when operating in three dimensions. A single multi-fractal would yield
                                                     * shell-like slices carved out of the terrain, rather than tunnels. */

                    if (realY >= 60) {
                        float adjustment = (float)Math.min((float)(realY - 60) / (float)(maxGenHeight - 60), 1.0) * .6f;
//                        float adjustment = (float)Math.min(2 * (float)(realY - easeInDepth) / (float)(maxGenHeight - easeInDepth), 1.0) * .6f;
                        noise += adjustment;
                    }

                    noises[localX][realY - 1][localZ] = noise; // note that y indices are from 0 to maxSurfaceHeight - 1
                }
            }
        }


        for (int localX = 0; localX < 16; localX++) {
            int realX = localX + 16*chunkX;

            for (int localZ = 0; localZ < 16; localZ++) {
                int realZ = localZ + 16*chunkZ;
                boolean changeNeeded = false;


                for (int realY = 128; realY > 0; realY--) {
                    if (!changeNeeded && (rand > 0 || maxSurfaceHeight < 60)) {
                        while (!BetterCaveUtil.canReplaceBlock(primer.getBlockState(localX, realY, localZ), Blocks.AIR.getDefaultState())) {
                            realY--;
                        }
                        realY -= 6;
                        changeNeeded = true;
                    }

                    // Adjust noise threshold when close to the surface to close off caves
                    float adjustedNoiseThreshold = Configuration.cavern.noiseThreshold;
//                    if (realY >= easeInDepth) {
//                        float adjustment = (float)(realY - easeInDepth) / (float)(maxGenHeight - easeInDepth) * .6f;
////                        float adjustment = (float)Math.min(2 * (float)(realY - easeInDepth) / (float)(maxGenHeight - easeInDepth), 1.0) * .6f;
//                        adjustedNoiseThreshold -= adjustment;
//                    }

                    float noise = noises[localX][realY - 1][localZ];

                    if (noise < adjustedNoiseThreshold) {
                        IBlockState blockState = primer.getBlockState(localX, realY, localZ);
                        IBlockState blockStateAbove = primer.getBlockState(localX, realY + 1, localZ);
                        boolean foundTopBlock = BetterCaveUtil.isTopBlock(world, primer, localX, realY, localZ, chunkX, chunkZ);
                        if (blockStateAbove.getMaterial() == Material.WATER)
                            continue;

                        // Check x + 1 position
                        if (localX == 15) {
                            if (world.isChunkGeneratedAt(chunkX + 1, chunkZ))
                                if (world.getBlockState(new BlockPos(realX + 1, realY, realZ)).getMaterial() == Material.WATER)
                                    continue;
                        } else {
                            if (primer.getBlockState(localX + 1, realY, localZ).getMaterial() == Material.WATER)
                                continue;
                        }

                        // Check x - 1 position
                        if (localX == 0) {
                            if (world.isChunkGeneratedAt(chunkX - 1, chunkZ))
                                if (world.getBlockState(new BlockPos(realX - 1, realY, realZ)).getMaterial() == Material.WATER)
                                    continue;
                        } else {
                            if (primer.getBlockState(localX - 1, realY, localZ).getMaterial() == Material.WATER)
                                continue;
                        }

                        // Check z + 1 position
                        if (localZ == 15) {
                            if (world.isChunkGeneratedAt(chunkX, chunkZ + 1))
                                if (world.getBlockState(new BlockPos(realX, realY, realZ + 1)).getMaterial() == Material.WATER)
                                    continue;
                        } else {
                            if (primer.getBlockState(localX, realY, localZ + 1).getMaterial() == Material.WATER)
                                continue;
                        }

                        // Check z - 1 position
                        if (localZ == 0) {
                            if (world.isChunkGeneratedAt(chunkX, chunkZ - 1))
                                if (world.getBlockState(new BlockPos(realX, realY, realZ - 1)).getMaterial() == Material.WATER)
                                    continue;
                        } else {
                            if (primer.getBlockState(localX, realY, localZ - 1).getMaterial() == Material.WATER)
                                continue;
                        }



                        BetterCaveUtil.digBlock(world, primer, localX, realY, localZ, chunkX, chunkZ, foundTopBlock, blockState, blockStateAbove);
                    }




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

                        if (noise < Configuration.cavern.noiseThreshold) {
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
