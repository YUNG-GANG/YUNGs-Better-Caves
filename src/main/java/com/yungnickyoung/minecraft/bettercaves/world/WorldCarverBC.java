package com.yungnickyoung.minecraft.bettercaves.world;


import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Pair;
import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.BetterCavesConfig;
import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtil;
import com.yungnickyoung.minecraft.bettercaves.world.bedrock.FlattenBedrock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.*;
import net.minecraft.world.gen.feature.ProbabilityConfig;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class WorldCarverBC extends WorldCarver<ProbabilityConfig> {
    public static int counter = 0;
    public long oldSeed = 0;

    private CaveCarverController caveCarverController;
    private CavernCarverController cavernCarverController;
    private WaterRegionController waterRegionController;

    // The minecraft world
    private long seed = 0; // world seed
    private int dimensionId;
    private String dimensionName;

    // List used to avoid operating on a chunk more than once
    public Set<Pair<Integer, Integer>> coordList = new HashSet<>();

    public WorldCarverBC(Function<Dynamic<?>, ? extends ProbabilityConfig> p_i49929_1_, int p_i49929_2_) {
        super(p_i49929_1_, p_i49929_2_);
    }

    // Override the default carver's method to use Better Caves carving instead.
    @Override
    public boolean carve(IChunk chunkIn, Random rand, int seaLevel, int cX, int cZ, int chunkX, int chunkZ, BitSet carvingMask, ProbabilityConfig config) {
        // Since the ChunkGenerator calls this method many times per chunk (~300), we must
        // check for duplicates so we don't operate on the same chunk more than once.
        Pair<Integer, Integer> pair = new Pair<>(chunkX, chunkZ);
        if (coordList.contains(pair))
            return true;

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
        if (BetterCavesConfig.flattenBedrock) {
            FlattenBedrock.flattenBedrock(chunkIn, BetterCavesConfig.bedrockWidth);
        }

        // Determine surface altitudes in this chunk
        int[][] surfaceAltitudes = new int[16][16];
        for (int subX = 0; subX < 16 / Settings.SUB_CHUNK_SIZE; subX++) {
            for (int subZ = 0; subZ < 16 / Settings.SUB_CHUNK_SIZE; subZ++) {
                int startX = subX * Settings.SUB_CHUNK_SIZE;
                int startZ = subZ * Settings.SUB_CHUNK_SIZE;
                for (int offsetX = 0; offsetX < Settings.SUB_CHUNK_SIZE; offsetX++) {
                    for (int offsetZ = 0; offsetZ < Settings.SUB_CHUNK_SIZE; offsetZ++) {
                        int surfaceHeight;
                        if (BetterCavesConfig.overrideSurfaceDetection) {
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
//        chunkIn.setModified(true);
        return true;
    }

    /**
     * Initialize Better Caves generators and cave region controllers for this world.
     */
    public void initialize(long seed, int dimensionId, String dimensionName) {
        // Extract world information
        this.seed = seed;
        this.dimensionId = dimensionId;
        this.dimensionName = dimensionName;

        // TODO - load config for this dimension

        // TODO - add this carver to map of active carvers by dimension ID

        // Initialize controllers
        this.waterRegionController = new WaterRegionController(seed, null);
        this.caveCarverController = new CaveCarverController(seed, null);
        this.cavernCarverController = new CavernCarverController(seed, null);

        BetterCaves.LOGGER.debug("BETTER CAVES WORLD CARVER INITIALIZED WITH SEED " + this.seed);
        BetterCaves.LOGGER.debug(String.format("DIMENSION %d: %s", dimensionId, dimensionName));
    }

    @Override
    public boolean shouldCarve(Random rand, int chunkX, int chunkZ, ProbabilityConfig config) {
        return true;
    }

    @Override
    protected boolean func_222708_a(double p_222708_1_, double p_222708_3_, double p_222708_5_, int p_222708_7_) {
        return false;
    }
}
