package com.yungnickyoung.minecraft.bettercaves.world;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.enums.CaveType;
import com.yungnickyoung.minecraft.bettercaves.enums.CavernType;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCaveUtil;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
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

    // Cellular noise (basically Voronoi diagrams) generators to group caves into cave "biomes" based on xz-coordinates
    private FastNoise waterCavernController;
    private FastNoise cavernBiomeController;
    private FastNoise caveBiomeController;

    // Biome generation noise thresholds, based on user config
    private float cubicCaveThreshold;
    private float simplexCaveThreshold;
    private float lavaCavernThreshold;
    private float flooredCavernThreshold;
    private float waterBiomeThreshold;

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

        int dimensionID = worldIn.provider.getDimension();

        // Only use Better Caves generation in non-blacklisted dimensions
        for (int dim : Configuration.caveSettings.blacklistedDimensionIDs) {
            if (dimensionID == dim) {
                defaultCaveGen.generate(worldIn, chunkX, chunkZ, primer);
                return;
            }
        }

        // We split chunks into 2x2 subchunks for surface height calculations
        for (int subX = 0; subX < 8; subX++) {
            for (int subZ = 0; subZ < 8; subZ++) {
                if (!Configuration.debugsettings.debugVisualizer)
                    maxSurfaceHeight = BetterCaveUtil.getMaxSurfaceHeightSubChunk(primer, subX, subZ);

                // maxSurfaceHeight (also used for max cave altitude) cannot exceed Max Cave Altitude setting
                maxSurfaceHeight = Math.min(maxSurfaceHeight, Configuration.caveSettings.maxCaveAltitude);

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
                            caveBottomY = Configuration.caveSettings.cubicCave.caveBottom;
                        } else if (caveBiomeNoise >= this.simplexCaveThreshold) {
                            caveGen = this.caveSimplex;
                            caveBottomY = Configuration.caveSettings.simplexCave.caveBottom;
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

    /**
     * @return threshold value for simplex cave spawn rate based on Config setting
     */
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

    /**
     * @return threshold value for lava cavern spawn rate based on Config setting
     */
    private float calcLavaCavernThreshold() {
        switch (Configuration.caveSettings.lavaCavern.caveFrequency) {
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
     * @return threshold value for water biome spawn rate based on Config setting
     */
    private float calcWaterBiomeThreshold() {
        switch (Configuration.caveSettings.waterBiomes.waterBiomeFrequency) {
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
        this.enableVanillaCaves = Configuration.caveSettings.vanillaCave.enableVanillaCaves;
        this.enableWaterBiomes = Configuration.caveSettings.waterBiomes.enableWaterBiomes;

        // Determine noise thresholds for cavern spawns based on user config
        this.lavaCavernThreshold = calcLavaCavernThreshold();
        this.flooredCavernThreshold = calcFlooredCavernThreshold();
        this.waterBiomeThreshold = calcWaterBiomeThreshold();

        // Determine noise thresholds for caverns based on user config
        this.cubicCaveThreshold = calcCubicCaveThreshold();
        this.simplexCaveThreshold = calcSimplexCaveThreshold();

        // Get user setting for surface cutoff depth used to close caves off towards the surface
        this.surfaceCutoff = Configuration.caveSettings.surfaceCutoff;

        // Determine cave biome size
        float caveBiomeSize;
        switch (Configuration.caveSettings.caveBiomeSize) {
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
        float waterCavernBiomeSize = .0015f;
        switch (Configuration.caveSettings.cavernBiomeSize) {
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
        this.caveBiomeController.SetCellularDistanceFunction(FastNoise.CellularDistanceFunction.Natural);

        this.cavernBiomeController = new FastNoise();
        this.cavernBiomeController.SetSeed((int)worldIn.getSeed() + 333);
        this.cavernBiomeController.SetFrequency(cavernBiomeSize);
        this.cavernBiomeController.SetCellularDistanceFunction(FastNoise.CellularDistanceFunction.Natural);

        this.waterCavernController = new FastNoise();
        this.waterCavernController.SetSeed((int)worldIn.getSeed() + 444);
        this.waterCavernController.SetFrequency(waterCavernBiomeSize);
        this.waterCavernController.SetCellularDistanceFunction(FastNoise.CellularDistanceFunction.Natural);

        /* ---------- Initialize all Better Cave generators using config options ---------- */
        this.caveCubic = new CaveBC(
                world,
                CaveType.CUBIC,
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
                Configuration.caveSettings.cubicCave.yAdjustF2,
                Blocks.QUARTZ_BLOCK.getDefaultState()
        );

        this.caveSimplex = new CaveBC(
                world,
                CaveType.SIMPLEX,
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
                Configuration.caveSettings.simplexCave.yAdjustF2,
                Blocks.COBBLESTONE.getDefaultState()
        );

        this.cavernLava = new CavernBC(
                world,
                CavernType.LAVA,
                Configuration.caveSettings.lavaCavern.fractalOctaves,
                Configuration.caveSettings.lavaCavern.fractalGain,
                Configuration.caveSettings.lavaCavern.fractalFrequency,
                Configuration.caveSettings.lavaCavern.numGenerators,
                Configuration.caveSettings.lavaCavern.noiseThreshold,
                Configuration.caveSettings.lavaCavern.yCompression,
                Configuration.caveSettings.lavaCavern.xzCompression,
                Blocks.REDSTONE_BLOCK.getDefaultState()
        );

        this.cavernFloored = new CavernBC(
                world,
                CavernType.FLOORED,
                Configuration.caveSettings.flooredCavern.fractalOctaves,
                Configuration.caveSettings.flooredCavern.fractalGain,
                Configuration.caveSettings.flooredCavern.fractalFrequency,
                Configuration.caveSettings.flooredCavern.numGenerators,
                Configuration.caveSettings.flooredCavern.noiseThreshold,
                Configuration.caveSettings.flooredCavern.yCompression,
                Configuration.caveSettings.flooredCavern.xzCompression,
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
