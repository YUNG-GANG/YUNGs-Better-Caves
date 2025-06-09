package com.yungnickyoung.minecraft.bettercaves.worldgen.controller;

import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseUtils;
import com.yungnickyoung.minecraft.yungsapi.math.ColPos;
import com.yungnickyoung.minecraft.yungsapi.noise.FastNoise;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class LiquidRegionController {
    private final ServerLevel serverLevel;
    private final FastNoise liquidRegionSampler;
    private final Random rand;
    private final float liquidRegionThreshold;

    // Constants
    private static final float SMOOTH_RANGE = .04f;
    private static final float SMOOTH_DELTA = .01f;

    public LiquidRegionController(ServerLevel serverLevel) {
        this.serverLevel = serverLevel;
        this.rand = new Random();

        liquidRegionThreshold = NoiseUtils.simplexNoiseOffsetByPercent(-1f,
                (float) (BetterCavesCommon.CONFIG.undergroundGen.waterRegions.waterRegionSpawnChance / 100));

        // Liquid region sampler
//        float waterRegionSize = BetterCavesCommon.CONFIG.undergroundGen.caverns.cavernRegionSize.get().equals("ExtraLarge")
//                ? .001f
//                : .004f;
        float waterRegionSize = 0.004f;
        liquidRegionSampler = new FastNoise();
        liquidRegionSampler.SetSeed((int) this.serverLevel.getSeed() + 444);
        liquidRegionSampler.SetFrequency(waterRegionSize);
    }

    public BlockState[][] getLiquidBlocksForChunk(ChunkPos chunkPos) {
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

        // If water region threshold check is passed, change liquid block to water
        float randOffset = rand.nextFloat() * SMOOTH_DELTA + SMOOTH_RANGE;
        if (liquidRegionNoise < liquidRegionThreshold - randOffset) {
            return BetterCavesCommon.CONFIG.undergroundGen.misc.waterBlock;
        } else if (liquidRegionNoise < liquidRegionThreshold + randOffset) {
            return null;
        } else {
            return BetterCavesCommon.CONFIG.undergroundGen.misc.lavaBlock;
        }
    }
}
