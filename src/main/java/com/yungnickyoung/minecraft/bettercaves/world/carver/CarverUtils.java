package com.yungnickyoung.minecraft.bettercaves.world.carver;


import com.google.common.collect.ImmutableSet;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.material.Material;

import java.util.BitSet;
import java.util.Random;
import java.util.Set;

/**
 * Utility functions for Better Caves carvers.
 * This class may not be instantiated - all members are {@code public} and {@code static},
 * and as such may be accessed freely.
 */
public class CarverUtils {
    private CarverUtils() {
    } // Private constructor prevents instantiation

    /* BlockStates used in this class */
    private static final BlockState CAVE_AIR = Blocks.CAVE_AIR.defaultBlockState();
    private static final BlockState WATER = Blocks.WATER.defaultBlockState();
    private static final BlockState SAND = Blocks.SAND.defaultBlockState();
    private static final BlockState RED_SAND = Blocks.RED_SAND.defaultBlockState();
    private static final BlockState SANDSTONE = Blocks.SANDSTONE.defaultBlockState();
    private static final BlockState RED_SANDSTONE = Blocks.RED_SANDSTONE.defaultBlockState();
    private static final BlockState GRAVEL = Blocks.GRAVEL.defaultBlockState();
    private static final BlockState ANDESITE = Blocks.ANDESITE.defaultBlockState();

    public static Set<Block> carvableBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.PACKED_ICE);
    public static Set<Block> liquidCarvableBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.SAND, Blocks.GRAVEL, Blocks.WATER, Blocks.LAVA, Blocks.OBSIDIAN, Blocks.AIR, Blocks.CAVE_AIR, Blocks.PACKED_ICE);

    private static final ImmutableSet<BlockState> DEBUG_BLOCKS = ImmutableSet.of(Blocks.GOLD_BLOCK.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), Blocks.COBBLESTONE.defaultBlockState(), Blocks.REDSTONE_BLOCK.defaultBlockState(), Blocks.EMERALD_BLOCK.defaultBlockState(), Blocks.BRICKS.defaultBlockState());

    /**
     * Digs out the current block, default implementation removes stone, filler, and top block.
     * Sets the block to liquidBlockState if y is less then the liquidAltitude in the Config, and air other wise.
     * If setting to air, it also checks to see if we've broken the surface, and if so,
     * tries to make the floor the biome's top block.
     *
     * @param chunkIn          the chunk containing the block
     * @param blockPos         The block's position - can be with real (absolute) or chunk-local coordinates
     * @param airBlockState    the BlockState to use for air.
     * @param liquidBlockState the BlockState to use for liquids. May be null if in buffer zone between liquid regions
     * @param liquidAltitude   altitude at and below which air is replaced with liquidBlockState
     * @param replaceGravel    if floating gravel should be replaced with andesite
     * @param carvingMask      BitSet that keeps track of which blocks have already been dug.
     */
    public static void carveBlock(ChunkAccess chunkIn, BlockPos blockPos, BlockState airBlockState, BlockState liquidBlockState, int liquidAltitude, boolean replaceGravel, BitSet carvingMask) {
        // Mark block as processed - for use by features
        int bitIndex = (blockPos.getX() & 0xF) | ((blockPos.getZ() & 0xF) << 4) | (blockPos.getY() << 8);
        carvingMask.set(bitIndex);

        BlockPos blockPosAbove = blockPos.above();
        BlockPos blockPosBelow = blockPos.below();

        Biome biome = chunkIn.getBiomes().getNoiseBiome(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        BlockState biomeTopBlockState = biome.getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial();
        BlockState biomeFillerBlockState = biome.getGenerationSettings().getSurfaceBuilderConfig().getUnderMaterial();
        BlockState blockState = chunkIn.getBlockState(blockPos);
        BlockState blockStateAbove = chunkIn.getBlockState(blockPosAbove);
        BlockState blockStateBelow = chunkIn.getBlockState(blockPosBelow);

        // Only continue if the block is replaceable
        if (!canReplaceBlock(blockState, blockStateAbove) && blockState != biomeTopBlockState && blockState != biomeFillerBlockState) {
            return;
        }

        if (airBlockState == CAVE_AIR && blockPos.getY() <= liquidAltitude) { // Replace any block below the liquid altitude with the liquid block passed in
            if (liquidBlockState != null) {
                chunkIn.setBlockState(blockPos, liquidBlockState, false);
            }
        } else {
            // Check for adjacent water blocks to avoid breaking into lakes or oceans
            if (airBlockState == CAVE_AIR && isWaterAdjacent(chunkIn, blockPos)) return;

            // Adjust block below if block removed is biome top block
            if (blockState == biomeTopBlockState && canReplaceBlock(blockStateBelow, CAVE_AIR))
                chunkIn.setBlockState(blockPosBelow, biomeTopBlockState, false);

            // If we caused floating sand to form, replace it with sandstone
            if (blockStateAbove == SAND)
                chunkIn.setBlockState(blockPosAbove, SANDSTONE, false);
            else if (blockStateAbove == RED_SAND)
                chunkIn.setBlockState(blockPosAbove, RED_SANDSTONE, false);

            // Replace floating gravel with andesite, if enabled
            if (replaceGravel && blockStateAbove == GRAVEL)
                chunkIn.setBlockState(blockPosAbove, ANDESITE, false);

            // Replace this block with air, effectively "digging" it out
            chunkIn.setBlockState(blockPos, airBlockState, false);
        }
    }

    public static void carveBlock(ChunkAccess chunkIn, BlockPos blockPos, BlockState liquidBlockState, int liquidAltitude, boolean replaceGravel, BitSet carvingMask) {
        carveBlock(chunkIn, blockPos, Blocks.CAVE_AIR.defaultBlockState(), liquidBlockState, liquidAltitude, replaceGravel, carvingMask);
    }

    public static void carveBlock(ChunkAccess chunkIn, int x, int y, int z, BlockState liquidBlockState, int liquidAltitude, boolean replaceGravel, BitSet carvingMask) {
        carveBlock(chunkIn, new BlockPos(x, y, z), Blocks.CAVE_AIR.defaultBlockState(), liquidBlockState, liquidAltitude, replaceGravel, carvingMask);
    }

    public static void carveBlock(ChunkAccess chunkIn, int x, int y, int z, BlockState airBlockState, BlockState liquidBlockState, int liquidAltitude, boolean replaceGravel, BitSet carvingMask) {
        carveBlock(chunkIn, new BlockPos(x, y, z), airBlockState, liquidBlockState, liquidAltitude, replaceGravel, carvingMask);
    }

    /**
     * Counterpart to carveBlock() for flooded caves.
     * Places magma and obsidian randomly 1 block above liquidAltitude.
     *
     * @param chunkIn          the chunk containing the block
     * @param rand             Random used to place magma and obsidian.
     * @param blockPos         The block's position - can be with real (absolute) or chunk-local coordinates
     * @param liquidBlockState the BlockState to use for liquids. May be null if in buffer zone between liquid regions
     * @param liquidAltitude   altitude at and below which air is replaced with liquidBlockState
     * @param carvingMask      BitSet that keeps track of which blocks have already been dug.
     */
    public static void carveFloodedBlock(ChunkAccess chunkIn, Random rand, BlockPos.MutableBlockPos blockPos, BlockState liquidBlockState, int liquidAltitude, boolean replaceGravel, BitSet carvingMask) {
        // Mark block as processed - for use by features
        int bitIndex = (blockPos.getX() & 0xF) | ((blockPos.getZ() & 0xF) << 4) | (blockPos.getY() << 8);
        carvingMask.set(bitIndex);

        // Dig flooded block
        Biome biome = chunkIn.getBiomes().getNoiseBiome(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        BlockState biomeTopBlockState = biome.getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial();
        BlockState biomeFillerBlockState = biome.getGenerationSettings().getSurfaceBuilderConfig().getUnderMaterial();
        BlockState blockState = chunkIn.getBlockState(blockPos);
        BlockState blockStateAbove = chunkIn.getBlockState(blockPos.above());
        if (!canReplaceLiquidBlock(blockState, blockStateAbove) && blockState != biomeTopBlockState && blockState != biomeFillerBlockState) {
            return;
        }

        // Add magma and obsidian right above liquid altitude
        if (blockPos.getY() == liquidAltitude + 1) {
            float f = rand.nextFloat();
            if (f < 0.25f) {
                chunkIn.setBlockState(blockPos, Blocks.MAGMA_BLOCK.defaultBlockState(), false);
                chunkIn.getBlockTicks().scheduleTick(blockPos, Blocks.MAGMA_BLOCK, 0);
            } else {
                chunkIn.setBlockState(blockPos, Blocks.OBSIDIAN.defaultBlockState(), false);
            }
        }
        // Replace any block below the liquid altitude with the liquid block passed in
        else if (blockPos.getY() <= liquidAltitude) {
            if (liquidBlockState != null) {
                chunkIn.setBlockState(blockPos, liquidBlockState, false);
            }
        }
        // Else, normal carving
        else {
            chunkIn.setBlockState(blockPos, WATER, false);

            // Replace floating gravel with andesite, if enabled
            if (replaceGravel && blockStateAbove == GRAVEL)
                chunkIn.setBlockState(blockPos.above(), ANDESITE, false);
        }
    }
    public static void carveFloodedBlock(ChunkAccess chunkIn, Random rand, BlockPos.MutableBlockPos blockPos, BlockState liquidBlockState, int liquidAltitude, BitSet carvingMask) {
        carveFloodedBlock(chunkIn, rand, blockPos, liquidBlockState, liquidAltitude, false, carvingMask);
    }

    /**
     * DEBUG method for visualizing cave systems. Used as a replacement for the {@code digBlock} method if the
     * debugVisualizer config option is enabled.
     * @param chunkIn Chunk containing the block
     * @param blockPos block position
     * @param blockState The blockState to set dug out blocks to
     * @param digBlock true if the block should be "dug"
     */
    public static void debugCarveBlock(ChunkAccess chunkIn, BlockPos blockPos, BlockState blockState, boolean digBlock) {
        if (DEBUG_BLOCKS.contains(chunkIn.getBlockState(blockPos))) return;

        if (digBlock)
            chunkIn.setBlockState(blockPos, blockState, false);
        else
            chunkIn.setBlockState(blockPos, Blocks.AIR.defaultBlockState(), false);
    }

    public static void debugCarveBlock(ChunkAccess chunkIn, int x, int y, int z, BlockState blockState, boolean digBlock) {
        debugCarveBlock(chunkIn, new BlockPos(x, y, z), blockState, digBlock);
    }

    /**
     * Determines if the Block of a given BlockState is suitable to be replaced during cave generation.
     * Basically returns true for most common worldgen blocks (e.g. stone, dirt, sand), false if the block is air.
     * @param blockState the block's IBlockState
     * @param blockStateAbove the IBlockState of the block above this one
     * @return true if the blockState can be replaced
     */
    public static boolean canReplaceBlock(BlockState blockState, BlockState blockStateAbove) {
        Block block = blockState.getBlock();

        // Avoid damaging trees
        if (
            blockState.getMaterial() == Material.LEAVES ||
            blockState.getMaterial() == Material.WOOD
        )
            return false;

        // Avoid digging out under trees
        if (blockStateAbove.getMaterial() == Material.WOOD)
            return false;

        // This should hopefully avoid damaging villages
        if (
            block == Blocks.FARMLAND ||
            block == Blocks.DIRT_PATH
        ) {
            return false;
        }

        // Accept stone-like blocks added from other mods
        if (
            blockState.getMaterial() == Material.STONE ||
            blockState.getMaterial() == Material.GRASS ||
            blockState.getMaterial() == Material.CLAY ||
            blockState.getMaterial() == Material.DIRT
        )
            return true;

        // List of carvable blocks provided by vanilla
        if (carvableBlocks.contains(block))
            return true;

        // Only accept gravel and sand if water is not directly above it
        return (block == Blocks.SAND || block == Blocks.GRAVEL)
            && blockStateAbove.getMaterial() != Material.WATER;
    }

    public static boolean canReplaceLiquidBlock(BlockState blockState, BlockState blockStateAbove) {
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
            || block == Blocks.DIRT_PATH) {
            return false;
        }

        // Accept stone-like blocks added from other mods
        if (blockState.getMaterial() == Material.STONE)
            return true;

        // List of carvable blocks provided by vanilla
        if (liquidCarvableBlocks.contains(block))
            return true;

        return false;
    }

    private static boolean isWaterAdjacent(ChunkAccess chunkIn, BlockPos blockPos) {
        int localX = BetterCavesUtils.getLocal(blockPos.getX());
        int localZ = BetterCavesUtils.getLocal(blockPos.getZ());
        int y = blockPos.getY();

        return (y < 255 && chunkIn.getBlockState(blockPos.above()).getMaterial() == Material.WATER)
            || localZ > 0 && chunkIn.getBlockState(blockPos.north()).getMaterial() == Material.WATER
            || localX < 15 && chunkIn.getBlockState(blockPos.east()).getMaterial() == Material.WATER
            || localZ < 15 && chunkIn.getBlockState(blockPos.south()).getMaterial() == Material.WATER
            || localX > 0 && chunkIn.getBlockState(blockPos.west()).getMaterial() == Material.WATER;
    }
}