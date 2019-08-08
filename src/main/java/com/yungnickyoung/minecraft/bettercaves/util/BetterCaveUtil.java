package com.yungnickyoung.minecraft.bettercaves.util;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import net.minecraft.block.Block;
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

    /**
     * Determine if the block at the specified location is the designated top block for the biome. We take into account
     * vanilla bugs to make sure that we generate the map the same way vanilla does.
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
        return isExceptionBiome(biome) ? (blockState.getBlock() == Blocks.GRASS) : (blockState == biome.topBlock);
    }

    /**
     * Digs out the current block, default implementation removes stone, filler, and top block.
     * Sets the block to lava if y is less then 10, and air other wise.
     * If setting to air, it also checks to see if we've broken the surface, and if so,
     * tries to make the floor the biome's top block.
     *
     * @param world the Minecraft world this block is in
     * @param primer the ChunkPrimer containing the block
     * @param x the block's x coordinate
     * @param y the block's y coordinate
     * @param z the block's z coordinate
     * @param chunkX the chunk's x coordinate
     * @param chunkZ the chunk's z coordinate
     * @param foundTop true if we've encountered the biome's top block (ideally, true if we've broken the surface)
     * @param blockState the block's IBlockState
     * @param blockStateAbove the IBlockState of the block above this one
     */
    public static void digBlock(World world, ChunkPrimer primer, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop, IBlockState blockState, IBlockState blockStateAbove) {
        Biome biome = world.getBiome(new BlockPos(x + chunkX * 16, 0, z + chunkZ * 16));
        Block biomeTopBlock = biome.topBlock.getBlock();
        Block biomeFillerBlock = biome.fillerBlock.getBlock();

        if (canReplaceBlock(blockState, blockStateAbove) || blockState.getBlock() == biomeTopBlock || blockState.getBlock() == biomeFillerBlock) {
            if (y <= Configuration.lavaDepth) {
                primer.setBlockState(x, y, z, Blocks.LAVA.getDefaultState());
            } else {
                primer.setBlockState(x, y, z, Blocks.AIR.getDefaultState());
                if (foundTop && primer.getBlockState(x, y - 1, z).getBlock() == biomeFillerBlock)
                    primer.setBlockState(x, y - 1, z, biome.topBlock);

                // Replace floating sand with sandstone
                if (blockStateAbove == Blocks.SAND.getDefaultState())
                    primer.setBlockState(x, y+1, z, Blocks.SANDSTONE.getDefaultState());
                else if (blockStateAbove == Blocks.SAND.getStateFromMeta(1))
                    primer.setBlockState(x, y+1, z, Blocks.RED_SANDSTONE.getDefaultState());
            }
        }
    }

    /**
     * Determines if the Block of a given IBlockState is suitable to be replaced during cave generation.
     * @param blockState the block's IBlockState
     * @param blockStateAbove the IBlockState of the block above this one
     * @return true if the blockState can be replaced
     */
    public static boolean canReplaceBlock(IBlockState blockState, IBlockState blockStateAbove) {
        if (blockState.getBlock() == Blocks.STONE) return true;
        else if (blockState.getBlock() == Blocks.DIRT) return true;
        else if (blockState.getBlock() == Blocks.GRASS) return true;
        else if (blockState.getBlock() == Blocks.HARDENED_CLAY) return true;
        else if (blockState.getBlock() == Blocks.STAINED_HARDENED_CLAY) return true;
        else if (blockState.getBlock() == Blocks.SANDSTONE) return true;
        else if (blockState.getBlock() == Blocks.RED_SANDSTONE) return true;
        else if (blockState.getBlock() == Blocks.MYCELIUM) return true;
        else if (blockState.getBlock() == Blocks.SNOW_LAYER) return true;
        else
            return (blockState.getBlock() == Blocks.SAND || blockState.getBlock() == Blocks.GRAVEL) && blockStateAbove.getMaterial() != Material.WATER;
    }

    /**
     * Checks for exception biomes to ensure we generate like vanilla (possibly due to vanilla bugs?)
     * @param biome The biome in question
     * @return true if the biome is an exception biome
     */
    public static boolean isExceptionBiome(Biome biome) {
        return biome == net.minecraft.init.Biomes.BEACH || biome == net.minecraft.init.Biomes.DESERT;
    }
}
