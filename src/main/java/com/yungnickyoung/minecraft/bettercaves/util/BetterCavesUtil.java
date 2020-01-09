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
    private static final BlockState ANDESITE = Blocks.ANDESITE.getDefaultState();
    private static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
    private static final BlockState WATER = Blocks.WATER.getDefaultState();

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

                //If we caused gravel to float in Oceans, replace it with andesite.
                if (BetterCavesConfig.oceanFloorSetting.equals("default"))
                    if (biome.getCategory() == Biome.Category.OCEAN)
                        if (blockStateAbove == GRAVEL)
                            chunkIn.setBlockState(blockPosAbove, ANDESITE, false);
                        else if (blockStateAbove == WATER)
                            chunkIn.setBlockState(blockPosAbove, ANDESITE, false);
                            /*This block replacement should prevent any generated gravel with
                            "isMoving:true" to be reverted back to false to prevent gravity
                            updates from applying if the previous 2 if statements fail.*/
                        else if (blockState == GRAVEL)
                            chunkIn.setBlockState(blockPos, GRAVEL, false);
            }
        }
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
     * Tests 8 edge points and center of chunk to approximate max surface altitude (y-coordinate) of the chunk.
     * Note that water blocks also count as the surface.
     * @param chunkIn chunk
     * @return y-coordinate of the approximate highest surface altitude in the chunk
     */
    public static int getMaxSurfaceAltitudeChunk(IChunk chunkIn) {
        int maxHeight = 0;
        int[] testCoords = {0, 7, 15}; // chunk-local x/z coordinates to test for max height

        for (int x : testCoords)
            for (int z : testCoords)
                maxHeight = Math.max(maxHeight, getSurfaceAltitudeForColumn(chunkIn, x, z));

        return maxHeight;
    }

    /**
     * Tests 8 edge points and center of chunk to approximate min surface altitude (y-coordinate) of the chunk.
     * Note that water blocks also count as the surface.
     * @param chunkIn chunk
     * @return y-coordinate of the approximate lowest surface altitude in the chunk
     */
    public static int getMinSurfaceAltitudeChunk(IChunk chunkIn) {
        int minHeight = 256;
        int[] testCoords = {0, 7, 15}; // chunk-local x/z coordinates to test for max height

        for (int x : testCoords)
            for (int z : testCoords)
                minHeight = Math.min(minHeight, getSurfaceAltitudeForColumn(chunkIn, x, z));

        return minHeight;
    }

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
    private static int getSurfaceAltitudeForColumn(IChunk chunkIn, int localX, int localZ) {
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
            if (chunkIn.getBlockState(blockPos) == Blocks.AIR.getDefaultState() || chunkIn.getBlockState(blockPos).getMaterial() == Material.WATER)
                return y;
            blockPos.move(Direction.UP);
        }

        return -1; // Surface somehow not found
    }
}