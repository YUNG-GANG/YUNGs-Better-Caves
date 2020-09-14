package com.yungnickyoung.minecraft.bettercaves.world.feature;

import com.mojang.serialization.Codec;
import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.world.carver.BetterCavesCarver;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

import javax.annotation.ParametersAreNonnullByDefault;
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
public class CarverFeature extends Feature<DefaultFeatureConfig> {

    public CarverFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }


    @ParametersAreNonnullByDefault
    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator generator, Random random, BlockPos pos, DefaultFeatureConfig config) {
        ChunkRegion worldGenRegion = (ChunkRegion) world;

        // Attempt to get dimension name, e.g. "minecraft:the_nether"
        String dimensionName = null;
        try {
            dimensionName = Objects.requireNonNull(worldGenRegion.toServerWorld().getRegistryKey().getValue()).toString();
        } catch (NullPointerException e) {
            BetterCaves.LOGGER.error("ERROR: Unable to get dimension name! Using default cave gen...");
        }

        // Extract chunk info from position
        Chunk chunk = world.getChunk(pos);
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

    private boolean useOtherCarver(StructureWorldAccess world, ChunkGenerator generator, BlockPos pos) {
        // New shared seed random for large features
        ChunkRandom sharedSeedRandom = new ChunkRandom();

        // Extract chunk info from position
        Chunk chunk = world.getChunk(pos);
        ChunkPos chunkPos = chunk.getPos();
        int xChunkPos = chunkPos.x;
        int zChunkPos = chunkPos.z;

        // Carving masks
        BitSet airCarvingMask = ((ProtoChunk) chunk).getOrCreateCarvingMask(GenerationStep.Carver.AIR);
        BitSet liquidCarvingMask = ((ProtoChunk) chunk).getOrCreateCarvingMask(GenerationStep.Carver.LIQUID);

        // Get biome info
        Biome biome = chunk.getBiomeArray().getBiomeForNoiseGen(pos.getX(), 1, pos.getZ());
        BiomeAccess biomeManager = world.getBiomeAccess().withSource(generator.getBiomeSource());

        // Grab the carvers we saved earlier for this biome
        List<Supplier<ConfiguredCarver<?>>> defaultAirCarvers = BetterCaves.defaultBiomeAirCarvers.get(biome.toString());
        List<Supplier<ConfiguredCarver<?>>> defaultLiquidCarvers = BetterCaves.defaultBiomeLiquidCarvers.get(biome.toString());

        // Verify lists are non-null to avoid NPE-related crashes.
        if (defaultAirCarvers == null || defaultLiquidCarvers == null) {
            return false;
        }

        // Simulate ordinary vanilla (or other cave mod, if installed) carving
        for(int currChunkX = xChunkPos - 8; currChunkX <= xChunkPos + 8; ++currChunkX) {
            for (int currChunkZ = zChunkPos - 8; currChunkZ <= zChunkPos + 8; ++currChunkZ) {
                // Air carvers
                for (int i = 0; i < defaultAirCarvers.size(); i++) {
                    sharedSeedRandom.setCarverSeed(world.getSeed() + (long)i, currChunkX, currChunkZ);
                    ConfiguredCarver<?> carver = defaultAirCarvers.get(i).get();
                    if (carver.shouldCarve(sharedSeedRandom, currChunkX, currChunkZ)) {
                        carver.carve(chunk, biomeManager::getBiome, sharedSeedRandom, world.getSeaLevel(), currChunkX, currChunkZ, xChunkPos, zChunkPos, airCarvingMask);
                    }
                }
                // Liquid carvers
                for (int i = 0; i < defaultLiquidCarvers.size(); i++) {
                    sharedSeedRandom.setCarverSeed(world.getSeed() + (long)i, currChunkX, currChunkZ);
                    ConfiguredCarver<?> carver = defaultLiquidCarvers.get(i).get();
                    if (carver.shouldCarve(sharedSeedRandom, currChunkX, currChunkZ)) {
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
