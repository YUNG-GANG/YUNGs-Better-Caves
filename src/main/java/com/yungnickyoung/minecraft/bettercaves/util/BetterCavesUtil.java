package com.yungnickyoung.minecraft.bettercaves.util;

import com.google.common.collect.ImmutableSet;
import com.yungnickyoung.minecraft.bettercaves.config.BetterCavesConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.Blocks;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

import java.util.Set;

/**
 * Utility functions for BetterCaves. These functions are mostly refactored versions of methods found in
 * {@code net.minecraft.world.gen.MapGenCaves}.
 * This class may not be instantiated - all members are {@code public} and {@code static},
 * and as such may be accessed freely.
 */
public class BetterCavesUtil {

    // Set of carvable blocks provided by vanilla 1.14
    public static Set<Block> carvableBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.PACKED_ICE);

    private BetterCavesUtil() {} // Private constructor prevents instantiation

    /* Common IBlockStates used in this class */
    private static final BlockState AIR = Blocks.AIR.getDefaultState();
    private static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
    private static final BlockState LAVA = Blocks.LAVA.getDefaultState();
    private static final BlockState SAND = Blocks.SAND.getDefaultState();
    private static final BlockState RED_SAND = Blocks.RED_SAND.getDefaultState();
    private static final BlockState SANDSTONE = Blocks.SANDSTONE.getDefaultState();
    private static final BlockState RED_SANDSTONE = Blocks.RED_SANDSTONE.getDefaultState();

    /**
     * Determine if the block at the specified location is the designated top block for the biome.
     *
     * @param chunkIn the chunk containing the block
     * @param localX the block's chunk-local x-coordinate
     * @param y the block's chunk-local y-coordinate (same as real y-coordinate)
     * @param localZ the block's chunk-local z-coordinate
     * @return true if this block is the same type as the biome's designated top block
     */
    public static boolean isTopBlock(IChunk chunkIn, int localX, int y, int localZ) {
        BlockPos blockPos = new BlockPos(localX, y, localZ);
        Biome biome = chunkIn.getBiome(blockPos);
        BlockState blockState = chunkIn.getBlockState(blockPos);

        return blockState == biome.getSurfaceBuilderConfig().getTop();
    }

    /**
     * Digs out the current block, default implementation removes stone, filler, and top block.
     * Sets the block to lavaBlockState if y is less then the lavaDepth in the Config, and air other wise.
     * If setting to air, it also checks to see if we've broken the surface, and if so,
     * tries to make the floor the biome's top block.
     *
     * @param chunkIn the chunk containing the block
     * @param lavaBlockState the BlockState to use as lava. If you want regular lava, you can either specify it, or
     *                       use the wrapper function without this param
     * @param localX the block's chunk-local x coordinate
     * @param y the block's chunk-local y coordinate (same as real y-coordinate)
     * @param localZ the block's chunk-local z coordinate
     * @param chunkX the chunk's x coordinate
     * @param chunkZ the chunk's z coordinate
     */
    public static void digBlock(IChunk chunkIn, BlockState lavaBlockState, int localX, int y, int localZ, int chunkX, int chunkZ) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(localX, y, localZ);

        BlockPos blockPos = new BlockPos(mutableBlockPos.getX(), mutableBlockPos.getY(), mutableBlockPos.getZ());
        BlockPos blockPosAbove = mutableBlockPos.move(Direction.UP);
        BlockPos blockPosBelow = mutableBlockPos.move(Direction.DOWN).move(Direction.DOWN);

        BlockState blockState = chunkIn.getBlockState(blockPos);
        BlockState blockStateAbove = chunkIn.getBlockState(blockPosAbove);

        Biome biome = chunkIn.getBiome(blockPos);
        BlockState biomeTopBlockState = biome.getSurfaceBuilderConfig().getTop();
        BlockState biomeFillerBlockState = biome.getSurfaceBuilderConfig().getUnder();

        // Only continue if the block is replaceable
        if (canReplaceBlock(blockState, blockStateAbove) || blockState == biomeTopBlockState || blockState == biomeFillerBlockState) {
            if (y <= BetterCavesConfig.lavaDepth) { // Replace any block below the lava depth with the lava block passed in
                chunkIn.setBlockState(blockPos, lavaBlockState, false);
            } else {
                // Adjust block below if block removed is biome top block
                if (isTopBlock(chunkIn, localX, y, localZ)
                        && canReplaceBlock(chunkIn.getBlockState(blockPosBelow), AIR))
                    chunkIn.setBlockState(blockPosBelow, biomeTopBlockState, false);

                // Replace this block with air, effectively "digging" it out
                chunkIn.setBlockState(blockPos, CAVE_AIR, false);

                // If we caused floating sand to form, replace it with sandstone
                if (blockStateAbove == SAND)
                    chunkIn.setBlockState(blockPosAbove, SANDSTONE, false);
                else if (blockStateAbove == RED_SAND)
                    chunkIn.setBlockState(blockPosAbove, RED_SANDSTONE, false);
            }
        }
    }

    /**
     * Wrapper function for digBlock with default lava block.
     * Digs out the current block, default implementation removes stone, filler, and top block.
     * Sets the block to lavaBlockState if y is less then the lavaDepth in the Config, and air other wise.
     * If setting to air, it also checks to see if we've broken the surface, and if so,
     * tries to make the floor the biome's top block.
     *
     * @param chunkIn the chunk containing the block
     * @param localX the block's chunk-local x coordinate
     * @param y the block's chunk-local y coordinate (same as real y-coordinate)
     * @param localZ the block's chunk-local z coordinate
     * @param chunkX the chunk's x coordinate
     * @param chunkZ the chunk's z coordinate
     */
    public static void digBlock(IChunk chunkIn, int localX, int y, int localZ, int chunkX, int chunkZ) {
        digBlock(chunkIn, LAVA, localX, y, localZ, chunkX, chunkZ);
    }

    /**
     * Determines if the Block of a given IBlockState is suitable to be replaced during cave generation.
     * Basically returns true for most common worldgen blocks (e.g. stone, dirt, sand), false if the block is air.
     *
     * @param blockState the block's IBlockState
     * @param blockStateAbove the IBlockState of the block above this one
     * @return true if the blockState can be replaced
     */
    public static boolean canReplaceBlock(BlockState blockState, BlockState blockStateAbove) {
        Block block = blockState.getBlock();

        // Avoid damaging trees
        if (blockState.getMaterial() == Material.LEAVES
                || blockState.getMaterial() == Material.WOOD)
            return false;

        // Avoid digging out under trees
        if (blockStateAbove.getMaterial() == Material.WOOD)
            return false;

        // This should hopefully avoid damaging villages
        if (block == Blocks.FARMLAND
                || block == Blocks.GRASS_PATH) {
            return false;
        }

        // Accept stone-like blocks added from other mods
        if (blockState.getMaterial() == Material.ROCK)
            return true;

        // List of carvable blocks provided by vanilla
        if (carvableBlocks.contains(block))
            return true;

        // Only accept gravel and sand if water is not directly above it
        return (block == Blocks.SAND || block == Blocks.GRAVEL)
                && !blockStateAbove.getFluidState().isTagged(FluidTags.WATER);
    }

    /**
     * Tests 8 edge points and center of chunk to approximate max surface height of the chunk.
     * @param chunkIn chunk
     * @return Max surface height of chunk
     */
    public static int getMaxSurfaceHeight(IChunk chunkIn) {
        int maxHeight = 0;
        int[] testCoords = {0, 7, 15}; // chunk-local x/z coordinates to test for max height

        for (int x : testCoords)
            for (int z : testCoords)
                maxHeight = Math.max(maxHeight, getSurfaceHeight(chunkIn, x, z));

        return maxHeight;
    }

    /**
     * Tests 8 edge points and center of chunk to approximate min surface height of the chunk.
     * @param chunkIn chunk
     * @return Min surface height of chunk
     */
    public static int getMinSurfaceHeight(IChunk chunkIn) {
        int minHeight = 256;
        int[] testCoords = {0, 7, 15}; // chunk-local x/z coordinates to test for max height

        for (int x : testCoords)
            for (int z : testCoords)
                minHeight = Math.min(minHeight, getSurfaceHeight(chunkIn, x, z));

        return minHeight;
    }

    /**
     * Tests every block in a 2x2 "sub-chunk" to get the max height of the sub-chunk.
     * @param chunkIn chunk
     * @param subX The x-coordinate of the sub-chunk. Note that this is regular chunk-local x-coordinate divided
     *             by 2. E.g. If you want the last 2 blocks on the x-axis in the chunk (blocks 14 and 15), use subX = 7.
     * @param subZ The z-coordinate of the sub-chunk. Note that this is regular chunk-local z-coordinate divided
     *             by 2. E.g. If you want the last 2 blocks on the z-axis in the chunk (blocks 14 and 15), use subZ = 7.
     * @return Max surface height of the sub-chunk
     */
    public static int getMaxSurfaceHeightSubChunk(IChunk chunkIn, int subX, int subZ)  {
        int maxHeight = 0;
        int[] testCoords = {0, 1}; // chunk-local x/z coordinates to test for max height

        for (int x : testCoords)
            for (int z : testCoords)
                maxHeight = Math.max(maxHeight, getSurfaceHeight(chunkIn, (subX * 2) + x, (subZ * 2) + z));

        return maxHeight;
    }

    /**
     * Returns the y-coordinate of the surface block for a given local block coordinate for a given chunk.
     * @param chunkIn chunk containing the block
     * @param x The block's chunk-local x-coordinate
     * @param z The block's chunk-local z-coordinate
     * @return The y-coordinate of the surface block
     */
    private static int getSurfaceHeight(IChunk chunkIn, int x, int z) {
//        return recursiveBinarySurfaceSearch(primer, x, z, 255, 0);
        return linarSurfaceSearch(chunkIn, x, z, 255, 0);
    }

    /**
     * Recursive binary search, this search always converges on the surface in 8 in cycles for the range 255 >= y >= 0.
     * Thanks to Worley's Caves for this idea.
     * @param chunkIn Chunk
     * @param x Chunk-local x-coordinate
     * @param z Chunk-local z-coordinate
     * @param top Upper y-coordinate search bound
     * @param bottom Lower y-coordinate search bound
     * @return Surface height at given coordinate
     */
    private static int recursiveBinarySurfaceSearch(IChunk chunkIn, int x, int z, int top, int bottom) {
        if (top > bottom) {
            int mid = (top + bottom) / 2;

            if (canReplaceBlock(chunkIn.getBlockState(new BlockPos(x, mid, z)), Blocks.AIR.getDefaultState()))
                top = recursiveBinarySurfaceSearch(chunkIn, x, z, top, mid + 1);
            else
                top = recursiveBinarySurfaceSearch(chunkIn, x, z, mid, bottom);
        }
        return top;
    }

    /**
     * Linear search from the bottom up to find the surface y-coordinate for a given block.
     * Slower than binary search, but more reliable since it also works for areas with overhangs or floating islands.
     * @param chunkIn The chunk containing the block
     * @param localX The chunk-local x-coordinate
     * @param localZ The chunk-local z-coordinate
     * @param topY The top y-coordinate to stop searching at
     * @param bottomY The bottom y-coordinate to start searching at
     * @return Surface height at given coordinate
     */
    private static int linarSurfaceSearch(IChunk chunkIn, int localX, int localZ, int topY, int bottomY) {
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(localX, bottomY, localZ);
        for (int y = bottomY; y <= topY; y++) {
            if (chunkIn.getBlockState(blockPos) == Blocks.AIR.getDefaultState() || chunkIn.getBlockState(new BlockPos(localX, y, localZ)) == Blocks.WATER.getDefaultState())
                return y;
            blockPos.move(Direction.UP);
        }

        return -1; // Surface somehow not found
    }
}