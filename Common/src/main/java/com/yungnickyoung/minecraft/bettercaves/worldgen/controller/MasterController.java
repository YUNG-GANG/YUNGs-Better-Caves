package com.yungnickyoung.minecraft.bettercaves.worldgen.controller;

import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.worldgen.BetterCavesWorldCarverConfig;
import com.yungnickyoung.minecraft.bettercaves.worldgen.ExperimentalLiquidRegions;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class MasterController {
    private final ServerLevel serverLevel;
    private final BetterCavesWorldCarverConfig config;
    private final List<CaveCarverController> caveLayers = new ArrayList<>();
    private final List<CavernCarverController> cavernLayers = new ArrayList<>();
//    private final LiquidRegionController liquidRegionController;
    private final Set<ChunkPos> carvedChunkCache = new HashSet<>();

    public MasterController(ServerLevel serverLevel, BetterCavesWorldCarverConfig config) {
        this.serverLevel = serverLevel;
        this.config = config;
        config.caveLayers.forEach(caveLayerSettings -> this.caveLayers.add(
                new CaveCarverController(serverLevel, config, caveLayerSettings)));
        config.cavernLayers.forEach(cavernLayerSettings -> this.cavernLayers.add(
                new CavernCarverController(serverLevel, config, cavernLayerSettings)));
//        this.liquidRegionController = new LiquidRegionController(serverLevel, config);
        BetterCavesCommon.LOGGER.debug("MASTER CONTROLLER INITIALIZED");
    }

    public boolean carve(ChunkAccess chunkAccess, Function<BlockPos, Holder<Biome>> biomeProvider,
                         CarvingMask carvingMask, Aquifer aquifer) {
        if (carvedChunkCache.contains(chunkAccess.getPos())) {
            return false; // Chunk has already been carved
        }

        int[][] surfaceAltitudes = getSurfaceAltitudes(chunkAccess);
//        BlockState[][] liquidBlocks = liquidRegionController.getLiquidBlocksForChunk(chunkAccess);
        BlockState[][] liquidBlocks = ExperimentalLiquidRegions.getInstance(this.serverLevel).generateLiquidBlocksForChunk(chunkAccess).liquidBlocks();

        // Carve chunk
        caveLayers.forEach(caveLayer -> caveLayer.carveChunk(chunkAccess, surfaceAltitudes, liquidBlocks, biomeProvider, carvingMask, aquifer));
        cavernLayers.forEach(cavernLayer -> cavernLayer.carveChunk(chunkAccess, surfaceAltitudes, liquidBlocks, biomeProvider, carvingMask, aquifer));

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
