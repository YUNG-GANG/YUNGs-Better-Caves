package com.yungnickyoung.minecraft.bettercaves.world;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.config.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.config.ConfigLoader;
import com.yungnickyoung.minecraft.bettercaves.enums.CaveType;
import com.yungnickyoung.minecraft.bettercaves.enums.CavernType;
import com.yungnickyoung.minecraft.bettercaves.enums.RegionSize;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtil;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.world.bedrock.FlattenBedrock;
import com.yungnickyoung.minecraft.bettercaves.world.cave.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraftforge.event.terraingen.InitMapGenEvent;

import javax.annotation.Nonnull;

/**
 * Class that overrides vanilla cave gen with Better Caves gen.
 * Combines multiple types of caves and caverns using different types of noise to create a
 * novel underground experience.
 */
public class MapGenBetterCaves extends MapGenCaves {
    // Cave types
    private UndergroundCarver caveCubic;
    private UndergroundCarver caveSimplex;

    // Cavern types
    private UndergroundCarver cavernLava;
    private UndergroundCarver cavernFloored;
    private UndergroundCarver cavernWater;

    // Vanilla cave gen if user sets config to use it
    private MapGenBase defaultCaveGen;

    // Noise generators to group caves into cave regions based on xz-coordinates.
    // Cavern Region Controller uses simplex noise while the others use Voronoi regions (cellular noise)
    private FastNoise waterCavernController;
    private FastNoise cavernRegionController;
    private FastNoise caveRegionController;

    // Region generation noise thresholds, based on user config
    private float cubicCaveThreshold;
    private float simplexCaveThreshold;
    private float lavaCavernThreshold;
    private float flooredCavernThreshold;
    private float waterRegionThreshold;

    // Dictates the degree of smoothing along cavern region boundaries
    private float transitionRange = .15f;

    // Liquid blocks (can be changed from water/lava via config)
    private IBlockState lavaBlock;
    private IBlockState waterBlock;

    // Dimension this instance of MapGenBetterCaves is used in
    public int dimensionID;
    public String dimensionName;

    // Config holder for non-global config options that may be specific to this carver
    public ConfigHolder config = new ConfigHolder();

    // DEBUG
    private int counter = 200;

    public MapGenBetterCaves() {
    }

    public MapGenBetterCaves(InitMapGenEvent event) {
        this.defaultCaveGen = event.getOriginalGen();
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
        if (world == null) { // First call - (lazy) initialization of all cave/cavern generators
            this.initialize(worldIn);
        }

        // Only operate on whitelisted dimensions.
        // I tried just setting the event's NewGen to its OriginalGen but that doesn't seem to do anything
        // after the cave gen process has been initiated.
        if (!isDimensionWhitelisted(dimensionID)) {
            defaultCaveGen.generate(worldIn, chunkX, chunkZ, primer);
            return;
        }

        counter--;
        if (counter <= 0) {
            Settings.LOGGER.warn("BETTERCAVESWORLD "+ world.getSeed() + " | " +
                    BetterCavesUtil.dimensionAsString(dimensionID, dimensionName) + " | " +
                    BetterCaves.activeCarversMap.size() + " | " + this.hashCode());
            counter = 200;
        }

        // Default vals for max/min surface height
        int maxSurfaceHeight = 128;
        int minSurfaceHeight = 60;

        // Cave generators - we will determine exactly what type these are based on the cave region for each column
        UndergroundCarver cavernGen;
        UndergroundCarver caveGen;

        // These values are later set to the correct cave/cavern type's config vars for
        // caveBottom, and caveTop (only applicable for caverns, since caves perform some additional
        // operations to smoothly transition into the surface)
        int cavernBottomY;
        int cavernTopY;
        int caveBottomY;

        // Flatten bedrock, if enabled
        if (config.flattenBedrock.get())
            FlattenBedrock.flattenBedrock(primer, config.bedrockWidth.get());

        // We split chunks into 2x2 sub-chunks along the x-z axis for surface height calculations
        for (int subX = 0; subX < 8; subX++) {
            for (int subZ = 0; subZ < 8; subZ++) {
                if (!config.debugVisualizer.get())
                    maxSurfaceHeight = BetterCavesUtil.getMaxSurfaceAltitudeSubChunk(primer, subX, subZ);

                // maxSurfaceHeight (also used for max cave altitude) cannot exceed Max Cave Altitude setting
                maxSurfaceHeight = Math.min(maxSurfaceHeight, config.maxCaveAltitude.get());

                for (int offsetX = 0; offsetX < 2; offsetX++) {
                    for (int offsetZ = 0; offsetZ < 2; offsetZ++) {
                        int localX = (subX * 2) + offsetX; // chunk-local x-coordinate (0-15, inclusive)
                        int localZ = (subZ * 2) + offsetZ; // chunk-local z-coordinate (0-15, inclusive)
                        int realX = (chunkX * 16) + localX;
                        int realZ = (chunkZ * 16) + localZ;

                        /* --------------------------- Configure Caves --------------------------- */

                        // Get noise values used to determine cave region
                        float caveRegionNoise = caveRegionController.GetNoise(realX, realZ);

                        /* Determine cave type for this column. We have two thresholds, one for cubic caves and one for
                         * simplex caves. Since the noise value generated for the region is between -1 and 1, we (by
                         * default) designate all negative values as cubic caves, and all positive as simplex. However,
                         * we allow the user to tweak the cutoff values based on the frequency they designate for each cave
                         * type, so we must also check for values between the two thresholds,
                         * e.g. if (cubicCaveThreshold <= noiseValue < simplexCaveThreshold).
                         * In this case, we use vanilla cave generation if it is enabled; otherwise we dig no caves
                         * out of this chunk.
                         */
                        if (caveRegionNoise < this.cubicCaveThreshold) {
                            caveGen = this.caveCubic;
                            caveBottomY = config.cubicCaveBottom.get();
                        } else if (caveRegionNoise >= this.simplexCaveThreshold) {
                            caveGen = this.caveSimplex;
                            caveBottomY = config.simplexCaveBottom.get();
                        } else {
                            if (config.enableVanillaCaves.get()) {
                                defaultCaveGen.generate(worldIn, chunkX, chunkZ, primer);
                                return;
                            }
                            caveGen = null;
                            caveBottomY = 255;
                        }

                        /* --------------------------- Configure Caverns --------------------------- */

                        // Noise values used to determine cavern region
                        float cavernRegionNoise = cavernRegionController.GetNoise(realX, realZ);
                        float waterRegionNoise = 99;

                        // Only bother calculating noise for water region if enabled
                        if (config.enableWaterRegions.get())
                            waterRegionNoise = waterCavernController.GetNoise(realX, realZ);

                        // If water region threshold check is passed, change liquid block to water
                        IBlockState liquidBlock = lavaBlock;
                        if (waterRegionNoise < waterRegionThreshold)
                            liquidBlock = waterBlock;

                        // Determine cavern type for this column. Caverns generate at low altitudes only.
                        if (cavernRegionNoise < lavaCavernThreshold) {
                            if (config.enableWaterRegions.get() && waterRegionNoise < this.waterRegionThreshold) {
                                // Generate water cavern in this column
                                cavernGen = this.cavernWater;
                            } else {
                                // Generate lava cavern in this column
                                cavernGen = this.cavernLava;
                            }
                            // Water caverns use the same cave top/bottom as lava caverns
                            cavernBottomY = config.lavaCavernBottom.get();
                            cavernTopY = config.lavaCavernTop.get();
                        } else if (cavernRegionNoise >= lavaCavernThreshold && cavernRegionNoise <= flooredCavernThreshold) {
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
                            cavernBottomY = config.flooredCavernBottom.get();
                            cavernTopY = config.flooredCavernTop.get();
                        }

                        // Extra check to provide close-off transitions on cavern edges
                        if (config.enableBoundarySmoothing.get()) {
                            if (cavernRegionNoise >= lavaCavernThreshold && cavernRegionNoise <= lavaCavernThreshold + transitionRange) {
                                float smoothAmp = Math.abs((cavernRegionNoise - (lavaCavernThreshold + transitionRange)) / transitionRange);
                                if (config.enableWaterRegions.get() && waterRegionNoise < this.waterRegionThreshold)
                                    this.cavernWater.generateColumn(chunkX, chunkZ, primer, localX, localZ,config.lavaCavernBottom.get(), config.lavaCavernTop.get(),
                                            maxSurfaceHeight, minSurfaceHeight, liquidBlock, smoothAmp);
                                else
                                    this.cavernLava.generateColumn(chunkX, chunkZ, primer, localX, localZ, config.lavaCavernBottom.get(), config.lavaCavernTop.get(),
                                        maxSurfaceHeight, minSurfaceHeight, liquidBlock, smoothAmp);
                            } else if (cavernRegionNoise <= flooredCavernThreshold && cavernRegionNoise >= flooredCavernThreshold - transitionRange) {
                                float smoothAmp = Math.abs((cavernRegionNoise - (flooredCavernThreshold - transitionRange)) / transitionRange);
                                this.cavernFloored.generateColumn(chunkX, chunkZ, primer, localX, localZ, config.flooredCavernBottom.get(), config.flooredCavernTop.get(),
                                        maxSurfaceHeight, minSurfaceHeight, liquidBlock, smoothAmp);
                            }
                        }

                        /* --------------- Dig out caves and caverns for this column --------------- */
                        // Top (Cave) layer:
                        if (caveGen != null)
                            caveGen.generateColumn(chunkX, chunkZ, primer, localX, localZ, caveBottomY, maxSurfaceHeight,
                                maxSurfaceHeight, minSurfaceHeight, liquidBlock);
                        // Bottom (Cavern) layer:
                        if (cavernGen != null)
                            cavernGen.generateColumn(chunkX, chunkZ, primer, localX, localZ, cavernBottomY, cavernTopY,
                                maxSurfaceHeight, minSurfaceHeight, liquidBlock);

                    }
                }
            }
        }
    }

    /**
     * Initialize Better Caves generators and cave region controllers for this world.
     * @param worldIn The minecraft world
     */
    private void initialize(World worldIn) {
        // Extract world information
        this.world = worldIn;
        this.dimensionID = worldIn.provider.getDimension();
        this.dimensionName = worldIn.provider.getDimensionType().toString();

        this.config = ConfigLoader.loadConfigFromFileForDimension(this.dimensionID);

        // Add this carver to map of active carvers by dimension ID.
        // Note that if a carver already exists for this dimension ID, its position
        // in the list will be overwritten.
        BetterCaves.activeCarversMap.put(dimensionID, this);

        Settings.LOGGER.info("BETTERCAVESWORLDINIT " + BetterCavesUtil.dimensionAsString(dimensionID, dimensionName));
        Settings.LOGGER.info("# of carvers: "+ BetterCaves.activeCarversMap.size());

        // Set water and lava blocks
        this.lavaBlock = getLavaBlock();
        this.waterBlock = getWaterBlock();

        // Determine noise thresholds for cave and cavern spawns
        this.lavaCavernThreshold = calcLavaCavernThreshold();
        this.flooredCavernThreshold = calcFlooredCavernThreshold();
        this.waterRegionThreshold = calcWaterRegionThreshold();
        this.cubicCaveThreshold = calcCubicCaveThreshold();
        this.simplexCaveThreshold = calcSimplexCaveThreshold();

        // Determine region controller frequencies, which control region sizes
        float caveRegionSize = calcCaveRegionSize();
        float cavernRegionSize = calcCavernRegionSize();
        float waterCavernRegionSize = .003f;

        // Special case - scale up water region size for ExtraLarge caverns
        if (config.cavernRegionSize.get() == RegionSize.ExtraLarge)
            waterCavernRegionSize = .0005f;

        // Begin initialize region controllers using world seed and user config options for region sizes
        this.caveRegionController = new FastNoise();
        this.caveRegionController.SetSeed((int)worldIn.getSeed() + 222);
        this.caveRegionController.SetFrequency(caveRegionSize);
        this.caveRegionController.SetNoiseType(FastNoise.NoiseType.Cellular);
        this.caveRegionController.SetCellularDistanceFunction(FastNoise.CellularDistanceFunction.Natural);

        // Note that Cavern Region Controller uses Simplex noise instead of Cellular
        this.cavernRegionController = new FastNoise();
        this.cavernRegionController.SetSeed((int)worldIn.getSeed() + 333);
        this.cavernRegionController.SetFrequency(cavernRegionSize);

        this.waterCavernController = new FastNoise();
        this.waterCavernController.SetSeed((int)worldIn.getSeed() + 444);
        this.waterCavernController.SetFrequency(waterCavernRegionSize);
        this.waterCavernController.SetNoiseType(FastNoise.NoiseType.Cellular);
        this.waterCavernController.SetCellularDistanceFunction(FastNoise.CellularDistanceFunction.Natural);

        /* ---------- Initialize all Better Cave carvers using config options ---------- */
        this.caveCubic = new CaveCarver.CaveCarverBuilder(worldIn)
                .ofTypeFromConfig(CaveType.CUBIC, config)
                .debugVisualizerBlock(Blocks.PLANKS.getDefaultState())
                .build();

        this.caveSimplex = new CaveCarver.CaveCarverBuilder(worldIn)
                .ofTypeFromConfig(CaveType.SIMPLEX, config)
                .debugVisualizerBlock(Blocks.COBBLESTONE.getDefaultState())
                .build();

        this.cavernLava = new CavernCarver.CavernCarverBuilder(worldIn)
                .ofTypeFromConfig(CavernType.LAVA, config)
                .debugVisualizerBlock(Blocks.REDSTONE_BLOCK.getDefaultState())
                .build();

        this.cavernFloored = new CavernCarver.CavernCarverBuilder(worldIn)
                .ofTypeFromConfig(CavernType.FLOORED, config)
                .debugVisualizerBlock(Blocks.GOLD_BLOCK.getDefaultState())
                .build();

        this.cavernWater = new CavernCarver.CavernCarverBuilder(worldIn)
                .ofTypeFromConfig(CavernType.WATER, config)
                .debugVisualizerBlock(Blocks.LAPIS_BLOCK.getDefaultState())
                .build();
    }

    /**
     * @return threshold value for cubic cave spawn rate based on Config setting
     */
    private float calcCubicCaveThreshold() {
        switch (config.cubicCaveFrequency.get()) {
            case None:
                return -99f;
            case Rare:
                return -.6f;
            case Common:
                return -.2f;
            case Custom:
                return -1f + config.cubicCaveCustomFrequency.get();
            default: // VeryCommon
                return 0;
        }
    }

    /**
     * @return threshold value for simplex cave spawn rate based on Config setting
     */
    private float calcSimplexCaveThreshold() {
        switch (config.simplexCaveFrequency.get()) {
            case None:
                return 99f;
            case Rare:
                return .6f;
            case Common:
                return .2f;
            case Custom:
                return 1f - config.simplexCaveCustomFrequency.get();
            default: // VeryCommon
                return 0;
        }
    }

    /**
     * @return threshold value for lava cavern spawn rate based on Config setting
     */
    private float calcLavaCavernThreshold() {
        switch (config.lavaCavernFrequency.get()) {
            case None:
                return -99f;
            case Rare:
                return -.8f;
            case Common:
                return -.3f;
            case VeryCommon:
                return -.1f;
            case Custom:
                return -1f + config.lavaCavernCustomFrequency.get();
            default: // Normal
                return -.4f;
        }
    }

    /**
     * @return threshold value for floored cavern spawn rate based on Config setting
     */
    private float calcFlooredCavernThreshold() {
        switch (config.flooredCavernFrequency.get()) {
            case None:
                return 99f;
            case Rare:
                return .8f;
            case Common:
                return .3f;
            case VeryCommon:
                return .1f;
            case Custom:
                return 1f - config.flooredCavernCustomFrequency.get();
            default: // Normal
                return .4f;
        }
    }

    /**
     * @return threshold value for water region spawn rate based on Config setting
     */
    private float calcWaterRegionThreshold() {
        switch (config.waterRegionFrequency.get()) {
            case Rare:
                return -.4f;
            case Common:
                return .1f;
            case VeryCommon:
                return .3f;
            case Always:
                return 99f;
            case Custom:
                return 2f * config.waterRegionCustomFrequency.get() - 1;
            default: // Normal
                return -.15f;
        }
    }

    /**
     * @return frequency value for cave region controller
     */
    private float calcCaveRegionSize() {
        switch (config.caveRegionSize.get()) {
            case Small:
                return .007f;
            case Large:
                return .0032f;
            case ExtraLarge:
                return .001f;
            default: // Medium
                return .005f;
        }
    }

    /**
     * @return frequency value for cavern region controller
     */
    private float calcCavernRegionSize() {
        switch (config.cavernRegionSize.get()) {
            case Small:
                return .01f;
            case Large:
                return .005f;
            case ExtraLarge:
                return .001f;
            default: // Medium
                return .007f;
        }
    }

    private IBlockState getLavaBlock() {
        IBlockState lava;
        try {
            lava = Block.getBlockFromName(config.lavaBlock.get()).getDefaultState();
            Settings.LOGGER.info("Using block '" + config.lavaBlock.get() + "' as lava in cave generation for dimension " +
                    BetterCavesUtil.dimensionAsString(dimensionID, dimensionName) + " ...");
        } catch (Exception e) {
            Settings.LOGGER.warn("Unable to use block '" + config.lavaBlock.get() + "': " + e);
            Settings.LOGGER.warn("Using vanilla lava instead...");
            lava = Blocks.FLOWING_LAVA.getDefaultState();
        }

        if (lava == null) {
            Settings.LOGGER.warn("Unable to use block '" + config.lavaBlock.get() + "': null block returned.\n Using vanilla lava instead...");
            lava = Blocks.FLOWING_LAVA.getDefaultState();
        }

        return lava;
    }

    private IBlockState getWaterBlock() {
        IBlockState water;
        try {
            water = Block.getBlockFromName(config.waterBlock.get()).getDefaultState();
            Settings.LOGGER.info("Using block '" + config.waterBlock.get() + "' as water in cave generation for dimension " +
                    BetterCavesUtil.dimensionAsString(dimensionID, dimensionName) + " ...");
        } catch (Exception e) {
            Settings.LOGGER.warn("Unable to use block '" + config.waterBlock.get() + "': " + e);
            Settings.LOGGER.warn("Using vanilla water instead...");
            water = Blocks.FLOWING_WATER.getDefaultState();
        }

        if (water == null) {
            Settings.LOGGER.warn("Unable to use block '" + config.waterBlock.get() + "': null block returned.\n Using vanilla water instead...");
            water = Blocks.FLOWING_WATER.getDefaultState();
        }

        return water;
    }

    private boolean isDimensionWhitelisted(int dimID) {
        // Ignore the dimension ID list if global whitelisting is enabled
        if (Configuration.caveSettings.enableGlobalWhitelist)
            return true;

        for (int dim : Configuration.caveSettings.whitelistedDimensionIDs)
            if (dimID == dim)
                return true;

        return false;
    }
}
