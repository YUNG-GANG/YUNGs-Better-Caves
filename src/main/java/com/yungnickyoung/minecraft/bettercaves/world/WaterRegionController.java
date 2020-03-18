package com.yungnickyoung.minecraft.bettercaves.world;

import com.yungnickyoung.minecraft.bettercaves.config.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.enums.RegionSize;
import com.yungnickyoung.minecraft.bettercaves.enums.WaterRegionFrequency;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import java.util.Random;

public class WaterRegionController {
    private FastNoise waterRegionController;
    private long worldSeed;
    private int dimensionID;
    private String dimensionName;

    // Vars from config
    private IBlockState lavaBlock;
    private IBlockState waterBlock;
    private float waterRegionThreshold;
    private boolean enableWaterRegions;

    // Constants
    private final float waterRegionSmoothRange = .05f;
    private final float waterRegionSmoothDelta = .015f;

    public WaterRegionController(World world, ConfigHolder config) {
        worldSeed = world.getSeed();
        dimensionID = world.provider.getDimension();
        dimensionName = world.provider.getDimensionType().toString();

        // Vars from config
        lavaBlock = getLavaBlockFromString(config.lavaBlock.get());
        waterBlock = getWaterBlockFromString(config.waterBlock.get());
        waterRegionThreshold = calcWaterRegionThreshold(config.waterRegionFrequency.get(), config.waterRegionCustomFrequency.get());
        enableWaterRegions = config.enableWaterRegions.get();

        // Water region controller
        float waterRegionSize = config.cavernRegionSize.get() == RegionSize.ExtraLarge ? .001f : .004f;
        waterRegionController = new FastNoise();
        waterRegionController.SetSeed((int)world.getSeed() + 444);
        waterRegionController.SetFrequency(waterRegionSize);
    }

    // alternatively, remove enable as an option and allow water regions to have None as frequency
    public IBlockState[][] getLiquidBlocksForChunk(int chunkX, int chunkZ) {
        Random rand = new Random(worldSeed + chunkX + chunkZ);
        IBlockState[][] blocks = new IBlockState[16][16];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                IBlockState liquidBlock = lavaBlock;
                if (enableWaterRegions) {
                    int realX = chunkX * 16 + x;
                    int realZ = chunkZ * 16 + z;

                    float waterRegionNoise = waterRegionController.GetNoise(realX, realZ);

                    // If water region threshold check is passed, change liquid block to water
                    float randOffset = rand.nextFloat() * waterRegionSmoothDelta + waterRegionSmoothRange;
                    if (waterRegionNoise < waterRegionThreshold - randOffset)
                        liquidBlock = waterBlock;
                    else if (waterRegionNoise < waterRegionThreshold + randOffset)
                        liquidBlock = null;
                }
                blocks[x][z] = liquidBlock;
            }
        }
        return blocks;
    }

    /**
     * @return threshold value for water region spawn rate based on Config setting
     */
    private float calcWaterRegionThreshold(WaterRegionFrequency waterRegionFrequency, float waterRegionCustomFrequency) {
        switch (waterRegionFrequency) {
            case Rare:
                return -.4f;
            case Common:
                return .1f;
            case VeryCommon:
                return .3f;
            case Always:
                return 99f;
            case Custom:
                return 2f * waterRegionCustomFrequency - 1;
            default: // Normal
                return -.15f;
        }
    }

    private IBlockState getLavaBlockFromString(String lavaString) {
        IBlockState lavaBlock;
        try {
            lavaBlock = Block.getBlockFromName(lavaString).getDefaultState();
            Settings.LOGGER.info("Using block '" + lavaString + "' as lava in cave generation for dimension " +
                    BetterCavesUtil.dimensionAsString(dimensionID, dimensionName) + " ...");
        } catch (Exception e) {
            Settings.LOGGER.warn("Unable to use block '" + lavaString + "': " + e);
            Settings.LOGGER.warn("Using vanilla lava instead...");
            lavaBlock = Blocks.FLOWING_LAVA.getDefaultState();
        }
        if (lavaBlock == null) {
            Settings.LOGGER.warn("Unable to use block '" + lavaString + "': null block returned.\n Using vanilla lava instead...");
            lavaBlock = Blocks.FLOWING_LAVA.getDefaultState();
        }
        return lavaBlock;
    }

    private IBlockState getWaterBlockFromString(String waterString) {
        IBlockState waterBlock;
        try {
            waterBlock = Block.getBlockFromName(waterString).getDefaultState();
            Settings.LOGGER.info("Using block '" + waterString + "' as water in cave generation for dimension " +
                    BetterCavesUtil.dimensionAsString(dimensionID, dimensionName) + " ...");
        } catch (Exception e) {
            Settings.LOGGER.warn("Unable to use block '" + waterString + "': " + e);
            Settings.LOGGER.warn("Using vanilla water instead...");
            waterBlock = Blocks.FLOWING_WATER.getDefaultState();
        }

        if (waterBlock == null) {
            Settings.LOGGER.warn("Unable to use block '" + waterString + "': null block returned.\n Using vanilla water instead...");
            waterBlock = Blocks.FLOWING_WATER.getDefaultState();
        }

        return waterBlock;
    }
}
