package com.yungnickyoung.minecraft.bettercaves.world.feature;

import com.mojang.datafixers.Dynamic;
import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.world.carver.BetterCavesCarver;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.BitSet;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

/**
 * Feature to wrap the Better Caves carver so that we have access to the world instance.
 * This allows us to do things like check the dimension (enabling per-dimension configurability),
 * retrieve the seed, and fallback to pre-existing carvers for non-whitelisted dimensions.
 */
public class CarverFeature extends Feature<NoFeatureConfig> {
    public CarverFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> chunkSettings, Random random, BlockPos position, NoFeatureConfig config) {
        int dimId = world.getDimension().getType().getId();
        Biome biome = world.getBiome(position);

        IChunk chunk = world.getChunk(position);
        ChunkPos chunkPos = chunk.getPos();
        int xChunkPos = chunkPos.x;
        int zChunkPos = chunkPos.z;

        // If dimension isn't whitelisted, use normal carvers instead of BC carver
        if (!isDimensionWhitelisted(dimId)) {
            SharedSeedRandom sharedSeedRandom = new SharedSeedRandom();
            BitSet airBitset = chunk.getCarvingMask(GenerationStage.Carving.AIR);
            BitSet liquidBitset = chunk.getCarvingMask(GenerationStage.Carving.LIQUID);
            List<ConfiguredCarver<?>> defaultAirCarvers = BetterCaves.defaultBiomeAirCarvers.get(biome.getClass());
            List<ConfiguredCarver<?>> defaultLiquidCarvers = BetterCaves.defaultBiomeLiquidCarvers.get(biome.getClass());

            for(int currChunkX = xChunkPos - 8; currChunkX <= xChunkPos + 8; ++currChunkX) {
                for (int currChunkZ = zChunkPos - 8; currChunkZ <= zChunkPos + 8; ++currChunkZ) {
                    // Air carvers
                    for (int i = 0; i < defaultAirCarvers.size(); i++) {
                        sharedSeedRandom.setLargeFeatureSeed(world.getSeed() + (long)i, currChunkX, currChunkZ);
                        ConfiguredCarver<?> carver = defaultAirCarvers.get(i);
                        if (carver.shouldCarve(sharedSeedRandom, currChunkX, currChunkZ)) {
                            carver.carve(chunk, sharedSeedRandom, world.getSeaLevel(), currChunkX, currChunkZ, xChunkPos, zChunkPos, airBitset);
                        }
                    }
                    // Liquid carvers
                    for (int i = 0; i < defaultLiquidCarvers.size(); i++) {
                        sharedSeedRandom.setLargeFeatureSeed(world.getSeed() + (long)i, currChunkX, currChunkZ);
                        ConfiguredCarver<?> carver = defaultLiquidCarvers.get(i);
                        if (carver.shouldCarve(sharedSeedRandom, currChunkX, currChunkZ)) {
                            carver.carve(chunk, sharedSeedRandom, world.getSeaLevel(), currChunkX, currChunkZ, xChunkPos, zChunkPos, liquidBitset);
                        }
                    }
                }
            }
            return true;
        }

        // Check if a carver hasn't been created for this dimension, or if
        // the seeds don't match (player probably changed worlds)
        if (BetterCaves.activeCarversMap.get(dimId) == null || BetterCaves.activeCarversMap.get(dimId).getSeed() != world.getSeed()) {
            BetterCavesCarver newCarver = new BetterCavesCarver();
            BetterCaves.activeCarversMap.put(dimId, newCarver);
            BetterCaves.LOGGER.info(String.format("CREATING AND INIT'ING CARVER W DIMENSION %s...", dimId));
            newCarver.initialize(world);
        }

        BetterCavesCarver carver = BetterCaves.activeCarversMap.get(dimId);
        carver.setWorld(world);
        carver.carve(chunk, xChunkPos, zChunkPos);

        return true;
    }

    /**
     * @return true if the provided dimension ID is whitelisted in the config
     */
    private boolean isDimensionWhitelisted(int dimID) {
        // Ignore the dimension ID list if global whitelisting is enabled
        if (Configuration.enableGlobalWhitelist.get())
            return true;

        for (int dim : Configuration.whitelistedDimensionIDs.get())
            if (dimID == dim) return true;

        return false;
    }
}
