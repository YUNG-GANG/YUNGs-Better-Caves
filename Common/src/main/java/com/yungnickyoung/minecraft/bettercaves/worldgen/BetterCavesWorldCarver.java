package com.yungnickyoung.minecraft.bettercaves.worldgen;

import com.mojang.serialization.Codec;
import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.duck.IMasterControllerProvider;
import com.yungnickyoung.minecraft.bettercaves.worldgen.context.CavegenContext;
import com.yungnickyoung.minecraft.bettercaves.worldgen.controller.MasterController;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

public class BetterCavesWorldCarver extends WorldCarver<BetterCavesWorldCarverConfig> {
    public BetterCavesWorldCarver(Codec<BetterCavesWorldCarverConfig> codec) {
        super(codec);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean carve(CarvingContext carvingContext, BetterCavesWorldCarverConfig config, ChunkAccess centerChunk,
                         Function<BlockPos, Holder<Biome>> biomeProvider, RandomSource random, Aquifer aquifer,
                         ChunkPos carvingChunkPos, CarvingMask carvingMask) {
        // A null CarvingContext indicates we're in not the 'air carving' stage so exit early.
        CavegenContext context = CavegenContext.peek();
        if (context == null) {
            return false;
        }

        ServerLevel serverLevel = context.getServerLevel();
        if (serverLevel == null) {
            BetterCavesCommon.LOGGER.error("Unable to retrieve ServerLevel from CarvingContext!");
            return false;
        }

        CavegenContext.pop();

        IMasterControllerProvider provider = (IMasterControllerProvider) serverLevel;
        MasterController masterController = provider.getMasterController();

        // Check if a carver hasn't been created for this dimension
        if (masterController == null) {
            BetterCavesCommon.LOGGER.info("CREATING AND INIT'ING MASTER CONTROLLER...");
            masterController = new MasterController(serverLevel, config);
            provider.setMasterController(masterController);
        }

        return masterController.carve(centerChunk, biomeProvider, carvingMask, aquifer);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isStartChunk(BetterCavesWorldCarverConfig config, RandomSource random) {
        return true;
    }
}
