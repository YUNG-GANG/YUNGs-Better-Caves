package com.yungnickyoung.minecraft.bettercaves.noise;


import com.yungnickyoung.minecraft.bettercaves.BCConstants;
import com.yungnickyoung.minecraft.yungsapi.noise.FastNoise;
import com.yungnickyoung.minecraft.yungsapi.noise.INoiseLibrary;
import com.yungnickyoung.minecraft.yungsapi.noise.OpenSimplex2S;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to generate noise tuples for blocks.
 * This class serves as an interface between Better Caves and FastNoise.
 */
public class NoiseGen {
    /** Noise generation seed. Minecraft world seed should be used for reproducibility. */
    private final long seed;

    /** Number of FastNoise functions to use. This will be the number of values in a noise tuple. Recommended: 2 */
    private final int numGenerators;

    /** Primary noise function parameters */
    private final NoiseSettings noiseSettings;

    /** Determines how steep and tall caves are */
    private final float yCompression;
    /** Determines how horizontally large and stretched out caves are */
    private final float xzCompression;

    /** List of all primary noise generators, one for each octave */
    private final List<INoiseLibrary> listNoiseGens = new ArrayList<>();

    /**
     * @param isFastNoise true if FastNoise, false if OpenSimplex2S
     * @param noiseSettings Primary noise function parameters
     * @param numGenerators Number of noise values to calculate per block. This number will be the number of noise
     *                      values in each resultant noise tuple. Increasing this will impact performance.
     * @param yComp y-compression factor
     * @param xzComp xz-compression factor
     */
    public NoiseGen(long seed, boolean isFastNoise, NoiseSettings noiseSettings,
                    int numGenerators, float yComp, float xzComp) {
        this.seed = seed;
        this.noiseSettings = noiseSettings;
        this.numGenerators = numGenerators;
        this.yCompression = yComp;
        this.xzCompression = xzComp;
        initializeNoiseGens(isFastNoise);
    }

    /**
     * Generate noise tuples for a column of blocks.
     * @param x The global x-coordinate of this column
     * @param z The global z-coordinate of this column
     * @param bottomY The bottom y-coordinate to start generating noise values for
     * @param topY The top y-coordinate to stop generating noise values for
     */
    private double[][] generateNoiseColumn(int x, int z, int bottomY, int topY) {
        double[][] noiseColumn = new double[topY - bottomY + 1][numGenerators];

        for (int y = bottomY; y <= topY; y++) {

            double[] noiseVals = new double[numGenerators];
            for (int i = 0; i < numGenerators; i++) {
                noiseVals[i] = listNoiseGens.get(i).GetNoise(x * xzCompression, y * yCompression, z * xzCompression);
            }
            noiseColumn[y - bottomY] = noiseVals;
        }

        return noiseColumn;
    }

    /**
     * Generate noise tuples for a cube of blocks.
     * Only columns of blocks at the four corners of each cube have noise values calculated for them.
     * Blocks in between have noise values estimated via a naive implementation of trilinear interpolation.
     * @param startPos Position of any block in the starting corner column of the cube.
     *                 This column must have x and z coordinates lower than that of endPos.
     * @param endPos   Position of any block in the ending corner column of the cube.
     *                 This column must have x and z coordinates higher than that of startPos.
     * @param bottomY The bottom y-coordinate to start generating noise values for
     * @param topY The top y-coordinate to stop generating noise values for
     */
    public double[][][][] interpolateNoiseCube(BlockPos startPos, BlockPos endPos, int bottomY, int topY) {
        float startCoeff, endCoeff;
        int startX       = startPos.getX();
        int endX         = endPos.getX();
        int startZ       = startPos.getZ();
        int endZ         = endPos.getZ();
        int subChunkSize = endX - startX + 1;

        // Calculate noise tuples for four corner columns
        double[][] noisesX0Z0 =
                generateNoiseColumn(startX, startZ, bottomY, topY);
        double[][] noisesX0Z1 =
                generateNoiseColumn(startX, endZ, bottomY, topY);
        double[][] noisesX1Z0 =
                generateNoiseColumn(endX, startZ, bottomY, topY);
        double[][] noisesX1Z1 =
                generateNoiseColumn(endX, endZ, bottomY, topY);

        // Initialize cube with 4 corner columns
        double[][][][] cube = new double[subChunkSize][subChunkSize][topY - bottomY + 1][numGenerators]; // [x len][z len][y len][num gens (usually 2)]
        cube[0][0] = noisesX0Z0;
        cube[0][subChunkSize - 1] = noisesX0Z1;
        cube[subChunkSize - 1][0] = noisesX1Z0;
        cube[subChunkSize - 1][subChunkSize - 1] = noisesX1Z1;

        // Populate edge planes along x axis
        for (int x = 1; x < subChunkSize - 1; x++) {
            startCoeff = BCConstants.START_COEFFS[x];
            endCoeff = BCConstants.END_COEFFS[x];

            for (int y = bottomY; y <= topY; y++) {
                for (int i = 0; i < numGenerators; i++) {
                    cube[x][0][y - bottomY][i] = (cube[0][0][y - bottomY][i] * startCoeff) + (cube[subChunkSize - 1][0][y - bottomY][i] * endCoeff);
                }
            }

            for (int y = bottomY; y <= topY; y++) {
                for (int i = 0; i < numGenerators; i++) {
                    cube[x][subChunkSize - 1][y - bottomY][i] = (cube[0][subChunkSize - 1][y - bottomY][i] * startCoeff) + (cube[subChunkSize - 1][subChunkSize - 1][y - bottomY][i] * endCoeff);
                }
            }
        }

        // Populate rest of cube by interpolating the two edge planes
        for (int x = 0; x < subChunkSize; x++) {
            for (int z = 1; z < subChunkSize - 1; z++) {
                startCoeff = BCConstants.START_COEFFS[z];
                endCoeff = BCConstants.END_COEFFS[z];

                for (int y = bottomY; y <= topY; y++) {
                    for (int i = 0; i < numGenerators; i++) {
                        cube[x][z][y - bottomY][i] = (cube[x][0][y - bottomY][i] * startCoeff) + (cube[x][subChunkSize - 1][y - bottomY][i] * endCoeff);
                    }
                }
            }
        }

        return cube;
    }

    /* ------------------------- Public Getters -------------------------*/
    public long getSeed() {
        return seed;
    }

    /* ------------------------- Private Methods -------------------------*/
    /**
     * Initialize fractal noise generators.
     */
    private void initializeNoiseGens(boolean isFastNoise) {
        if (isFastNoise) {
            for (int i = 0; i < numGenerators; i++) {
                FastNoise noiseGen = new FastNoise();
                noiseGen.SetSeed((int) (seed) + (1111 * (i + 1)));
                noiseGen.SetFractalType(noiseSettings.getFractalType());
                noiseGen.SetNoiseType(noiseSettings.getNoiseType());
                noiseGen.SetFractalOctaves(noiseSettings.getOctaves());
                noiseGen.SetFractalGain(noiseSettings.getGain());
                noiseGen.SetFrequency(noiseSettings.getFrequency());
                listNoiseGens.add(noiseGen);
            }
        }
        else {
            for (int i = 0; i < numGenerators; i++) {
                OpenSimplex2S noiseGen = new OpenSimplex2S(seed + (1111 * (i + 1)));
                noiseGen.setGain(noiseSettings.getGain());
                noiseGen.setOctaves(noiseSettings.getOctaves());
                noiseGen.setFrequency(noiseSettings.getFrequency());
                noiseGen.setLacunarity(2.0);
                listNoiseGens.add(noiseGen);
            }
        }
    }
}
