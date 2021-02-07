package com.yungnickyoung.minecraft.bettercaves.world.carver.controller;


import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.io.ConfigLoader;
import com.yungnickyoung.minecraft.bettercaves.config.util.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.world.carver.bedrock.BedrockFlattener;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;

import java.util.BitSet;
import java.util.Objects;
import java.util.function.Function;

public class MasterController {
    private ISeedReader world;
    public long seed = 0;
    public ConfigHolder configHolder;

    // Controllers
    private CaveCarverController   caveCarverController;
    private CavernCarverController cavernCarverController;
    private LiquidRegionController liquidRegionController;
    private RavineCarverController ravineCarverController;

    public boolean carveRegion(IChunk chunkIn, Function<BlockPos, Biome> biomePos, int chunkX, int chunkZ, BitSet airCarvingMask, BitSet liquidCarvingMask) {
        // Flatten bedrock into single layer, if enabled in user config, before carving caves
        if (configHolder.flattenBedrock.get()) {
            BedrockFlattener.flattenBedrock(chunkIn, biomePos, configHolder.bedrockWidth.get());
        }

        // Determine surface altitudes in this chunk
        int[][] surfaceAltitudes = new int[16][16];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                surfaceAltitudes[x][z] = configHolder.overrideSurfaceDetection.get()
                    ? 1 // Don't bother doing unnecessary calculations
                    : Math.min(
                        chunkIn.getTopBlockY(Heightmap.Type.WORLD_SURFACE_WG, x, z),
                        chunkIn.getTopBlockY(Heightmap.Type.OCEAN_FLOOR_WG, x, z));
            }
        }

        // Determine liquid blocks for this chunk
        BlockState[][] liquidBlocks = liquidRegionController.getLiquidBlocksForChunk(chunkX, chunkZ);

        // Carve chunk
        ravineCarverController.carveChunk(chunkIn, chunkX, chunkZ, liquidBlocks, biomePos, airCarvingMask, liquidCarvingMask);
        caveCarverController.carveChunk(chunkIn, chunkX, chunkZ, surfaceAltitudes, liquidBlocks, biomePos, airCarvingMask, liquidCarvingMask);
        cavernCarverController.carveChunk(chunkIn, chunkX, chunkZ, surfaceAltitudes, liquidBlocks, biomePos, airCarvingMask, liquidCarvingMask);

        return true;
    }

    /**
     * Initialize Better Caves generators and cave region controllers for this world.
     */
    public void initialize(ISeedReader worldIn) {
        // Extract world information
        this.world = worldIn;
        this.seed = worldIn.getSeed();
        String dimensionName = "";

        try {
            dimensionName = Objects.requireNonNull(world.getWorld().getDimensionKey().getLocation()).toString();
        } catch (NullPointerException e) {
            BetterCaves.LOGGER.error("ERROR: Unable to get dimension name! This could be a problem...");
        }

        // Load config from file for this dimension
        this.configHolder = dimensionName.equals("") ? new ConfigHolder() : ConfigLoader.loadConfigFromFileForDimension(dimensionName);

        // Initialize controllers
        this.caveCarverController   = new CaveCarverController(worldIn, configHolder);
        this.cavernCarverController = new CavernCarverController(worldIn, configHolder);
        this.liquidRegionController = new LiquidRegionController(worldIn, configHolder);
        this.ravineCarverController = new RavineCarverController(worldIn, configHolder);

        BetterCaves.LOGGER.debug(String.format("BETTER CAVES WORLD CARVER INITIALIZED WITH SEED %d IN %s", seed, dimensionName));
    }

    /**
     * Updates the current world, and propogates the update to all carver controllers.
     */
    public void setWorld(ISeedReader worldIn) {
        this.world = worldIn;
        this.caveCarverController.setWorld(worldIn);
        this.cavernCarverController.setWorld(worldIn);
        this.liquidRegionController.setWorld(worldIn);
        this.ravineCarverController.setWorld(worldIn);
    }

    public long getSeed() {
        return this.seed;
    }
}
