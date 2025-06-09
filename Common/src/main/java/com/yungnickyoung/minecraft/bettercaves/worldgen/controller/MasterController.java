package com.yungnickyoung.minecraft.bettercaves.worldgen.controller;

import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;

import java.util.function.Function;

public class MasterController {
    public long seed = 0;
//    public ConfigHolder configHolder;

    // Controllers
    private CaveCarverController caveCarverController;
    private CavernCarverController cavernCarverController;
    private LiquidRegionController liquidRegionController;
//    private RavineCarverController ravineCarverController;

    public boolean carve(CarverConfiguration config, ChunkAccess chunkAccess, Function<BlockPos, Holder<Biome>> biomeProvider,
                         ChunkPos chunkPos, CarvingMask carvingMask) {
        // Determine surface altitudes in this chunk
        int[][] surfaceAltitudes = new int[16][16];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                surfaceAltitudes[x][z] = BetterCavesCommon.CONFIG.undergroundGen.misc.overrideSurfaceDetection
                        ? 1 // Don't bother doing unnecessary calculations
                        : Math.min(
                        chunkAccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z),
                        chunkAccess.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z));
            }
        }

        // Determine liquid blocks for this chunk
        BlockState[][] liquidBlocks = liquidRegionController.getLiquidBlocksForChunk(chunkPos);

        // Carve chunk
//        ravineCarverController.carveChunk(chunkAccess, chunkX, chunkZ, liquidBlocks, biomePos, airCarvingMask, liquidCarvingMask);
        caveCarverController.carveChunk(config, chunkAccess, chunkPos, surfaceAltitudes, liquidBlocks, biomeProvider, carvingMask);
        cavernCarverController.carveChunk(config, chunkAccess, chunkPos, surfaceAltitudes, liquidBlocks, biomeProvider, carvingMask);

        return true;
    }

    /**
     * Initialize Better Caves generators and cave region controllers for this world.
     */
    public void initialize(ServerLevel serverLevel) {
        // Extract world information
        this.seed = serverLevel.getSeed();

        // Initialize controllers
        this.caveCarverController   = new CaveCarverController(serverLevel);
        this.cavernCarverController = new CavernCarverController(serverLevel);
        this.liquidRegionController = new LiquidRegionController(serverLevel);
//        this.ravineCarverController = new RavineCarverController(serverLevel, configHolder);

        BetterCavesCommon.LOGGER.debug("BETTER CAVES WORLD CARVER INITIALIZED");
    }

//    /**
//     * Updates the current world, and propagates the update to all carver controllers.
//     */
//    public void setServerLevel(LevelReader serverLevel) {
//        this.world = serverLevel;
//        this.caveCarverController.setWorld(serverLevel);
//        this.cavernCarverController.setWorld(serverLevel);
//        this.liquidRegionController.setWorld(serverLevel);
//        this.ravineCarverController.setWorld(serverLevel);
//    }

    public long getSeed() {
        return this.seed;
    }
}
