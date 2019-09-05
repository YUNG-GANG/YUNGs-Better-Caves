package com.yungnickyoung.minecraft.bettercaves.world;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCaveUtil;
import com.yungnickyoung.minecraft.bettercaves.util.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.world.cave.BetterCave;
import com.yungnickyoung.minecraft.bettercaves.world.cave.CaveSimplex;
import com.yungnickyoung.minecraft.bettercaves.world.cave.TestCave;
import com.yungnickyoung.minecraft.bettercaves.world.cavern.BetterCavern;
import com.yungnickyoung.minecraft.bettercaves.world.cavern.CaveBiomeFlooredCavern;
import com.yungnickyoung.minecraft.bettercaves.world.cavern.CaveBiomeLavaCavern;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenCaves;

import javax.annotation.Nonnull;
import javax.vecmath.Vector2f;

public class MapGenBetterCaves extends MapGenCaves {
    private BetterCave caveSimplexBig;
    private BetterCave caveSimplexSmall;
    private BetterCave testCave;

    private CaveBiomeLavaCavern caveBiomeLavaCavern;
    private CaveBiomeFlooredCavern caveBiomeFlooredCavern;


    private MapGenCaves defaultCaveGen;

    private FastNoise cavernBiomeController;
    private FastNoise caveBiomeController;
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

            this.caveBiomeController = new FastNoise();
            this.caveBiomeController.SetSeed((int)worldIn.getSeed() + 999);
            this.caveBiomeController.SetFrequency(.005f);
            this.caveBiomeController.SetCellularDistanceFunction(FastNoise.CellularDistanceFunction.Natural);

            this.controllerJitter = new FastNoise();
            this.controllerJitter.SetSeed((int)(worldIn.getSeed()) + 69420);
            this.controllerJitter.SetGradientPerturbAmp(60);
            this.controllerJitter.SetFrequency(.01f);

            this.caveSimplexBig = new CaveSimplex(
                    world,
                    Configuration.caveSettings.bigSimplexCave.fractalOctaves,
                    Configuration.caveSettings.bigSimplexCave.fractalGain,
                    Configuration.caveSettings.bigSimplexCave.fractalFrequency,
                    Configuration.caveSettings.bigSimplexCave.numGenerators,
                    Configuration.caveSettings.bigSimplexCave.noiseThreshold,
                    Configuration.caveSettings.bigSimplexCave.turbulenceOctaves,
                    Configuration.caveSettings.bigSimplexCave.turbulenceGain,
                    Configuration.caveSettings.bigSimplexCave.turbulenceFrequency,
                    Configuration.caveSettings.bigSimplexCave.enableTurbulence,
                    Configuration.caveSettings.bigSimplexCave.yCompression,
                    Configuration.caveSettings.bigSimplexCave.xzCompression,
                    Configuration.caveSettings.bigSimplexCave.yAdjust,
                    Configuration.caveSettings.bigSimplexCave.yAdjustF1,
                    Configuration.caveSettings.bigSimplexCave.yAdjustF2
            );

            this.caveSimplexSmall = new CaveSimplex(
                    world,
                    Configuration.caveSettings.smallSimplexCave.fractalOctaves,
                    Configuration.caveSettings.smallSimplexCave.fractalGain,
                    Configuration.caveSettings.smallSimplexCave.fractalFrequency,
                    Configuration.caveSettings.smallSimplexCave.numGenerators,
                    Configuration.caveSettings.smallSimplexCave.noiseThreshold,
                    Configuration.caveSettings.smallSimplexCave.turbulenceOctaves,
                    Configuration.caveSettings.smallSimplexCave.turbulenceGain,
                    Configuration.caveSettings.smallSimplexCave.turbulenceFrequency,
                    Configuration.caveSettings.smallSimplexCave.enableTurbulence,
                    Configuration.caveSettings.smallSimplexCave.yCompression,
                    Configuration.caveSettings.smallSimplexCave.xzCompression,
                    Configuration.caveSettings.smallSimplexCave.yAdjust,
                    Configuration.caveSettings.smallSimplexCave.yAdjustF1,
                    Configuration.caveSettings.smallSimplexCave.yAdjustF2
            );

            this.testCave = new TestCave(
                    world,
                    Configuration.testSettings.fractalOctaves,
                    Configuration.testSettings.fractalGain,
                    Configuration.testSettings.fractalFrequency,
                    Configuration.testSettings.numGenerators,
                    Configuration.testSettings.noiseThreshold,
                    Configuration.testSettings.turbulenceOctaves,
                    Configuration.testSettings.turbulenceGain,
                    Configuration.testSettings.turbulenceFrequency,
                    Configuration.testSettings.enableTurbulence,
                    Configuration.testSettings.yCompression,
                    Configuration.testSettings.xzCompression,
                    Configuration.testSettings.yAdjust,
                    Configuration.testSettings.yAdjustF1,
                    Configuration.testSettings.yAdjustF2
            );
        }

        // Find the lowest and highest surface altitudes in this chunk
        int maxSurfaceHeight = BetterCaveUtil.getMaxSurfaceHeight(primer);
        int minSurfaceHeight = BetterCaveUtil.getMinSurfaceHeight(primer);

        // Determine noise thresholds for cavern spawns based on config
        float lavaCavernThreshold = -.5f;
        float flooredCavernThreshold = .5f;

        switch (Configuration.caveSettings.lavaCavernFrequency) {
            case VeryRare:
                lavaCavernThreshold = -.8f;
                break;
            case Rare:
                lavaCavernThreshold = -.5f;
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
                flooredCavernThreshold = .5f;
                break;
            case Common:
                flooredCavernThreshold = .4f;
                break;
            case VeryCommon:
                flooredCavernThreshold = .1f;
                break;
        }

        BetterCave caveGen;
        BetterCavern cavernGen;
        int caveBottom;

        // Only use Better Caves generation in overworld
        if (worldIn.provider.getDimension() == 0) {
            for (int localX = 0; localX < 16; localX++) {
                for (int localZ = 0; localZ < 16; localZ++) {
                    Vector2f blockPos = new Vector2f(((chunkX * 16) + localX), ((chunkZ * 16) + localZ));
                    controllerJitter.GradientPerturb(blockPos);
                    float cavernBiomeNoise = cavernBiomeController.GetNoise(blockPos.x, blockPos.y);
                    float caveBiomeNoise = caveBiomeController.GetNoise(blockPos.x, blockPos.y);

                    // Determine cave type for this column
                    if (caveBiomeNoise < -.1f) {
                        caveGen = this.caveSimplexBig;
                        caveBottom = Configuration.caveSettings.bigSimplexCave.caveBottom;
                    } else {
                        caveGen = this.caveSimplexSmall;
                        caveBottom = Configuration.caveSettings.smallSimplexCave.caveBottom;
                    }

                    // Dig out caverns
                    if (cavernBiomeNoise < lavaCavernThreshold) {
                        caveBiomeLavaCavern.generateColumn(chunkX, chunkZ, primer, localX, localZ,
                                Configuration.caveSettings.invertedPerlinCavern.caveBottom,
                                Configuration.caveSettings.invertedPerlinCavern.caveTop, maxSurfaceHeight,
                                minSurfaceHeight);
                        caveGen.generateColumn(chunkX, chunkZ, primer,localX, localZ, caveBottom, maxSurfaceHeight, maxSurfaceHeight, minSurfaceHeight);
                    } else if (cavernBiomeNoise >= lavaCavernThreshold && cavernBiomeNoise <= flooredCavernThreshold) {
                        caveGen.generateColumn(chunkX, chunkZ, primer, localX, localZ, 1, maxSurfaceHeight, maxSurfaceHeight, minSurfaceHeight);
                    } else {
                        caveBiomeFlooredCavern.generateColumn(chunkX, chunkZ, primer, localX, localZ,
                                Configuration.caveSettings.invertedPerlinCavern.caveBottom,
                                Configuration.caveSettings.invertedPerlinCavern.caveTop, maxSurfaceHeight,
                                minSurfaceHeight);
                        caveGen.generateColumn(chunkX, chunkZ, primer,localX, localZ, caveBottom, maxSurfaceHeight, maxSurfaceHeight, minSurfaceHeight);
                    }
                }
            }
        } else
            defaultCaveGen.generate(worldIn, chunkX, chunkZ, primer);
    }
}
