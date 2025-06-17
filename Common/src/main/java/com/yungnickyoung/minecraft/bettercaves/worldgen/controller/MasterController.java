package com.yungnickyoung.minecraft.bettercaves.worldgen.controller;

import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.worldgen.BetterCavesWorldCarverConfig;
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

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class MasterController {
    private final BetterCavesWorldCarverConfig config;
    private final CaveCarverController caveCarverController;
    private final CavernCarverController cavernCarverController;
    private final LiquidRegionController liquidRegionController;
    private final Set<ChunkPos> carvedChunkCache = new HashSet<>();

    public MasterController(ServerLevel serverLevel, BetterCavesWorldCarverConfig config) {
        this.config = config;
        this.caveCarverController = new CaveCarverController(serverLevel, config);
        this.cavernCarverController = new CavernCarverController(serverLevel, config);
        this.liquidRegionController = new LiquidRegionController(serverLevel, config);
        BetterCavesCommon.LOGGER.debug("MASTER CONTROLLER INITIALIZED");
    }

    public boolean carve(ChunkAccess chunkAccess, Function<BlockPos, Holder<Biome>> biomeProvider,
                         CarvingMask carvingMask, Aquifer aquifer) {
        if (carvedChunkCache.contains(chunkAccess.getPos())) {
            return false; // Chunk has already been carved
        }

        int[][] surfaceAltitudes = getSurfaceAltitudes(chunkAccess);
        BlockState[][] liquidBlocks = liquidRegionController.getLiquidBlocksForChunk(chunkAccess);

        // Carve chunk
        caveCarverController.carveChunk(chunkAccess, surfaceAltitudes, liquidBlocks, biomeProvider, carvingMask, aquifer);
        cavernCarverController.carveChunk(chunkAccess, surfaceAltitudes, liquidBlocks, biomeProvider, carvingMask, aquifer);

        // Mark chunk as carved to prevent duplicate processing
        carvedChunkCache.add(chunkAccess.getPos());

        return true;
    }

    private int[][] getSurfaceAltitudes(ChunkAccess chunkAccess) {
        int[][] surfaceAltitudes = new int[16][16];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                surfaceAltitudes[x][z] = this.config.misc.overrideSurfaceDetection()
                        ? 1 // Don't bother doing unnecessary calculations
                        : Math.min(
                        chunkAccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z),
                        chunkAccess.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z));
            }
        }
        return surfaceAltitudes;
    }
}
