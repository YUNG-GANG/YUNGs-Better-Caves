package com.yungnickyoung.minecraft.bettercaves.world.carver;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.world.carver.controller.MasterController;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.DefaultCarverConfig;

import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Delegates carving to existing BetterCavesCarver instances for each dimension,
 * creating new instances when needed.
 */
public class BetterCavesCarver extends Carver<DefaultCarverConfig> {
    public BetterCavesCarver() {
        super(DefaultCarverConfig.CODEC, 256);
    }

    @Override
    public boolean carve(Chunk chunkIn, Function<BlockPos, Biome> biomePos, Random rand, int seaLevel, int chunkXOffset, int chunkZOffset, int chunkX, int chunkZ, BitSet carvingMask, DefaultCarverConfig config) {
        // A null CarvingContext indicates we're in not the 'air carving' stage so exit early.
        CarvingContext context = CarvingContext.peek();
        if (context == null) {
            return false;
        }

        ServerWorld world = context.getWorld();
        if (world == null) {
            BetterCaves.LOGGER.error("ERROR: Unable to retrieve world from CarvingContext!");
            return false;
        }

        // Despite being passed the carving mask as a parameter, we explicitly retrieve the masks for both stages of carving.
        // This allows us to update the proper mask depending on the biome (flooded caves, which spawn in ocean biomes,
        // use the liquid carving mask) in a single carver.
        // This is not intuitive for vanilla-style carvers, but works well for mine since Better Caves carve in a single pass-through
        // using noise with global world coordinates.
        BitSet airCarvingMask = context.getMask(GenerationStep.Carver.AIR);
        BitSet liquidCarvingMask = context.getMask(GenerationStep.Carver.LIQUID);

        // Attempt to get dimension name, e.g. "minecraft:the_nether"
        String dimensionName = null;
        try {
            dimensionName = Objects.requireNonNull(world.toServerWorld().getRegistryKey().getValue()).toString();
        } catch (NullPointerException e) {
            BetterCaves.LOGGER.error("ERROR: Unable to get dimension name! Using default cave gen...");
        }

        // If dimension isn't whitelisted, use default carvers instead of BC carver
        if (dimensionName == null || !isDimensionWhitelisted(dimensionName)) {
            return useDefaultCarvers(world, chunkIn, biomePos, rand, seaLevel, chunkXOffset, chunkZOffset, chunkX, chunkZ, airCarvingMask, liquidCarvingMask);
        }

        // Pop this thread's carving context, as it is no longer needed.
        // It's important we do this after the dimension whitelist check, as default carvers should use the ordinary
        // vanilla algorithm, where a single chunk is examined multiple times.
        CarvingContext.pop();

        // Check if a carver hasn't been created for this dimension, or if
        // the seeds don't match (player probably changed worlds)
        if (BetterCaves.activeCarversMap.get(dimensionName) == null || BetterCaves.activeCarversMap.get(dimensionName).getSeed() != world.getSeed()) {
            // Replace map entry with fresh carver
            MasterController newCarver = new MasterController();
            BetterCaves.activeCarversMap.put(dimensionName, newCarver);
            BetterCaves.LOGGER.info(String.format("CREATING AND INIT'ING CARVER W DIMENSION %s...", dimensionName));
            newCarver.initialize(world);
        }

        // Retrieve the master controller for this dimension
        MasterController masterController = BetterCaves.activeCarversMap.get(dimensionName);
        masterController.setWorld(world); // Ensure controller's world is up to date

        return masterController.carveRegion(chunkIn, biomePos, chunkIn.getPos().x, chunkIn.getPos().z, airCarvingMask, liquidCarvingMask);
    }

    @Override
    public boolean shouldCarve(Random rand, int chunkX, int chunkZ, DefaultCarverConfig config) {
        return true;
    }

    @Override
    protected boolean isPositionExcluded(double scaledRelativeX, double scaledRelativeY, double scaledRelativeZ, int y) {
        return true;
    }

    private boolean useDefaultCarvers(ServerWorld world, Chunk chunkIn, Function<BlockPos, Biome> biomePos, Random rand, int seaLevel, int chunkXOffset, int chunkZOffset, int chunkX, int chunkZ, BitSet airCarvingMask, BitSet liquidCarvingMask) {
        // Grab the carvers we saved earlier for this biome
        String biomeName = biomePos.apply(new BlockPos(chunkIn.getPos().x << 4, 0, chunkIn.getPos().z << 4)).toString();
        List<Supplier<ConfiguredCarver<?>>> defaultAirCarvers = BetterCaves.defaultBiomeAirCarvers.get(biomeName);
        List<Supplier<ConfiguredCarver<?>>> defaultLiquidCarvers = BetterCaves.defaultBiomeLiquidCarvers.get(biomeName);

        // Verify lists are non-null to avoid NPE-related crashes.
        if (defaultAirCarvers == null || defaultLiquidCarvers == null) {
            return false;
        }

        // Air carvers
        for (Supplier<ConfiguredCarver<?>> carverSupplier : defaultAirCarvers) {
            ConfiguredCarver<?> carver = carverSupplier.get();
            if (carver.shouldCarve(rand, chunkX, chunkZ)) {
                carver.carve(chunkIn, biomePos, rand, seaLevel, chunkXOffset, chunkZOffset, chunkX, chunkZ, airCarvingMask);
            }
        }

        // Liquid carvers
        for (Supplier<ConfiguredCarver<?>> carverSupplier : defaultLiquidCarvers) {
            ConfiguredCarver<?> carver = carverSupplier.get();
            if (carver.shouldCarve(rand, chunkX, chunkZ)) {
                carver.carve(chunkIn, biomePos, rand, seaLevel, chunkXOffset, chunkZOffset, chunkX, chunkZ, liquidCarvingMask);
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