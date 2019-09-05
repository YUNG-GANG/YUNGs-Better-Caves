package com.yungnickyoung.minecraft.bettercaves.world.cave;

import com.yungnickyoung.minecraft.bettercaves.noise.NoiseTuple;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.Map;

public abstract class BetterCave {
    private World world;
    private long seed;

    /* ============================== Values passed in through config ============================== */
    /* ------------- Rigid Multifractal Params ------------- */
    int fractalOctaves;            // Number of rigid multifractal octaves
    float fractalGain;             // Rigid multifractal gain
    float fractalFreq;             // Rigid multifractal frequency
    int numGens;                   // Number of noise values to generate per iteration (block, sub-chunk, etc)

    /* ----------------- Turbulence Params ----------------- */
    int turbOctaves;               // Number of octaves in turbulence function
    float turbGain;                // Gain of turbulence function
    float turbFreq;                // Frequency of turbulence function
    boolean enableTurbulence;      // Set true to enable turbulence (adds performance overhead, generally not worth it)

    /* -------------- Noise Processing Params -------------- */
    float yCompression;            // Vertical cave gen compression
    float xzCompression;           // Horizontal cave gen compression
    private float yAdjustF1;       // Adjustment value for the block immediately above. Must be between 0 and 1.0
    private float yAdjustF2;       // Adjustment value for the block two blocks above. Must be between 0 and 1.0
    float noiseThreshold;          // Noise threshold for determining whether or not a block gets dug out
    boolean enableYAdjust;               // Set true to perform preprocessing on noise values, adjusting them to increase
                                   // headroom in the y direction. This is generally useful for caves (esp. Simplex),
                                   // but not really necessary for caverns

    public BetterCave(World world, int fOctaves, float fGain, float fFreq, int numGens, float threshold, int tOctaves, float tGain,
                      float tFreq, boolean enableTurbulence, float yComp, float xzComp, boolean yAdj, float yAdjF1,
                      float yAdjF2) {
        this.world = world;
        this.seed = world.getSeed();
        this.fractalOctaves = fOctaves;
        this.fractalGain = fGain;
        this.fractalFreq = fFreq;
        this.numGens = numGens;
        this.noiseThreshold = threshold;
        this.turbOctaves = tOctaves;
        this.turbGain = tGain;
        this.turbFreq = tFreq;
        this.enableTurbulence = enableTurbulence;
        this.yCompression = yComp;
        this.xzCompression = xzComp;
        this.enableYAdjust = yAdj;
        this.yAdjustF1 = yAdjF1;
        this.yAdjustF2 = yAdjF2;
    }

    public World getWorld() {
        return this.world;
    }

    public long getSeed() {
        return this.seed;
    }

    public abstract void generateColumn(int chunkX, int chunkZ, ChunkPrimer primer, int localX, int localZ, int bottomY,
                      int topY, int maxSurfaceHeight, int minSurfaceHeight);

    void preprocessCaveNoiseCol(Map<Integer, NoiseTuple> noises, int topY, int bottomY, int numGens) {
        /* Adjust simplex noise values based on blocks above in order to give the player more headroom */
        for (int realY = topY; realY >= bottomY; realY--) {
            NoiseTuple sBlockNoise = noises.get(realY);
            float avgSNoise = 0;

            for (float noise : sBlockNoise.getNoiseValues())
                avgSNoise += noise;

            avgSNoise /= sBlockNoise.size();

            if (avgSNoise > this.noiseThreshold) {
                /* Adjust noise values of blocks above to give the player more head room */
                float f1 = this.yAdjustF1;
                float f2 = this.yAdjustF2;

                if (realY < topY) {
                    NoiseTuple tupleAbove = noises.get(realY + 1);
                    for (int i = 0; i < numGens; i++)
                        tupleAbove.set(i, ((1 - f1) * tupleAbove.get(i)) + (f1 * sBlockNoise.get(i)));
                }

                if (realY < topY - 1) {
                    NoiseTuple tupleTwoAbove = noises.get(realY + 2);
                    for (int i = 0; i < numGens; i++)
                        tupleTwoAbove.set(i, ((1 - f2) * tupleTwoAbove.get(i)) + (f2 * sBlockNoise.get(i)));
                }
            }
        }
    }
}
