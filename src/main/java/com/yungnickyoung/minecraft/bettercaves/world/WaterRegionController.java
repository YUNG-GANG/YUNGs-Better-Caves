package com.yungnickyoung.minecraft.bettercaves.world;

import com.yungnickyoung.minecraft.bettercaves.config.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.enums.RegionSize;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseUtils;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class WaterRegionController {
    private FastNoise waterRegionController;
    private long worldSeed;
    private int dimensionID;
    private String dimensionName;
    private Random rand;

    // Vars from config
    private IBlockState lavaBlock;
    private IBlockState waterBlock;
    private float waterRegionThreshold;

    // Constants
    private static final float SMOOTH_RANGE = .04f;
    private static final float SMOOTH_DELTA = .01f;

    public WaterRegionController(World world, ConfigHolder config) {
        worldSeed = world.getSeed();
        dimensionID = world.provider.getDimension();
        dimensionName = world.provider.getDimensionType().toString();
        rand = new Random();

        // Vars from config
        lavaBlock = getLavaBlockFromString(config.lavaBlock.get());
        waterBlock = getWaterBlockFromString(config.waterBlock.get());
        waterRegionThreshold = NoiseUtils.simplexNoiseOffsetByPercent(-1f, config.waterRegionSpawnChance.get() / 100f);

        // Water region controller
        float waterRegionSize = calcWaterRegionSize(config.waterRegionSize.get(), config.waterRegionCustomSize.get());
        waterRegionController = new FastNoise();
        waterRegionController.SetSeed((int)world.getSeed() + 444);
        waterRegionController.SetFrequency(waterRegionSize);
    }

    public IBlockState[][] getLiquidBlocksForChunk(int chunkX, int chunkZ) {
        rand.setSeed(worldSeed ^ chunkX ^ chunkZ);
        IBlockState[][] blocks = new IBlockState[16][16];
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

    private IBlockState getLiquidBlockAtPos(Random rand, BlockPos blockPos) {
        IBlockState liquidBlock = lavaBlock;
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

    private IBlockState getLavaBlockFromString(String lavaString) {
        IBlockState lavaBlock;
        try {
            lavaBlock = Block.getBlockFromName(lavaString).getDefaultState();
            Settings.LOGGER.info("Using block '" + lavaString + "' as lava in cave generation for dimension " +
                    BetterCavesUtils.dimensionAsString(dimensionID, dimensionName) + " ...");
        } catch (Exception e) {
            Settings.LOGGER.warn("Unable to use block '" + lavaString + "': " + e);
            Settings.LOGGER.warn("Using vanilla lava instead...");
            lavaBlock = Blocks.LAVA.getDefaultState();
        }
        if (lavaBlock == null) {
            Settings.LOGGER.warn("Unable to use block '" + lavaString + "': null block returned.\n Using vanilla lava instead...");
            lavaBlock = Blocks.LAVA.getDefaultState();
        }
        return lavaBlock;
    }

    private IBlockState getWaterBlockFromString(String waterString) {
        IBlockState waterBlock;
        try {
            waterBlock = Block.getBlockFromName(waterString).getDefaultState();
            Settings.LOGGER.info("Using block '" + waterString + "' as water in cave generation for dimension " +
                    BetterCavesUtils.dimensionAsString(dimensionID, dimensionName) + " ...");
        } catch (Exception e) {
            Settings.LOGGER.warn("Unable to use block '" + waterString + "': " + e);
            Settings.LOGGER.warn("Using vanilla water instead...");
            waterBlock = Blocks.WATER.getDefaultState();
        }

        if (waterBlock == null) {
            Settings.LOGGER.warn("Unable to use block '" + waterString + "': null block returned.\n Using vanilla water instead...");
            waterBlock = Blocks.WATER.getDefaultState();
        }

        return waterBlock;
    }

    /**
     * @return frequency value for water region controller
     */
    private float calcWaterRegionSize(RegionSize waterRegionSize, float waterRegionCustomSize) {
        switch (waterRegionSize) {
            case Small:
                return .008f;
            case Large:
                return .0028f;
            case ExtraLarge:
                return .001f;
            case Custom:
                return waterRegionCustomSize;
            default: // Medium
                return .004f;
        }
    }
}
