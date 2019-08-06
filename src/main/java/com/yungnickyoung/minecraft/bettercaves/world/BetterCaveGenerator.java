package com.yungnickyoung.minecraft.bettercaves.world;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.util.FastNoise;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenCaves;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Random;

public class BetterCaveGenerator extends MapGenCaves {

    private FastNoise noiseGenerator1;
    private FastNoise noiseGenerator2;
    private FastNoise turbulenceX;
    private FastNoise turbulenceY;
    private FastNoise turbulenceZ;

    // DEBUG VALS
    private int numChunksGenerated = 0;
    private double avgNoise1 = 0;
    private double avgNoise2 = 0;
    private double maxNoise1 = -10;
    private double minNoise1 = 10;
    private double maxNoise2 = -10;
    private double minNoise2 = 10;


    public BetterCaveGenerator() {
        // Base noise functions - Ridged Multi-fractals
        noiseGenerator1 = new FastNoise();
        noiseGenerator2 = new FastNoise();

        noiseGenerator1.SetNoiseType(FastNoise.NoiseType.PerlinFractal);
        noiseGenerator2.SetNoiseType(FastNoise.NoiseType.PerlinFractal);
        noiseGenerator1.SetFractalType(FastNoise.FractalType.RigidMulti);
        noiseGenerator2.SetFractalType(FastNoise.FractalType.RigidMulti);
        noiseGenerator1.SetFractalOctaves(1);
        noiseGenerator2.SetFractalOctaves(1);
        noiseGenerator1.SetFractalGain(.3f);
        noiseGenerator2.SetFractalGain(.3f);
        noiseGenerator1.SetFrequency(.05f);
        noiseGenerator2.SetFrequency(.05f);

        turbulenceX = new FastNoise();
        turbulenceY = new FastNoise();
        turbulenceZ = new FastNoise();

        turbulenceX.SetNoiseType(FastNoise.NoiseType.PerlinFractal);
        turbulenceY.SetNoiseType(FastNoise.NoiseType.PerlinFractal);
        turbulenceZ.SetNoiseType(FastNoise.NoiseType.PerlinFractal);
        turbulenceX.SetFractalType(FastNoise.FractalType.FBM);
        turbulenceY.SetFractalType(FastNoise.FractalType.FBM);
        turbulenceZ.SetFractalType(FastNoise.FractalType.FBM);
        turbulenceX.SetFractalOctaves(3);
        turbulenceY.SetFractalOctaves(3);
        turbulenceZ.SetFractalOctaves(3);
        turbulenceX.SetFractalGain(.3f);
        turbulenceY.SetFractalGain(.3f);
        turbulenceZ.SetFractalGain(.3f);
        turbulenceX.SetFrequency(.01f);
        turbulenceY.SetFrequency(.01f);
        turbulenceZ.SetFrequency(.01f);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void generate(World worldIn, int chunkX, int chunkZ, ChunkPrimer primer) {
        if (Settings.DEBUG_LOG_ENABLED)
            Settings.LOGGER.info("generate() cave called: {} | {} | {} | {}", worldIn, chunkX, chunkZ, primer);


        if (world == null) {
            world = worldIn;
            int worldSeed = (int)(world.getSeed());
            noiseGenerator1.SetSeed(worldSeed);
            noiseGenerator2.SetSeed(worldSeed + 1);
            turbulenceX.SetSeed(worldSeed+ 1000);
            turbulenceY.SetSeed(worldSeed+ 2000);
            turbulenceZ.SetSeed(worldSeed+ 3000);
        }

//        generateWorleys(chunkX, chunkZ, primer);
        generateFractal(chunkX, chunkZ, primer);
//        generateFractalModelForScreenShots(chunkX, chunkZ, primer);
    }




    private void generateFractal(int chunkX, int chunkZ, ChunkPrimer primer) {
        for (int localX = 0; localX < 16; localX++) {
            int realX = localX + 16*chunkX;

            for (int localZ = 0; localZ < 16; localZ++) {
                int realZ = localZ + 16*chunkZ;

                for (int realY = 64; realY > 0; realY--) {
                    float noise1 = noiseGenerator1.GetNoise(realX, realY, realZ);
                    float noise2 = noiseGenerator2.GetNoise(realX, realY, realZ);

                    if (Settings.DEBUG_LOG_ENABLED) {
                        Settings.LOGGER.info("noise1: {}, noise2: {}", noise1, noise2);
                    }
//                    int state1 = (noise1 < 0.85) ? 0 : 1;
//                    int state2 = (noise2 < 0.85) ? 0 : 1;
//                    int state = state1 * state2;

                    float noise = noise1 * noise2;



                    // TURBULENCE:
//                    if (Configuration.)

//                    if (state == 1) {
                    if (noise > .8) {
                        IBlockState currentBlockState = primer.getBlockState(localX, realY, localZ);
                        IBlockState aboveBlockState = primer.getBlockState(localX, realY + 1, localZ);
                        boolean foundTopBlock = isTopBlock(primer, localX, realY, localZ, chunkX, chunkZ);
                        digBlock(primer, localX, realY, localZ, chunkX, chunkZ, foundTopBlock, currentBlockState, aboveBlockState);
                    }

                    if (Settings.DEBUG_LOG_ENABLED) {
                        avgNoise1 = ((numChunksGenerated * avgNoise1) + noise1) / (numChunksGenerated + 1);
                        avgNoise2 = ((numChunksGenerated * avgNoise2) + noise2) / (numChunksGenerated + 1);

                        if (noise1 > maxNoise1) maxNoise1 = noise1;
                        if (noise2 > maxNoise2) maxNoise2 = noise2;
                        if (noise1 < minNoise1) minNoise1 = noise1;
                        if (noise2 < minNoise2) minNoise2 = noise2;

                        numChunksGenerated++;

                        if (numChunksGenerated == 2000) {
                            Settings.LOGGER.info("2000 Chunks Generated Report");

                            Settings.LOGGER.info("--> Noise 1");
                            Settings.LOGGER.info("  > Average: {}", avgNoise1);
                            Settings.LOGGER.info("  > Max: {}", maxNoise1);
                            Settings.LOGGER.info("  > Min: {}", minNoise1);

                            Settings.LOGGER.info("--> Noise 2");
                            Settings.LOGGER.info("  > Average: {}", avgNoise2);
                            Settings.LOGGER.info("  > Max: {}", maxNoise2);
                            Settings.LOGGER.info("  > Min: {}", minNoise2);

                            // Reset vals
                            numChunksGenerated = 0;
                            avgNoise1 = 0;
                            avgNoise2 = 0;
                            maxNoise1 = -10;
                            minNoise1 = 10;
                            maxNoise2 = -10;
                            minNoise2 = 10;
                        }
                    }
                }
            }
        }
    }

    private void generateFractalModelForScreenShots(int chunkX, int chunkZ, ChunkPrimer primer) {
        for (int localX = 0; localX < 16; localX++) {
            int realX = localX + 16*chunkX;

            for (int localZ = 0; localZ < 16; localZ++) {
                int realZ = localZ + 16*chunkZ;

                for (int realY = 128; realY > 0; realY--) {
                    if (realX < 0) {
                        primer.setBlockState(localX, realY, localZ, Blocks.AIR.getDefaultState());
                    } else {
                        float noise1 = noiseGenerator1.GetNoise(realX, realY, realZ);
                        float noise2 = noiseGenerator2.GetNoise(realX, realY, realZ);
                        float noise = noise1 * noise2;

                        if (noise > .8) {
                            primer.setBlockState(localX, realY, localZ, Blocks.QUARTZ_BLOCK.getDefaultState());
                        } else {
                            primer.setBlockState(localX, realY, localZ, Blocks.AIR.getDefaultState());
                        }
                    }
                }
            }
        }
    }

    private void generateWorleys(int chunkX, int chunkZ, ChunkPrimer primer) {
        Random r = new Random(primer.hashCode()); // seed rng with this chunk
        ArrayList<Float3> featurePointList = new ArrayList<>();

        // Generate feature points for this chunk
        for (int localY = 0; localY < 64; localY += 16) { // we look at 16x16x16 subchunks at a time
            int numFeaturePointsInSubChunk = r.nextInt(4);

            // Feature point locations are randomly determined
            for (int i = 0; i < numFeaturePointsInSubChunk; i++) {
                int featurePointX = r.nextInt(16);
                int featurePointY = r.nextInt(16);
                int featurePointZ = r.nextInt(16);

                featurePointList.add(new Float3(featurePointX, featurePointY + localY, featurePointZ));
            }
        }

        // Dig out blocks according to their F1 values
        for (int localX = 0; localX < 16; localX++) {
            for (int localY = 0; localY < 64; localY++) {
                for (int localZ = 0; localZ < 16; localZ++) {
                    double F1 = Double.MAX_VALUE;
                    double F2 = Double.MAX_VALUE;
                    Float3 currPoint = new Float3 (localX, localY, localZ);

                    // Find the closest feature point to this block
                    for (Float3 featurePoint : featurePointList) {
                        double curDistance = distance(currPoint, featurePoint);
                        if (curDistance < F1) {
                            F2 = F1;
                            F1 = curDistance;
                        } else if (curDistance < F2) {
                            F2 = curDistance;
                        }
                    }

                    double F = Math.abs(F2 - F1);

                    if (F < Configuration.FThreshold) {
                        IBlockState currentBlockState = primer.getBlockState(localX, localY, localZ);
                        IBlockState aboveBlockState = primer.getBlockState(localX, localY + 1, localZ);
                        boolean foundTopBlock = isTopBlock(primer, localX, localY, localZ, chunkX, chunkZ);
                        digBlock(primer, localX, localY, localZ, chunkX, chunkZ, foundTopBlock, currentBlockState, aboveBlockState);
                    }
                }
            }
        }
    }

    private double distance(Float3 point1, Float3 point2) {
        return Math.pow((point2.x - point1.x), 2)
                + Math.pow((point2.y - point1.y), 2)
                + Math.pow((point2.z - point1.z), 2);
    }

    // Because it's private in MapGenCaves this is reimplemented
    // Determine if the block at the specified location is the top block for the biome, we take into account
    // Vanilla bugs to make sure that we generate the map the same way vanilla does.
    private boolean isTopBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ) {
        Biome biome = world.getBiome(new BlockPos(x + chunkX * 16, 0, z + chunkZ * 16));
        IBlockState state = data.getBlockState(x, y, z);
        return isExceptionBiome(biome) ? state.getBlock() == Blocks.GRASS : state == biome.topBlock;
    }

    // Exception biomes to make sure we generate like vanilla
    private boolean isExceptionBiome(net.minecraft.world.biome.Biome biome) {
        return biome == net.minecraft.init.Biomes.BEACH || biome == net.minecraft.init.Biomes.DESERT;
    }

    @Override
    protected void digBlock(ChunkPrimer primer, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop, IBlockState state, IBlockState up) {
        net.minecraft.world.biome.Biome biome = world.getBiome(new BlockPos(x + chunkX * 16, 0, z + chunkZ * 16));
        IBlockState top = biome.topBlock;
        IBlockState filler = biome.fillerBlock;

        if (this.canReplaceBlock(state, up) || state.getBlock() == top.getBlock() || state.getBlock() == filler.getBlock()) {
            if (y <= Configuration.lavaDepth)
                primer.setBlockState(x, y, z, Blocks.LAVA.getDefaultState());
            else {
                primer.setBlockState(x, y, z, Blocks.AIR.getDefaultState());
                if (foundTop && primer.getBlockState(x, y - 1, z).getBlock() == filler.getBlock())
                    primer.setBlockState(x, y - 1, z, top);


                //replace floating sand with sandstone
                if(up == Blocks.SAND.getDefaultState()) {
                    primer.setBlockState(x, y+1, z, BLK_SANDSTONE);
                } else if(up == Blocks.SAND.getStateFromMeta(1)) {
                    primer.setBlockState(x, y+1, z, BLK_RED_SANDSTONE);
                }
            }
        }
    }

    private class Float3 {
        double x;
        double y;
        double z;

        Float3(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
