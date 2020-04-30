package com.yungnickyoung.minecraft.bettercaves.world;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.util.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseUtils;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;

public class WaterRegionController {
    private FastNoise waterRegionController;
    private long seed;
    private int dimensionId;
    private String dimensionName;
    private Random rand;

    // Vars from config
    private BlockState lavaBlock;
    private BlockState waterBlock;
    private float waterRegionThreshold;

    // Constants
    private static final float SMOOTH_RANGE = .04f;
    private static final float SMOOTH_DELTA = .01f;

    public WaterRegionController(long seed, DimensionType dimensionType, ConfigHolder config) {
        this.seed = seed;
        this.dimensionId = dimensionType.getId();
        this.dimensionName = DimensionType.getKey(dimensionType).toString();
        rand = new Random();

        // Vars from config
        lavaBlock = getLavaBlockFromString(config.lavaBlock.get());
        waterBlock = getWaterBlockFromString(config.waterBlock.get());
        waterRegionThreshold = NoiseUtils.simplexNoiseOffsetByPercent(-1f, config.waterRegionSpawnChance.get().floatValue() / 100f);

        // Water region controller
        float waterRegionSize = config.cavernRegionSize.get().equals("ExtraLarge") ? .001f : .004f;
        waterRegionController = new FastNoise();
        waterRegionController.SetSeed((int)seed + 444);
        waterRegionController.SetFrequency(waterRegionSize);
    }

    public BlockState[][] getLiquidBlocksForChunk(int chunkX, int chunkZ) {
        rand.setSeed(seed ^ chunkX ^ chunkZ);
        BlockState[][] blocks = new BlockState[16][16];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realX = chunkX * 16 + x;
                int realZ = chunkZ * 16 + z;
                BlockPos pos = new BlockPos(realX, 1, realZ);
                blocks[x][z] = getLiquidBlockAtPos(rand, pos);
            }
        }
        return blocks;
    }

    private BlockState getLiquidBlockAtPos(Random rand, BlockPos blockPos) {
        BlockState liquidBlock = lavaBlock;
        if (waterRegionThreshold > -1f) { // Don't bother calculating noise if water regions are disabled
            float waterRegionNoise = waterRegionController.GetNoise(blockPos.getX(), blockPos.getZ());

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
            lavaBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(lavaString)).getDefaultState();
            BetterCaves.LOGGER.info("Using block '" + lavaString + "' as lava in cave generation for dimension " +
                BetterCavesUtil.dimensionAsString(dimensionId, dimensionName) + " ...");
        } catch (Exception e) {
            BetterCaves.LOGGER.warn("Unable to use block '" + lavaString + "': " + e);
            BetterCaves.LOGGER.warn("Using vanilla lava instead...");
            lavaBlock = Blocks.LAVA.getDefaultState();
        }

        // Default to vanilla lava if lavaBlock is null or contains air (the default registry block) when air was not specified
        if (lavaBlock == null || (lavaBlock == Blocks.AIR.getDefaultState() && !lavaString.equals("minecraft:air"))) {
            BetterCaves.LOGGER.warn("Unable to use block '" + lavaString + "': null block returned.\n Using vanilla lava instead...");
            lavaBlock = Blocks.LAVA.getDefaultState();
        }
        return lavaBlock;
    }

    private BlockState getWaterBlockFromString(String waterString) {
        BlockState waterBlock;
        try {
            waterBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(waterString)).getDefaultState();
            BetterCaves.LOGGER.info("Using block '" + waterString + "' as water in cave generation for dimension " +
                BetterCavesUtil.dimensionAsString(dimensionId, dimensionName) + " ...");
        } catch (Exception e) {
            BetterCaves.LOGGER.warn("Unable to use block '" + waterString + "': " + e);
            BetterCaves.LOGGER.warn("Using vanilla water instead...");
            waterBlock = Blocks.WATER.getDefaultState();
        }

        // Default to vanilla water if waterBlock is null or contains air (the default registry block) when air was not specified
        if (waterBlock == null || (waterBlock == Blocks.AIR.getDefaultState() && !waterString.equals("minecraft:air"))) {
            BetterCaves.LOGGER.warn("Unable to use block '" + waterString + "': null block returned.\n Using vanilla water instead...");
            waterBlock = Blocks.WATER.getDefaultState();
        }

        return waterBlock;
    }
}