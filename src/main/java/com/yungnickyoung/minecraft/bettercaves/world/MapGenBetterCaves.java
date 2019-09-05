package com.yungnickyoung.minecraft.bettercaves.world;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCaveUtil;
import com.yungnickyoung.minecraft.bettercaves.util.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.world.cave.BetterCaveSimplex;
import com.yungnickyoung.minecraft.bettercaves.world.cave.TestCave;
import com.yungnickyoung.minecraft.bettercaves.world.cavern.BetterCavern;
import com.yungnickyoung.minecraft.bettercaves.world.cavern.BetterCavernFloored;
import com.yungnickyoung.minecraft.bettercaves.world.cavern.BetterCavernLava;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenCaves;

import javax.annotation.Nonnull;
import javax.vecmath.Vector2f;

public class MapGenBetterCaves extends MapGenCaves {
    private BetterCave caveSimplexBig;
    private BetterCave caveSimplexSmall;

    private BetterCavern cavernLava;
    private BetterCavern cavernFloored;

    private BetterCave testCave;

    private MapGenCaves defaultCaveGen;

    private FastNoise cavernBiomeController;
    private FastNoise midCaveBiomeController;
    private FastNoise topCaveBiomeController;

    private FastNoise controllerJitter;

    public MapGenBetterCaves() {
    }

    @Override
    public void generate(World worldIn, int chunkX, int chunkZ, @Nonnull ChunkPrimer primer) {
        if (world == null) { // First call - initialize all cave types
            this.initialize(worldIn);
        }

        // Find the lowest and highest surface altitudes in this chunk
        int maxSurfaceHeight = BetterCaveUtil.getMaxSurfaceHeight(primer);
        int minSurfaceHeight = BetterCaveUtil.getMinSurfaceHeight(primer);

        // Determine noise thresholds for cavern spawns based on config
        float lavaCavernThreshold = -.5f;
        float flooredCavernThreshold = .5f;

        switch (Configuration.caveSettings.lavaCavern.caveFrequency) {
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

        switch (Configuration.caveSettings.flooredCavern.caveFrequency) {
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

        BetterCavern cavernGen;
        BetterCave midCaveGen;
        BetterCave topCaveGen;
        int caveBottom;

        // Only use Better Caves generation in overworld
        if (worldIn.provider.getDimension() == 0) {
            for (int localX = 0; localX < 16; localX++) {
                for (int localZ = 0; localZ < 16; localZ++) {
                    Vector2f columnPos = new Vector2f(((chunkX * 16) + localX), ((chunkZ * 16) + localZ));

                    controllerJitter.GradientPerturb(columnPos);

                    float cavernBiomeNoise = cavernBiomeController.GetNoise(columnPos.x, columnPos.y);
                    float topCaveBiomeNoise = topCaveBiomeController.GetNoise(columnPos.x, columnPos.y);

                    // Determine cave type for this column
                    if (topCaveBiomeNoise < -.1f) {
                        topCaveGen = this.caveSimplexBig;
                        caveBottom = Configuration.caveSettings.bigSimplexCave.caveBottom;
                    } else {
                        topCaveGen = this.caveSimplexSmall;
                        caveBottom = Configuration.caveSettings.smallSimplexCave.caveBottom;
                    }

                    // Dig out caverns based on noise
                    if (cavernBiomeNoise < lavaCavernThreshold) {
                        // Generate lava caverns at the bottom
                        cavernLava.generateColumn(chunkX, chunkZ, primer, localX, localZ,
                                Configuration.caveSettings.lavaCavern.caveBottom,
                                Configuration.caveSettings.lavaCavern.caveTop, maxSurfaceHeight, minSurfaceHeight);
                        // Generate caves the rest of the way up
                        topCaveGen.generateColumn(chunkX, chunkZ, primer,localX, localZ, caveBottom, maxSurfaceHeight, maxSurfaceHeight, minSurfaceHeight);
                    } else if (cavernBiomeNoise >= lavaCavernThreshold && cavernBiomeNoise <= flooredCavernThreshold) {
                        // Generate caves all the way down
                        topCaveGen.generateColumn(chunkX, chunkZ, primer, localX, localZ, 1, maxSurfaceHeight, maxSurfaceHeight, minSurfaceHeight);
                    } else {
                        // Generate lava caverns at the bottom
                        cavernFloored.generateColumn(chunkX, chunkZ, primer, localX, localZ,
                                Configuration.caveSettings.flooredCavern.caveBottom,
                                Configuration.caveSettings.flooredCavern.caveTop, maxSurfaceHeight, minSurfaceHeight);
                        // Generate caves the rest of the way up
                        topCaveGen.generateColumn(chunkX, chunkZ, primer,localX, localZ, caveBottom, maxSurfaceHeight, maxSurfaceHeight, minSurfaceHeight);
                    }
                }
            }
        } else
            defaultCaveGen.generate(worldIn, chunkX, chunkZ, primer);
    }

    private void initialize(World worldIn) {
        world = worldIn;
        this.defaultCaveGen = new MapGenCaves();

        this.cavernBiomeController = new FastNoise();
        this.cavernBiomeController.SetSeed((int)worldIn.getSeed());
        this.cavernBiomeController.SetFrequency(.005f);
        this.cavernBiomeController.SetCellularDistanceFunction(FastNoise.CellularDistanceFunction.Natural);

        this.midCaveBiomeController = new FastNoise();
        this.midCaveBiomeController.SetSeed((int)worldIn.getSeed() + 111);
        this.midCaveBiomeController.SetFrequency(.01f);
        this.midCaveBiomeController.SetCellularDistanceFunction(FastNoise.CellularDistanceFunction.Natural);

        this.topCaveBiomeController = new FastNoise();
        this.topCaveBiomeController.SetSeed((int)worldIn.getSeed() + 222);
        this.topCaveBiomeController.SetFrequency(.01f);
        this.topCaveBiomeController.SetCellularDistanceFunction(FastNoise.CellularDistanceFunction.Natural);

        this.controllerJitter = new FastNoise();
        this.controllerJitter.SetSeed((int)(worldIn.getSeed()) + 69420);
        this.controllerJitter.SetGradientPerturbAmp(60);
        this.controllerJitter.SetFrequency(.01f);

        this.caveSimplexBig = new BetterCaveSimplex(
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

        this.caveSimplexSmall = new BetterCaveSimplex(
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

        this.cavernLava = new BetterCavernLava(
                world,
                Configuration.caveSettings.lavaCavern.fractalOctaves,
                Configuration.caveSettings.lavaCavern.fractalGain,
                Configuration.caveSettings.lavaCavern.fractalFrequency,
                Configuration.caveSettings.lavaCavern.numGenerators,
                Configuration.caveSettings.lavaCavern.noiseThreshold,
                Configuration.caveSettings.lavaCavern.yCompression,
                Configuration.caveSettings.lavaCavern.xzCompression
        );

        this.cavernFloored = new BetterCavernFloored(
                world,
                Configuration.caveSettings.flooredCavern.fractalOctaves,
                Configuration.caveSettings.flooredCavern.fractalGain,
                Configuration.caveSettings.flooredCavern.fractalFrequency,
                Configuration.caveSettings.flooredCavern.numGenerators,
                Configuration.caveSettings.flooredCavern.noiseThreshold,
                Configuration.caveSettings.flooredCavern.yCompression,
                Configuration.caveSettings.flooredCavern.xzCompression
        );
    }
}
