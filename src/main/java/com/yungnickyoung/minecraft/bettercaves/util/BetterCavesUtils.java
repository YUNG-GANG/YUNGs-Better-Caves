package com.yungnickyoung.minecraft.bettercaves.util;

import com.yungnickyoung.minecraft.yungsapi.math.ColPos;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Miscellaneous utility functions and fields for Better Caves.
 * This class need not be instantiated - all members are {@code public} and {@code static},
 * and as such may be accessed freely.
 */
public class BetterCavesUtils {
    private BetterCavesUtils() {}

    // Equality checking functions used for closing off flooded caves
    public static Predicate<Biome.Category> isOcean = b -> b == Biome.Category.OCEAN;
    public static Predicate<Biome.Category> isNotOcean = b -> b != Biome.Category.OCEAN;

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
        BlockPos.Mutable blockPos = new BlockPos.Mutable(localX, bottomY, localZ);

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

    /**
     * Returns chunk-local coordinate value (0 - 15, inclusive).
     */
    public static int getLocal(int coordinate) {
        return coordinate & 0xF; // This is same as modulo 16, but quicker
    }

    /**
     * Checks if an IWorld contains a block based on its position.
     * Typically, the world provided will be an instance of WorldGenRegion, and so
     * we are testing if the WorldGenRegion contains the provided BlockPos.
     */
    public static boolean isPosInWorld(ColPos pos, IWorld world) {
        return world.chunkExists(pos.getX() >> 4, pos.getZ() >> 4);
    }

    public static boolean isPosInWorld(BlockPos pos, IWorld world) {
        return world.chunkExists(pos.getX() >> 4, pos.getZ() >> 4);
    }

    /**
     * Returns a linear amplifier (from 0 to 1, inclusive) indicating how far away a target biome is.
     * The target biome is searched for in a circle with a given radius centered around the starting block.
     * The circle is searched radially outward from the starting position, so as not to perform unnecessary computation.
     *
     * This function is primarily used to search for nearby ocean/non-ocean biomes to close off flooded caves
     * from non-flooded caves, preventing weird water walls.
     *
     * @param biomeMap Map of block positions as Longs to Biomes
     * @param pos Center position to search around
     * @param radius Radius of search circle
     * @param isTargetBiome Function to use when testing if a given block's biome is the biome we are lookin for
     */
    public static float getDistFactor(IWorld worldIn, Map<Long, Biome> biomeMap, ColPos pos, int radius, Predicate<Biome.Category> isTargetBiome) {
        ColPos.Mutable checkpos = new ColPos.Mutable();
        for (int i = 1; i <= radius; i++) {
            for (int j = 0; j <= i; j++) {
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    checkpos.setPos(pos).move(direction, i).move(direction.rotateY(), j);
                    if (isPosInWorld(checkpos, worldIn) && isTargetBiome.test(biomeMap.get(checkpos.toLong()).getCategory())) {
                        return (float)(i + j) / (2 * radius);
                    }
                    if (j != 0 && i != j) {
                        checkpos.setPos(pos).move(direction, i).move(direction.rotateYCCW(), j);
                        if (isPosInWorld(checkpos, worldIn) && isTargetBiome.test(biomeMap.get(checkpos.toLong()).getCategory())) {
                            return (float)(i + j) / (2 * radius);
                        }
                    }
                }
            }
        }

        return 1;
    }

    /**
     * Alternative method that uses vanilla biomePos Function (accepts BlockPos and returns the Biome at that pos)
     * instead of my own biome map.
     */
    public static float getDistFactor(IWorld worldIn, Function<BlockPos, Biome> biomePos, BlockPos pos, int radius, Predicate<Biome.Category> isTargetBiome) {
        BlockPos.Mutable checkpos = new BlockPos.Mutable();
        for (int i = 1; i <= radius; i++) {
            for (int j = 0; j <= i; j++) {
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    checkpos.setPos(pos).move(direction, i).move(direction.rotateY(), j);
                    if (isPosInWorld(checkpos, worldIn) && isTargetBiome.test(biomePos.apply(checkpos).getCategory())) {
                        return (float)(i + j) / (2 * radius);
                    }
                    if (j != 0 && i != j) {
                        checkpos.setPos(pos).move(direction, i).move(direction.rotateYCCW(), j);
                        if (isPosInWorld(checkpos, worldIn) && isTargetBiome.test(biomePos.apply(checkpos).getCategory())) {
                            return (float)(i + j) / (2 * radius);
                        }
                    }
                }
            }
        }

        return 1;
    }
}