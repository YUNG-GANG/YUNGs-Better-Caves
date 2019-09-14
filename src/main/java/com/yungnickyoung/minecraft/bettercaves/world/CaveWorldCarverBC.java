package com.yungnickyoung.minecraft.bettercaves.world;

import com.mojang.datafixers.Dynamic;
import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;

public class CaveWorldCarverBC extends WorldCarver<ProbabilityConfig> {
    private static final Logger LOGGER = LogManager.getLogger(Settings.MOD_ID + "CaveWorldCarverBC");

    public CaveWorldCarverBC(Function<Dynamic<?>, ? extends ProbabilityConfig> p_i49929_1_, int p_i49929_2_) {
        super(p_i49929_1_, p_i49929_2_);
    }

    @Override
    public boolean carve(IChunk chunkIn, Random rand, int seaLevel, int chunkX, int chunkZ, int p_212867_6_, int p_212867_7_, BitSet carvingMask, ProbabilityConfig config) {
        LOGGER.info("carve");
        return false;
    }

    @Override
    public boolean shouldCarve(Random rand, int chunkX, int chunkZ, ProbabilityConfig config) {
        LOGGER.info("should carve");
        return false;
    }

    @Override
    protected boolean func_222708_a(double p_222708_1_, double p_222708_3_, double p_222708_5_, int p_222708_7_) {
        return false;
    }
}
