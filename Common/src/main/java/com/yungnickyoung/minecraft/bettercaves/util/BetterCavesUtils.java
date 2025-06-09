package com.yungnickyoung.minecraft.bettercaves.util;


import com.yungnickyoung.minecraft.yungsapi.math.ColPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Miscellaneous utility functions and fields for Better Caves.
 * This class need not be instantiated - all members are {@code public} and {@code static},
 * and as such may be accessed freely.
 */
public class BetterCavesUtils {
    // Equality checking functions used for closing off flooded caves
    public static Predicate<Holder<Biome>> isOcean = b -> b.is(BiomeTags.IS_OCEAN);
    public static Predicate<Holder<Biome>> isNotOcean = b -> !isOcean.test(b);

    private BetterCavesUtils() {
    }

    /**
     * Tests every block in a 2x2 "sub-chunk" to get the max surface altitude (y-coordinate) of the sub-chunk.
     * Note that water blocks also count as the surface.
     *
     * @param chunkAccess chunk
     * @param subX        The x-coordinate of the sub-chunk. Note that this is regular chunk-local x-coordinate divided
     *                    by 2. E.g. If you want the last 2 blocks on the x-axis in the chunk (blocks 14 and 15), use subX = 7.
     * @param subZ        The z-coordinate of the sub-chunk. Note that this is regular chunk-local z-coordinate divided
     *                    by 2. E.g. If you want the last 2 blocks on the z-axis in the chunk (blocks 14 and 15), use subZ = 7.
     * @return Max surface height of the sub-chunk
     */
    public static int getMaxSurfaceAltitudeSubChunk(ChunkAccess chunkAccess, int subX, int subZ) {
        int maxHeight = 0;
        int[] testCoords = {0, 1}; // chunk-local x/z coordinates to test for max height

        for (int x : testCoords)
            for (int z : testCoords)
                maxHeight = Math.max(maxHeight, getSurfaceAltitudeForColumn(chunkAccess, (subX * 2) + x, (subZ * 2) + z));

        return maxHeight;
    }

    /**
     * Returns the y-coordinate of the surface block for a given local block coordinate for a given chunk.
     * Note that water blocks also count as the surface.
     *
     * @param chunkAccess chunk
     * @param localX      The block's chunk-local x-coordinate
     * @param localZ      The block's chunk-local z-coordinate
     * @return The y-coordinate of the surface block
     */
    public static int getSurfaceAltitudeForColumn(ChunkAccess chunkAccess, int localX, int localZ) {
        return searchSurfaceAltitudeInRangeForColumn(chunkAccess, localX, localZ, 255, 0);
    }

    /**
     * Searches for the y-coordinate of the surface block for a given local block coordinate for a given chunk in a
     * specific range of y-coordinates.
     * Note that water blocks also count as the surface.
     *
     * @param chunkAccess chunk
     * @param localX      The block's chunk-local x-coordinate
     * @param localZ      The block's chunk-local z-coordinate
     * @param topY        The top y-coordinate to stop searching at
     * @param bottomY     The bottom y-coordinate to start searching at
     * @return The y-coordinate of the surface block
     */
    public static int searchSurfaceAltitudeInRangeForColumn(ChunkAccess chunkAccess, int localX, int localZ, int topY, int bottomY) {
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(localX, bottomY, localZ);

        // Edge case: blocks go all the way up to build height
        if (topY == 255) {
            BlockPos topPos = new BlockPos(localX, topY, localZ);
            if (chunkAccess.getBlockState(topPos) != Blocks.AIR.defaultBlockState() && !chunkAccess.getBlockState(topPos).liquid())
                return 255;
        }

        for (int y = bottomY; y <= topY; y++) {
            BlockState blockState = chunkAccess.getBlockState(blockPos);
            if (blockState == Blocks.AIR.defaultBlockState() || blockState.liquid())
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
    public static boolean isPosInWorld(ColPos pos, WorldGenRegion world) {
        return world.hasChunk(pos.getX() >> 4, pos.getZ() >> 4);
    }

    public static boolean isPosInWorld(ColPos pos, ServerLevel serverLevel) {
        return true;
    }

    public static boolean isPosInWorld(BlockPos pos, WorldGenRegion world) {
        return world.hasChunk(pos.getX() >> 4, pos.getZ() >> 4);
    }

    public static boolean isPosInWorld(BlockPos pos, ServerLevel serverLevel) {
        return true;
    }

    /**
     * Returns a linear amplifier (from 0 to 1, inclusive) indicating how far away a target biome is.
     * The target biome is searched for in a circle with a given radius centered around the starting block.
     * The circle is searched radially outward from the starting position, so as not to perform unnecessary computation.
     * <p>
     * This function is primarily used to search for nearby ocean/non-ocean biomes to close off flooded caves
     * from non-flooded caves, preventing weird water walls.
     *
     * @param biomeMap      Map of block positions as Longs to Biomes
     * @param colPos        Center position to search around
     * @param radius        Radius of search circle
     * @param isTargetBiome Function to use when testing if a given block's biome is the biome we are lookin for
     */
    public static float getDistFactor(ServerLevel serverLevel, Map<Long, Biome> biomeMap, ColPos colPos, int radius,
                                      Predicate<Biome> isTargetBiome) {
        ColPos.Mutable checkPos = new ColPos.Mutable();
        for (int i = 1; i <= radius; i++) {
            for (int j = 0; j <= i; j++) {
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    checkPos.set(colPos).move(direction, i).move(direction.getClockWise(), j);
//                    if (isPosInWorld(checkPos, serverLevel) && isTargetBiome.test(biomeMap.get(checkPos.toLong()))) {
                    if (isTargetBiome.test(biomeMap.get(checkPos.toLong()))) {
                        return (float) (i + j) / (2 * radius);
                    }
                    if (j != 0 && i != j) {
                        checkPos.set(colPos).move(direction, i).move(direction.getCounterClockWise(), j);
//                        if (isPosInWorld(checkPos, serverLevel) && isTargetBiome.test(biomeMap.get(checkPos.toLong()))) {
                        if (isTargetBiome.test(biomeMap.get(checkPos.toLong()))) {
                            return (float) (i + j) / (2 * radius);
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
    public static float getDistFactor(ServerLevel serverLevel, Function<BlockPos, Holder<Biome>> biomeProvider, BlockPos pos,
                                      int radius, Predicate<Holder<Biome>> isTargetBiome) {
        BlockPos.MutableBlockPos checkpos = new BlockPos.MutableBlockPos();
        for (int i = 1; i <= radius; i++) {
            for (int j = 0; j <= i; j++) {
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    checkpos.set(pos).move(direction, i).move(direction.getClockWise(), j);
//                    if (isPosInWorld(checkpos, serverLevel) && isTargetBiome.test(biomePos.apply(checkpos).getCategory())) {
                    if (isTargetBiome.test(biomeProvider.apply(checkpos))) {
                        return (float) (i + j) / (2 * radius);
                    }
                    if (j != 0 && i != j) {
                        checkpos.set(pos).move(direction, i).move(direction.getCounterClockWise(), j);
//                        if (isPosInWorld(checkpos, serverLevel) && isTargetBiome.test(biomePos.apply(checkpos).getCategory())) {
                        if (isTargetBiome.test(biomeProvider.apply(checkpos))) {
                            return (float) (i + j) / (2 * radius);
                        }
                    }
                }
            }
        }

        return 1;
    }
}
