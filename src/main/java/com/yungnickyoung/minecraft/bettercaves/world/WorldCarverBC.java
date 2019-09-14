package com.yungnickyoung.minecraft.bettercaves.world;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.ICarverConfig;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;

import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class WorldCarverBC extends WorldCarver<ProbabilityConfig> {
    public WorldCarverBC() {
        super(ProbabilityConfig::deserialize, 256);
    }

    @Override
    public boolean carve(IChunk chunkIn, Random rand, int seaLevel, int chunkX, int chunkZ, int p_212867_6_, int p_212867_7_, BitSet carvingMask, ProbabilityConfig config) {
        return false;
    }

    @Override
    public boolean shouldCarve(Random rand, int chunkX, int chunkZ, ProbabilityConfig config) {
        return false;
    }

    @Override
    protected boolean func_222708_a(double p_222708_1_, double p_222708_3_, double p_222708_5_, int p_222708_7_) {
        return false;
    }
}
