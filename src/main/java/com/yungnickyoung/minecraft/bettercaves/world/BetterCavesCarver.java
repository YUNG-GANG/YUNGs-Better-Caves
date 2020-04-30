package com.yungnickyoung.minecraft.bettercaves.world;


import com.mojang.datafixers.util.Pair;
import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.BCSettings;
import com.yungnickyoung.minecraft.bettercaves.config.io.ConfigLoader;
import com.yungnickyoung.minecraft.bettercaves.config.util.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtil;
import com.yungnickyoung.minecraft.bettercaves.world.bedrock.FlattenBedrock;
import net.minecraft.block.BlockState;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashSet;
import java.util.Set;

public class BetterCavesCarver {
    public ConfigHolder config;

    public int counter = 0;
    private long oldSeed = 0;

    private CaveCarverController caveCarverController;
    private CavernCarverController cavernCarverController;
    public WaterRegionController waterRegionController;

    // The minecraft world
    public long seed = 0; // world seed

    // List used to avoid operating on a chunk more than once
    public Set<Pair<Integer, Integer>> coordList = new HashSet<>();

    public BetterCavesCarver() {
    }

    // Override the default carver's method to use Better Caves carving instead.
    public boolean carve(IChunk chunkIn, int chunkX, int chunkZ) {
        // Since the ChunkGenerator calls this method many times per chunk (~300), we must
        // check for duplicates so we don't operate on the same chunk more than once.
        Pair<Integer, Integer> pair = new Pair<>(chunkX, chunkZ);
        if (coordList.contains(pair)) {
            BetterCaves.LOGGER.warn("WARNING: DUPLICATE PAIR: " + pair);
            return true;
        }

        // Clear the list occasionally to prevent excessive memory usage.
        // This is a hacky solution, and may introduce bugs due to chunks being over- or under-processed
        if (coordList.size() > 10000) {
            coordList.clear();
            BetterCaves.LOGGER.warn("WARNING: BetterCaves chunk list reached max capacity!");
            BetterCaves.LOGGER.info("Clearing chunk list...");
        }

        coordList.add(pair);

        // Debug logging to see if any chunks may have been generated erroneously with the wrong seed
        if (seed != oldSeed) {
            BetterCaves.LOGGER.debug("CHUNKS LOADED SINCE SEED CHANGE: " + counter);
            counter = 0;
            oldSeed = seed;
        }

        counter++;

        // Flatten bedrock into single layer, if enabled in user config
        if (config.flattenBedrock.get()) {
            FlattenBedrock.flattenBedrock(chunkIn, config.bedrockWidth.get());
        }

        // Determine surface altitudes in this chunk
        int[][] surfaceAltitudes = new int[16][16];
        for (int subX = 0; subX < 16 / BCSettings.SUB_CHUNK_SIZE; subX++) {
            for (int subZ = 0; subZ < 16 / BCSettings.SUB_CHUNK_SIZE; subZ++) {
                int startX = subX * BCSettings.SUB_CHUNK_SIZE;
                int startZ = subZ * BCSettings.SUB_CHUNK_SIZE;
                for (int offsetX = 0; offsetX < BCSettings.SUB_CHUNK_SIZE; offsetX++) {
                    for (int offsetZ = 0; offsetZ < BCSettings.SUB_CHUNK_SIZE; offsetZ++) {
                        int surfaceHeight;
                        if (config.overrideSurfaceDetection.get()) {
                            surfaceHeight = 1; // Don't waste time calculating surface height if it's going to be overridden anyway
                        }
                        else {
                            surfaceHeight = BetterCavesUtil.getSurfaceAltitudeForColumn(chunkIn, startX + offsetX, startZ + offsetZ);
                        }
                        surfaceAltitudes[startX + offsetX][startZ + offsetZ] = surfaceHeight;
                    }
                }
            }
        }

        // Determine liquid blocks for this chunk
        BlockState[][] liquidBlocks = waterRegionController.getLiquidBlocksForChunk(chunkX, chunkZ);

        // Carve chunk
        caveCarverController.carveChunk(chunkIn, chunkX, chunkZ, surfaceAltitudes, liquidBlocks);
        cavernCarverController.carveChunk(chunkIn, chunkX, chunkZ, surfaceAltitudes, liquidBlocks);

        // Unsure if this is needed?
        return true;
    }

    /**
     * Initialize Better Caves generators and cave region controllers for this world.
     */
    public void initialize(long seed, DimensionType dimensionType) {
        // Extract world information
        this.seed = seed;
        int dimensionId = dimensionType.getId();
        String dimensionName = DimensionType.getKey(dimensionType).toString();

        // Load config from file for this dimension
        this.config = ConfigLoader.loadConfigFromFileForDimension(dimensionId);

        // Initialize controllers
        this.waterRegionController = new WaterRegionController(seed, dimensionType, config);
        this.caveCarverController = new CaveCarverController(seed, config);
        this.cavernCarverController = new CavernCarverController(seed, config);

        BetterCaves.LOGGER.debug("BETTER CAVES WORLD CARVER INITIALIZED WITH SEED " + this.seed);
        BetterCaves.LOGGER.debug(String.format("  > DIMENSION %d: %s", dimensionId, dimensionName));
        BetterCaves.LOGGER.debug(String.format("  > COUNTER: %d", counter));
    }
}
