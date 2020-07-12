package com.yungnickyoung.minecraft.bettercaves.util;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.function.Predicate;

/**
 * Utility functions for Better Caves.
 * This class may not be instantiated - all members are {@code public} and {@code static},
 * and as such may be accessed freely.
 */
public class BetterCavesUtils {
    private BetterCavesUtils() {} // Private constructor prevents instantiation

    /**
     * Returns the y-coordinate of the surface block for a given local block coordinate for a given chunk.
     * Note that water blocks also count as the surface.
     * @param primer primer for chunk
     * @param localX The block's chunk-local x-coordinate
     * @param localZ The block's chunk-local z-coordinate
     * @return The y-coordinate of the surface block
     */
    public static int getSurfaceAltitudeForColumn(ChunkPrimer primer, int localX, int localZ) {
        return searchSurfaceAltitudeInRangeForColumn(primer, localX, localZ, 255, 0);
    }

    /**
     * Searches for the y-coordinate of the surface block for a given local block coordinate for a given chunk in a
     * specific range of y-coordinates.
     * Note that water blocks also count as the surface.
     * @param primer primer for chunk
     * @param localX The block's chunk-local x-coordinate
     * @param localZ The block's chunk-local z-coordinate
     * @param topY The top y-coordinate to stop searching at
     * @param bottomY The bottom y-coordinate to start searching at
     * @return The y-coordinate of the surface block
     */
    public static int searchSurfaceAltitudeInRangeForColumn(ChunkPrimer primer, int localX, int localZ, int topY, int bottomY) {
        // Edge case: blocks go all the way up to build height
        if (topY == 255
                && primer.getBlockState(localX, 255, localZ) != Blocks.AIR.getDefaultState()
                && primer.getBlockState(localX, 255, localZ).getMaterial() != Material.WATER)
            return 255;

        for (int y = bottomY; y <= topY; y++) {
            IBlockState blockState = primer.getBlockState(localX, y, localZ);
            if (
                    blockState == Blocks.AIR.getDefaultState()
                    || blockState.getMaterial() == Material.WATER
            )
                return y;
        }

        return 1; // Surface somehow not found
    }

    public static String dimensionAsString(int dimensionID, String dimensionName) {
        return String.format("%d (%s)", dimensionID, dimensionName);
    }

    public static int getLocal(int coordinate) {
        return coordinate & 0xF; // This is same as modulo 16, but quicker
    }

    /**
     * @return true if the provided dimension ID is whitelisted in the config
     */
    public static boolean isDimensionWhitelisted(int dimID) {
        // Ignore the dimension ID list if global whitelisting is enabled
        if (Configuration.enableGlobalWhitelist)
            return true;

        for (int dim : Configuration.whitelistedDimensionIDs)
            if (dimID == dim) return true;

        return false;
    }


    /**
     * Returns a linear measure (from 0 to 1, inclusive) indicating how far away a target biome is.
     * The target biome is searched for in a circle with a given radius centered around the starting block.
     * The circle is searched radially outward from the starting position, so as not to perform unnecessary computation.
     *
     * This function is primarily used to search for nearby ocean/non-ocean biomes to close off flooded caves
     * from non-flooded caves, preventing weird water walls.
     *
     * @param world World
     * @param pos Center position to search around
     * @param radius Radius of search circle
     * @param isTargetBiome Function to use when testing if a given block's biome is the biome we are lookin for
     */
    public static float biomeDistanceFactor(World world, BlockPos pos, int radius, Predicate<Biome> isTargetBiome) {
        BlockPos.MutableBlockPos checkpos = new BlockPos.MutableBlockPos();
        for (int i = 1; i <= radius; i++) {
            for (int j = 0; j <= i; j++) {
                for (EnumFacing direction : EnumFacing.Plane.HORIZONTAL) {
                    checkpos.setPos(pos).move(direction, i).move(direction.rotateY(), j);
                    if (isTargetBiome.test(world.getBiome(checkpos))) {
                        return (float)(i + j) / (2 * radius);
                    }
                    if (j != 0 && i != j) {
                        checkpos.setPos(pos).move(direction, i).move(direction.rotateYCCW(), j);
                        if (isTargetBiome.test(world.getBiome(checkpos))) {
                            return (float)(i + j) / (2 * radius);
                        }
                    }
                }
            }
        }

        return 1;
    }
}
