package com.yungnickyoung.minecraft.bettercaves.noise;

import com.yungnickyoung.minecraft.bettercaves.config.BCSettings;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to generate NoiseTuples for blocks.
 * This class serves as an interface between Better Caves and FastNoise.
 */
public class NoiseGen {
    /** Noise generation seed. Minecraft world seed should be used for reproducibility. */
    private long seed;

    /** Number of FastNoise functions to use. This will be the number of valuesin a NoiseTuple. Recommended: 2 */
    private int numGenerators;

    /** Primary noise function parameters */
    private NoiseSettings noiseSettings;

    /** Determines how steep and tall caves are */
    private float yCompression;
    /** Determines how horizontally large and stretched out caves are */
    private float xzCompression;

    /** List of all primary noise generators, one for each octave */
    private List<INoiseLibrary> listNoiseGens = new ArrayList<>();

    /**
     * @param isFastNoise true if FastNoise, false if OpenSimplex2S
     * @param noiseSettings Primary noise function parameters
     * @param numGenerators Number of noise values to calculate per block. This number will be the number of noise
     *                      values in each resultant NoiseTuple. Increasing this will impact performance.
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
     * @param minHeight The bottom y-coordinate to start generating noise values for
     * @param maxHeight The top y-coordinate to stop generating noise values for
     */
    public double[][] generateNoiseColumn(int x, int z, int minHeight, int maxHeight) {
        double[][] noiseColumn = new double[maxHeight - minHeight + 1][numGenerators];

        for (int y = minHeight; y <= maxHeight; y++) {
            double[] noiseVals = new double[numGenerators];
            for (int i = 0; i < numGenerators; i++) {
                noiseVals[i] = listNoiseGens.get(i).GetNoise(x * xzCompression, y * yCompression, z * xzCompression);
            }
            noiseColumn[y - minHeight] = noiseVals;
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
     * @param minHeight The bottom y-coordinate to start generating noise values for
     * @param maxHeight The top y-coordinate to stop generating noise values for
     */
    public double[][][][] interpolateNoiseCube(BlockPos startPos, BlockPos endPos, int minHeight, int maxHeight) {
        float startCoeff, endCoeff;
        int startX       = startPos.getX();
        int endX         = endPos.getX();
        int startZ       = startPos.getZ();
        int endZ         = endPos.getZ();
        int subChunkSize = endX - startX + 1;

        // Calculate noise tuples for four corner columns
        double[][] noisesX0Z0 =
            generateNoiseColumn(startX, startZ, minHeight, maxHeight);
        double[][] noisesX0Z1 =
            generateNoiseColumn(startX, endZ, minHeight, maxHeight);
        double[][] noisesX1Z0 =
            generateNoiseColumn(endX, startZ, minHeight, maxHeight);
        double[][] noisesX1Z1 =
            generateNoiseColumn(endX, endZ, minHeight, maxHeight);

        // Initialize cube with 4 corner columns
        double[][][][] cube = new double[subChunkSize][subChunkSize][maxHeight - minHeight + 1][numGenerators]; // [x len][z len][y len][num gens (usually 2)]
        cube[0][0] = noisesX0Z0;
        cube[0][subChunkSize - 1] = noisesX0Z1;
        cube[subChunkSize - 1][0] = noisesX1Z0;
        cube[subChunkSize - 1][subChunkSize - 1] = noisesX1Z1;

        // Populate edge planes along x axis
        for (int x = 1; x < subChunkSize - 1; x++) {
            startCoeff = BCSettings.START_COEFFS[x];
            endCoeff = BCSettings.END_COEFFS[x];

            for (int y = minHeight; y <= maxHeight; y++) {
                for (int i = 0; i < numGenerators; i++) {
                    cube[x][0][y - minHeight][i] = (cube[0][0][y - minHeight][i] * startCoeff) + (cube[subChunkSize - 1][0][y - minHeight][i] * endCoeff);
                }
            }

            for (int y = minHeight; y <= maxHeight; y++) {
                for (int i = 0; i < numGenerators; i++) {
                    cube[x][subChunkSize - 1][y - minHeight][i] = (cube[0][subChunkSize - 1][y - minHeight][i] * startCoeff) + (cube[subChunkSize - 1][subChunkSize - 1][y - minHeight][i] * endCoeff);
                }
            }
        }

        // Populate rest of cube by interpolating the two edge planes
        for (int x = 0; x < subChunkSize; x++) {
            for (int z = 1; z < subChunkSize - 1; z++) {
                startCoeff = BCSettings.START_COEFFS[z];
                endCoeff = BCSettings.END_COEFFS[z];

                for (int y = minHeight; y <= maxHeight; y++) {
                    for (int i = 0; i < numGenerators; i++) {
                        cube[x][z][y - minHeight][i] = (cube[x][0][y - minHeight][i] * startCoeff) + (cube[x][subChunkSize - 1][y - minHeight][i] * endCoeff);
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
