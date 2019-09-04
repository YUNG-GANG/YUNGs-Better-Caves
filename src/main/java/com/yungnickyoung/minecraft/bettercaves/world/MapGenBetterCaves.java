package com.yungnickyoung.minecraft.bettercaves.world;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCaveUtil;
import com.yungnickyoung.minecraft.bettercaves.util.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.world.cave.OGSimplexCave;
import com.yungnickyoung.minecraft.bettercaves.world.cave.SimplexCave2;
import com.yungnickyoung.minecraft.bettercaves.world.cave.TestCave;
import com.yungnickyoung.minecraft.bettercaves.world.cavern.CaveBiomeFlooredCavern;
import com.yungnickyoung.minecraft.bettercaves.world.cavern.CaveBiomeLavaCavern;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenCaves;

import javax.annotation.Nonnull;
import javax.vecmath.Vector2f;

public class MapGenBetterCaves extends MapGenCaves {
    private CaveBiomeLavaCavern caveBiomeLavaCavern;
    private CaveBiomeFlooredCavern caveBiomeFlooredCavern;
    private MapGenCaves defaultCaveGen;
    private OGSimplexCave ogSimplexCave;
    private TestCave testCave;
    private SimplexCave2 simplexCave2;

    private FastNoise cavernBiomeController;
    private FastNoise controllerJitter;

    public MapGenBetterCaves() {
    }

    @Override
    public void generate(World worldIn, int chunkX, int chunkZ, @Nonnull ChunkPrimer primer) {
        if (world == null) { // First call - initialize all cave types
            world = worldIn;
            this.caveBiomeLavaCavern = new CaveBiomeLavaCavern(worldIn);
            this.caveBiomeFlooredCavern = new CaveBiomeFlooredCavern(worldIn);
            defaultCaveGen = new MapGenCaves();

            this.cavernBiomeController = new FastNoise();
            this.cavernBiomeController.SetSeed((int)worldIn.getSeed());
            this.cavernBiomeController.SetFrequency(.0025f);
            this.cavernBiomeController.SetCellularDistanceFunction(FastNoise.CellularDistanceFunction.Natural);

            this.controllerJitter = new FastNoise();
            this.controllerJitter.SetSeed((int)(worldIn.getSeed()) + 69420);
            this.controllerJitter.SetGradientPerturbAmp(60);
            this.controllerJitter.SetFrequency(.01f);

            this.ogSimplexCave = new OGSimplexCave(worldIn);
            this.testCave = new TestCave(worldIn);
            this.simplexCave2 = new SimplexCave2(worldIn);
        }

        // Find the lowest and highest surface altitudes in this chunk
        int maxSurfaceHeight = BetterCaveUtil.getMaxSurfaceHeight(primer);
        int minSurfaceHeight = BetterCaveUtil.getMinSurfaceHeight(primer);

        // Determine noise thresholds for cavern spawns based on config
        float lavaCavernThreshold = -.6f;
        float flooredCavernThreshold = .6f;

        switch (Configuration.caveSettings.lavaCavernFrequency) {
            case VeryRare:
                lavaCavernThreshold = -.8f;
                break;
            case Rare:
                lavaCavernThreshold = -.6f;
                break;
            case Common:
                lavaCavernThreshold = -.4f;
                break;
            case VeryCommon:
                lavaCavernThreshold = -.1f;
                break;
        }

        switch (Configuration.caveSettings.flooredCavernFrequency) {
            case VeryRare:
                flooredCavernThreshold = .8f;
                break;
            case Rare:
                flooredCavernThreshold = .6f;
                break;
            case Common:
                flooredCavernThreshold = .4f;
                break;
            case VeryCommon:
                flooredCavernThreshold = .1f;
                break;
        }

        // Only use Better Caves generation in overworld
        if (worldIn.provider.getDimension() == 0) {
            for (int localX = 0; localX < 16; localX++) {
                for (int localZ = 0; localZ < 16; localZ++) {
//                    Vector2f f = new Vector2f(((chunkX * 16) + localX), ((chunkZ * 16) + localZ));
////                    controllerJitter.GradientPerturb(f);
//                    float noiseVal = cavernBiomeController.GetNoise(f.x, f.y);
//
//                    if (noiseVal < lavaCavernThreshold) {
//                        caveBiomeLavaCavern.generateColumn(chunkX, chunkZ, primer, localX, localZ,
//                                Configuration.caveSettings.invertedPerlinCavern.caveBottom,
//                                Configuration.caveSettings.invertedPerlinCavern.caveTop, maxSurfaceHeight,
//                                minSurfaceHeight);
//                        ogSimplexCave.generateColumn(chunkX, chunkZ, primer,localX, localZ, Configuration.caveSettings.simplexFractalCave.caveBottom, maxSurfaceHeight, maxSurfaceHeight, minSurfaceHeight);
//                    } else if (noiseVal >= lavaCavernThreshold && noiseVal <= flooredCavernThreshold) {
//                        ogSimplexCave.generateColumn(chunkX, chunkZ, primer, localX, localZ, 1, maxSurfaceHeight, maxSurfaceHeight, minSurfaceHeight);
//                    } else {
//                        caveBiomeFlooredCavern.generateColumn(chunkX, chunkZ, primer, localX, localZ,
//                                Configuration.caveSettings.invertedPerlinCavern.caveBottom,
//                                Configuration.caveSettings.invertedPerlinCavern.caveTop, maxSurfaceHeight,
//                                minSurfaceHeight);
//                        ogSimplexCave.generateColumn(chunkX, chunkZ, primer,localX, localZ, Configuration.caveSettings.simplexFractalCave.caveBottom, maxSurfaceHeight, maxSurfaceHeight, minSurfaceHeight);
//                    }

                    testCave.generateColumn(chunkX, chunkZ, primer, localX, localZ, 1, maxSurfaceHeight, maxSurfaceHeight, minSurfaceHeight);
                }
            }
        } else
            defaultCaveGen.generate(worldIn, chunkX, chunkZ, primer);
    }
}
