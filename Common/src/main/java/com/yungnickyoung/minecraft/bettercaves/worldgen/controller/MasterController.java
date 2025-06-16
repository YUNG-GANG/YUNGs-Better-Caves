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
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MasterController {
    private final CaveCarverController caveCarverController;
    private final CavernCarverController cavernCarverController;
    private final LiquidRegionController liquidRegionController;
    private final Map<ChunkPos, Boolean> chunkCarvedMap = new HashMap<>();

    public MasterController(ServerLevel serverLevel) {
        this.caveCarverController = new CaveCarverController(serverLevel);
        this.cavernCarverController = new CavernCarverController(serverLevel);
        this.liquidRegionController = new LiquidRegionController(serverLevel);
        BetterCavesCommon.LOGGER.debug("MASTER CONTROLLER INITIALIZED");
    }

    public boolean carve(CarverConfiguration config, ChunkAccess chunkAccess, Function<BlockPos, Holder<Biome>> biomeProvider,
                         CarvingMask carvingMask, Aquifer aquifer) {
        if (chunkCarvedMap.getOrDefault(chunkAccess.getPos(), false)) {
            return false; // Chunk has already been carved
        }

        int[][] surfaceAltitudes = getSurfaceAltitudes(chunkAccess);
        BlockState[][] liquidBlocks = liquidRegionController.getLiquidBlocksForChunk(chunkAccess);

        // Carve chunk
        caveCarverController.carveChunk(config, chunkAccess, surfaceAltitudes, liquidBlocks, biomeProvider, carvingMask, aquifer);
        cavernCarverController.carveChunk(config, chunkAccess, surfaceAltitudes, liquidBlocks, biomeProvider, carvingMask, aquifer);

        // Mark chunk as carved to prevent reprocessing
        chunkCarvedMap.put(chunkAccess.getPos(), true);

        return true;
    }

    private int[][] getSurfaceAltitudes(ChunkAccess chunkAccess) {
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
        return surfaceAltitudes;
    }
}
