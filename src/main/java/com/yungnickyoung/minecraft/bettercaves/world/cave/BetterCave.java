package com.yungnickyoung.minecraft.bettercaves.world.cave;

import com.yungnickyoung.minecraft.bettercaves.util.FastNoise;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

/**
 * Base class for all Better Cave types. Cannot be instantiated - child classes must override the generate() method.
 * Child classes should also properly tweak noise and turbulence parameters in the constructor.
 */
public abstract class BetterCave {

    World world;
    FastNoise noiseGenerator1;
    FastNoise noiseGenerator2;
    FastNoise turbulence;

    public BetterCave(World world) {
        this.world = world;
        int worldSeed = (int)(world.getSeed());

        // Noise generators - these use ridged multi-fractals.
        // The intersection of these two functions is used to generate a single noise value, which is necessary
        // for three-dimensional cave generation.
        noiseGenerator1 = new FastNoise();
        noiseGenerator2 = new FastNoise();

        // Optional turbulence - uses fBM fractal.
        // Tends to make caves less smooth/noticeably patterned. Can be good for generating a "cave feel"
        turbulence = new FastNoise();

        // Initialize fractal types and seeds. These are universal across all BetterCaves
        noiseGenerator1.SetFractalType(FastNoise.FractalType.RigidMulti);
        this.noiseGenerator1.SetSeed(worldSeed);

        noiseGenerator2.SetFractalType(FastNoise.FractalType.RigidMulti);
        this.noiseGenerator2.SetSeed(worldSeed + 1);

        turbulence.SetNoiseType(FastNoise.NoiseType.PerlinFractal);
        turbulence.SetFractalType(FastNoise.FractalType.FBM);
        this.turbulence.SetSeed(worldSeed + 1000);
    }

    /**
     * Generates caves for a given chunk. Note that the coordinate parameters are
     * on the chunk grid; they are NOT block coordinates.
     * @param chunkX the hunk x-coordinate
     * @param chunkZ the chunk z-coordinate
     * @param primer the chunk's primer object
     */
    abstract public void generate(int chunkX, int chunkZ, ChunkPrimer primer);

    // DEBUG vals used for logging
    final int CHUNKS_PER_REPORT = 5000;
    int numChunksGenerated = 0;
    double avgNoise = 0;
    double maxNoise = -10;
    double minNoise = 10;
}
