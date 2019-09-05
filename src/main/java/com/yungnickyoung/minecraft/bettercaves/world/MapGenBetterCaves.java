package com.yungnickyoung.minecraft.bettercaves.world;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCaveUtil;
import com.yungnickyoung.minecraft.bettercaves.util.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.world.cave.BetterCaveSimplex;
import com.yungnickyoung.minecraft.bettercaves.world.cave.TestCave;
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

    private BetterCave cavernLava;
    private BetterCave cavernFloored;

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

        BetterCave cavernGen;
        BetterCave midCaveGen;
        BetterCave topCaveGen;

        int cavernBottomY;
        int cavernTopY;

        int midCaveBottomY = 30;
        int midCaveTopY = 60;

        int topCaveBottomY;

        // Only use Better Caves generation in overworld
        if (worldIn.provider.getDimension() == 0) {
            for (int localX = 0; localX < 16; localX++) {
                for (int localZ = 0; localZ < 16; localZ++) {
                    Vector2f columnPos = new Vector2f(((chunkX * 16) + localX), ((chunkZ * 16) + localZ));

                    controllerJitter.GradientPerturb(columnPos);

                    float cavernBiomeNoise = cavernBiomeController.GetNoise(columnPos.x, columnPos.y);
                    float midCaveBiomeNoise = midCaveBiomeController.GetNoise(columnPos.x, columnPos.y);
                    float topCaveBiomeNoise = topCaveBiomeController.GetNoise(columnPos.x, columnPos.y);

                    // Determine cavern type for this column
                    if (cavernBiomeNoise < lavaCavernThreshold) { // .3
                        cavernGen = this.cavernLava;
                        cavernBottomY = Configuration.caveSettings.lavaCavern.caveBottom;
                        cavernTopY = Configuration.caveSettings.lavaCavern.caveTop;
                    } else if (cavernBiomeNoise >= lavaCavernThreshold && cavernBiomeNoise <= flooredCavernThreshold) {
                        cavernGen = this.caveSimplexBig;
                        cavernBottomY = 1;
                        cavernTopY = 30;
                    } else {
                        cavernGen = this.cavernFloored;
                        cavernBottomY = Configuration.caveSettings.flooredCavern.caveBottom;
                        cavernTopY = Configuration.caveSettings.flooredCavern.caveTop;
                    }

                    // Determine mid cave type for this column
                    if (midCaveBiomeNoise < -.1f) { // .3
                        midCaveGen = this.cavernFloored;
                    } else if (midCaveBiomeNoise >= -.1f && midCaveBiomeNoise <= .1f) {
                        midCaveGen = this.caveSimplexBig;
                    } else {
                        midCaveGen = this.caveSimplexSmall;
                    }

                    // Determine top cave type for this column
                    if (topCaveBiomeNoise < -.1f) {
                        topCaveGen = this.caveSimplexBig;
                        topCaveBottomY = Configuration.caveSettings.bigSimplexCave.caveBottom;
                    } else {
                        topCaveGen = this.caveSimplexSmall;
                        topCaveBottomY = Configuration.caveSettings.smallSimplexCave.caveBottom;
                    }

                    // Dig out caves and caverns
                    // Bottom layer:
                    cavernGen.generateColumn(chunkX, chunkZ, primer, localX, localZ, cavernBottomY, cavernTopY,
                            maxSurfaceHeight, minSurfaceHeight);
                    // Mid layer:
                    midCaveGen.generateColumn(chunkX, chunkZ, primer, localX, localZ, midCaveBottomY, midCaveTopY,
                            maxSurfaceHeight, minSurfaceHeight);
                    // Top layer:
                    topCaveGen.generateColumn(chunkX, chunkZ, primer, localX, localZ, topCaveBottomY, maxSurfaceHeight,
                            maxSurfaceHeight, minSurfaceHeight);

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
