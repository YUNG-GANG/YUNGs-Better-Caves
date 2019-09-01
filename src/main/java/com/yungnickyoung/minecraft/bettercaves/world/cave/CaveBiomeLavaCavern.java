package com.yungnickyoung.minecraft.bettercaves.world.cave;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseGen;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseTuple;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCaveUtil;
import com.yungnickyoung.minecraft.bettercaves.util.FastNoise;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.List;
import java.util.Map;

public class CaveBiomeLavaCavern {
    private World world;
    private long seed;

    private NoiseGen simplexNoiseGen;
    private NoiseGen perlinNoiseGen;

    public CaveBiomeLavaCavern(World world) {
        this.world = world;
        this.seed = world.getSeed();

        simplexNoiseGen = new NoiseGen(
                FastNoise.NoiseType.SimplexFractal,
                world,
                Configuration.caveSettings.simplexFractalCave.fractalOctaves,
                Configuration.caveSettings.simplexFractalCave.fractalGain,
                Configuration.caveSettings.simplexFractalCave.fractalFrequency,
                Configuration.caveSettings.simplexFractalCave.turbulenceOctaves,
                Configuration.caveSettings.simplexFractalCave.turbulenceGain,
                Configuration.caveSettings.simplexFractalCave.turbulenceFrequency,
                Configuration.caveSettings.simplexFractalCave.enableTurbulence,
                Configuration.caveSettings.simplexFractalCave.yCompression,
                Configuration.caveSettings.simplexFractalCave.xzCompression
        );

        perlinNoiseGen = new NoiseGen(
                FastNoise.NoiseType.PerlinFractal,
                world,
                Configuration.caveSettings.invertedPerlinCavern.fractalOctaves,
                Configuration.caveSettings.invertedPerlinCavern.fractalGain,
                Configuration.caveSettings.invertedPerlinCavern.fractalFrequency,
                Configuration.caveSettings.invertedPerlinCavern.turbulenceOctaves,
                Configuration.caveSettings.invertedPerlinCavern.turbulenceGain,
                Configuration.caveSettings.invertedPerlinCavern.turbulenceFrequency,
                Configuration.caveSettings.invertedPerlinCavern.enableTurbulence,
                Configuration.caveSettings.invertedPerlinCavern.yCompression,
                Configuration.caveSettings.invertedPerlinCavern.xzCompression
        );
    }

    /**
     * Generates caves and caverns for a given chunk. Note that the coordinate parameters are
     * on the chunk grid; they are NOT block coordinates.
     * @param chunkX the hunk x-coordinate
     * @param chunkZ the chunk z-coordinate
     * @param primer the chunk's primer object
     */
    public void generate(int chunkX, int chunkZ, ChunkPrimer primer) {
        if (Settings.DEBUG_WORLD_GEN) {
            debugGenerate(chunkX, chunkZ, primer);
            return;
        }

        // Find the lowest and highest surface altitudes in this chunk
        int maxSurfaceHeight = BetterCaveUtil.getMaxSurfaceHeight(primer);
        int minSurfaceHeight = BetterCaveUtil.getMinSurfaceHeight(primer);

        /* ========================= Import cave variables from the config ========================= */
        // Bottom y-coordinate at which caves (not caverns) should start spawning
        int simplexCaveBottom = Configuration.caveSettings.simplexFractalCave.caveBottom;

        // Top y-coordinate at which caves (not caverns) should start spawning
        int simplexCaveTop = maxSurfaceHeight;

        // Altitude at which caves start closing off so they aren't all open to the surface
        int simplexCaveTransitionBoundary = maxSurfaceHeight - 20;

        // Number of noise values to calculate. Their intersection will be taken to determine a single noise value
        int simplexNumGens = Configuration.caveSettings.simplexFractalCave.numGenerators;

        /* ======================== Import cavern variables from the config ======================== */
        // Bottom y-coordinate at which caverns (not caves) should start spawning
        int perlinCavernBottom = Configuration.caveSettings.invertedPerlinCavern.caveBottom;

        // Top y-coordinate at which caverns (not caves) close off
        int perlinCavernTop = Configuration.caveSettings.invertedPerlinCavern.caveTop;

        // Altitude at which caverns start closing off
        int perlinCavernTransitionBoundary = Configuration.caveSettings.invertedPerlinCavern.caveTransitionTop;

        // Number of noise values to calculate. Their intersection will be taken to determine a single noise value
        int perlinNumGens = Configuration.caveSettings.invertedPerlinCavern.numGenerators;

        /* ========== Generate noise for caves (using Simplex noise) and caverns (using Perlin noise) ========== */
        // The noise for an individual block is represented by a NoiseTuple, which is essentially an n-tuple of
        // floats, where n is equal to the number of generators passed to the function (perlinNumGens or simplexNumGens)
        List<NoiseTuple[][]> perlinNoises =
                perlinNoiseGen.generateNoise(chunkX, chunkZ, perlinCavernBottom, perlinCavernTop, perlinNumGens);
        List<NoiseTuple[][]> simplexNoises =
                simplexNoiseGen.generateNoise(chunkX, chunkZ, simplexCaveBottom, simplexCaveTop, simplexNumGens);

        // Do some pre-processing on the simplex noises to facilitate better cave generation.
        // See the javadoc for the function for more info.
        simplexNoises = preprocessCaveNoise(simplexNoises, simplexCaveTop, simplexCaveBottom, simplexNumGens);

        /* =============== Dig out caves and caverns in this chunk, based on noise values =============== */
        for (int realY = simplexCaveTop; realY >= perlinCavernBottom; realY--) {
            for (int localX = 0; localX < 16; localX++) {
                for (int localZ = 0; localZ < 16; localZ++) {
                    List<Float> perlinNoiseLayer, simplexNoiseLayer;
                    boolean digBlock = false;

                    // Process inverse perlin noise for big caverns at low altitudes
                    if (realY <= perlinCavernTop) {
                        // Compute a single noise value to represent all the noise values in the NoiseTuple
                        float perlinNoise = 1;
                        perlinNoiseLayer = perlinNoises.get(perlinCavernTop - realY)[localX][localZ].getNoiseValues();
                        for (float noise : perlinNoiseLayer)
                            perlinNoise *= noise;

                        // Adjust threshold if we're in the transition range to provide smoother transition into ceiling
                        float noiseThreshold = Configuration.caveSettings.invertedPerlinCavern.noiseThreshold;
                        if (realY >= perlinCavernTransitionBoundary)
                            noiseThreshold *= Math.max((float) (realY - perlinCavernTop) / (perlinCavernTransitionBoundary - perlinCavernTop), .5f);

                        // Close off caverns if we're in ease-in depth range (probably means this is under the ocean)
                        if (realY >= minSurfaceHeight - 5)
                            noiseThreshold *= (float) (realY - perlinCavernTop) / (minSurfaceHeight - 5 - perlinCavernTop);

                        // Mark block for removal if the noise passes the threshold check
                        if (perlinNoise < noiseThreshold)
                            digBlock = true;
                    }

                    // Process simplex noise. We can ignore this if
                    // we've already determined we need to dig this block.
                    if (realY >= simplexCaveBottom && !digBlock) {
                        // Compute a single noise value to represent all the noise values in the NoiseTuple
                        float simplexNoise = 0;
                        simplexNoiseLayer= simplexNoises.get(simplexCaveTop - realY)[localX][localZ].getNoiseValues();
                        for (float noise : simplexNoiseLayer)
                            simplexNoise += noise;

                        simplexNoise /= simplexNoiseLayer.size();

                        // Close off caves if we're in ease-in depth range
                        float noiseThreshold = Configuration.caveSettings.simplexFractalCave.noiseThreshold;
                        if (realY >= simplexCaveTransitionBoundary)
                            noiseThreshold *= (1 + .3f * ((float)(realY - simplexCaveTransitionBoundary) / (simplexCaveTop - simplexCaveTransitionBoundary)));

                        // Mark block for removal if the noise passes the threshold check
                        if (simplexNoise > noiseThreshold)
                            digBlock = true;
                    }

                    // Dig/remove the block if it passed the threshold check
                    if (digBlock) {
                        // Check for adjacent water blocks to avoid breaking into lakes or oceans
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
                }
            }
        }

        /* ============ Post-Processing to remove any singular floating blocks in the ease-in range ============ */
        IBlockState BlockStateAir = Blocks.AIR.getDefaultState();
        for (int realY = simplexCaveTransitionBoundary + 1; realY < simplexCaveTop - 1; realY++) {
            for (int localX = 0; localX < 16; localX++) {
                for (int localZ = 0; localZ < 16; localZ++) {
                    IBlockState currBlock = primer.getBlockState(localX, realY, localZ);

                    if (BetterCaveUtil.canReplaceBlock(currBlock, BlockStateAir)
                            && primer.getBlockState(localX, realY + 1, localZ) == BlockStateAir
                            && primer.getBlockState(localX, realY - 1, localZ) == BlockStateAir
                    )
                        BetterCaveUtil.digBlock(world, primer, localX, realY, localZ, chunkX, chunkZ);
                }
            }
        }
    }

    public long getSeed() {
        return this.seed;
    }

    private void debugGenerate(int chunkX, int chunkZ, ChunkPrimer primer) {
        int maxSurfaceHeight = 128;
        int simplexCaveBottom = Configuration.caveSettings.simplexFractalCave.caveBottom;
        int simplexCaveTop = maxSurfaceHeight;
        int simplexNumGens = Configuration.caveSettings.simplexFractalCave.numGenerators;
        int perlinCavernBottom = Configuration.caveSettings.invertedPerlinCavern.caveBottom;
        int perlinCavernTop = Configuration.caveSettings.invertedPerlinCavern.caveTop;
        int perlinCavernTransitionBoundary = Configuration.caveSettings.invertedPerlinCavern.caveTransitionTop;
        int perlinNumGens = Configuration.caveSettings.invertedPerlinCavern.numGenerators;

        List<NoiseTuple[][]> perlinNoises =
                perlinNoiseGen.generateNoise(chunkX, chunkZ, perlinCavernBottom, perlinCavernTop, perlinNumGens);
        List<NoiseTuple[][]> simplexNoises =
                simplexNoiseGen.generateNoise(chunkX, chunkZ, simplexCaveBottom, simplexCaveTop, simplexNumGens);

        simplexNoises = preprocessCaveNoise(simplexNoises, simplexCaveTop, simplexCaveBottom, simplexNumGens);

        /* =============== Dig out caves and caverns in this chunk, based on noise values =============== */
        for (int realY = simplexCaveTop; realY >= perlinCavernBottom; realY--) {
            for (int localX = 0; localX < 16; localX++) {
                int realX = (chunkX * 16) + localX;
                for (int localZ = 0; localZ < 16; localZ++) {
                    List<Float> perlinNoiseLayer, simplexNoiseLayer;
                    boolean digBlock = false;

                    // Process inverse perlin noise for big caverns at low altitudes
                    if (realY <= perlinCavernTop) {
                        // Compute a single noise value to represent all the noise values in the NoiseTuple
                        float perlinNoise = 1;
                        perlinNoiseLayer = perlinNoises.get(perlinCavernTop - realY)[localX][localZ].getNoiseValues();
                        for (float noise : perlinNoiseLayer)
                            perlinNoise *= noise;

                        // Adjust threshold if we're in the transition range to provide smoother transition into ceiling
                        float noiseThreshold = Configuration.caveSettings.invertedPerlinCavern.noiseThreshold;
                        if (realY >= perlinCavernTransitionBoundary)
                            noiseThreshold *= Math.max((float) (realY - perlinCavernTop) / (perlinCavernTransitionBoundary - perlinCavernTop), .5f);

                        // Mark block for removal if the noise passes the threshold check
                        if (perlinNoise < noiseThreshold)
                            digBlock = true;
                    }

                    // Process simplex noise. We can ignore this if
                    // we've already determined we need to dig this block.
                    if (realY >= simplexCaveBottom && !digBlock) {
                        // Compute a single noise value to represent all the noise values in the NoiseTuple
                        float simplexNoise = 0;
                        simplexNoiseLayer= simplexNoises.get(simplexCaveTop - realY)[localX][localZ].getNoiseValues();
                        for (float noise : simplexNoiseLayer)
                            simplexNoise += noise;

                        simplexNoise /= simplexNoiseLayer.size();

                        float noiseThreshold = Configuration.caveSettings.simplexFractalCave.noiseThreshold;

                        // Mark block for removal if the noise passes the threshold check
                        if (simplexNoise > noiseThreshold)
                            digBlock = true;
                    }

                    if (digBlock && realX > 0) {
                        primer.setBlockState(localX, realY, localZ, Blocks.QUARTZ_BLOCK.getDefaultState());
                    } else {
                        primer.setBlockState(localX, realY, localZ, Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
    }

    /**
     * Pre-processing for simplex noises used in cave (not cavern) generation.
     * Noise values are adjusted based on the blocks below them in order to give more headroom to the player.
     * @param noises List of 2D array of NoiseTuples of size 16 x 16
     * @param caveTop The y-coordinate of the top altitude at which caves start spawning
     * @param caveBottom The y-coordinate of the low altitude at which caves start spawning
     * @param numGens The number of noise values to calculate per block. This is dubbed "number of generators" since
     *                this value is the number of times we "generate" a noise value per block
     * @return The modified list of 2D arrays of NoiseTuples
     */
    private List<NoiseTuple[][]> preprocessCaveNoise(List<NoiseTuple[][]> noises, int caveTop, int caveBottom, int numGens) {
        /* Adjust simplex noise values based on blocks above in order to give the player more headroom */
        for (int realY = caveTop; realY >= caveBottom; realY--) {
            for (int localX = 0; localX < 16; localX++) {
                for (int localZ = 0; localZ < 16; localZ++) {
                    NoiseTuple sBlockNoise = noises.get(caveTop - realY)[localX][localZ];
                    float avgSNoise = 0;

                    for (float noise : sBlockNoise.getNoiseValues())
                        avgSNoise += noise;

                    avgSNoise /= sBlockNoise.size();

                    if (avgSNoise > Configuration.caveSettings.simplexFractalCave.noiseThreshold) {
                        /* Adjust noise values of blocks above to give the player more head room */
                        if (realY < caveTop) {
                            NoiseTuple tupleAbove = noises.get(caveTop - realY - 1)[localX][localZ];
                            for (int i = 0; i < numGens; i++)
                                tupleAbove.set(i, (.1f * tupleAbove.get(i)) + (.9f * sBlockNoise.get(i)));
                        }

                        if (realY < caveTop - 1) {
                            NoiseTuple tupleTwoAbove = noises.get(caveTop - realY - 2)[localX][localZ];
                            for (int i = 0; i < numGens; i++)
                                tupleTwoAbove.set(i, (.15f * tupleTwoAbove.get(i)) + (.85f * sBlockNoise.get(i)));
                        }
                    }
                }
            }
        }
        return noises;
    }


    public void generateColumn(int chunkX, int chunkZ, ChunkPrimer primer, int localX, int localZ, int maxSurfaceHeight, int minSurfaceHeight) {
        /* ========================= Import cave variables from the config ========================= */
        // Bottom y-coordinate at which caves (not caverns) should start spawning
        int simplexCaveBottom = Configuration.caveSettings.simplexFractalCave.caveBottom;

        // Top y-coordinate at which caves (not caverns) should start spawning
        int simplexCaveTop = maxSurfaceHeight;

        // Altitude at which caves start closing off so they aren't all open to the surface
        int simplexCaveTransitionBoundary = maxSurfaceHeight - 20;

        // Number of noise values to calculate. Their intersection will be taken to determine a single noise value
        int simplexNumGens = Configuration.caveSettings.simplexFractalCave.numGenerators;

        /* ======================== Import cavern variables from the config ======================== */
        // Bottom y-coordinate at which caverns (not caves) should start spawning
        int perlinCavernBottom = Configuration.caveSettings.invertedPerlinCavern.caveBottom;

        // Top y-coordinate at which caverns (not caves) close off
        int perlinCavernTop = Configuration.caveSettings.invertedPerlinCavern.caveTop;

        // Altitude at which caverns start closing off
        int perlinCavernTransitionBoundary = Configuration.caveSettings.invertedPerlinCavern.caveTransitionTop;

        // Number of noise values to calculate. Their intersection will be taken to determine a single noise value
        int perlinNumGens = Configuration.caveSettings.invertedPerlinCavern.numGenerators;

        /* ========== Generate noise for caves (using Simplex noise) and caverns (using Perlin noise) ========== */
        // The noise for an individual block is represented by a NoiseTuple, which is essentially an n-tuple of
        // floats, where n is equal to the number of generators passed to the function (perlinNumGens or simplexNumGens)
        Map<Integer, NoiseTuple> perlinNoises =
                perlinNoiseGen.generateNoiseCol(chunkX, chunkZ, perlinCavernBottom, perlinCavernTop, perlinNumGens, localX, localZ);
        Map<Integer, NoiseTuple> simplexNoises =
                simplexNoiseGen.generateNoiseCol(chunkX, chunkZ, simplexCaveBottom, simplexCaveTop, simplexNumGens, localX, localZ);

        // Do some pre-processing on the simplex noises to facilitate better cave generation.
        // See the javadoc for the function for more info.
        simplexNoises = preprocessCaveNoiseCol(simplexNoises, simplexCaveTop, simplexCaveBottom, simplexNumGens);

        /* =============== Dig out caves and caverns in this chunk, based on noise values =============== */
        for (int realY = simplexCaveTop; realY >= perlinCavernBottom; realY--) {
            List<Float> perlinNoiseBlock, simplexNoiseBlock;
            boolean digBlock = false;

            // Process inverse perlin noise for big caverns at low altitudes
            if (realY <= perlinCavernTop) {
                // Compute a single noise value to represent all the noise values in the NoiseTuple
                float perlinNoise = 1;
                perlinNoiseBlock = perlinNoises.get(realY).getNoiseValues();
                for (float noise : perlinNoiseBlock)
                    perlinNoise *= noise;

                // Adjust threshold if we're in the transition range to provide smoother transition into ceiling
                float noiseThreshold = Configuration.caveSettings.invertedPerlinCavern.noiseThreshold;
                if (realY >= perlinCavernTransitionBoundary)
                    noiseThreshold *= Math.max((float) (realY - perlinCavernTop) / (perlinCavernTransitionBoundary - perlinCavernTop), .5f);

                // Close off caverns if we're in ease-in depth range (probably means this is under the ocean)
                if (realY >= minSurfaceHeight - 5)
                    noiseThreshold *= (float) (realY - perlinCavernTop) / (minSurfaceHeight - 5 - perlinCavernTop);

                // Mark block for removal if the noise passes the threshold check
                if (perlinNoise < noiseThreshold)
                    digBlock = true;
            }

            // Process simplex noise. We can ignore this if
            // we've already determined we need to dig this block.
            if (realY >= simplexCaveBottom && !digBlock) {
                // Compute a single noise value to represent all the noise values in the NoiseTuple
                float simplexNoise = 0;
                simplexNoiseBlock = simplexNoises.get(realY).getNoiseValues();
                for (float noise : simplexNoiseBlock)
                    simplexNoise += noise;

                simplexNoise /= simplexNoiseBlock.size();

                // Close off caves if we're in ease-in depth range
                float noiseThreshold = Configuration.caveSettings.simplexFractalCave.noiseThreshold;
                if (realY >= simplexCaveTransitionBoundary)
                    noiseThreshold *= (1 + .3f * ((float)(realY - simplexCaveTransitionBoundary) / (simplexCaveTop - simplexCaveTransitionBoundary)));

                // Mark block for removal if the noise passes the threshold check
                if (simplexNoise > noiseThreshold)
                    digBlock = true;
            }

            // Dig/remove the block if it passed the threshold check
            if (digBlock) {
                // Check for adjacent water blocks to avoid breaking into lakes or oceans
                if (primer.getBlockState(localX, realY + 1, localZ).getMaterial() == Material.WATER)
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
        }

        /* ============ Post-Processing to remove any singular floating blocks in the ease-in range ============ */
        IBlockState BlockStateAir = Blocks.AIR.getDefaultState();
        for (int realY = simplexCaveTransitionBoundary + 1; realY < simplexCaveTop - 1; realY++) {
            IBlockState currBlock = primer.getBlockState(localX, realY, localZ);

            if (BetterCaveUtil.canReplaceBlock(currBlock, BlockStateAir)
                    && primer.getBlockState(localX, realY + 1, localZ) == BlockStateAir
                    && primer.getBlockState(localX, realY - 1, localZ) == BlockStateAir
            )
                BetterCaveUtil.digBlock(world, primer, localX, realY, localZ, chunkX, chunkZ);
        }
    }


    private Map<Integer, NoiseTuple> preprocessCaveNoiseCol(Map<Integer, NoiseTuple> noises, int caveTop, int caveBottom, int numGens) {
        /* Adjust simplex noise values based on blocks above in order to give the player more headroom */
        for (int realY = caveTop; realY >= caveBottom; realY--) {
            NoiseTuple sBlockNoise = noises.get(realY);
            float avgSNoise = 0;

            for (float noise : sBlockNoise.getNoiseValues())
                avgSNoise += noise;

            avgSNoise /= sBlockNoise.size();

            if (avgSNoise > Configuration.caveSettings.simplexFractalCave.noiseThreshold) {
                /* Adjust noise values of blocks above to give the player more head room */
                if (realY < caveTop) {
                    NoiseTuple tupleAbove = noises.get(realY + 1);
                    for (int i = 0; i < numGens; i++)
                        tupleAbove.set(i, (.1f * tupleAbove.get(i)) + (.9f * sBlockNoise.get(i)));
                }

                if (realY < caveTop - 1) {
                    NoiseTuple tupleTwoAbove = noises.get(realY + 2);
                    for (int i = 0; i < numGens; i++)
                        tupleTwoAbove.set(i, (.15f * tupleTwoAbove.get(i)) + (.85f * sBlockNoise.get(i)));
                }
            }
        }
        return noises;
    }

    private List<NoiseTuple[][]> preprocessCaveNoiseRegion(List<NoiseTuple[][]> noises, int caveTop, int caveBottom, int numGens, int startX, int endX, int startZ, int endZ) {
        /* Adjust simplex noise values based on blocks above in order to give the player more headroom */
        for (int realY = caveTop; realY >= caveBottom; realY--) {
            for (int localX = startX; localX <= endX; localX++) {
                for (int localZ = startZ; localZ <= endZ; localZ++) {
                    NoiseTuple sBlockNoise = noises.get(caveTop - realY)[localX - startX][localZ - startZ];
                    float avgSNoise = 0;

                    for (float noise : sBlockNoise.getNoiseValues())
                        avgSNoise += noise;

                    avgSNoise /= sBlockNoise.size();

                    if (avgSNoise > Configuration.caveSettings.simplexFractalCave.noiseThreshold) {
                        /* Adjust noise values of blocks above to give the player more head room */
                        if (realY < caveTop) {
                            NoiseTuple tupleAbove = noises.get(caveTop - realY - 1)[localX - startX][localZ - startZ];
                            for (int i = 0; i < numGens; i++)
                                tupleAbove.set(i, (.1f * tupleAbove.get(i)) + (.9f * sBlockNoise.get(i)));
                        }

                        if (realY < caveTop - 1) {
                            NoiseTuple tupleTwoAbove = noises.get(caveTop - realY - 2)[localX - startX][localZ - startZ];
                            for (int i = 0; i < numGens; i++)
                                tupleTwoAbove.set(i, (.15f * tupleTwoAbove.get(i)) + (.85f * sBlockNoise.get(i)));
                        }
                    }
                }
            }
        }
        return noises;
    }
}
