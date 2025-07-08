package com.yungnickyoung.minecraft.bettercaves.worldgen;

import com.yungnickyoung.minecraft.bettercaves.noise.NoiseUtils;
import com.yungnickyoung.minecraft.yungsapi.math.ColPos;
import com.yungnickyoung.minecraft.yungsapi.noise.FastNoise;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class ExperimentalLiquidRegions {
    private static ExperimentalLiquidRegions INSTANCE;
    public static ExperimentalLiquidRegions getInstance(ServerLevel serverLevel) {
        if (INSTANCE == null) {
            INSTANCE = new ExperimentalLiquidRegions(serverLevel);
        }
        return INSTANCE;
    }
    public static ExperimentalLiquidRegions getInstance() {
        return INSTANCE;
    }

    private final BetterCavesWorldCarverConfig.LiquidRegionSettings config;
    private final ServerLevel serverLevel;
    private final Random rand;
    private final FastNoise liquidRegionSampler;
    private final float liquidRegionThreshold;

    // TODO - make this work for multiple dimensions?
    public final ConcurrentHashMap<ChunkPos, CacheData> cache = new ConcurrentHashMap<>();
    public record CacheData(BlockState[][] liquidBlocks, int liquidAltitude) {}

    /*
     * Constants used to add a small amount of random offset to the noise threshold check to smooth out the transition
     * between liquid and non-liquid blocks.
     */
    private static final float SMOOTH_RANGE = .04f;
    private static final float SMOOTH_DELTA = .01f;

    public ExperimentalLiquidRegions(ServerLevel serverLevel) {
        this.config = new BetterCavesWorldCarverConfig.LiquidRegionSettings(.001, 40.0, -55,
                Blocks.WATER.defaultBlockState(), Blocks.LAVA.defaultBlockState());
        this.serverLevel = serverLevel;
        this.rand = new Random();

        liquidRegionThreshold = NoiseUtils.simplexNoiseOffsetByPercent(-1f,
                (float) (config.waterRegionSpawnChance() / 100));

        // Liquid region sampler
        double liquidRegionSize = config.liquidRegionSize();
        liquidRegionSampler = new FastNoise();
        liquidRegionSampler.SetSeed((int) this.serverLevel.getSeed() + 444);
        liquidRegionSampler.SetFrequency((float) liquidRegionSize);
    }

    public CacheData generateLiquidBlocksForChunk(ChunkAccess chunkAccess) {
        ChunkPos chunkPos = chunkAccess.getPos();

        // Return cached value, if available
        if (cache.containsKey(chunkPos)) {
            return cache.get(chunkPos);
        }

        // If not cached, generate the liquid blocks for the chunk
        this.rand.setSeed(this.serverLevel.getSeed() ^ chunkPos.x ^ chunkPos.z);
        BlockState[][] blocks = new BlockState[16][16];
        ColPos.Mutable pos = new ColPos.Mutable();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                pos.set(chunkPos.x * 16 + x, chunkPos.z * 16 + z);
                blocks[x][z] = this.getLiquidBlockAtPos(this.rand, pos);
            }
        }

        CacheData cacheData = new CacheData(blocks, this.getLiquidAltitude());
        cache.put(chunkPos,cacheData);
        return cacheData;
    }

    public int getLiquidAltitude() {
        return this.config.liquidAltitude();
    }

    private BlockState getLiquidBlockAtPos(Random rand, ColPos colPos) {
        if (this.liquidRegionThreshold <= -1f) { // Don't bother calculating noise if water regions are disabled
            return this.config.lavaBlockState();
        }

        float liquidRegionNoise = this.liquidRegionSampler.GetNoise(colPos.getX(), colPos.getZ());
        float barrierZoneWidth = rand.nextFloat() * SMOOTH_DELTA + SMOOTH_RANGE;

        if (liquidRegionNoise < liquidRegionThreshold - barrierZoneWidth) {
            return config.waterBlockState();
        } else if (liquidRegionNoise < liquidRegionThreshold + barrierZoneWidth) {
            return null; // Solid block barrier between water and lava regions
        } else {
            return config.lavaBlockState();
        }
    }
}
