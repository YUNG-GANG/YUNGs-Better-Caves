package com.yungnickyoung.minecraft.bettercaves.worldgen.controller;

import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseUtils;
import com.yungnickyoung.minecraft.yungsapi.math.ColPos;
import com.yungnickyoung.minecraft.yungsapi.noise.FastNoise;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.Random;

public class LiquidRegionController {
    private final ServerLevel serverLevel;
    private final Random rand;
    private final FastNoise liquidRegionSampler;
    private final float liquidRegionThreshold;

    /*
     * Constants used to add a small amount of random offset to the noise threshold check to smooth out the transition
     * between liquid and non-liquid blocks.
     */
    private static final float SMOOTH_RANGE = .04f;
    private static final float SMOOTH_DELTA = .01f;

    public LiquidRegionController(ServerLevel serverLevel) {
        this.serverLevel = serverLevel;
        this.rand = new Random();

        liquidRegionThreshold = NoiseUtils.simplexNoiseOffsetByPercent(-1f,
                (float) (BetterCavesCommon.CONFIG.undergroundGen.waterRegions.waterRegionSpawnChance / 100));

        // Liquid region sampler
        double waterRegionSize = BetterCavesCommon.CONFIG.undergroundGen.waterRegions.waterRegionSize;
        liquidRegionSampler = new FastNoise();
        liquidRegionSampler.SetSeed((int) this.serverLevel.getSeed() + 444);
        liquidRegionSampler.SetFrequency((float) waterRegionSize);
    }

    public BlockState[][] getLiquidBlocksForChunk(ChunkAccess chunkAccess) {
        ChunkPos chunkPos = chunkAccess.getPos();
        rand.setSeed(serverLevel.getSeed() ^ chunkPos.x ^ chunkPos.z);
        BlockState[][] blocks = new BlockState[16][16];
        ColPos.Mutable pos = new ColPos.Mutable();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                pos.set(chunkPos.x * 16 + x, chunkPos.z * 16 + z);
                blocks[x][z] = getLiquidBlockAtPos(rand, pos);
            }
        }
        return blocks;
    }

    private BlockState getLiquidBlockAtPos(Random rand, ColPos colPos) {
        if (this.liquidRegionThreshold <= -1f) { // Don't bother calculating noise if water regions are disabled
            return BetterCavesCommon.CONFIG.undergroundGen.misc.lavaBlock;
        }

        float liquidRegionNoise = liquidRegionSampler.GetNoise(colPos.getX(), colPos.getZ());
        float barrierZoneWidth = rand.nextFloat() * SMOOTH_DELTA + SMOOTH_RANGE;

        if (liquidRegionNoise < liquidRegionThreshold - barrierZoneWidth) {
            return BetterCavesCommon.CONFIG.undergroundGen.misc.waterBlock;
        } else if (liquidRegionNoise < liquidRegionThreshold + barrierZoneWidth) {
            return null; // Solid block barrier between water and lava regions
        } else {
            return BetterCavesCommon.CONFIG.undergroundGen.misc.lavaBlock;
        }
    }
}
