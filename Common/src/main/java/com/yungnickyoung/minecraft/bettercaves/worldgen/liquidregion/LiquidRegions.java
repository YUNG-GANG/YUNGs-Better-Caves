package com.yungnickyoung.minecraft.bettercaves.worldgen.liquidregion;

import com.google.gson.annotations.SerializedName;
import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseUtils;
import com.yungnickyoung.minecraft.yungsapi.math.ColPos;
import com.yungnickyoung.minecraft.yungsapi.noise.FastNoise;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

import java.util.concurrent.ConcurrentHashMap;

public class LiquidRegions {
    public static final double DEFAULT_SIZE = 0.001;
    public static final double DEFAULT_SPAWN_CHANCE = 40.0;
    public static final int DEFAULT_ALTITUDE = -55;

    private final Settings settings;
    private final PositionalRandomFactory rand;
    private final FastNoise liquidRegionSampler;
    private final float liquidRegionThreshold;

    public final ConcurrentHashMap<ChunkPos, CacheData> cache = new ConcurrentHashMap<>();

    public record CacheData(BlockState[][] liquidBlocks, int liquidAltitude) {
    }

    /*
     * Constants used to add a small amount of random offset to the noise threshold check to smooth out the transition
     * between liquid and non-liquid blocks.
     */
    private static final float SMOOTH_RANGE = .05f;
    private static final float SMOOTH_DELTA = .005f;

    public LiquidRegions(RandomSource random, Settings settings) {
        this.settings = settings;
        long seed = random.nextLong();
        this.rand = random.forkPositional();

        this.liquidRegionThreshold = NoiseUtils.simplexNoiseOffsetByPercent(-1f,
                                                                            (float) (settings.waterRegionSpawnChance() / 100));

        // Liquid region sampler
        double liquidRegionSize = settings.liquidRegionSize();
        this.liquidRegionSampler = new FastNoise();
        this.liquidRegionSampler.SetSeed((int) seed + 444);
        this.liquidRegionSampler.SetFrequency((float) liquidRegionSize);
    }

    public CacheData getOrCreateLiquidBlocksForChunk(ChunkPos chunkPos) {
        // Return cached value, if available
        if (this.cache.containsKey(chunkPos)) {
            return this.cache.get(chunkPos);
        }

        // If not cached, generate the liquid blocks for the chunk
        var rand = this.rand.at(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ());
        BlockState[][] blocks = new BlockState[16][16];
        ColPos.Mutable pos = new ColPos.Mutable();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                pos.set(chunkPos.x() * 16 + x, chunkPos.z() * 16 + z);
                blocks[x][z] = this.getLiquidBlockAtPos(rand, pos);
            }
        }

        CacheData cacheData = new CacheData(blocks, this.getLiquidAltitude());
        this.cache.put(chunkPos, cacheData);
        return cacheData;
    }

    public int getLiquidAltitude() {
        return this.settings.liquidAltitude();
    }

    private BlockState getLiquidBlockAtPos(RandomSource rand, ColPos colPos) {
        if (this.liquidRegionThreshold <= -1f) { // Don't bother calculating noise if water regions are disabled
            return Blocks.LAVA.defaultBlockState();
        }

        float liquidRegionNoise = this.liquidRegionSampler.GetNoise(colPos.getX(), colPos.getZ());
        float barrierZoneWidth = rand.nextFloat() * SMOOTH_DELTA + SMOOTH_RANGE;

        if (liquidRegionNoise < this.liquidRegionThreshold - barrierZoneWidth) {
            return Blocks.WATER.defaultBlockState();
        } else if (liquidRegionNoise < this.liquidRegionThreshold + barrierZoneWidth) {
            return null; // Solid block barrier between water and lava regions
        } else {
            return Blocks.LAVA.defaultBlockState();
        }
    }

    public record Settings(@SerializedName("liquid_region_size") double liquidRegionSize,
                           @SerializedName("water_region_spawn_chance") double waterRegionSpawnChance,
                           @SerializedName("liquid_altitude") int liquidAltitude) {
        public static final Settings DEFAULT = new Settings(
                DEFAULT_SIZE, DEFAULT_SPAWN_CHANCE, DEFAULT_ALTITUDE
        );

        // Validate parameters after deserialization
        public Settings {
            if (liquidRegionSize <= 0) {
                BetterCavesCommon.LOGGER.error("liquid_region_size must be greater than 0.");
                BetterCavesCommon.LOGGER.error("Double check that your liquidregions.json config is correct.");
                BetterCavesCommon.LOGGER.error("Using default value of {}...", DEFAULT_SIZE);
                liquidRegionSize = DEFAULT_SIZE;
            }
            if (waterRegionSpawnChance < 0 || waterRegionSpawnChance > 100) {
                BetterCavesCommon.LOGGER.error("water_region_spawn_chance must be between 0 and 100.");
                BetterCavesCommon.LOGGER.error("Double check that your liquidregions.json config is correct.");
                BetterCavesCommon.LOGGER.error("Using default value of {}...", DEFAULT_SPAWN_CHANCE);
                waterRegionSpawnChance = DEFAULT_SPAWN_CHANCE;
            }
        }

        public Settings copy() {
            return new Settings(this.liquidRegionSize, this.waterRegionSpawnChance, this.liquidAltitude);
        }
    }
}
