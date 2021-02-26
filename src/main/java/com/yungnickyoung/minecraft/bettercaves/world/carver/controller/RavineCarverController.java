package com.yungnickyoung.minecraft.bettercaves.world.carver.controller;

import com.yungnickyoung.minecraft.bettercaves.config.util.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.world.carver.ravine.BetterRavineCarver;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.ConfiguredCarver;

import java.util.BitSet;
import java.util.function.Function;

public class RavineCarverController {
    private StructureWorldAccess world;
    private ChunkRandom random = new ChunkRandom();

    // Vars from config
    private boolean isRavinesEnabled;
    private boolean isDebugViewEnabled;

    private ConfiguredCarver<ProbabilityConfig> configuredCarver;
    private BetterRavineCarver betterRavineCarver;

    public RavineCarverController(StructureWorldAccess worldIn, ConfigHolder config) {
        this.world = worldIn;
        this.isRavinesEnabled = config.enableVanillaRavines.get();
        this.isDebugViewEnabled = config.debugVisualizer.get();

        this.betterRavineCarver = new BetterRavineCarver(world, config, ProbabilityConfig.CODEC);
        this.configuredCarver = new ConfiguredCarver<>(betterRavineCarver, new ProbabilityConfig(.02f));
    }

    public void carveChunk(Chunk chunkIn, int chunkX, int chunkZ, BlockState[][] liquidBlocks, Function<BlockPos, Biome> biomePos, BitSet airCarvingMask, BitSet liquidCarvingMask) {
        // Don't carve ravines if disabled or in debug view
        if (isDebugViewEnabled || !isRavinesEnabled) {
            return;
        }

        // The method for carving ravines is taken straight from vanilla.
        // We check blocks in an 8-chunk radius around each chunk to ensure ravines won't
        // be cut short along chunk boundaries.
        for (int currChunkX = chunkX - 8; currChunkX <= chunkX + 8; currChunkX++) {
            for (int currChunkZ = chunkZ - 8; currChunkZ <= chunkZ + 8; currChunkZ++) {
                random.setCarverSeed(this.world.getSeed(), currChunkX, currChunkZ);
                if (configuredCarver.shouldCarve(random, chunkX, chunkZ)) {
                    betterRavineCarver.carve(chunkIn, random, world.getSeaLevel(), currChunkX, currChunkZ, chunkX, chunkZ, liquidBlocks, biomePos, airCarvingMask, liquidCarvingMask);
                }
            }
        }
    }

    public void setWorld(StructureWorldAccess worldIn) {
        this.world = worldIn;
        this.betterRavineCarver.setWorld(worldIn);
    }
}
