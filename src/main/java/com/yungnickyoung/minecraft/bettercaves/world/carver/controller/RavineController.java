package com.yungnickyoung.minecraft.bettercaves.world.carver.controller;

import com.yungnickyoung.minecraft.bettercaves.config.util.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.world.carver.ravine.RavineCarver;
import net.minecraft.block.BlockState;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;

import java.util.BitSet;
import java.util.Map;

public class RavineController {
    private ISeedReader world;
    private SharedSeedRandom random = new SharedSeedRandom();

    // Vars from config
    private boolean isRavinesEnabled;
    private boolean isDebugViewEnabled;

    private ConfiguredCarver<ProbabilityConfig> configuredCarver;
    private RavineCarver ravineCarver;

    public RavineController(ISeedReader worldIn, ConfigHolder config) {
        this.world = worldIn;
        this.isRavinesEnabled = config.enableVanillaRavines.get();
        this.isDebugViewEnabled = config.debugVisualizer.get();

        this.ravineCarver = new RavineCarver(world, config, ProbabilityConfig.field_236576_b_);
        this.configuredCarver = new ConfiguredCarver<>(ravineCarver, new ProbabilityConfig(.02f));
    }

    public void carveChunk(IChunk chunkIn, int chunkX, int chunkZ, BlockState[][] liquidBlocks, Map<Long, Biome> biomeMap, BitSet airCarvingMask, BitSet liquidCarvingMask) {
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
                if (configuredCarver.shouldCarve(random, chunkX, chunkZ)) {
                    ravineCarver.carve(chunkIn, random, world.getSeaLevel(), currChunkX, currChunkZ, chunkX, chunkZ, liquidBlocks, biomeMap, airCarvingMask, liquidCarvingMask);
                }
            }
        }
    }

    public void setWorld(ISeedReader worldIn) {
        this.world = worldIn;
        this.ravineCarver.setWorld(worldIn);
    }
}
