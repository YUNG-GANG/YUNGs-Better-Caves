package com.yungnickyoung.minecraft.bettercaves.world.carver.controller;

import com.yungnickyoung.minecraft.bettercaves.config.util.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.world.carver.ravine.BetterRavineCarver;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;

import java.util.BitSet;
import java.util.Map;

public class RavineController {
    private WorldGenLevel world;
    private WorldgenRandom random = new WorldgenRandom();

    // Vars from config
    private boolean isRavinesEnabled;
    private boolean isDebugViewEnabled;

    private ConfiguredWorldCarver<ProbabilityFeatureConfiguration> configuredCarver;
    private BetterRavineCarver betterRavineCarver;

    public RavineController(WorldGenLevel worldIn, ConfigHolder config) {
        this.world = worldIn;
        this.isRavinesEnabled = config.enableVanillaRavines.get();
        this.isDebugViewEnabled = config.debugVisualizer.get();

        this.betterRavineCarver = new BetterRavineCarver(world, config, ProbabilityFeatureConfiguration.CODEC);
        this.configuredCarver = new ConfiguredWorldCarver<>(betterRavineCarver, new ProbabilityFeatureConfiguration(.02f));
    }

    public void carveChunk(ChunkAccess chunkIn, int chunkX, int chunkZ, BlockState[][] liquidBlocks, Map<Long, Biome> biomeMap, BitSet airCarvingMask, BitSet liquidCarvingMask) {
        // Don't carve ravines if disabled or in debug view
        if (isDebugViewEnabled || !isRavinesEnabled) {
            return;
        }

        // The method for carving ravines is taken straight from vanilla.
        // We check blocks in an 8-chunk radius around each chunk to ensure ravines won't
        // be cut short along chunk boundaries.
        for (int currChunkX = chunkX - 8; currChunkX <= chunkX + 8; currChunkX++) {
            for (int currChunkZ = chunkZ - 8; currChunkZ <= chunkZ + 8; currChunkZ++) {
                random.setLargeFeatureSeed(this.world.getSeed(), currChunkX, currChunkZ);
                if (configuredCarver.isStartChunk(random, chunkX, chunkZ)) {
                    betterRavineCarver.carve(chunkIn, random, world.getSeaLevel(), currChunkX, currChunkZ, chunkX, chunkZ, liquidBlocks, biomeMap, airCarvingMask, liquidCarvingMask);
                }
            }
        }
    }

    public void setWorld(WorldGenLevel worldIn) {
        this.world = worldIn;
        this.betterRavineCarver.setWorld(worldIn);
    }
}
