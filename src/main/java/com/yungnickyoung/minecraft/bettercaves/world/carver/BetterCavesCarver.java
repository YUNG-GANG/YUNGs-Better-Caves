package com.yungnickyoung.minecraft.bettercaves.world.carver;


import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.BCSettings;
import com.yungnickyoung.minecraft.bettercaves.config.io.ConfigLoader;
import com.yungnickyoung.minecraft.bettercaves.config.util.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtils;
import com.yungnickyoung.minecraft.bettercaves.world.ColPos;
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
                if (config.overrideSurfaceDetection.get()) {
                    surfaceAltitudes[x][z] = 1; // Don't bother doing unnecessary data retrieval
                }
                else {
                    surfaceAltitudes[x][z] = chunkIn.getTopBlockY(Heightmap.Type.WORLD_SURFACE_WG, x, z);
                }
            }
        }
//        for (int subX = 0; subX < 16 / BCSettings.SUB_CHUNK_SIZE; subX++) {
//            for (int subZ = 0; subZ < 16 / BCSettings.SUB_CHUNK_SIZE; subZ++) {
//                int startX = subX * BCSettings.SUB_CHUNK_SIZE;
//                int startZ = subZ * BCSettings.SUB_CHUNK_SIZE;
//                for (int offsetX = 0; offsetX < BCSettings.SUB_CHUNK_SIZE; offsetX++) {
//                    for (int offsetZ = 0; offsetZ < BCSettings.SUB_CHUNK_SIZE; offsetZ++) {
//                        int surfaceHeight;
//                        if (config.overrideSurfaceDetection.get()) {
//                            surfaceHeight = 1; // Don't waste time calculating surface height if it's going to be overridden anyway
//                        }
//                        else {
//                            surfaceHeight = BetterCavesUtils.getSurfaceAltitudeForColumn(chunkIn, startX + offsetX, startZ + offsetZ);
//                        }
//                        surfaceAltitudes[startX + offsetX][startZ + offsetZ] = surfaceHeight;
//                    }
//                }
//            }
//        }

        Map<Long, Biome> biomeMap = new HashMap<>();

        // Cache biomes at each block pos for use in carvers
        for (int x = chunkX * 16 - 2; x <= chunkX * 16 + 17; x++) {
            for (int z = chunkZ * 16 - 2; z <= chunkZ * 16 + 17; z++) {
                ColPos pos = new ColPos(x, z);
                biomeMap.put(pos.toLong(), world.getBiome(pos.toBlockPos()));
            }
        }

        // Determine liquid blocks for this chunk
        BlockState[][] liquidBlocks = waterRegionController.getLiquidBlocksForChunk(chunkX, chunkZ);

        // Carve chunk
        caveCarverController.carveChunk(chunkIn, chunkX, chunkZ, surfaceAltitudes, liquidBlocks, biomeMap, airCarvingMask, liquidCarvingMask);
        cavernCarverController.carveChunk(chunkIn, chunkX, chunkZ, surfaceAltitudes, liquidBlocks, biomeMap, airCarvingMask, liquidCarvingMask);
        ravineController.carveChunk(chunkIn, chunkX, chunkZ, liquidBlocks, biomeMap, airCarvingMask, liquidCarvingMask);

        // Update ocean floor heightmap so that seagrass doesn't spawn floating above open flooded ravines.
        // This is necessary now that this class is accessed via a feature. Heightmaps are not updated between
        // features; only before and after all of them are generated.
        // Note that kelp does not suffer from this problem because (I think) it checks for a solid block.
//        Heightmap.updateChunkHeightmaps(chunkIn, EnumSet.of(Heightmap.Type.OCEAN_FLOOR_WG, Heightmap.Type.OCEAN_FLOOR));

        // Set carving masks for features to use
        ((ChunkPrimer)chunkIn).setCarvingMask(GenerationStage.Carving.AIR, airCarvingMask);
        ((ChunkPrimer)chunkIn).setCarvingMask(GenerationStage.Carving.LIQUID, liquidCarvingMask);


    }

    /**
     * Initialize Better Caves generators and cave region controllers for this world.
     */
    public void initialize(IWorld worldIn) {
        // Extract world information
        this.world = worldIn;
        this.seed = worldIn.getSeed();
        int dimensionId = worldIn.getDimension().getType().getId();
        String dimensionName;

        try {
            dimensionName = Objects.requireNonNull(DimensionType.getKey(worldIn.getDimension().getType())).toString();
        } catch (NullPointerException e) {
            dimensionName = "";
        }

        // Load config from file for this dimension
        this.config = ConfigLoader.loadConfigFromFileForDimension(dimensionId);

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
