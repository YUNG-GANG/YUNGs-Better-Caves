package com.yungnickyoung.minecraft.bettercaves.world.carver.controller;

import com.yungnickyoung.minecraft.bettercaves.config.util.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.world.carver.ravine.RavineCarver;
import net.minecraft.block.BlockState;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;

import java.util.BitSet;

public class RavineController {
    private IWorld world;
    private long seed;
    private SharedSeedRandom random = new SharedSeedRandom();

    // Vars from config
    private boolean isRavinesEnabled;
    private boolean isDebugViewEnabled;

    private ConfiguredCarver<ProbabilityConfig> ravineCarver;

    public RavineController(IWorld worldIn, ConfigHolder config) {
        this.world = worldIn;
        this.seed = world.getSeed();
        this.isRavinesEnabled = config.enableVanillaRavines.get();
        this.isDebugViewEnabled = config.debugVisualizer.get();

        this.ravineCarver = new ConfiguredCarver<>(new RavineCarver(world, config, ProbabilityConfig::deserialize), new ProbabilityConfig(.02f));
    }

    public void carveChunk(IChunk chunkIn, int chunkX, int chunkZ, BlockState[][] liquidBlocks, BitSet airCarvingMask, BitSet liquidCarvingMask) {
        // Don't carve ravines if disabled or in debug view
        if (isDebugViewEnabled || !isRavinesEnabled) {
            return;
        }

        // The method for carving ravines is taken straight from vanilla.
        // We check blocks in an 8-chunk radius around each chunk to ensure ravines won't
        // be cut short along chunk boundaries.
        for (int currChunkX = chunkX - 8; currChunkX <= chunkX + 8; currChunkX++) {
            for (int currChunkZ = chunkZ - 8; currChunkZ <= chunkZ + 8; currChunkZ++) {
                random.setLargeFeatureSeed(seed, currChunkX, currChunkZ);
                if (ravineCarver.shouldCarve(random, chunkX, chunkZ)) {
                    ((RavineCarver) ravineCarver.carver).carve(chunkIn, random, world.getSeaLevel(), currChunkX, currChunkZ, chunkX, chunkZ, liquidBlocks, airCarvingMask, liquidCarvingMask);
                }
            }
        }
    }

    public void setWorld(IWorld worldIn) {
        this.world = worldIn;
        ((RavineCarver)this.ravineCarver.carver).setWorld(worldIn);
    }
}
