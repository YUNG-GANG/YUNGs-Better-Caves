package com.yungnickyoung.minecraft.bettercaves.util;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkPrimer;

/**
 * Utility functions for Better Caves.
 * This class may not be instantiated - all members are {@code public} and {@code static},
 * and as such may be accessed freely.
 */
public class BetterCavesUtils {
    private BetterCavesUtils() {} // Private constructor prevents instantiation

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

        return maxHeight;
    }

    public static int estimateMinSurfaceAltitudeSubChunk(ChunkPrimer primer, BlockPos startPos, int subChunkSize) {
        int minHeight = 255;
        int startX = getLocal(startPos.getX());
        int startZ = getLocal(startPos.getZ());
        int endX = startX + subChunkSize - 1;
        int endZ = startZ + subChunkSize - 1;

        if (subChunkSize == 1)
            return Math.min(minHeight, getSurfaceAltitudeForColumn(primer, startX, startZ));

        minHeight = Math.min(minHeight, getSurfaceAltitudeForColumn(primer, startX, startZ));
        minHeight = Math.min(minHeight, getSurfaceAltitudeForColumn(primer, startX, endZ));
        minHeight = Math.min(minHeight, getSurfaceAltitudeForColumn(primer, endX, startZ));
        minHeight = Math.min(minHeight, getSurfaceAltitudeForColumn(primer, endX, endZ));

        return minHeight;
    }


    public static int estimateAvgSurfaceAltitudeSubChunk(ChunkPrimer primer, BlockPos startPos, int subChunkSize) {
        int avgHeight = 0;
        int startX = getLocal(startPos.getX());
        int startZ = getLocal(startPos.getZ());
        int endX = startX + subChunkSize - 1;
        int endZ = startZ + subChunkSize - 1;

        if (subChunkSize == 1)
            return getSurfaceAltitudeForColumn(primer, startX, startZ);

        avgHeight += getSurfaceAltitudeForColumn(primer, startX, startZ);
        avgHeight += getSurfaceAltitudeForColumn(primer, startX, endZ);
        avgHeight += getSurfaceAltitudeForColumn(primer, endX, startZ);
        avgHeight += getSurfaceAltitudeForColumn(primer, endX, endZ);
        avgHeight /= 4;

        return avgHeight;
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
        return "" + dimensionID + " (" + dimensionName + ")";
    }

    public static int getLocal(int coordinate) {
        return coordinate & 0xF; // This is same as modulo 16, but quicker
    }
}
