package com.yungnickyoung.minecraft.bettercaves.noise;

import com.yungnickyoung.minecraft.bettercaves.config.BCSettings;
import com.yungnickyoung.minecraft.bettercaves.util.Vector3f;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
     * @param world World this generaton function will be used in
     * @param isFastNoise true if FastNoise, false if OpenSimplex2S
     * @param noiseSettings Primary noise function parameters
     * @param numGenerators Number of noise values to calculate per block. This number will be the number of noise
     *                      values in each resultant NoiseTuple. Increasing this will impact performance.
     * @param yComp y-compression factor
     * @param xzComp xz-compression factor
     */
    public NoiseGen(World world, boolean isFastNoise, NoiseSettings noiseSettings,
                    int numGenerators, float yComp, float xzComp) {
        this.seed = world.getSeed();
        this.noiseSettings = noiseSettings;
        this.numGenerators = numGenerators;
        this.yCompression = yComp;
        this.xzCompression = xzComp;
        initializeNoiseGens(isFastNoise);
    }

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
     * Generate NoiseTuples for a column of blocks, as a NoiseColumn.
     * @param blockPos Position of any block in the column (the y-coordinate is ignored)
     * @param minHeight The bottom y-coordinate to start generating noise values for
     * @param maxHeight The top y-coordinate to stop generating noise values for
     * @return NoiseColumn
     */
    public NoiseColumn generateNoiseColumn(BlockPos blockPos, int minHeight, int maxHeight) {
        int x = blockPos.getX();
        int z = blockPos.getZ();
        NoiseColumn noiseColumn = new NoiseColumn();

        for (int y = minHeight; y <= maxHeight; y++) {
            Vector3f f = new Vector3f(x * xzCompression, y * yCompression, z * xzCompression);

            // Create NoiseTuple for this block
            NoiseTuple newTuple = new NoiseTuple();
            for (int i = 0; i < numGenerators; i++)
                newTuple.put(listNoiseGens.get(i).GetNoise(f.x, f.y, f.z));

            noiseColumn.put(y, newTuple);
        }

        return noiseColumn;
    }

    /**
     * Generate NoiseTuples for a column of blocks, as a NoiseColumn.
     * Only blocks at the top & bottom of each subChunk (as designated by subChunkSize) have noise values actually
     * generated for them. Blocks in between have noise values estimated via bilinear interpolation.
     *
     * This function is currently unused, since such interpolation along the y-axis ends up reducing
     * cave interconnectivity. It is kept here for possible future use.
     * @param blockPos Position of any block in the column (the y-coordinate is ignored)
     * @param minHeight The bottom y-coordinate to start generating noise values for
     * @param maxHeight The top y-coordinate to stop generating noise values for
     * @param subChunkSize size of the subChunk, in blocks. Should be a power of 2.
     * @return NoiseColumn
     */
    public NoiseColumn interpolateNoiseColumn(BlockPos blockPos, int minHeight, int maxHeight, int subChunkSize) {
        int startY;
        int x = blockPos.getX();
        int z = blockPos.getZ();
        NoiseColumn noiseColumn = new NoiseColumn();

        // Calculate noise for every nth block in the column, using bilinear interpolation for the rest
        for (startY = minHeight; startY <= maxHeight; startY += subChunkSize) {
            int endY = Math.min(startY + subChunkSize - 1, maxHeight);
            Vector3f subChunkStart = new Vector3f(x * xzCompression, startY * yCompression, z * xzCompression);
            Vector3f subChunkEnd = new Vector3f(x * xzCompression, endY * yCompression, z * xzCompression);

            // Create NoiseTuples for subchunk edge blocks
            NoiseTuple startTuple = new NoiseTuple();
            NoiseTuple endTuple = new NoiseTuple();
            for (int i = 0; i < numGenerators; i++) {
                startTuple.put(listNoiseGens.get(i).GetNoise(subChunkStart.x, subChunkStart.y, subChunkStart.z));
                endTuple.put(listNoiseGens.get(i).GetNoise(subChunkEnd.x, subChunkEnd.y, subChunkEnd.z));
            }
            noiseColumn.put(startY, startTuple);
            noiseColumn.put(endY, endTuple);

            // Fill in middle values via bilinear interpolation of edge values
            for (int y = startY + 1; y < endY; y++) {
                float startCoeff, endCoeff;
                if (endY == maxHeight) {
                    startCoeff = (float)(endY - startY - y - startY) / (endY - startY);
                    endCoeff = (float)(y - startY) / (endY - startY);
                } else {
                    startCoeff = BCSettings.START_COEFFS[y - startY];
                    endCoeff = BCSettings.END_COEFFS[y - startY];
                }
                NoiseTuple newTuple = startTuple
                    .times(startCoeff)
                    .plus(endTuple
                        .times(endCoeff));
                noiseColumn.put(y, newTuple);
            }
        }

        return noiseColumn;
    }

    /**
     * Generate NoiseTuples for a cube of blocks.
     * Only columns of blocks at the four corners of each cube have noise values calculated for them.
     * Blocks in between have noise values estimated via a naive implementation of trilinear interpolation.
     * @param startPos Position of any block in the starting corner column of the cube.
     *                 This column must have x and z coordinates lower than that of endPos.
     * @param endPos   Position of any block in the ending corner column of the cube.
     *                 This column must have x and z coordinates higher than that of startPos.
     * @param minHeight The bottom y-coordinate to start generating noise values for
     * @param maxHeight The top y-coordinate to stop generating noise values for
     * @return NoiseCube
     */
    public NoiseCube interpolateNoiseCube(BlockPos startPos, BlockPos endPos, int minHeight, int maxHeight) {
        float startCoeff, endCoeff;
        int startX       = startPos.getX();
        int endX         = endPos.getX();
        int startZ       = startPos.getZ();
        int endZ         = endPos.getZ();
        int subChunkSize = endX - startX + 1;

        // Calculate noise tuples for four corner columns
        NoiseColumn noisesX0Z0 =
            generateNoiseColumn(new BlockPos(startX, 1, startZ), minHeight, maxHeight);
        NoiseColumn noisesX0Z1 =
            generateNoiseColumn(new BlockPos(startX, 1, endZ), minHeight, maxHeight);
        NoiseColumn noisesX1Z0 =
            generateNoiseColumn(new BlockPos(endX, 1, startZ), minHeight, maxHeight);
        NoiseColumn noisesX1Z1 =
            generateNoiseColumn(new BlockPos(endX, 1, endZ), minHeight, maxHeight);

        // Initialize cube with 4 corner columns
        NoiseCube cube = new NoiseCube(subChunkSize);
        cube.get(0).set(0, noisesX0Z0);
        cube.get(0).set(subChunkSize - 1, noisesX0Z1);
        cube.get(subChunkSize - 1).set(0, noisesX1Z0);
        cube.get(subChunkSize - 1).set(subChunkSize - 1, noisesX1Z1);

        // Populate edge planes along x axis
        for (int x = 1; x < subChunkSize - 1; x++) {
            startCoeff = BCSettings.START_COEFFS[x];
            endCoeff = BCSettings.END_COEFFS[x];

            NoiseColumn xz0 = cube.get(x).get(0);
            for (int y = minHeight; y <= maxHeight; y++) {
                NoiseTuple startTuple = cube.get(0).get(0).get(y);
                NoiseTuple endTuple = cube.get(subChunkSize - 1).get(0).get(y);
                NoiseTuple newTuple = startTuple
                    .times(startCoeff)
                    .plus(endTuple
                        .times(endCoeff));
                xz0.put(y, newTuple);
            }

            NoiseColumn xz1 = cube.get(x).get(subChunkSize - 1);
            for (int y = minHeight; y <= maxHeight; y++) {
                NoiseTuple startTuple = cube.get(0).get(subChunkSize - 1).get(y);
                NoiseTuple endTuple = cube.get(subChunkSize - 1).get(subChunkSize - 1).get(y);
                NoiseTuple newTuple = startTuple
                    .times(startCoeff)
                    .plus(endTuple
                        .times(endCoeff));
                xz1.put(y, newTuple);
            }
        }

        // Populate rest of cube by interpolating the two edge planes
        for (int x = 0; x < subChunkSize; x++) {
            for (int z = 1; z < subChunkSize - 1; z++) {
                startCoeff = BCSettings.START_COEFFS[z];
                endCoeff = BCSettings.END_COEFFS[z];

                NoiseColumn xz = cube.get(x).get(z);

                for (int y = minHeight; y <= maxHeight; y++) {
                    NoiseTuple startTuple = cube.get(x).get(0).get(y);
                    NoiseTuple endTuple = cube.get(x).get(subChunkSize - 1).get(y);
                    NoiseTuple newTuple = startTuple
                        .times(startCoeff)
                        .plus(endTuple
                            .times(endCoeff));

                    xz.put(y, newTuple);
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
