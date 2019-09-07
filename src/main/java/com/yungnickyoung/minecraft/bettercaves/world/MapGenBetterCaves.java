package com.yungnickyoung.minecraft.bettercaves.world;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCaveUtil;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.world.cave.BetterCaveCubic;
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
    // Cave types
    private BetterCave caveCubic;
    private BetterCave caveSimplex;

    // Cavern types
    private BetterCave cavernLava;
    private BetterCave cavernFloored;

    // Vanilla cave gen if user sets config to use it
    private MapGenCaves defaultCaveGen;

    // Cellular noise (basically Voronoi diagrams) generators to group caves into cave "biomes" based on xz-coordinates
    private FastNoise cavernBiomeController;
    private FastNoise topCaveBiomeController;

    // Noise generator for adding gradient perturbations to the Voronoi regions, effectively adding some jitter
    // to make the Voronoi regions' shapes vary more
    private FastNoise controllerJitter;

    // Biome generation noise thresholds, based on user config
    private float cubicCaveThreshold;
    private float simplexCaveThreshold;
    private float lavaCavernThreshold;
    private float flooredCavernThreshold;

    // Config option for using vanilla cave gen in some areas
    private boolean enableVanillaCaves;

    // DEBUG
    private BetterCave testCave;

    public MapGenBetterCaves() {
    }

    // DEBUG - used to test new noise types/params with the TestCave type
    private void debugGenerate(World worldIn, int chunkX, int chunkZ, @Nonnull ChunkPrimer primer) {
        int maxSurfaceHeight = BetterCaveUtil.getMaxSurfaceHeight(primer);
        int minSurfaceHeight = BetterCaveUtil.getMinSurfaceHeight(primer);
        if (worldIn.provider.getDimension() == 0) {
            for (int localX = 0; localX < 16; localX++) {
                for (int localZ = 0; localZ < 16; localZ++) {
                    testCave.generateColumn(chunkX, chunkZ, primer, localX, localZ, 1, maxSurfaceHeight, maxSurfaceHeight, minSurfaceHeight);
                }
            }
        }
    }

    /**
     * Function for generating Better Caves in a single chunk. This overrides the vanilla cave generation, which is
     * ordinarily performed by the MapGenCaves class.
     * This function is called for every new chunk that is generated in a world.
     * @param worldIn The minecraft world
     * @param chunkX The chunk's x-coordinate (on the chunk grid, not the block grid)
     * @param chunkZ The chunk's z-coordinate (on the chunk grid, not the block grid)
     * @param primer The chunk's ChunkPrimer
     */
    @Override
    public void generate(World worldIn, int chunkX, int chunkZ, @Nonnull ChunkPrimer primer) {
        if (world == null) { // First call - (lazy) initialization of all cave generators
            this.initialize(worldIn);
        }

        if (Settings.DEBUG_WORLD_GEN) {
            debugGenerate(worldIn, chunkX, chunkZ, primer);
            return;
        }

        // Find the (approximate) lowest and highest surface altitudes in this chunk
        int maxSurfaceHeight = BetterCaveUtil.getMaxSurfaceHeight(primer);
        int minSurfaceHeight = BetterCaveUtil.getMinSurfaceHeight(primer);

        // Cave generators - we will determine exactly what type these are based on the cave biome for each column
        BetterCave cavernGen;
        BetterCave caveGen;

        // These values probably could be hardcoded, but are kept as vars for future extensibility.
        // These values are later set to the correct cave type's config vars for
        // caveBottom and caveTop (only applicable for caverns, since caves perform some additional
        // operations to smoothly transition into the surface)
        int cavernBottomY;
        int cavernTopY;
        int caveBottomY;

        if (worldIn.provider.getDimension() == 0) { // Only use Better Caves generation in overworld
            for (int localX = 0; localX < 16; localX++) {
                for (int localZ = 0; localZ < 16; localZ++) {
                    // Store column position (block coords) in vector
                    Vector2f columnPos = new Vector2f(((chunkX * 16) + localX), ((chunkZ * 16) + localZ));

                    // Perturb the col position. This has the effect of applying jitter to the
                    // cave biome's Voronoi region to add variety.
                    controllerJitter.GradientPerturb(columnPos);

                    // Get noise values used to determine cave biome and cavern biome for this column
                    float cavernBiomeNoise = cavernBiomeController.GetNoise(columnPos.x, columnPos.y);
                    float caveBiomeNoise = topCaveBiomeController.GetNoise(columnPos.x, columnPos.y);

                    /* Determine cave type for this column. We have two thresholds, one for cubic caves and one for
                     * simplex caves. Since the noise value generated for the biome is between -1 and 1, we (by
                     * default) designate all negative values as cubic caves, and all positive as simplex. However,
                     * we allow the user to tweak the cutoff values based on the frequency they designate for each cave
                     * type, so we must also check for values between the two thresholds,
                     * e.g. if (cubicCaveThreshold <= noiseValue < simplexCaveThreshold).
                     * In this case, we use vanilla cave generation if it is enabled; otherwise we dig no caves
                     * out of this chunk.
                     */
                    if (caveBiomeNoise < this.cubicCaveThreshold) {
                        caveGen = this.caveCubic;
                        caveBottomY = Configuration.caveSettings.cubicCave.caveBottom;
                    } else if (caveBiomeNoise >= this.simplexCaveThreshold){
                        caveGen = this.caveSimplex;
                        caveBottomY = Configuration.caveSettings.simplexCave.caveBottom;
                    } else {
                        if (this.enableVanillaCaves) {
                            defaultCaveGen.generate(worldIn, chunkX, chunkZ, primer);
                            return;
                        }
                        continue;
                    }

                    // Determine cavern type for this column. Caverns generate at low altitudes only.
                    if (cavernBiomeNoise < lavaCavernThreshold) {
                        // Generate lava cavern in this column
                        cavernGen = this.cavernLava;
                        cavernBottomY = Configuration.caveSettings.lavaCavern.caveBottom;
                        cavernTopY = Configuration.caveSettings.lavaCavern.caveTop;
                    } else if (cavernBiomeNoise >= lavaCavernThreshold && cavernBiomeNoise <= flooredCavernThreshold) {
                        /* Similar to determining cave type above, we must check for values between the two adjusted
                         * thresholds, i.e. lavaCavernThreshold < noiseValue <= flooredCavernThreshold.
                         * In this case, we just continue generating the caves we were generating above, instead
                         * of generating a cavern.
                         */
                        cavernGen = caveGen;
                        cavernBottomY = caveBottomY;
                        cavernTopY = caveBottomY;
                    } else {
                        // Generate floored cavern in this column
                        cavernGen = this.cavernFloored;
                        cavernBottomY = Configuration.caveSettings.flooredCavern.caveBottom;
                        cavernTopY = Configuration.caveSettings.flooredCavern.caveTop;
                    }

                    // Dig out caves and caverns
                    // Bottom (Cavern) layer:
                    cavernGen.generateColumn(chunkX, chunkZ, primer, localX, localZ, cavernBottomY, cavernTopY,
                            maxSurfaceHeight, minSurfaceHeight);
                    // Top (Cave) layer:
                    caveGen.generateColumn(chunkX, chunkZ, primer, localX, localZ, caveBottomY, maxSurfaceHeight,
                            maxSurfaceHeight, minSurfaceHeight);

                }
            }
        } else // use vanilla generation in other dimensions
            defaultCaveGen.generate(worldIn, chunkX, chunkZ, primer);
    }

    private float calcCubicCaveThreshold() {
        switch (Configuration.caveSettings.cubicCave.caveFrequency) {
            case None:
                return -99f;
            case Rare:
                return -.6f;
            case Common:
                return -.2f;
            default: // VeryCommon
                return 0;
        }
    }
    private float calcSimplexCaveThreshold() {
        switch (Configuration.caveSettings.simplexCave.caveFrequency) {
            case None:
                return 99f;
            case Rare:
                return .6f;
            case Common:
                return .2f;
            default: // VeryCommon
                return 0;
        }
    }

    private float calcLavaCavernThreshold() {
        switch (Configuration.caveSettings.lavaCavern.caveFrequency) {
            case None:
                return -99f;
            case Rare:
                return -.8f;
            case Common:
                return -.4f;
            case VeryCommon:
                return -.1f;
            default: // Normal
                return -.6f;
        }
    }

    private float calcFlooredCavernThreshold() {
        switch (Configuration.caveSettings.flooredCavern.caveFrequency) {
            case None:
                return 99f;
            case Rare:
                return .8f;
            case Common:
                return .3f;
            case VeryCommon:
                return .1f;
            default: // Normal
                return .4f;
        }
    }

    /**
     * Initialize Better Caves generators and cave biome controllers for this world.
     * @param worldIn The minecraft world
     */
    private void initialize(World worldIn) {
        world = worldIn;
        this.defaultCaveGen = new MapGenCaves();

        // Determine noise thresholds for cavern spawns based on user config
        this.lavaCavernThreshold = calcLavaCavernThreshold();
        this.flooredCavernThreshold = calcFlooredCavernThreshold();

        // Determine noise thresholds for caverns based on user config
        this.cubicCaveThreshold = calcCubicCaveThreshold();
        this.simplexCaveThreshold = calcSimplexCaveThreshold();
        this.enableVanillaCaves = Configuration.caveSettings.vanillaCave.enableVanillaCaves;

        // Determine cave biome size. Biome controller jitter will also change based on this
        float caveBiomeSize;
        float jitterFreq;
        switch (Configuration.caveSettings.caveBiomeSize) {
            case Small:
                caveBiomeSize = .01f;
                jitterFreq = .01f;
                break;
            case Large:
                caveBiomeSize = .005f;
                jitterFreq = .0115f;
                break;
            case ExtraLarge:
                caveBiomeSize = .001f;
                jitterFreq = .015f;
                break;
            default: // Normal
                caveBiomeSize = .007f;
                jitterFreq = .01f;
                break;
        }

        // Initialize Biome Controllers using world seed and user config option for cave biome size
        this.cavernBiomeController = new FastNoise();
        this.cavernBiomeController.SetSeed((int)worldIn.getSeed());
        this.cavernBiomeController.SetFrequency(caveBiomeSize);
        this.cavernBiomeController.SetCellularDistanceFunction(FastNoise.CellularDistanceFunction.Natural);

        this.topCaveBiomeController = new FastNoise();
        this.topCaveBiomeController.SetSeed((int)worldIn.getSeed() + 222);
        this.topCaveBiomeController.SetFrequency(caveBiomeSize);
        this.topCaveBiomeController.SetCellularDistanceFunction(FastNoise.CellularDistanceFunction.Natural);

        // Initialize noise generator for adding jitter to cave biome regions to create variety in shape and size
        this.controllerJitter = new FastNoise();
        this.controllerJitter.SetSeed((int)(worldIn.getSeed()) + 69420);
        this.controllerJitter.SetGradientPerturbAmp(30);
        this.controllerJitter.SetFrequency(jitterFreq);

        // Initialize all Better Cave generators using config options
        this.caveCubic = new BetterCaveCubic(
                world,
                Configuration.caveSettings.cubicCave.fractalOctaves,
                Configuration.caveSettings.cubicCave.fractalGain,
                Configuration.caveSettings.cubicCave.fractalFrequency,
                Configuration.caveSettings.cubicCave.numGenerators,
                Configuration.caveSettings.cubicCave.noiseThreshold,
                Configuration.caveSettings.cubicCave.turbulenceOctaves,
                Configuration.caveSettings.cubicCave.turbulenceGain,
                Configuration.caveSettings.cubicCave.turbulenceFrequency,
                Configuration.caveSettings.cubicCave.enableTurbulence,
                Configuration.caveSettings.cubicCave.yCompression,
                Configuration.caveSettings.cubicCave.xzCompression,
                Configuration.caveSettings.cubicCave.yAdjust,
                Configuration.caveSettings.cubicCave.yAdjustF1,
                Configuration.caveSettings.cubicCave.yAdjustF2
        );

        this.caveSimplex = new BetterCaveSimplex(
                world,
                Configuration.caveSettings.simplexCave.fractalOctaves,
                Configuration.caveSettings.simplexCave.fractalGain,
                Configuration.caveSettings.simplexCave.fractalFrequency,
                Configuration.caveSettings.simplexCave.numGenerators,
                Configuration.caveSettings.simplexCave.noiseThreshold,
                Configuration.caveSettings.simplexCave.turbulenceOctaves,
                Configuration.caveSettings.simplexCave.turbulenceGain,
                Configuration.caveSettings.simplexCave.turbulenceFrequency,
                Configuration.caveSettings.simplexCave.enableTurbulence,
                Configuration.caveSettings.simplexCave.yCompression,
                Configuration.caveSettings.simplexCave.xzCompression,
                Configuration.caveSettings.simplexCave.yAdjust,
                Configuration.caveSettings.simplexCave.yAdjustF1,
                Configuration.caveSettings.simplexCave.yAdjustF2
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
}
