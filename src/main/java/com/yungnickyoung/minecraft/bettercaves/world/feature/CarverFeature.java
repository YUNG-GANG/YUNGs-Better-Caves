package com.yungnickyoung.minecraft.bettercaves.world.feature;

import com.mojang.serialization.Codec;
import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.world.carver.BetterCavesCarver;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Feature to wrap the Better Caves carver so that we have access to the world instance.
 * This allows us to do things like check the dimension (enabling per-dimension configurability),
 * retrieve the seed, and fallback to pre-existing carvers for non-whitelisted dimensions.
 */
public class CarverFeature extends Feature<NoneFeatureConfiguration> {

    public CarverFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel world, ChunkGenerator generator, Random random, BlockPos pos, NoneFeatureConfiguration config) {
        // Attempt to get dimension name, e.g. "minecraft:the_nether"
        String dimensionName = null;
        try {
            dimensionName = Objects.requireNonNull(world.registryAccess().dimensionTypes().getKey(world.dimensionType())).toString();
        } catch (NullPointerException e) {
            BetterCaves.LOGGER.error("ERROR: Unable to get dimension name! Using default cave gen...");
        }

        // Extract chunk info from position
        ChunkAccess chunk = world.getChunk(pos);
        ChunkPos chunkPos = chunk.getPos();
        int xChunkPos = chunkPos.x;
        int zChunkPos = chunkPos.z;

        // If dimension isn't whitelisted, use other carvers instead of BC carver
        if (dimensionName == null || !isDimensionWhitelisted(dimensionName)) {
            return useOtherCarver(world, generator, pos);
        }

        // Check if a carver hasn't been created for this dimension, or if
        // the seeds don't match (player probably changed worlds)
        if (BetterCaves.activeCarversMap.get(dimensionName) == null || BetterCaves.activeCarversMap.get(dimensionName).getSeed() != world.getSeed()) {
            // Replace map entry with fresh carver
            BetterCavesCarver newCarver = new BetterCavesCarver();
            BetterCaves.activeCarversMap.put(dimensionName, newCarver);
            BetterCaves.LOGGER.info(String.format("CREATING AND INIT'ING CARVER W DIMENSION %s...", dimensionName));
            newCarver.initialize(world);
        }

        BetterCavesCarver carver = BetterCaves.activeCarversMap.get(dimensionName);
        carver.setWorld(world); // Ensure carver's world is up to date
        carver.carve(chunk, xChunkPos, zChunkPos); // Let's carve, baby

        return true;
    }

    private boolean useOtherCarver(WorldGenLevel world, ChunkGenerator generator, BlockPos pos) {
        // New shared seed random for large features
        WorldgenRandom sharedSeedRandom = new WorldgenRandom();

        // Extract chunk info from position
        ChunkAccess chunk = world.getChunk(pos);
        ChunkPos chunkPos = chunk.getPos();
        int xChunkPos = chunkPos.x;
        int zChunkPos = chunkPos.z;

        // Carving masks
        BitSet airCarvingMask = ((ProtoChunk) chunk).getOrCreateCarvingMask(GenerationStep.Carving.AIR);
        BitSet liquidCarvingMask = ((ProtoChunk) chunk).getOrCreateCarvingMask(GenerationStep.Carving.LIQUID);

        // Get biome info
        Biome biome = chunk.getBiomes().getNoiseBiome(pos.getX(), 1, pos.getZ());
        BiomeManager biomeManager = world.getBiomeManager().withDifferentSource(generator.getBiomeSource());

        // Grab the carvers we saved earlier for this biome
        List<Supplier<ConfiguredWorldCarver<?>>> defaultAirCarvers = BetterCaves.defaultBiomeAirCarvers.get(biome.toString());
        List<Supplier<ConfiguredWorldCarver<?>>> defaultLiquidCarvers = BetterCaves.defaultBiomeLiquidCarvers.get(biome.toString());

        // Verify lists are non-null to avoid NPE-related crashes.
        if (defaultAirCarvers == null || defaultLiquidCarvers == null) {
            return false;
        }

        // Simulate ordinary vanilla (or other cave mod, if installed) carving
        for(int currChunkX = xChunkPos - 8; currChunkX <= xChunkPos + 8; ++currChunkX) {
            for (int currChunkZ = zChunkPos - 8; currChunkZ <= zChunkPos + 8; ++currChunkZ) {
                // Air carvers
                for (int i = 0; i < defaultAirCarvers.size(); i++) {
                    sharedSeedRandom.setLargeFeatureSeed(world.getSeed() + (long)i, currChunkX, currChunkZ);
                    ConfiguredWorldCarver<?> carver = defaultAirCarvers.get(i).get();
                    if (carver.isStartChunk(sharedSeedRandom, currChunkX, currChunkZ)) {
                        carver.carve(chunk, biomeManager::getBiome, sharedSeedRandom, world.getSeaLevel(), currChunkX, currChunkZ, xChunkPos, zChunkPos, airCarvingMask);
                    }
                }
                // Liquid carvers
                for (int i = 0; i < defaultLiquidCarvers.size(); i++) {
                    sharedSeedRandom.setLargeFeatureSeed(world.getSeed() + (long)i, currChunkX, currChunkZ);
                    ConfiguredWorldCarver<?> carver = defaultLiquidCarvers.get(i).get();
                    if (carver.isStartChunk(sharedSeedRandom, currChunkX, currChunkZ)) {
                        carver.carve(chunk, biomeManager::getBiome, sharedSeedRandom, world.getSeaLevel(), currChunkX, currChunkZ, xChunkPos, zChunkPos, liquidCarvingMask);
                    }
                }
            }
        }
        return true;
    }

    /**
     * @return true if the provided dimension ID is whitelisted in the config
     */
    private boolean isDimensionWhitelisted(String dimensionName) {
        return BetterCaves.CONFIG.betterCaves.enableGlobalWhitelist || BetterCaves.whitelistedDimensions.contains(dimensionName);
    }
}
