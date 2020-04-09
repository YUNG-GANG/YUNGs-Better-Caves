package com.yungnickyoung.minecraft.bettercaves.world;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.config.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.config.ConfigLoader;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtils;
import com.yungnickyoung.minecraft.bettercaves.world.bedrock.FlattenBedrock;
import com.yungnickyoung.minecraft.bettercaves.world.carver.surface.SurfaceCaveCarver;
import net.minecraft.block.state.IBlockState;
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
    // Vanilla cave gen if user sets config to use it
    private MapGenBase defaultCaveGen;

    // Region Controllers
    public WaterRegionController waterRegionController;
    private CaveCarverController caveCarverController;
    private CavernCarverController cavernCarverController;
    private SurfaceCaveCarver surfaceCaveCarver;

    // Dimension this instance of MapGenBetterCaves is used in
    private int dimensionID;
    private String dimensionName;

    // Config holder for non-global config options that may be specific to this carver
    public ConfigHolder config = new ConfigHolder();

    // DEBUG
    private int counter = 200;

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
        if (world == null) { // First call - lazy initialization of all controllers and config
            this.initialize(worldIn);
        }

        // Only operate on whitelisted dimensions.
        // I tried just setting the event's NewGen to its OriginalGen but turns out that doesn't
        // doesn't do anything after the cave gen process has been initiated.
        if (!isDimensionWhitelisted(dimensionID)) {
            defaultCaveGen.generate(worldIn, chunkX, chunkZ, primer);
            return;
        }

        counter--;
        if (counter <= 0) {
            Settings.LOGGER.warn("BETTERCAVESWORLD " + world.getSeed() + " | " +
                    BetterCavesUtils.dimensionAsString(dimensionID, dimensionName) + " | " +
                    BetterCaves.activeCarversMap.size() + " | " + this.hashCode());
            counter = 200;
        }

        // Flatten bedrock, if enabled
        if (config.flattenBedrock.get())
            FlattenBedrock.flattenBedrock(primer, config.bedrockWidth.get());

        // Determine surface altitudes in this chunk
        int[][] surfaceAltitudes = new int[16][16];
        for (int subX = 0; subX < 16 / Settings.SUB_CHUNK_SIZE; subX++) {
            for (int subZ = 0; subZ < 16 / Settings.SUB_CHUNK_SIZE; subZ++) {
                int startX = subX * Settings.SUB_CHUNK_SIZE;
                int startZ = subZ * Settings.SUB_CHUNK_SIZE;
                for (int offsetX = 0; offsetX < Settings.SUB_CHUNK_SIZE; offsetX++) {
                    for (int offsetZ = 0; offsetZ < Settings.SUB_CHUNK_SIZE; offsetZ++) {
                        int surfaceHeight;
                        if (config.overrideSurfaceDetection.get()) {
                            surfaceHeight = 1; // Don't waste time calculating surface height if it's going to be overridden anyway
                        }
                        else {
                            surfaceHeight = BetterCavesUtils.getSurfaceAltitudeForColumn(primer, startX + offsetX, startZ + offsetZ);
                        }
                        surfaceAltitudes[startX + offsetX][startZ + offsetZ] = surfaceHeight;
                    }
                }
            }
        }

        // Determine liquid blocks for this chunk
        IBlockState[][] liquidBlocks = waterRegionController.getLiquidBlocksForChunk(chunkX, chunkZ);

        // Carve chunk
        caveCarverController.carveChunk(primer, chunkX, chunkZ, surfaceAltitudes, liquidBlocks);
        cavernCarverController.carveChunk(primer, chunkX, chunkZ, surfaceAltitudes, liquidBlocks);
        surfaceCaveCarver.generate(worldIn, chunkX, chunkZ, primer);
    }

    /**
     * Initialize Better Caves carvers and controllers for this dimension.
     * @param worldIn The minecraft world
     */
    private void initialize(World worldIn) {
        // Extract world information
        this.world = worldIn;
        this.dimensionID = worldIn.provider.getDimension();
        this.dimensionName = worldIn.provider.getDimensionType().toString();

        // Load config for this dimension
        this.config = ConfigLoader.loadConfigFromFileForDimension(this.dimensionID);

        // Add this carver to map of active carvers by dimension ID.
        // Note that if a carver already exists for this dimension ID, its position
        // in the list will be overwritten.
        BetterCaves.activeCarversMap.put(dimensionID, this);

        Settings.LOGGER.info("BETTERCAVESWORLDINIT " + BetterCavesUtils.dimensionAsString(dimensionID, dimensionName));
        Settings.LOGGER.info("# of carvers: "+ BetterCaves.activeCarversMap.size());

        // Initialize controllers
        this.waterRegionController = new WaterRegionController(worldIn, config);
        this.caveCarverController = new CaveCarverController(world, config, defaultCaveGen);
        this.cavernCarverController = new CavernCarverController(worldIn, config);
        this.surfaceCaveCarver = new SurfaceCaveCarver();
    }

    /**
     * @return true if the provided dimension ID is whitelisted in the config
     */
    private boolean isDimensionWhitelisted(int dimID) {
        // Ignore the dimension ID list if global whitelisting is enabled
        if (Configuration.enableGlobalWhitelist)
            return true;

        for (int dim : Configuration.whitelistedDimensionIDs)
            if (dimID == dim) return true;

        return false;
    }
}
