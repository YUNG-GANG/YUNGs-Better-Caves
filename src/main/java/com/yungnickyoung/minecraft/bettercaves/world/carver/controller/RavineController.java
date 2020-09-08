package com.yungnickyoung.minecraft.bettercaves.world.carver.controller;

import com.yungnickyoung.minecraft.bettercaves.config.util.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.world.carver.ravine.BCRavineCarver;
import net.minecraft.block.BlockState;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.ConfiguredCarver;

import java.util.BitSet;
import java.util.Map;

public class RavineController {
    private StructureWorldAccess world;
    private ChunkRandom random = new ChunkRandom();

    // Vars from config
    private boolean isRavinesEnabled;
    private boolean isDebugViewEnabled;

    private ConfiguredCarver<ProbabilityConfig> configuredCarver;
    private BCRavineCarver BCRavineCarver;

    public RavineController(StructureWorldAccess worldIn, ConfigHolder config) {
        this.world = worldIn;
        this.isRavinesEnabled = config.enableVanillaRavines.get();
        this.isDebugViewEnabled = config.debugVisualizer.get();

        this.BCRavineCarver = new BCRavineCarver(world, config, ProbabilityConfig.CODEC);
        this.configuredCarver = new ConfiguredCarver<>(BCRavineCarver, new ProbabilityConfig(.02f));
    }

    public void carveChunk(Chunk chunkIn, int chunkX, int chunkZ, BlockState[][] liquidBlocks, Map<Long, Biome> biomeMap, BitSet airCarvingMask, BitSet liquidCarvingMask) {
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
                    BCRavineCarver.carve(chunkIn, random, world.getSeaLevel(), currChunkX, currChunkZ, chunkX, chunkZ, liquidBlocks, biomeMap, airCarvingMask, liquidCarvingMask);
                }
            }
        }
    }

    public void setWorld(StructureWorldAccess worldIn) {
        this.world = worldIn;
        this.BCRavineCarver.setWorld(worldIn);
    }
}
