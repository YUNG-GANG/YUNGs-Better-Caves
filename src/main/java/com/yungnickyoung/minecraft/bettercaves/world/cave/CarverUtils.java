package com.yungnickyoung.minecraft.bettercaves.world.cave;

import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtil;
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
 * Utility functions for Better Caves carvers.
 * This class may not be instantiated - all members are {@code public} and {@code static},
 * and as such may be accessed freely.
 */
public class CarverUtils {
    private CarverUtils() {} // Private constructor prevents instantiation

    /* IBlockStates used in this class */
    private static final IBlockState AIR = Blocks.AIR.getDefaultState();
    private static final IBlockState SAND = Blocks.SAND.getDefaultState();
    private static final IBlockState SANDSTONE = Blocks.SANDSTONE.getDefaultState();
    private static final IBlockState REDSANDSTONE = Blocks.RED_SANDSTONE.getDefaultState();

    /**
     * Digs out the current block, default implementation removes stone, filler, and top block.
     * Sets the block to lavaBlockState if y is less then the liquidAltitude in the Config, and air other wise.
     * If setting to air, it also checks to see if we've broken the surface, and if so,
     * tries to make the floor the biome's top block.
     *
     * @param world the Minecraft world this block is in
     * @param primer the ChunkPrimer containing the block
     * @param blockPos The block's position
     * @param liquidBlockState the BlockState to use for liquids. If you want regular lava, you can either specify it, or
     *                       use the wrapper function without this param
     * @param liquidAltitude altitude at and below which air is replaced with liquidBlockState
     */
    public static void digBlock(World world, ChunkPrimer primer, BlockPos blockPos, IBlockState liquidBlockState, int liquidAltitude) {
        int localX = BetterCavesUtil.getLocal(blockPos.getX());
        int localZ = BetterCavesUtil.getLocal(blockPos.getZ());
        int y = blockPos.getY();

        if (liquidBlockState.getMaterial() != Material.WATER) {
            // Check for adjacent water blocks to avoid breaking into lakes or oceans
            if (primer.getBlockState(localX, y + 1, localZ).getMaterial() == Material.WATER)
                return;
            if (localX < 15 && primer.getBlockState(localX + 1, y, localZ).getMaterial() == Material.WATER)
                return;
            if (localX > 0 && primer.getBlockState(localX - 1, y, localZ).getMaterial() == Material.WATER)
                return;
            if (localZ < 15 && primer.getBlockState(localX, y, localZ + 1).getMaterial() == Material.WATER)
                return;
            if (localZ > 0 && primer.getBlockState(localX, y, localZ - 1).getMaterial() == Material.WATER)
                return;
        }

        IBlockState blockState = primer.getBlockState(localX, y, localZ);
        IBlockState blockStateAbove = primer.getBlockState(localX, y + 1, localZ);

        Biome biome = world.getBiome(blockPos);
        Block biomeTopBlock = biome.topBlock.getBlock();
        Block biomeFillerBlock = biome.fillerBlock.getBlock();

        // Only continue if the block is replaceable
        if (canReplaceBlock(blockState, blockStateAbove) || blockState.getBlock() == biomeTopBlock || blockState.getBlock() == biomeFillerBlock) {
            if ( y <= liquidAltitude) { // Replace any air below the liquid altitude with the liquid block passed in
                primer.setBlockState(localX, y, localZ, liquidBlockState);
            } else {
                // Adjust block below if block removed is biome top block
                if (isTopBlock(world, primer, blockPos) && canReplaceBlock(primer.getBlockState(localX, y - 1, localZ), AIR))
                    primer.setBlockState(localX, y - 1, localZ, biome.topBlock);

                // Replace this block with air, effectively "digging" it out
                primer.setBlockState(localX, y, localZ, AIR);

                // If we caused floating sand to form, replace it with sandstone
                if (blockStateAbove == SAND)
                    primer.setBlockState(localX, y + 1, localZ, SANDSTONE);
                else if (blockStateAbove == SAND.withProperty(BlockSand.VARIANT, BlockSand.EnumType.RED_SAND))
                    primer.setBlockState(localX, y + 1, localZ, REDSANDSTONE);
            }
        }
    }

    /**
     * DEBUG method for visualizing cave systems. Used as a replacement for the {@code digBlock} method if the
     * debugVisualizer config option is enabled.
     * @param primer Chunk containing the block
     * @param blockPos block position
     * @param blockState The blockState to set dug out blocks to
     */
    public static void debugDigBlock(ChunkPrimer primer, BlockPos blockPos, IBlockState blockState, boolean digBlock) {
        int localX = BetterCavesUtil.getLocal(blockPos.getX());
        int localZ = BetterCavesUtil.getLocal(blockPos.getZ());
        int y = blockPos.getY();

        if (digBlock)
            primer.setBlockState(localX, y, localZ, blockState);
        else
            primer.setBlockState(localX, y, localZ, Blocks.AIR.getDefaultState());
    }

    /**
     * Determine if the block at the specified location is the designated top block for the biome.
     *
     * @param world the Minecraft world this block is in
     * @param primer the ChunkPrimer containing the block
     * @param blockPos The block's position
     * @return true if this block is the same type as the biome's designated top block
     */
    public static boolean isTopBlock(World world, ChunkPrimer primer, BlockPos blockPos) {
        int localX = BetterCavesUtil.getLocal(blockPos.getX());
        int localZ = BetterCavesUtil.getLocal(blockPos.getZ());
        int y = blockPos.getY();
        Biome biome = world.getBiome(blockPos);
        IBlockState blockState = primer.getBlockState(localX, y, localZ);
        return blockState == biome.topBlock;
    }

    /**
     * Determines if the Block of a given IBlockState is suitable to be replaced during cave generation.
     * Basically returns true for most common worldgen blocks (e.g. stone, dirt, sand), false if the block is air.
     *
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

        // Avoid digging out under trees
        if (blockStateAbove == Blocks.LOG.getDefaultState()
                || blockStateAbove == Blocks.LOG2.getDefaultState())
            return false;

        // Don't mine bedrock
        if (blockState == Blocks.BEDROCK.getDefaultState())
            return false;

        // Accept stone-like blocks added from other mods
        if (blockState.getMaterial() == Material.ROCK)
            return true;

        // Mine-able blocks
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
}
