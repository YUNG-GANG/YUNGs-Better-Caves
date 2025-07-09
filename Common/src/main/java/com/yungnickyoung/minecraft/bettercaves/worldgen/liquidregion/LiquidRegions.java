package com.yungnickyoung.minecraft.bettercaves.worldgen.liquidregion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseUtils;
import com.yungnickyoung.minecraft.yungsapi.math.ColPos;
import com.yungnickyoung.minecraft.yungsapi.noise.FastNoise;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class LiquidRegions {
    private final Settings settings;
    private final ServerLevel serverLevel;
    private final Random rand;
    private final FastNoise liquidRegionSampler;
    private final float liquidRegionThreshold;

    public final ConcurrentHashMap<ChunkPos, CacheData> cache = new ConcurrentHashMap<>();
    public record CacheData(BlockState[][] liquidBlocks, int liquidAltitude) {}

    /*
     * Constants used to add a small amount of random offset to the noise threshold check to smooth out the transition
     * between liquid and non-liquid blocks.
     */
    private static final float SMOOTH_RANGE = .04f;
    private static final float SMOOTH_DELTA = .01f;

    public LiquidRegions(ServerLevel serverLevel) {
        // TODO - use dimension-specific configs instead
        this.settings = new Settings(
                BetterCavesCommon.CONFIG.liquidRegions.liquidRegionSize,
                BetterCavesCommon.CONFIG.liquidRegions.waterRegionSpawnChance,
                BetterCavesCommon.CONFIG.liquidRegions.liquidAltitude,
                Blocks.WATER.defaultBlockState(),
                Blocks.LAVA.defaultBlockState()
        );
        this.serverLevel = serverLevel;
        this.rand = new Random();

        liquidRegionThreshold = NoiseUtils.simplexNoiseOffsetByPercent(-1f,
                (float) (settings.waterRegionSpawnChance() / 100));

        // Liquid region sampler
        double liquidRegionSize = settings.liquidRegionSize();
        liquidRegionSampler = new FastNoise();
        liquidRegionSampler.SetSeed((int) this.serverLevel.getSeed() + 444);
        liquidRegionSampler.SetFrequency((float) liquidRegionSize);
    }

    public CacheData getOrCreateLiquidBlocksForChunk(ChunkPos chunkPos) {
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
        cache.put(chunkPos, cacheData);
        return cacheData;
    }

//    public CacheData getLiquidBlocksForChunk(ChunkPos chunkPos) {
//        return cache.get(chunkPos);
//    }

    public int getLiquidAltitude() {
        return this.settings.liquidAltitude();
    }

    private BlockState getLiquidBlockAtPos(Random rand, ColPos colPos) {
        if (this.liquidRegionThreshold <= -1f) { // Don't bother calculating noise if water regions are disabled
            return this.settings.lavaBlockState();
        }

        float liquidRegionNoise = this.liquidRegionSampler.GetNoise(colPos.getX(), colPos.getZ());
        float barrierZoneWidth = rand.nextFloat() * SMOOTH_DELTA + SMOOTH_RANGE;

        if (liquidRegionNoise < liquidRegionThreshold - barrierZoneWidth) {
            return settings.waterBlockState();
        } else if (liquidRegionNoise < liquidRegionThreshold + barrierZoneWidth) {
            return null; // Solid block barrier between water and lava regions
        } else {
            return settings.lavaBlockState();
        }
    }

    private record Settings(double liquidRegionSize, double waterRegionSpawnChance, int liquidAltitude,
                                       BlockState waterBlockState, BlockState lavaBlockState) {
        public static final Codec<Settings> CODEC = RecordCodecBuilder.create(
                builder -> builder.group(
                        Codec.DOUBLE.fieldOf("liquid_region_size").forGetter(Settings::liquidRegionSize),
                        Codec.DOUBLE.fieldOf("water_region_spawn_chance").forGetter(Settings::waterRegionSpawnChance),
                        Codec.INT.fieldOf("liquid_altitude").forGetter(Settings::liquidAltitude),
                        BlockState.CODEC.fieldOf("water_block_state").forGetter(Settings::waterBlockState),
                        BlockState.CODEC.fieldOf("lava_block_state").forGetter(Settings::lavaBlockState)
                ).apply(builder, Settings::new));
    }
}
