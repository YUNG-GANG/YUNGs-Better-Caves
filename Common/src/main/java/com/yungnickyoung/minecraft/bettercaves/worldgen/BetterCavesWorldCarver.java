package com.yungnickyoung.minecraft.bettercaves.worldgen;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.duck.IMasterControllerProvider;
import com.yungnickyoung.minecraft.bettercaves.worldgen.context.CavegenContext;
import com.yungnickyoung.minecraft.bettercaves.worldgen.controller.MasterController;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.CarverOutput;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

import javax.annotation.ParametersAreNonnullByDefault;
public class BetterCavesWorldCarver implements WorldCarver {
    public static final MapCodec<BetterCavesWorldCarver> CODEC = BetterCavesWorldCarverConfig.CODEC.fieldOf("config")
            .xmap(BetterCavesWorldCarver::new, carver -> carver.config);

    private final BetterCavesWorldCarverConfig config;

    public BetterCavesWorldCarver(BetterCavesWorldCarverConfig config) {
        this.config = config;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean carve(WorldGenerationContext generationContext, RandomSource random, ChunkPos chunkPos,
                         ChunkPos sourceChunkPos, CarverOutput output) {
        // A null CarvingContext indicates we're in not the 'air carving' stage so exit early.
        CavegenContext context = CavegenContext.peek();
        if (context == null) {
            return false;
        }

        ServerLevel serverLevel = context.getServerLevel();
        ChunkAccess centerChunk = context.getChunk();
        if (serverLevel == null || centerChunk == null || context.getAquifer() == null) {
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

        CarvingMask carvingMask = new CarvingMask(generationContext.getMinGenY(),
                generationContext.getMinGenY() + generationContext.getGenDepth() - 1);
        return masterController.carve(centerChunk, carvingMask, context.getAquifer());
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isStartChunk(RandomSource random) {
        return true;
    }

    @Override
    public MapCodec<BetterCavesWorldCarver> codec() {
        return CODEC;
    }
}
