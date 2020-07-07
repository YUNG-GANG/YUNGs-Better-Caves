package com.yungnickyoung.minecraft.bettercaves.world.feature;

import com.mojang.serialization.Codec;
import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.world.carver.BetterCavesCarver;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.StructureManager;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Feature to wrap the Better Caves carver so that we have access to the world instance.
 * This allows us to do things like check the dimension (enabling per-dimension configurability),
 * retrieve the seed, and fallback to pre-existing carvers for non-whitelisted dimensions.
 */
public class CarverFeature extends Feature<NoFeatureConfig> {

    public CarverFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @ParametersAreNonnullByDefault
    @Override
    public boolean func_230362_a_(ISeedReader world, StructureManager p_230362_2_, ChunkGenerator generator, Random random, BlockPos pos, NoFeatureConfig config) {
//        String dimensionName = null;
//        try {
//            dimensionName = Objects.requireNonNull(DimensionType.getKey(world.getDimension().getType())).toString();
//        } catch (NullPointerException ignored) {
//            BetterCaves.LOGGER.error("ERROR: Unable to get dimension name! Using default cave gen...");
//        }
//
//        IChunk chunk = world.getChunk(pos);
//        ChunkPos chunkPos = chunk.getPos();
//        int xChunkPos = chunkPos.x;
//        int zChunkPos = chunkPos.z;
//        Biome biome = chunk.getBiomes().getNoiseBiome(pos.getX(), 1, pos.getZ());
//
//        BiomeManager biomeManager = world.getBiomeManager().copyWithProvider(generator.getBiomeProvider());
//
//        // If dimension isn't whitelisted, use normal carvers instead of BC carver
//        if (dimensionName == null || !isDimensionWhitelisted(dimensionName)) {
//            SharedSeedRandom sharedSeedRandom = new SharedSeedRandom();
//            BitSet airBitset = ((ChunkPrimer) chunk).func_230345_b_(GenerationStage.Carving.AIR);
//            BitSet liquidBitset = ((ChunkPrimer) chunk).func_230345_b_(GenerationStage.Carving.LIQUID);
//            List<ConfiguredCarver<?>> defaultAirCarvers = BetterCaves.defaultBiomeAirCarvers.get(biome.getClass());
//            List<ConfiguredCarver<?>> defaultLiquidCarvers = BetterCaves.defaultBiomeLiquidCarvers.get(biome.getClass());
//
//            for(int currChunkX = xChunkPos - 8; currChunkX <= xChunkPos + 8; ++currChunkX) {
//                for (int currChunkZ = zChunkPos - 8; currChunkZ <= zChunkPos + 8; ++currChunkZ) {
//                    // Air carvers
//                    for (int i = 0; i < defaultAirCarvers.size(); i++) {
//                        sharedSeedRandom.setLargeFeatureSeed(world.getSeed() + (long)i, currChunkX, currChunkZ);
//                        ConfiguredCarver<?> carver = defaultAirCarvers.get(i);
//                        if (carver.shouldCarve(sharedSeedRandom, currChunkX, currChunkZ)) {
//                            carver.func_227207_a_(chunk, biomeManager::getBiome, sharedSeedRandom, world.getSeaLevel(), currChunkX, currChunkZ, xChunkPos, zChunkPos, airBitset);
//                        }
//                    }
//                    // Liquid carvers
//                    for (int i = 0; i < defaultLiquidCarvers.size(); i++) {
//                        sharedSeedRandom.setLargeFeatureSeed(world.getSeed() + (long)i, currChunkX, currChunkZ);
//                        ConfiguredCarver<?> carver = defaultLiquidCarvers.get(i);
//                        if (carver.shouldCarve(sharedSeedRandom, currChunkX, currChunkZ)) {
//                            carver.func_227207_a_(chunk, biomeManager::getBiome, sharedSeedRandom, world.getSeaLevel(), currChunkX, currChunkZ, xChunkPos, zChunkPos, liquidBitset);
//                        }
//                    }
//                }
//            }
//            return true;
//        }
//
//        // Check if a carver hasn't been created for this dimension, or if
//        // the seeds don't match (player probably changed worlds)
//        if (BetterCaves.activeCarversMap.get(dimensionName) == null || BetterCaves.activeCarversMap.get(dimensionName).getSeed() != world.getSeed()) {
//            BetterCavesCarver newCarver = new BetterCavesCarver();
//            BetterCaves.activeCarversMap.put(dimensionName, newCarver);
//            BetterCaves.LOGGER.info(String.format("CREATING AND INIT'ING CARVER W DIMENSION %s...", dimensionName));
//            newCarver.initialize(world);
//        }
//
//        BetterCavesCarver carver = BetterCaves.activeCarversMap.get(dimensionName);
//        carver.setWorld(world);
//        carver.carve(chunk, xChunkPos, zChunkPos);

        return true;
    }

    /**
     * @return true if the provided dimension ID is whitelisted in the config
     */
    private boolean isDimensionWhitelisted(String dimensionName) {
        return Configuration.enableGlobalWhitelist.get() || BetterCaves.whitelistedDimensions.contains(dimensionName);
    }
}
