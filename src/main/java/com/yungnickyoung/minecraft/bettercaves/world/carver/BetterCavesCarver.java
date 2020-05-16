package com.yungnickyoung.minecraft.bettercaves.world.carver;


import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.io.ConfigLoader;
import com.yungnickyoung.minecraft.bettercaves.config.util.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.util.ColPos;
import com.yungnickyoung.minecraft.bettercaves.world.carver.bedrock.FlattenBedrock;
import com.yungnickyoung.minecraft.bettercaves.world.carver.controller.CaveCarverController;
import com.yungnickyoung.minecraft.bettercaves.world.carver.controller.CavernCarverController;
import com.yungnickyoung.minecraft.bettercaves.world.carver.controller.RavineController;
import com.yungnickyoung.minecraft.bettercaves.world.carver.controller.WaterRegionController;
import net.minecraft.block.BlockState;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;

import java.util.*;

public class BetterCavesCarver {
    private IWorld world;
    public long seed = 0;
    public ConfigHolder config;

    // Controllers
    private CaveCarverController   caveCarverController;
    private CavernCarverController cavernCarverController;
    private WaterRegionController  waterRegionController;
    private RavineController       ravineController;

    public BetterCavesCarver() {
    }

    // Override the default carver's method to use Better Caves carving instead.
    public void carve(IChunk chunkIn, int chunkX, int chunkZ) {
        BitSet airCarvingMask = chunkIn.getCarvingMask(GenerationStage.Carving.AIR);
        BitSet liquidCarvingMask = chunkIn.getCarvingMask(GenerationStage.Carving.LIQUID);

        // Flatten bedrock into single layer, if enabled in user config
        if (config.flattenBedrock.get()) {
            FlattenBedrock.flattenBedrock(chunkIn, config.bedrockWidth.get());
        }

        // Determine surface altitudes in this chunk
        int[][] surfaceAltitudes = new int[16][16];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                surfaceAltitudes[x][z] = config.overrideSurfaceDetection.get()
                    ? 1 // Don't bother doing unnecessary calculations
                    : Math.min(
                        chunkIn.getTopBlockY(Heightmap.Type.WORLD_SURFACE_WG, x, z),
                        chunkIn.getTopBlockY(Heightmap.Type.OCEAN_FLOOR_WG, x, z));
            }
        }

        // Determine biomes in this chunk - used for flooded cave checking
        Map<Long, Biome> biomeMap = new HashMap<>();
        for (int x = chunkX * 16 - 2; x <= chunkX * 16 + 17; x++) {
            for (int z = chunkZ * 16 - 2; z <= chunkZ * 16 + 17; z++) {
                ColPos pos = new ColPos(x, z);
                biomeMap.put(pos.toLong(), world.getBiome(pos.toBlockPos()));
            }
        }

        // Determine liquid blocks for this chunk
        BlockState[][] liquidBlocks = waterRegionController.getLiquidBlocksForChunk(chunkX, chunkZ);

        // Carve chunk
        ravineController.carveChunk(chunkIn, chunkX, chunkZ, liquidBlocks, biomeMap, airCarvingMask, liquidCarvingMask);
        caveCarverController.carveChunk(chunkIn, chunkX, chunkZ, surfaceAltitudes, liquidBlocks, biomeMap, airCarvingMask, liquidCarvingMask);
        cavernCarverController.carveChunk(chunkIn, chunkX, chunkZ, surfaceAltitudes, liquidBlocks, biomeMap, airCarvingMask, liquidCarvingMask);

        // Set carving masks for features to use
        ((ChunkPrimer) chunkIn).setCarvingMask(GenerationStage.Carving.AIR, airCarvingMask);
        ((ChunkPrimer) chunkIn).setCarvingMask(GenerationStage.Carving.LIQUID, liquidCarvingMask);
    }

    /**
     * Initialize Better Caves generators and cave region controllers for this world.
     */
    public void initialize(IWorld worldIn) {
        // Extract world information
        this.world = worldIn;
        this.seed = worldIn.getSeed();
        int dimensionId = worldIn.getDimension().getType().getId();
        String dimensionName = "";

        try {
            dimensionName = Objects.requireNonNull(DimensionType.getKey(worldIn.getDimension().getType())).toString();
        } catch (NullPointerException e) {
            BetterCaves.LOGGER.error(String.format("ERROR: Failed to find name of dimension with ID %d! Using default settings...", dimensionId));
        }

        // Load config from file for this dimension
        this.config = dimensionName.equals("") ? new ConfigHolder() : ConfigLoader.loadConfigFromFileForDimension(dimensionName);

        // Initialize controllers
        this.caveCarverController   = new CaveCarverController(worldIn, config);
        this.cavernCarverController = new CavernCarverController(worldIn, config);
        this.waterRegionController  = new WaterRegionController(worldIn, config);
        this.ravineController       = new RavineController(worldIn, config);

        BetterCaves.LOGGER.debug("BETTER CAVES WORLD CARVER INITIALIZED WITH SEED " + this.seed);
        BetterCaves.LOGGER.debug(String.format("  > DIMENSION %d: %s", dimensionId, dimensionName));
    }

    public void setWorld(IWorld worldIn) {
        this.world = worldIn;
        this.caveCarverController.setWorld(worldIn);
        this.cavernCarverController.setWorld(worldIn);
        this.waterRegionController.setWorld(worldIn);
        this.ravineController.setWorld(worldIn);
    }

    public long getSeed() {
        return this.seed;
    }
}
