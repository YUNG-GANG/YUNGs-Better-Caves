package com.yungnickyoung.minecraft.bettercaves.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;

/**
 * Utility functions for BetterCaves. These functions are mostly refactored versions of methods found in
 * {@code net.minecraft.world.gen.MapGenCaves}.
 * This class may not be instantiated - all members are {@code public} and {@code static},
 * and as such may be accessed freely.
 */
public class BetterCavesUtil {
    /**
     * Tests every block in a 2x2 "sub-chunk" to get the max surface altitude (y-coordinate) of the sub-chunk.
     * Note that water blocks also count as the surface.
     * @param chunkIn chunk
     * @param subX The x-coordinate of the sub-chunk. Note that this is regular chunk-local x-coordinate divided
     *             by 2. E.g. If you want the last 2 blocks on the x-axis in the chunk (blocks 14 and 15), use subX = 7.
     * @param subZ The z-coordinate of the sub-chunk. Note that this is regular chunk-local z-coordinate divided
     *             by 2. E.g. If you want the last 2 blocks on the z-axis in the chunk (blocks 14 and 15), use subZ = 7.
     * @return Max surface height of the sub-chunk
     */
    public static int getMaxSurfaceAltitudeSubChunk(IChunk chunkIn, int subX, int subZ)  {
        int maxHeight = 0;
        int[] testCoords = {0, 1}; // chunk-local x/z coordinates to test for max height

        for (int x : testCoords)
            for (int z : testCoords)
                maxHeight = Math.max(maxHeight, getSurfaceAltitudeForColumn(chunkIn, (subX * 2) + x, (subZ * 2) + z));

        return maxHeight;
    }

    /**
     * Returns the y-coordinate of the surface block for a given local block coordinate for a given chunk.
     * Note that water blocks also count as the surface.
     * @param chunkIn chunk
     * @param localX The block's chunk-local x-coordinate
     * @param localZ The block's chunk-local z-coordinate
     * @return The y-coordinate of the surface block
     */
    public static int getSurfaceAltitudeForColumn(IChunk chunkIn, int localX, int localZ) {
        return searchSurfaceAltitudeInRangeForColumn(chunkIn, localX, localZ, 255, 0);
    }

    /**
     * Searches for the y-coordinate of the surface block for a given local block coordinate for a given chunk in a
     * specific range of y-coordinates.
     * Note that water blocks also count as the surface.
     * @param chunkIn chunk
     * @param localX The block's chunk-local x-coordinate
     * @param localZ The block's chunk-local z-coordinate
     * @param topY The top y-coordinate to stop searching at
     * @param bottomY The bottom y-coordinate to start searching at
     * @return The y-coordinate of the surface block
     */
    public static int searchSurfaceAltitudeInRangeForColumn(IChunk chunkIn, int localX, int localZ, int topY, int bottomY) {
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(localX, bottomY, localZ);

        // Edge case: blocks go all the way up to build height
        if (topY == 255) {
            BlockPos topPos = new BlockPos(localX, topY, localZ);
            if (chunkIn.getBlockState(topPos) != Blocks.AIR.getDefaultState() && chunkIn.getBlockState(topPos).getMaterial() != Material.WATER)
                return 255;
        }

        for (int y = bottomY; y <= topY; y++) {
            BlockState blockState = chunkIn.getBlockState(blockPos);
            if (blockState == Blocks.AIR.getDefaultState() || blockState.getMaterial() == Material.WATER)
                return y;
            blockPos.move(Direction.UP);
        }

        return 1; // Surface somehow not found
    }

    public static String dimensionAsString(int dimensionID, String dimensionName) {
        return String.format("%d (%s)", dimensionID, dimensionName);
    }

    public static int getLocal(int coordinate) {
        return coordinate & 0xF; // This is same as modulo 16, but quicker
    }
}