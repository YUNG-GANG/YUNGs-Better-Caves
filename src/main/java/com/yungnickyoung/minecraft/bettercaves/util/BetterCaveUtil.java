package com.yungnickyoung.minecraft.bettercaves.util;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

/**
 * Utility functions for BetterCaves. These functions are mostly refactored versions of methods found in
 * {@code net.minecraft.world.gen.MapGenCaves}.
 * This class may not be instantiated - all members are {@code public} and {@code static},
 * and as such may be accessed freely.
 */
public class BetterCaveUtil {
    private BetterCaveUtil() {} // Private constructor prevents instantiation

    /* Common IBlockStates used in this class */
    private static final IBlockState BLOCKSTATE_AIR = Blocks.AIR.getDefaultState();
    private static final IBlockState BLOCKSTATE_LAVA = Blocks.LAVA.getDefaultState();
    private static final IBlockState BLOCKSTATE_WATER = Blocks.WATER.getDefaultState();
    private static final IBlockState BLOCKSTATE_SAND = Blocks.SAND.getDefaultState();
    private static final IBlockState BLOCKSTATE_SANDSTONE = Blocks.SANDSTONE.getDefaultState();
    private static final IBlockState BLOCKSTATE_REDSANDSTONE = Blocks.RED_SANDSTONE.getDefaultState();

    /**
     * Determine if the block at the specified location is the designated top block for the biome.
     * @param world the Minecraft world this block is in
     * @param primer the ChunkPrimer containing the block
     * @param x the block's x coordinate
     * @param y the block's y coordinate
     * @param z the block's z coordinate
     * @param chunkX the chunk's x coordinate
     * @param chunkZ the chunk's z coordinate
     * @return true if this block is the same type as the biome's designated top block
     */
    public static boolean isTopBlock(World world, ChunkPrimer primer, int x, int y, int z, int chunkX, int chunkZ) {
        Biome biome = world.getBiome(new BlockPos(x + chunkX * 16, 0, z + chunkZ * 16));
        IBlockState blockState = primer.getBlockState(x, y, z);
        return blockState == biome.topBlock;
    }

    /**
     * Digs out the current block, default implementation removes stone, filler, and top block.
     * Sets the block to lava if y is less then 10, and air other wise.
     * If setting to air, it also checks to see if we've broken the surface, and if so,
     * tries to make the floor the biome's top block.
     *
     * @param world the Minecraft world this block is in
     * @param primer the ChunkPrimer containing the block
     * @param lavaBlockState the BlockState to use as lava. If you want regular lava, you can use the wrapper function
     *                       without this param
     * @param x the block's chunk-local x coordinate
     * @param y the block's chunk-local y coordinate
     * @param z the block's chunk-local z coordinate
     * @param chunkX the chunk's x coordinate
     * @param chunkZ the chunk's z coordinate
     */
    public static void digBlock(World world, ChunkPrimer primer, IBlockState lavaBlockState, int x, int y, int z, int chunkX, int chunkZ) {
        IBlockState blockState = primer.getBlockState(x, y, z);
        IBlockState blockStateAbove = primer.getBlockState(x, y + 1, z);

        Biome biome = world.getBiome(new BlockPos(x + chunkX * 16, y, z + chunkZ * 16));
        Block biomeTopBlock = biome.topBlock.getBlock();
        Block biomeFillerBlock = biome.fillerBlock.getBlock();

        // Only continue if the block is replaceable
        if (canReplaceBlock(blockState, blockStateAbove) || blockState.getBlock() == biomeTopBlock || blockState.getBlock() == biomeFillerBlock) {
            if (y <= Configuration.lavaDepth) { // Replace any block below the lava depth with lava
                primer.setBlockState(x, y, z, lavaBlockState);
            } else {
                // Adjust block below if block removed is biome top block
                if (isTopBlock(world, primer, x, y, z, chunkX, chunkZ) && canReplaceBlock(primer.getBlockState(x, y - 1, z), BLOCKSTATE_AIR))
                    primer.setBlockState(x, y - 1, z, biome.topBlock);

                // Replace this block with air, effectively "digging" it out
                primer.setBlockState(x, y, z, BLOCKSTATE_AIR);

                // If we caused floating sand to form, replace it with sandstone
                if (blockStateAbove == BLOCKSTATE_SAND)
                    primer.setBlockState(x, y + 1, z, BLOCKSTATE_SANDSTONE);
                else if (blockStateAbove == BLOCKSTATE_SAND.withProperty(BlockSand.VARIANT, BlockSand.EnumType.RED_SAND))
                    primer.setBlockState(x, y + 1, z, BLOCKSTATE_REDSANDSTONE);
            }
        }
    }

    // Wrapper function for digBlock with default lava block
    public static void digBlock(World world, ChunkPrimer primer, int x, int y, int z, int chunkX, int chunkZ) {
        digBlock(world, primer, BLOCKSTATE_LAVA, x, y, z, chunkX, chunkZ);
    }

        /**
         * Determines if the Block of a given IBlockState is suitable to be replaced during cave generation.
         * Basically returns true for most common world-get blocks, false if the block is air.
         * @param blockState the block's IBlockState
         * @param blockStateAbove the IBlockState of the block above this one
         * @return true if the blockState can be replaced
         */
    public static boolean canReplaceBlock(IBlockState blockState, IBlockState blockStateAbove) {
        Block block = blockState.getBlock();

        // Avoid damaging trees
        if (block == Blocks.LEAVES
                || block == Blocks.LEAVES2
                || block == Blocks.LOG
                || block == Blocks.LOG2)
            return false;

        // Accept stone-like blocks added from other mods
        if (blockState.getMaterial() == Material.ROCK)
            return true;

        // Minable blocks
        if (block == Blocks.STONE
                || block == Blocks.DIRT
                || block == Blocks.GRASS
                || block == Blocks.HARDENED_CLAY
                || block == Blocks.STAINED_HARDENED_CLAY
                || block == Blocks.SANDSTONE
                || block == Blocks.RED_SANDSTONE
                || block == Blocks.MYCELIUM
                || block  == Blocks.SNOW_LAYER)
            return true;

        // Only accept gravel and sand if water is not directly above it
        return (block == Blocks.SAND || block == Blocks.GRAVEL)
                    && blockStateAbove.getMaterial() != Material.WATER;
    }

    /**
     * Tests 8 edge points and center of chunk to approximate max surface height of the chunk.
     * @param primer primer for chunk
     * @return Max surface height of chunk
     */
    public static int getMaxSurfaceHeight(ChunkPrimer primer) {
        int maxHeight = 0;
        int[] testCoords = {0, 7, 15}; // chunk-local x/z coordinates to test for max height

        for (int x : testCoords)
            for (int z : testCoords)
                maxHeight = Math.max(maxHeight, getSurfaceHeight(primer, x, z));

        return maxHeight;
    }

    /**
     * Tests 8 edge points and center of chunk to approximate min surface height of the chunk.
     * @param primer primer for chunk
     * @return Max surface height of chunk
     */
    public static int getMinSurfaceHeight(ChunkPrimer primer) {
        int minHeight = 256;
        int[] testCoords = {0, 7, 15}; // chunk-local x/z coordinates to test for max height

        for (int x : testCoords)
            for (int z : testCoords)
                minHeight = Math.min(minHeight, getSurfaceHeight(primer, x, z));

        return minHeight;
    }

    /**
     * Returns the y-coordinate of the surface block for a given local block coordinate for a given chunk.
     * @param primer The ChunkPrimer containing the block
     * @param x The block's chunk-local x-coordinate
     * @param z The block's chunk-local z-coordinate
     * @return The y-coordinate of the surface block
     */
    private static int getSurfaceHeight(ChunkPrimer primer, int x, int z) {
//        return recursiveBinarySurfaceSearch(primer, x, z, 255, 0);
        return linarSurfaceSearch(primer, x, z, 255, 0);
    }

    /**
     * Recursive binary search, this search always converges on the surface in 8 in cycles for the range 255 >= y >= 0.
     * Thanks to Worley's Caves for this idea.
     * @param primer Chunk's ChunkPrimer
     * @param x Chunk-local x-coordinate
     * @param z Chunk-local z-coordinate
     * @param top Upper y-coordinate search bound
     * @param bottom Lower y-coordinate search bound
     * @return Surface height at given coordinate
     */
    private static int recursiveBinarySurfaceSearch(ChunkPrimer primer, int x, int z, int top, int bottom) {
        if (top > bottom) {
            int mid = (top + bottom) / 2;

            if (canReplaceBlock(primer.getBlockState(x, mid, z), Blocks.AIR.getDefaultState()))
                top = recursiveBinarySurfaceSearch(primer, x, z, top, mid + 1);
            else
                top = recursiveBinarySurfaceSearch(primer, x, z, mid, bottom);
        }
        return top;
    }

    private static int linarSurfaceSearch(ChunkPrimer primer, int x, int z, int top, int bottom) {
        for (int y = bottom; y <= top; y++) {
            if (primer.getBlockState(x, y, z) == Blocks.AIR.getDefaultState())
                return y;
        }

        return -1; // Surface somehow not found
    }
}
