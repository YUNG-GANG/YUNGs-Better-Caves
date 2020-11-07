package com.yungnickyoung.minecraft.bettercaves.world.carver.controller;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.util.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseUtils;
import com.yungnickyoung.minecraft.bettercaves.util.ColPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.Random;

public class WaterRegionController {
    private FastNoise waterRegionController;
    private WorldGenLevel world;
    private String dimensionName;
    private Random rand;

    // Vars determined from config
    private BlockState lavaBlock;
    private BlockState waterBlock;
    private float waterRegionThreshold;

    // Constants
    private static final float SMOOTH_RANGE = .04f;
    private static final float SMOOTH_DELTA = .01f;

    public WaterRegionController(WorldGenLevel worldIn, ConfigHolder config) {
        this.world = worldIn;
        dimensionName = Objects.requireNonNull(world.registryAccess().dimensionTypes().getKey(world.dimensionType())).toString();
        this.rand = new Random();

        // Vars from config
        lavaBlock = getLavaBlockFromString(config.lavaBlock.get());
        waterBlock = getWaterBlockFromString(config.waterBlock.get());
        waterRegionThreshold = NoiseUtils.simplexNoiseOffsetByPercent(-1f, config.waterRegionSpawnChance.get().floatValue() / 100f);

        // Water region controller
        float waterRegionSize = config.cavernRegionSize.get().equalsIgnoreCase("extralarge") ? .001f : .004f;
        waterRegionController = new FastNoise();
        waterRegionController.SetSeed((int) world.getSeed() + 444);
        waterRegionController.SetFrequency(waterRegionSize);
    }

    public BlockState[][] getLiquidBlocksForChunk(int chunkX, int chunkZ) {
        rand.setSeed(world.getSeed() ^ chunkX ^ chunkZ);
        BlockState[][] blocks = new BlockState[16][16];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realX = chunkX * 16 + x;
                int realZ = chunkZ * 16 + z;
                ColPos pos = new ColPos(realX, realZ);
                blocks[x][z] = getLiquidBlockAtPos(rand, pos);
            }
        }
        return blocks;
    }

    private BlockState getLiquidBlockAtPos(Random rand, ColPos colPos) {
        BlockState liquidBlock = lavaBlock;
        if (waterRegionThreshold > -1f) { // Don't bother calculating noise if water regions are disabled
            float waterRegionNoise = waterRegionController.GetNoise(colPos.getX(), colPos.getZ());

            // If water region threshold check is passed, change liquid block to water
            float randOffset = rand.nextFloat() * SMOOTH_DELTA + SMOOTH_RANGE;
            if (waterRegionNoise < waterRegionThreshold - randOffset)
                liquidBlock = waterBlock;
            else if (waterRegionNoise < waterRegionThreshold + randOffset)
                liquidBlock = null;
        }
        return liquidBlock;
    }

    private BlockState getLavaBlockFromString(String lavaString) {
        BlockState lavaBlock;
        try {
            lavaBlock = Registry.BLOCK.get(new ResourceLocation(lavaString)).defaultBlockState();
            BetterCaves.LOGGER.info(String.format("Using block '%s' as lava in cave generation for dimension %s", lavaBlock, dimensionName));
        } catch (Exception e) {
            BetterCaves.LOGGER.warn(String.format("Unable to use block '%s': %s", lavaString, e));
            BetterCaves.LOGGER.warn("Using vanilla lava instead...");
            lavaBlock = Blocks.LAVA.defaultBlockState();
        }

        // Default to vanilla lava if lavaBlock is null or contains air (the default registry block) when air was not specified
        if (lavaBlock == null || (lavaBlock == Blocks.AIR.defaultBlockState() && !lavaString.equals("minecraft:air"))) {
            BetterCaves.LOGGER.warn(String.format("Unable to use block '%s': null block returned.\n Using vanilla lava instead...", lavaString));
            lavaBlock = Blocks.LAVA.defaultBlockState();
        }
        return lavaBlock;
    }

    private BlockState getWaterBlockFromString(String waterString) {
        BlockState waterBlock;
        try {
            waterBlock = Registry.BLOCK.get(new ResourceLocation(waterString)).defaultBlockState();
            BetterCaves.LOGGER.info(String.format("Using block '%s' as water in cave generation for dimension %s", waterBlock, dimensionName));

        } catch (Exception e) {
            BetterCaves.LOGGER.warn(String.format("Unable to use block '%s': %s", waterString, e));
            BetterCaves.LOGGER.warn("Using vanilla water instead...");
            waterBlock = Blocks.WATER.defaultBlockState();
        }

        // Default to vanilla water if waterBlock is null or contains air (the default registry block) when air was not specified
        if (waterBlock == null || (waterBlock == Blocks.AIR.defaultBlockState() && !waterString.equals("minecraft:air"))) {
            BetterCaves.LOGGER.warn(String.format("Unable to use block '%s': null block returned.\n Using vanilla water instead...", waterString));
            waterBlock = Blocks.WATER.defaultBlockState();
        }

        return waterBlock;
    }

    public void setWorld(WorldGenLevel worldIn) {
        this.world = worldIn;
    }
}