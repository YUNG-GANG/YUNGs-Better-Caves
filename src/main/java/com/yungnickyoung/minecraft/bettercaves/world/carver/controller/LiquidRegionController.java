package com.yungnickyoung.minecraft.bettercaves.world.carver.controller;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.util.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseUtils;
import com.yungnickyoung.minecraft.yungsapi.math.ColPos;
import com.yungnickyoung.minecraft.yungsapi.noise.FastNoise;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ISeedReader;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.Random;

public class LiquidRegionController {
    private FastNoise liquidRegionSampler;
    private ISeedReader world;
    private String dimensionName;
    private Random rand;

    // Vars determined from config
    private BlockState lavaBlock;
    private BlockState waterBlock;
    private float liquidRegionThreshold;

    // Constants
    private static final float SMOOTH_RANGE = .04f;
    private static final float SMOOTH_DELTA = .01f;

    public LiquidRegionController(ISeedReader worldIn, ConfigHolder config) {
        this.world = worldIn;
        this.dimensionName = Objects.requireNonNull(world.getWorld().getDimensionKey().getLocation()).toString();
        this.rand = new Random();

        // Vars from config
        lavaBlock = getLavaBlockFromString(config.lavaBlock.get());
        waterBlock = getWaterBlockFromString(config.waterBlock.get());
        liquidRegionThreshold = NoiseUtils.simplexNoiseOffsetByPercent(-1f, config.waterRegionSpawnChance.get().floatValue() / 100f);

        // Liquid region sampler
        float waterRegionSize = config.cavernRegionSize.get().equals("ExtraLarge") ? .001f : .004f;
        liquidRegionSampler = new FastNoise();
        liquidRegionSampler.SetSeed((int) world.getSeed() + 444);
        liquidRegionSampler.SetFrequency(waterRegionSize);
    }

    public BlockState[][] getLiquidBlocksForChunk(int chunkX, int chunkZ) {
        rand.setSeed(world.getSeed() ^ chunkX ^ chunkZ);
        BlockState[][] blocks = new BlockState[16][16];
        ColPos.Mutable pos = new ColPos.Mutable();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                pos.setPos(chunkX * 16 + x, chunkZ * 16 + z);
                blocks[x][z] = getLiquidBlockAtPos(rand, pos);
            }
        }
        return blocks;
    }

    private BlockState getLiquidBlockAtPos(Random rand, ColPos colPos) {
        BlockState liquidBlock = lavaBlock;
        if (liquidRegionThreshold > -1f) { // Don't bother calculating noise if water regions are disabled
            float liquidRegionNoise = liquidRegionSampler.GetNoise(colPos.getX(), colPos.getZ());

            // If water region threshold check is passed, change liquid block to water
            float randOffset = rand.nextFloat() * SMOOTH_DELTA + SMOOTH_RANGE;
            if (liquidRegionNoise < liquidRegionThreshold - randOffset)
                liquidBlock = waterBlock;
            else if (liquidRegionNoise < liquidRegionThreshold + randOffset)
                liquidBlock = null;
        }
        return liquidBlock;
    }

    private BlockState getLavaBlockFromString(String lavaString) {
        BlockState lavaBlock;
        try {
            lavaBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(lavaString)).getDefaultState();
            BetterCaves.LOGGER.info(String.format("Using block '%s' as lava in cave generation for dimension %s", lavaString, dimensionName));
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
            waterBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(waterString)).getDefaultState();
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

    public void setWorld(ISeedReader worldIn) {
        this.world = worldIn;
    }
}