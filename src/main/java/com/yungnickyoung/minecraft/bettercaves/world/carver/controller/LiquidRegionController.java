package com.yungnickyoung.minecraft.bettercaves.world.carver.controller;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.util.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseUtils;
import com.yungnickyoung.minecraft.bettercaves.util.ColPos;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;

import java.util.Objects;
import java.util.Random;

public class LiquidRegionController {
    private FastNoise waterRegionSampler;
    private StructureWorldAccess world;
    private String dimensionName;
    private Random rand;

    // Vars determined from config
    private BlockState lavaBlock;
    private BlockState waterBlock;
    private float waterRegionThreshold;

    // Constants
    private static final float SMOOTH_RANGE = .04f;
    private static final float SMOOTH_DELTA = .01f;

    public LiquidRegionController(StructureWorldAccess worldIn, ConfigHolder config) {
        this.world = worldIn;
        this.dimensionName = Objects.requireNonNull(world.toServerWorld().getRegistryKey().getValue()).toString();
        this.rand = new Random();

        // Vars from config
        lavaBlock = getLavaBlockFromString(config.lavaBlock.get());
        waterBlock = getWaterBlockFromString(config.waterBlock.get());
        waterRegionThreshold = NoiseUtils.simplexNoiseOffsetByPercent(-1f, config.waterRegionSpawnChance.get().floatValue() / 100f);

        // Water region sampler
        float waterRegionSize = config.cavernRegionSize.get().equalsIgnoreCase("extralarge") ? .001f : .004f;
        waterRegionSampler = new FastNoise();
        waterRegionSampler.SetSeed((int) world.getSeed() + 444);
        waterRegionSampler.SetFrequency(waterRegionSize);
    }

    public BlockState[][] getLiquidBlocksForChunk(int chunkX, int chunkZ) {
        rand.setSeed(world.getSeed() ^ chunkX ^ chunkZ);
        BlockState[][] blocks = new BlockState[16][16];
        ColPos.Mutable pos = new ColPos.Mutable();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realX = chunkX * 16 + x;
                int realZ = chunkZ * 16 + z;
                pos.set(chunkX * 16 + x, chunkZ * 16 + z);
                blocks[x][z] = getLiquidBlockAtPos(rand, pos);
            }
        }
        return blocks;
    }

    private BlockState getLiquidBlockAtPos(Random rand, ColPos colPos) {
        BlockState liquidBlock = lavaBlock;
        if (waterRegionThreshold > -1f) { // Don't bother calculating noise if water regions are disabled
            float waterRegionNoise = waterRegionSampler.GetNoise(colPos.getX(), colPos.getZ());

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
            lavaBlock = Registry.BLOCK.get(new Identifier(lavaString)).getDefaultState();
            BetterCaves.LOGGER.info(String.format("Using block '%s' as lava in cave generation for dimension %s", lavaBlock, dimensionName));
        } catch (Exception e) {
            BetterCaves.LOGGER.warn(String.format("Unable to use block '%s': %s", lavaString, e));
            BetterCaves.LOGGER.warn("Using vanilla lava instead...");
            lavaBlock = Blocks.LAVA.getDefaultState();
        }

        // Default to vanilla lava if lavaBlock is null or contains air (the default registry block) when air was not specified
        if (lavaBlock == null || (lavaBlock == Blocks.AIR.getDefaultState() && !lavaString.equals("minecraft:air"))) {
            BetterCaves.LOGGER.warn(String.format("Unable to use block '%s': null block returned.\n Using vanilla lava instead...", lavaString));
            lavaBlock = Blocks.LAVA.getDefaultState();
        }
        return lavaBlock;
    }

    private BlockState getWaterBlockFromString(String waterString) {
        BlockState waterBlock;
        try {
            waterBlock = Registry.BLOCK.get(new Identifier(waterString)).getDefaultState();
            BetterCaves.LOGGER.info(String.format("Using block '%s' as water in cave generation for dimension %s", waterBlock, dimensionName));

        } catch (Exception e) {
            BetterCaves.LOGGER.warn(String.format("Unable to use block '%s': %s", waterString, e));
            BetterCaves.LOGGER.warn("Using vanilla water instead...");
            waterBlock = Blocks.WATER.getDefaultState();
        }

        // Default to vanilla water if waterBlock is null or contains air (the default registry block) when air was not specified
        if (waterBlock == null || (waterBlock == Blocks.AIR.getDefaultState() && !waterString.equals("minecraft:air"))) {
            BetterCaves.LOGGER.warn(String.format("Unable to use block '%s': null block returned.\n Using vanilla water instead...", waterString));
            waterBlock = Blocks.WATER.getDefaultState();
        }

        return waterBlock;
    }

    public void setWorld(StructureWorldAccess worldIn) {
        this.world = worldIn;
    }
}