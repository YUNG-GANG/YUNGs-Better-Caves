package com.yungnickyoung.minecraft.bettercaves.world;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.enums.CaveType;
import com.yungnickyoung.minecraft.bettercaves.enums.CavernType;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCaveUtil;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.world.bedrock.FlattenBedrock;
import com.yungnickyoung.minecraft.bettercaves.world.cave.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenCaves;

import javax.annotation.Nonnull;

/**
 * Class that overrides vanilla cave gen with Better Caves gen.
 * Combines multiple types of caves and caverns using different types of noise to create diverse a
 * diverse underground experience.
 */
public class MapGenBetterCaves extends MapGenCaves {
    // Cave types
    private AbstractBC caveCubic;
    private AbstractBC caveSimplex;

    // Cavern types
    private AbstractBC cavernLava;
    private AbstractBC cavernFloored;
    private AbstractBC cavernWater;

    private int surfaceCutoff;

    // Vanilla cave gen if user sets config to use it
    private MapGenCaves defaultCaveGen;

    // Noise generators to group caves into cave "biomes" based on xz-coordinates.
    // Cavern Biome Controller uses simplex noise while the others use Voronoi regions (cellular noise)
    private FastNoise waterCavernController;
    private FastNoise cavernBiomeController;
    private FastNoise caveBiomeController;

    // Biome generation noise thresholds, based on user config
    private float cubicCaveThreshold;
    private float simplexCaveThreshold;
    private float lavaCavernThreshold;
    private float flooredCavernThreshold;
    private float waterBiomeThreshold;

    // Dictates the degree of smoothing along cavern biome boundaries
    private float transitionRange = .15f;

    // Config option for using vanilla cave gen in some areas
    private boolean enableVanillaCaves;

    // Config option for using water biomes
    private boolean enableWaterBiomes;

    // DEBUG
    private AbstractBC testCave;

    public MapGenBetterCaves() {
    }

    // DEBUG - used to test new noise types/params with the TestCave type
    private void debugGenerate(World worldIn, int chunkX, int chunkZ, @Nonnull ChunkPrimer primer) {
        int maxSurfaceHeight = BetterCaveUtil.getMaxSurfaceHeight(primer);
        int minSurfaceHeight = BetterCaveUtil.getMinSurfaceHeight(primer);
        if (worldIn.provider.getDimension() == 0) {
            for (int localX = 0; localX < 16; localX++) {
                for (int localZ = 0; localZ < 16; localZ++) {
                    testCave.generateColumn(chunkX, chunkZ, primer, localX, localZ, 1, maxSurfaceHeight, maxSurfaceHeight, minSurfaceHeight, surfaceCutoff, Blocks.FLOWING_LAVA.getDefaultState());
                }
            }
        }
    }

    /**
     * Function for generating Better Caves in a single chunk. This overrides the vanilla cave generation, which is
     * ordinarily performed by the MapGenCaves class.
     * This function is called for every new chunk that is generated in a world.
     * @param worldIn The Minecraft world
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

        int maxSurfaceHeight = 128;
        int minSurfaceHeight = 60;

        // Cave generators - we will determine exactly what type these are based on the cave biome for each column
        AbstractBC cavernGen;
        AbstractBC caveGen;

        // These values probably could be hardcoded, but are kept as vars for future extensibility.
        // These values are later set to the correct cave type's config vars for
        // caveBottom and caveTop (only applicable for caverns, since caves perform some additional
        // operations to smoothly transition into the surface)
        int cavernBottomY;
        int cavernTopY;
        int caveBottomY;

        // Only use Better Caves generation in whitelisted dimensions
        int dimensionID = worldIn.provider.getDimension();
        boolean isWhitelisted = false;

        // Ignore the dimension ID list if global whitelisting is enabled
        if (Configuration.caveSettings.enableGlobalWhitelist)
            isWhitelisted = true;

        // Check if dimension is whitelisted
        for (int dim : Configuration.caveSettings.whitelistedDimensionIDs) {
            if (dimensionID == dim) {
                isWhitelisted = true;
                break;
            }
        }

        // If not whitelisted, use default cave gen instead of Better Caves
        if (!isWhitelisted) {
            defaultCaveGen.generate(worldIn, chunkX, chunkZ, primer);
            return;
        }

        // Flatten bedrock, if enabled
        FlattenBedrock.flattenBedrock(primer);

        // We split chunks into 2x2 subchunks for surface height calculations
        for (int subX = 0; subX < 8; subX++) {
            for (int subZ = 0; subZ < 8; subZ++) {
                if (!Configuration.debugsettings.debugVisualizer)
                    maxSurfaceHeight = BetterCaveUtil.getMaxSurfaceHeightSubChunk(primer, subX, subZ);

                // maxSurfaceHeight (also used for max cave altitude) cannot exceed Max Cave Altitude setting
                maxSurfaceHeight = Math.min(maxSurfaceHeight, Configuration.caveSettings.caves.maxCaveAltitude);

                for (int offsetX = 0; offsetX < 2; offsetX++) {
                    for (int offsetZ = 0; offsetZ < 2; offsetZ++) {
                        int localX = (subX * 2) + offsetX; // chunk-local x-coordinate (0-15, inclusive)
                        int localZ = (subZ * 2) + offsetZ; // chunk-local z-coordinate (0-15, inclusive)
                        int realX = (chunkX * 16) + localX;
                        int realZ = (chunkZ * 16) + localZ;

                        /* --------------------------- Configure Caves --------------------------- */

                        // Get noise values used to determine cave biome
                        float caveBiomeNoise = caveBiomeController.GetNoise(realX, realZ);

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
                            caveBottomY = Configuration.caveSettings.caves.cubicCave.caveBottom;
                        } else if (caveBiomeNoise >= this.simplexCaveThreshold) {
                            caveGen = this.caveSimplex;
                            caveBottomY = Configuration.caveSettings.caves.simplexCave.caveBottom;
                        } else {
                            if (this.enableVanillaCaves) {
                                defaultCaveGen.generate(worldIn, chunkX, chunkZ, primer);
                                return;
                            }
                            caveGen = null;
                            caveBottomY = 255;
                        }

                        /* --------------------------- Configure Caverns --------------------------- */

                        // Get noise values used to determine cavern biome
                        float cavernBiomeNoise = cavernBiomeController.GetNoise(realX, realZ);
                        float waterBiomeNoise = 99;

                        // Only bother calculating noise for water biome if enabled
                        if (enableWaterBiomes)
                            waterBiomeNoise = waterCavernController.GetNoise(realX, realZ);

                        // If water biome threshold check is passed, change lava block to water
                        IBlockState lavaBlock = Blocks.FLOWING_LAVA.getDefaultState();
                        if (waterBiomeNoise < waterBiomeThreshold)
                            lavaBlock = Blocks.WATER.getDefaultState();

                        // Determine cavern type for this column. Caverns generate at low altitudes only.
                        if (cavernBiomeNoise < lavaCavernThreshold) {
                            if (this.enableWaterBiomes && waterBiomeNoise < this.waterBiomeThreshold) {
                                // Generate water cavern in this column
                                cavernGen = this.cavernWater;
                            } else {
                                // Generate lava cavern in this column
                                cavernGen = this.cavernLava;
                            }
                            // Water caverns use the same cave top/bottom as lava caverns
                            cavernBottomY = Configuration.caveSettings.caverns.lavaCavern.caveBottom;
                            cavernTopY = Configuration.caveSettings.caverns.lavaCavern.caveTop;
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
                            cavernBottomY = Configuration.caveSettings.caverns.flooredCavern.caveBottom;
                            cavernTopY = Configuration.caveSettings.caverns.flooredCavern.caveTop;
                        }

                        // Extra check to provide close-off transitions on cavern edges
                        if (Configuration.caveSettings.caverns.enableBoundarySmoothing) {
                            if (cavernBiomeNoise >= lavaCavernThreshold && cavernBiomeNoise <= lavaCavernThreshold + transitionRange) {
                                float smoothAmp = Math.abs((cavernBiomeNoise - (lavaCavernThreshold + transitionRange)) / transitionRange);
                                this.cavernLava.generateColumn(chunkX, chunkZ, primer, localX, localZ, Configuration.caveSettings.caverns.lavaCavern.caveBottom, Configuration.caveSettings.caverns.lavaCavern.caveTop,
                                        maxSurfaceHeight, minSurfaceHeight, surfaceCutoff, lavaBlock, smoothAmp);
                            } else if (cavernBiomeNoise <= flooredCavernThreshold && cavernBiomeNoise >= flooredCavernThreshold - transitionRange) {
                                float smoothAmp = Math.abs((cavernBiomeNoise - (flooredCavernThreshold - transitionRange)) / transitionRange);
                                this.cavernFloored.generateColumn(chunkX, chunkZ, primer, localX, localZ, Configuration.caveSettings.caverns.flooredCavern.caveBottom, Configuration.caveSettings.caverns.flooredCavern.caveTop,
                                        maxSurfaceHeight, minSurfaceHeight, surfaceCutoff, lavaBlock, smoothAmp);
                            }
                        }

                        /* --------------- Dig out caves and caverns for this column --------------- */
                        // Top (Cave) layer:
                        if (caveGen != null)
                            caveGen.generateColumn(chunkX, chunkZ, primer, localX, localZ, caveBottomY, maxSurfaceHeight,
                                maxSurfaceHeight, minSurfaceHeight, surfaceCutoff, lavaBlock);
                        // Bottom (Cavern) layer:
                        if (cavernGen != null)
                            cavernGen.generateColumn(chunkX, chunkZ, primer, localX, localZ, cavernBottomY, cavernTopY,
                                maxSurfaceHeight, minSurfaceHeight, surfaceCutoff, lavaBlock);

                    }
                }
            }
        }
    }

    /**
     * @return threshold value for cubic cave spawn rate based on Config setting
     */
    private float calcCubicCaveThreshold() {
        switch (Configuration.caveSettings.caves.cubicCave.caveFrequency) {
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

    /**
     * @return threshold value for simplex cave spawn rate based on Config setting
     */
    private float calcSimplexCaveThreshold() {
        switch (Configuration.caveSettings.caves.simplexCave.caveFrequency) {
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

    /**
     * @return threshold value for lava cavern spawn rate based on Config setting
     */
    private float calcLavaCavernThreshold() {
        switch (Configuration.caveSettings.caverns.lavaCavern.caveFrequency) {
            case None:
                return -99f;
            case Rare:
                return -.8f;
            case Common:
                return -.3f;
            case VeryCommon:
                return -.1f;
            default: // Normal
                return -.4f;
        }
    }

    /**
     * @return threshold value for floored cavern spawn rate based on Config setting
     */
    private float calcFlooredCavernThreshold() {
        switch (Configuration.caveSettings.caverns.flooredCavern.caveFrequency) {
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
     * @return threshold value for water biome spawn rate based on Config setting
     */
    private float calcWaterBiomeThreshold() {
        switch (Configuration.caveSettings.waterBiomes.waterRegionFrequency) {
            case Rare:
                return -.4f;
            case Common:
                return .1f;
            case VeryCommon:
                return .3f;
            case Always:
                return 99f;
            default: // Normal
                return -.15f;
        }
    }

    /**
     * Initialize Better Caves generators and cave biome controllers for this world.
     * @param worldIn The minecraft world
     */
    private void initialize(World worldIn) {
        this.world = worldIn;
        this.defaultCaveGen = new MapGenCaves();
        this.enableVanillaCaves = Configuration.caveSettings.caves.vanillaCave.enableVanillaCaves;
        this.enableWaterBiomes = Configuration.caveSettings.waterBiomes.enableWaterBiomes;

        // Determine noise thresholds for cavern spawns based on user config
        this.lavaCavernThreshold = calcLavaCavernThreshold();
        this.flooredCavernThreshold = calcFlooredCavernThreshold();
        this.waterBiomeThreshold = calcWaterBiomeThreshold();

        // Determine noise thresholds for caverns based on user config
        this.cubicCaveThreshold = calcCubicCaveThreshold();
        this.simplexCaveThreshold = calcSimplexCaveThreshold();

        // Get user setting for surface cutoff depth used to close caves off towards the surface
        this.surfaceCutoff = Configuration.caveSettings.caves.surfaceCutoff;

        // Determine cave biome size
        float caveBiomeSize;
        switch (Configuration.caveSettings.caves.caveRegionSize) {
            case Small:
                caveBiomeSize = .007f;
                break;
            case Large:
                caveBiomeSize = .0032f;
                break;
            case ExtraLarge:
                caveBiomeSize = .001f;
                break;
            default: // Medium
                caveBiomeSize = .005f;
                break;
        }

        // Determine cavern biome size, as well as jitter to make Voronoi regions more varied in shape
        float cavernBiomeSize;
        float waterCavernBiomeSize = .003f;
        switch (Configuration.caveSettings.caverns.cavernRegionSize) {
            case Small:
                cavernBiomeSize = .01f;
                break;
            case Large:
                cavernBiomeSize = .005f;
                break;
            case ExtraLarge:
                cavernBiomeSize = .001f;
                waterCavernBiomeSize = .0005f;
                break;
            default: // Medium
                cavernBiomeSize = .007f;
                break;
        }

        // Initialize Biome Controllers using world seed and user config option for biome size
        this.caveBiomeController = new FastNoise();
        this.caveBiomeController.SetSeed((int)worldIn.getSeed() + 222);
        this.caveBiomeController.SetFrequency(caveBiomeSize);
        this.caveBiomeController.SetNoiseType(FastNoise.NoiseType.Cellular);
        this.caveBiomeController.SetCellularDistanceFunction(FastNoise.CellularDistanceFunction.Natural);

        // Note that Cavern Biome Controller uses Simplex noise instead of Cellular
        this.cavernBiomeController = new FastNoise();
        this.cavernBiomeController.SetSeed((int)worldIn.getSeed() + 333);
        this.cavernBiomeController.SetFrequency(cavernBiomeSize);

        this.waterCavernController = new FastNoise();
        this.waterCavernController.SetSeed((int)worldIn.getSeed() + 444);
        this.waterCavernController.SetFrequency(waterCavernBiomeSize);
        this.waterCavernController.SetNoiseType(FastNoise.NoiseType.Cellular);
        this.waterCavernController.SetCellularDistanceFunction(FastNoise.CellularDistanceFunction.Natural);

        /* ---------- Initialize all Better Cave generators using config options ---------- */
        this.caveCubic = new CaveBC(
                world,
                CaveType.CUBIC,
                Configuration.caveSettings.caves.cubicCave.fractalOctaves,
                Configuration.caveSettings.caves.cubicCave.fractalGain,
                Configuration.caveSettings.caves.cubicCave.fractalFrequency,
                Configuration.caveSettings.caves.cubicCave.numGenerators,
                Configuration.caveSettings.caves.cubicCave.noiseThreshold,
                Configuration.caveSettings.caves.cubicCave.turbulenceOctaves,
                Configuration.caveSettings.caves.cubicCave.turbulenceGain,
                Configuration.caveSettings.caves.cubicCave.turbulenceFrequency,
                Configuration.caveSettings.caves.cubicCave.enableTurbulence,
                Configuration.caveSettings.caves.cubicCave.yCompression,
                Configuration.caveSettings.caves.cubicCave.xzCompression,
                Configuration.caveSettings.caves.cubicCave.yAdjust,
                Configuration.caveSettings.caves.cubicCave.yAdjustF1,
                Configuration.caveSettings.caves.cubicCave.yAdjustF2,
                Blocks.PLANKS.getDefaultState()
        );

        this.caveSimplex = new CaveBC(
                world,
                CaveType.SIMPLEX,
                Configuration.caveSettings.caves.simplexCave.fractalOctaves,
                Configuration.caveSettings.caves.simplexCave.fractalGain,
                Configuration.caveSettings.caves.simplexCave.fractalFrequency,
                Configuration.caveSettings.caves.simplexCave.numGenerators,
                Configuration.caveSettings.caves.simplexCave.noiseThreshold,
                Configuration.caveSettings.caves.simplexCave.turbulenceOctaves,
                Configuration.caveSettings.caves.simplexCave.turbulenceGain,
                Configuration.caveSettings.caves.simplexCave.turbulenceFrequency,
                Configuration.caveSettings.caves.simplexCave.enableTurbulence,
                Configuration.caveSettings.caves.simplexCave.yCompression,
                Configuration.caveSettings.caves.simplexCave.xzCompression,
                Configuration.caveSettings.caves.simplexCave.yAdjust,
                Configuration.caveSettings.caves.simplexCave.yAdjustF1,
                Configuration.caveSettings.caves.simplexCave.yAdjustF2,
                Blocks.COBBLESTONE.getDefaultState()
        );

        this.cavernLava = new CavernBC(
                world,
                CavernType.LAVA,
                Configuration.caveSettings.caverns.lavaCavern.fractalOctaves,
                Configuration.caveSettings.caverns.lavaCavern.fractalGain,
                Configuration.caveSettings.caverns.lavaCavern.fractalFrequency,
                Configuration.caveSettings.caverns.lavaCavern.numGenerators,
                Configuration.caveSettings.caverns.lavaCavern.noiseThreshold,
                Configuration.caveSettings.caverns.lavaCavern.yCompression,
                Configuration.caveSettings.caverns.lavaCavern.xzCompression,
                Blocks.REDSTONE_BLOCK.getDefaultState()
        );

        this.cavernFloored = new CavernBC(
                world,
                CavernType.FLOORED,
                Configuration.caveSettings.caverns.flooredCavern.fractalOctaves,
                Configuration.caveSettings.caverns.flooredCavern.fractalGain,
                Configuration.caveSettings.caverns.flooredCavern.fractalFrequency,
                Configuration.caveSettings.caverns.flooredCavern.numGenerators,
                Configuration.caveSettings.caverns.flooredCavern.noiseThreshold,
                Configuration.caveSettings.caverns.flooredCavern.yCompression,
                Configuration.caveSettings.caverns.flooredCavern.xzCompression,
                Blocks.GOLD_BLOCK.getDefaultState()
        );

        this.cavernWater = new CavernBC(
                world,
                CavernType.WATER,
                Configuration.caveSettings.waterBiomes.waterCavern.fractalOctaves,
                Configuration.caveSettings.waterBiomes.waterCavern.fractalGain,
                Configuration.caveSettings.waterBiomes.waterCavern.fractalFrequency,
                Configuration.caveSettings.waterBiomes.waterCavern.numGenerators,
                Configuration.caveSettings.waterBiomes.waterCavern.noiseThreshold,
                Configuration.caveSettings.waterBiomes.waterCavern.yCompression,
                Configuration.caveSettings.waterBiomes.waterCavern.xzCompression,
                Blocks.LAPIS_BLOCK.getDefaultState()
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
