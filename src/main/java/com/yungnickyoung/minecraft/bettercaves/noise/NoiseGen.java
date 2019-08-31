package com.yungnickyoung.minecraft.bettercaves.noise;

import com.yungnickyoung.minecraft.bettercaves.util.FastNoise;
import net.minecraft.world.World;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

/**
 * Class used to generate NoiseTuples for blocks in a specified range within a chunk.
 * Supported noise types: SimplexFractal, PerlinFractal
 */
public class NoiseGen {
    private long seed; // world seed should be used for reproducibility
    private FastNoise.NoiseType noiseType;

    /* ------------- Noise generation function parameters ------------- */
    private int fractalOctaves;
    private float fractalGain;
    private float fractalFrequency;

    /* ---------------- Turbulence function parameters ---------------- */
    private int turbulenceOctaves;
    private float turbulenceGain;
    private float turbulenceFrequency;

    /* ----- Switch for enabling turbulence---- */
    private boolean enableTurbulence;

    /* ----- Compression values - these control the size of caves ----- */
    private float yCompression;  // Determines how steep and tall caves are
    private float xzCompression; // Determines how horizontally large and spread out caves are

    /* ------------- List of all distinct noise generators ------------ */
    private List<FastNoise> listNoiseGens = new ArrayList<>();

    /* --------------------- Turbulence generator --------------------- */
    private FastNoise turbulenceGen = new FastNoise();

    /**
     *
     * @param noiseType The type of noise to generate, e.g. PerlinFractal, SimplexFractal, etc.
     * @param seed Seed for noise generation - should be the world seed
     * @param fOctaves Number of fractal octaves used in noise generation
     * @param fGain Amount of fractal gain used in noise generation
     * @param fFreq Fractal frequency used in noise generation
     * @param tOctaves Number of octaves used in the turbulence function
     * @param tGain Amount of gain used in the turbulence function
     * @param tFreq Frequency used in the turbulence function
     * @param useTurb Whether or not turbulence should be applied
     * @param yComp y-compression factor
     * @param xzComp xz-compression factor
     */
    public NoiseGen(FastNoise.NoiseType noiseType, long seed, int fOctaves, float fGain, float fFreq,
                    int tOctaves, float tGain, float tFreq, boolean useTurb, float yComp, float xzComp) {
        this.noiseType = noiseType;
        this.seed = seed;
        this.fractalOctaves = fOctaves;
        this.fractalGain = fGain;
        this.fractalFrequency = fFreq;
        this.turbulenceOctaves = tOctaves;
        this.turbulenceGain = tGain;
        this.turbulenceFrequency = tFreq;
        this.enableTurbulence = useTurb;
        this.yCompression = yComp;
        this.xzCompression = xzComp;
        initializeTurbulenceGen();
    }

    /**
     *
     * @param noiseType The type of noise to generate, e.g. PerlinFractal, SimplexFractal, etc.
     * @param world World this generaton function will be used in
     * @param fOctaves Number of fractal octaves used in noise generation
     * @param fGain Amount of fractal gain used in noise generation
     * @param fFreq Fractal frequency used in noise generation
     * @param tOctaves Number of octaves used in the turbulence function
     * @param tGain Amount of gain used in the turbulence function
     * @param tFreq Frequency used in the turbulence function
     * @param useTurb Whether or not turbulence should be applied
     * @param yComp y-compression factor
     * @param xzComp xz-compression factor
     */
    public NoiseGen(FastNoise.NoiseType noiseType, World world,int fOctaves, float fGain, float fFreq,
                    int tOctaves, float tGain, float tFreq, boolean useTurb, float yComp, float xzComp) {
        this(noiseType, world.getSeed(), fOctaves,fGain, fFreq, tOctaves, tGain, tFreq, useTurb, yComp, xzComp);
    }

    /**
     * Generates 16 x 16 array of NoiseTuples for each y-value from minHeight to maxHeight, inclusive.
     * Each resultant NoiseTuple contains n noise values, where n = numGenerators.
     * @param chunkX The chunk's x-coordinate
     * @param chunkZ The chunk's y-coordinate
     * @param minHeight Minimum y-coordinate to generate noise for
     * @param maxHeight Maximum y-coordinate to generate noise for
     * @param numGenerators Number of noise generation functions to use when calculating noise. This is the number
     *                      of noise values a single NoiseTuple will contain in the return value.
     * @return A list of 16 x 16 arrays of NoiseTuples, each containing n = numGenerators noise values.
     */
    public List<NoiseTuple[][]> generateNoise(int chunkX, int chunkZ, int minHeight, int maxHeight, int numGenerators) {
        initializeNoiseGens(numGenerators);

        List<NoiseTuple[][]> noises = new ArrayList<>();

        for (int y = maxHeight; y >= minHeight; y--) {
            NoiseTuple[][] noiseLayer = new NoiseTuple[16][16];

            for (int localX = 0; localX < 16; localX++) {
                int realX = localX + 16 * chunkX;

                for (int localZ = 0; localZ < 16; localZ++) {
                    int realZ = localZ + 16 * chunkZ;

                    Vector3f f = new Vector3f(realX * xzCompression, y * yCompression, realZ * xzCompression);

                    // Use turbulence function to apply gradient perturbation, if enabled
                    if (this.enableTurbulence)
                        turbulenceGen.GradientPerturbFractal(f);

                    // Create NoiseTuple for this block
                    NoiseTuple newTuple = new NoiseTuple();
                    for (int i = 0; i < numGenerators; i++)
                        newTuple.put(listNoiseGens.get(i).GetNoise(f.x, f.y, f.z));

                    noiseLayer[localX][localZ] = newTuple;
                }
            }
            noises.add(noiseLayer);
        }

        return noises;
    }

    public long getSeed() {
        return seed;
    }

    public FastNoise.NoiseType getNoiseType() {
        return this.noiseType;
    }

    private void initializeNoiseGens(int numGenerators) {
        if (numGenerators <= listNoiseGens.size())
            return;

        int numGensNeeded = numGenerators - listNoiseGens.size();
        int seedModifier = listNoiseGens.size();

        for (int i = 0; i < numGensNeeded; i++) {
            FastNoise noiseGen = new FastNoise();
            noiseGen.SetFractalType(FastNoise.FractalType.RigidMulti);
            noiseGen.SetSeed((int)(seed) + (1111 * seedModifier));
            noiseGen.SetNoiseType(this.noiseType);
            noiseGen.SetFractalOctaves(this.fractalOctaves);
            noiseGen.SetFractalGain(this.fractalGain);
            noiseGen.SetFrequency(this.fractalFrequency);

            listNoiseGens.add(noiseGen);
            seedModifier++;
        }

    }

    private void initializeTurbulenceGen() {
        turbulenceGen.SetNoiseType(FastNoise.NoiseType.PerlinFractal);
        turbulenceGen.SetFractalType(FastNoise.FractalType.FBM);
        turbulenceGen.SetSeed((int)(seed) + 69);
        turbulenceGen.SetFractalOctaves(this.turbulenceOctaves);
        turbulenceGen.SetFractalGain(this.turbulenceGain);
        turbulenceGen.SetFrequency(this.turbulenceFrequency);
    }
}
