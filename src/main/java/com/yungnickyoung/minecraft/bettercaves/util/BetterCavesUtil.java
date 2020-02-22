package com.yungnickyoung.minecraft.bettercaves.util;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkPrimer;

/**
 * Utility functions for Better Caves.
 * This class may not be instantiated - all members are {@code public} and {@code static},
 * and as such may be accessed freely.
 */
public class BetterCavesUtil {
    private BetterCavesUtil() {} // Private constructor prevents instantiation

    /**
     * Tests 8 edge points and center of chunk to approximate max surface altitude (y-coordinate) of the chunk.
     * Note that water blocks also count as the surface.
     * @param primer primer for chunk
     * @return y-coordinate of the approximate highest surface altitude in the chunk
     */
    public static int getMaxSurfaceAltitudeChunk(ChunkPrimer primer) {
        int maxHeight = 0;
        int[] testCoords = {0, 7, 15}; // chunk-local x/z coordinates to test for max height

        for (int x : testCoords)
            for (int z : testCoords)
                maxHeight = Math.max(maxHeight, getSurfaceAltitudeForColumn(primer, x, z));

        return maxHeight;
    }

    /**
     * Tests 8 edge points and center of chunk to approximate min surface altitude (y-coordinate) of the chunk.
     * Note that water blocks also count as the surface.
     * @param primer primer for chunk
     * @return y-coordinate of the approximate lowest surface altitude in the chunk
     */
    public static int getMinSurfaceAltitudeChunk(ChunkPrimer primer) {
        int minHeight = 256;
        int[] testCoords = {0, 7, 15}; // chunk-local x/z coordinates to test for max height

        for (int x : testCoords)
            for (int z : testCoords)
                minHeight = Math.min(minHeight, getSurfaceAltitudeForColumn(primer, x, z));

        return minHeight;
    }

    /**
     * Tests every block in a 2x2 "sub-chunk" to get the max surface altitude (y-coordinate) of the sub-chunk.
     * Note that water blocks also count as the surface.
     * @param primer primer for chunk
     * @param subX The x-coordinate of the sub-chunk. Note that this is regular chunk-local x-coordinate divided
     *             by 2. E.g. If you want the last 2 blocks on the x-axis in the chunk (blocks 14 and 15), use subX = 7.
     * @param subZ The z-coordinate of the sub-chunk. Note that this is regular chunk-local z-coordinate divided
     *             by 2. E.g. If you want the last 2 blocks on the z-axis in the chunk (blocks 14 and 15), use subZ = 7.
     * @return Max surface height of the sub-chunk
     */
    public static int getMaxSurfaceAltitudeSubChunk(ChunkPrimer primer, int subX, int subZ)  {
        int maxHeight = 0;
        int[] testCoords = {0, 1}; // chunk-local x/z coordinates to test for max height

        for (int x : testCoords)
            for (int z : testCoords)
                maxHeight = Math.max(maxHeight, getSurfaceAltitudeForColumn(primer, (subX * 2) + x, (subZ * 2) + z));

        return maxHeight;
    }

    public static int estimateMaxSurfaceAltitudeSubChunk(ChunkPrimer primer, BlockPos startPos, int subChunkSize) {
        int maxHeight = 0;
        int startX = getLocal(startPos.getX());
        int startZ = getLocal(startPos.getZ());
        int endX = startX + subChunkSize - 1;
        int endZ = startZ + subChunkSize - 1;

        if (subChunkSize == 1)
            return Math.max(maxHeight, getSurfaceAltitudeForColumn(primer, startX, startZ));

        maxHeight = Math.max(maxHeight, getSurfaceAltitudeForColumn(primer, startX, startZ));
        maxHeight = Math.max(maxHeight, getSurfaceAltitudeForColumn(primer, startX, endZ));
        maxHeight = Math.max(maxHeight, getSurfaceAltitudeForColumn(primer, endX, startZ));
        maxHeight = Math.max(maxHeight, getSurfaceAltitudeForColumn(primer, endX, endZ));
        maxHeight = Math.max(maxHeight, getSurfaceAltitudeForColumn(primer, (endX - startX) / 2, (endZ - startZ) / 2));

        return maxHeight;
    }

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
            if (primer.getBlockState(localX, y, localZ) == Blocks.AIR.getDefaultState() || primer.getBlockState(localX, y, localZ).getMaterial() == Material.WATER)
                return y;
        }

        return -1; // Surface somehow not found
    }

    public static String dimensionAsString(int dimensionID, String dimensionName) {
        return "" + dimensionID + " (" + dimensionName + ")";
    }

    public static int getLocal(int coordinate) {
        return coordinate & 0xF; // This is same as modulo 16, but quicker
    }
}
