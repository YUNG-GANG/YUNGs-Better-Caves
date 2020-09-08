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
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.GenerationStep;

import java.util.*;

public class BetterCavesCarver {
    private StructureWorldAccess world;
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
    public void carve(Chunk chunkIn, int chunkX, int chunkZ) {
        BitSet airCarvingMask = ((ProtoChunk) chunkIn).getOrCreateCarvingMask(GenerationStep.Carver.AIR);
        BitSet liquidCarvingMask = ((ProtoChunk) chunkIn).getOrCreateCarvingMask(GenerationStep.Carver.LIQUID);

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
                        chunkIn.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG).get(x, z),
                        chunkIn.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG).get(x, z));
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
        ((ProtoChunk) chunkIn).setCarvingMask(GenerationStep.Carver.AIR, airCarvingMask);
        ((ProtoChunk) chunkIn).setCarvingMask(GenerationStep.Carver.LIQUID, liquidCarvingMask);
    }

    /**
     * Initialize Better Caves generators and cave region controllers for this world.
     */
    public void initialize(StructureWorldAccess worldIn) {
        // Extract world information
        this.world = worldIn;
        this.seed = worldIn.getSeed();
        String dimensionName = "";

        try {
            dimensionName = Objects.requireNonNull(world.toServerWorld().getRegistryKey().getValue()).toString();
        } catch (NullPointerException e) {
            BetterCaves.LOGGER.error("ERROR: Unable to get dimension name! This could be a problem...");
        }

        // Load config from file for this dimension
        this.config = dimensionName.equals("") ? new ConfigHolder() : ConfigLoader.loadConfigFromFileForDimension(dimensionName);

        // Initialize controllers
        this.caveCarverController   = new CaveCarverController(worldIn, config);
        this.cavernCarverController = new CavernCarverController(worldIn, config);
        this.waterRegionController  = new WaterRegionController(worldIn, config);
        this.ravineController       = new RavineController(worldIn, config);

        BetterCaves.LOGGER.debug(String.format("BETTER CAVES WORLD CARVER INITIALIZED WITH SEED %d IN %s", seed, dimensionName));
    }

    public void setWorld(StructureWorldAccess worldIn) {
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
