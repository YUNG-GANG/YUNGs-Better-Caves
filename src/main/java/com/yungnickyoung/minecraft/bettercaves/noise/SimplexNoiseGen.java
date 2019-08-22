package com.yungnickyoung.minecraft.bettercaves.noise;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.util.FastNoise;
import net.minecraft.world.World;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class SimplexNoiseGen extends NoiseGen {
    // List of all distinct generators
    private List<FastNoise> listNoiseGens = new ArrayList<>();
    private FastNoise turbulenceGen = new FastNoise();

    public SimplexNoiseGen (long seed) {
        super(seed);
        initializeTurbulenceGen();
    }

    public SimplexNoiseGen(World world) {
        this(world.getSeed());
    }

    @Override
    public List<NoiseTuple[][]> generateNoise(int chunkX, int chunkZ, int minHeight, int maxHeight, int numGenerators) {
        initializeNoiseGens(numGenerators);

        List<NoiseTuple[][]> noises = new ArrayList<>();
        int noisesIndex = 0;

        for (int y = maxHeight; y >= minHeight; y--) {
            NoiseTuple[][] noiseLayer = new NoiseTuple[16][16];

            for (int localX = 0; localX < 16; localX++) {
                int realX = localX + 16 * chunkX;

                for (int localZ = 0; localZ < 16; localZ++) {
                    int realZ = localZ + 16 * chunkZ;

                    Vector3f f = new Vector3f(realX, y, realZ);

                    // Use turbulence function to apply gradient perturbation, if enabled
                    if (Configuration.simplexFractalCave.enableTurbulence)
                        turbulenceGen.GradientPerturbFractal(f);

                    // Create NoiseTuple for this block
                    NoiseTuple newTuple = new NoiseTuple();
                    for (int i = 0; i < numGenerators; i++)
                        newTuple.add(listNoiseGens.get(i).GetNoise(f.x, f.y, f.z));

                    /* Adjust noise values based on blocks above in order to give players more headroom */
                    // Adjust block immediately above:
                    if (y < maxHeight - 1) {
                        NoiseTuple tupleAbove = noises.get(noisesIndex - 1)[localX][localZ];
                        boolean updateNeeded = true; // Flag for determining if the noise of this block is large enough
                        // to warrant updating the noise val of the block above

                        // Check if we need to update the block above
                        for (int i = 0; i < numGenerators; i++) {
                            if (newTuple.get(i) < tupleAbove.get(i)) {
                                updateNeeded = false;
                                break;
                            }
                        }

                        // Update the block above, if the check was passed
                        if (updateNeeded) {
                            for (int i = 0; i < numGenerators; i++) {
                                tupleAbove.set(i, (.2f * tupleAbove.get(i)) + (.8f * newTuple.get(i)));
                            }
                        }
                    }

                    // Adjust block two blocks above:
                    if (y < maxHeight - 2) {
                        NoiseTuple tupleTwoAbove = noises.get(noisesIndex - 2)[localX][localZ];
                        boolean updateNeeded = true; // Flag for determining if the noise of this block is large enough
                        // to warrant updating the noise val of the block above

                        // Check if we need to update the block above
                        for (int i = 0; i < numGenerators; i++) {
                            if (newTuple.get(i) < tupleTwoAbove.get(i)) {
                                updateNeeded = false;
                                break;
                            }
                        }

                        // Update the block above, if the check was passed
                        if (updateNeeded) {
                            for (int i = 0; i < numGenerators; i++) {
                                tupleTwoAbove.set(i, (.2f * tupleTwoAbove.get(i)) + (.8f * newTuple.get(i)));
                            }
                        }
                    }

                    /* Adjust noise values based on horizontal neighbors. This might help prevent cave fracturing.*/
                    if (localX > 0) {
                        NoiseTuple neighbor = noiseLayer[localX - 1][localZ];
                        for (int i = 0; i < numGenerators; i++)
                            neighbor.set(i, (neighbor.get(i) * .6f) + (newTuple.get(i) * .4f));

                    }

                    if (localZ > 0) {
                        NoiseTuple neighbor = noiseLayer[localX][localZ - 1];
                        for (int i = 0; i < numGenerators; i++)
                            neighbor.set(i, (neighbor.get(i) * .5f) + (newTuple.get(i) * .5f));

                    }

                    noiseLayer[localX][localZ] = newTuple;
               }
            }
            noises.add(noiseLayer);
            noisesIndex++;
        }

        // Attempt smoothing
        if (Configuration.simplexFractalCave.enableSmoothing) {
            for (int y = 1; y < noises.size() - 1; y++) {
                NoiseTuple[][] layer = noises.get(y);
                NoiseTuple[][] layerUp = noises.get(y + 1);
                NoiseTuple[][] layerDown = noises.get(y - 1);

                for (int x = 1; x < 15; x++) {
                    for (int z = 1; z < 15; z++) {
                        NoiseTuple curr = layer[x][z];
                        NoiseTuple left = layer[x - 1][z];
                        NoiseTuple right = layer[x + 1][z];
                        NoiseTuple front = layer[x][z + 1];
                        NoiseTuple back = layer[x][z - 1];
                        NoiseTuple up = layerUp[x][z];
                        NoiseTuple down = layerDown[x][z];
                        NoiseTuple edge1 = layerUp[x + 1][z];
                        NoiseTuple edge2 = layerUp[x - 1][z];
                        NoiseTuple edge3 = layerUp[x][z + 1];
                        NoiseTuple edge4 = layerUp[x][z - 1];
                        NoiseTuple edge5 = layerDown[x + 1][z];
                        NoiseTuple edge6 = layerDown[x - 1][z];
                        NoiseTuple edge7 = layerDown[x][z + 1];
                        NoiseTuple edge8 = layerDown[x][z - 1];


                        for (int i = 0; i < curr.size(); i++) {
                            noises.get(y)[x][z].set(i, (left.get(i) + right.get(i) + front.get(i) + back.get(i) + up.get(i) + down.get(i) + curr.get(i)
                             + edge1.get(i) + edge2.get(i) + edge3.get(i) + edge4.get(i) + edge5.get(i) + edge6.get(i) + edge7.get(i) + edge8.get(i)) / 15f);
                        }
                    }
                }
            }
        }

        return noises;
    }

    private void initializeNoiseGens(int numGenerators) {
        if (numGenerators <= listNoiseGens.size())
            return;

        int numGensNeeded = numGenerators - listNoiseGens.size();
        int seedModifier = listNoiseGens.size();

        for (int i = 0; i < numGensNeeded; i++) {
            FastNoise noiseGen = new FastNoise();
            noiseGen.SetFractalType(FastNoise.FractalType.RigidMulti);
            noiseGen.SetSeed((int) (this.getSeed()) + (1111 * seedModifier));
            noiseGen.SetNoiseType(FastNoise.NoiseType.SimplexFractal);
            noiseGen.SetFractalOctaves(Configuration.simplexFractalCave.fractalOctaves);
            noiseGen.SetFractalGain(Configuration.simplexFractalCave.fractalGain);
            noiseGen.SetFrequency(Configuration.simplexFractalCave.fractalFrequency);

            listNoiseGens.add(noiseGen);
            seedModifier++;
        }

    }

    private void initializeTurbulenceGen() {
        turbulenceGen.SetNoiseType(FastNoise.NoiseType.PerlinFractal);
        turbulenceGen.SetFractalType(FastNoise.FractalType.FBM);
        turbulenceGen.SetSeed((int)(this.getSeed()) + 69);
        turbulenceGen.SetFractalOctaves(Configuration.simplexFractalCave.turbulenceOctaves);
        turbulenceGen.SetFractalGain(Configuration.simplexFractalCave.turbulenceGain);
        turbulenceGen.SetFrequency(Configuration.simplexFractalCave.turbulenceFrequency);
    }
}
