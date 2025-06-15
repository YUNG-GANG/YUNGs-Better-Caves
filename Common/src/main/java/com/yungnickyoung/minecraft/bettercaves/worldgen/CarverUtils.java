package com.yungnickyoung.minecraft.bettercaves.worldgen;

import com.google.common.collect.ImmutableSet;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;

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
     * Sets the block to liquidBlockState if y is less then the liquidAltitude in the Config, and air otherwise.
     * If setting to air, it also checks to see if we've broken the surface, and if so,
     * tries to make the floor the biome's top block.
     *
     * @param chunkAccess          the chunk containing the block
     * @param blockPos         The block's position - can be with real (absolute) or chunk-local coordinates
     * @param airBlockState    the BlockState to use for air.
     * @param liquidBlockState the BlockState to use for liquids. May be null if in buffer zone between liquid regions
     * @param liquidAltitude   altitude at and below which air is replaced with liquidBlockState
     * @param carvingMask      BitSet that keeps track of which blocks have already been dug.
     */
    public static void carveBlock(CarverConfiguration config, ChunkAccess chunkAccess, BlockPos blockPos, BlockState airBlockState,
                                  BlockState liquidBlockState, int liquidAltitude, CarvingMask carvingMask, Aquifer aquifer) {
        // Mark block as processed - for use by features
        carvingMask.set(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        BlockPos blockPosAbove = blockPos.above();
        BlockPos blockPosBelow = blockPos.below();

//        Biome biome = chunkAccess.getBiomes().getNoiseBiome(blockPos.getX(), blockPos.getY(), blockPos.getZ());
//        BlockState biomeTopBlockState = biome.getGenerationSettings().getSurfaceBuilderConfig().getTop();
//        BlockState biomeFillerBlockState = biome.getGenerationSettings().getSurfaceBuilderConfig().getUnder();
        BlockState blockState = chunkAccess.getBlockState(blockPos);
        BlockState blockStateAbove = chunkAccess.getBlockState(blockPosAbove);
        BlockState blockStateBelow = chunkAccess.getBlockState(blockPosBelow);

        // Only continue if the block is replaceable
        if (!canReplaceBlock(config, blockState, blockStateAbove)) {
            return;
        }

        if (airBlockState.isAir() && blockPos.getY() <= liquidAltitude) { // Replace any block below the liquid altitude with the liquid block passed in
            if (liquidBlockState != null) {
                chunkAccess.setBlockState(blockPos, liquidBlockState, false);
            }
        } else {
            // Check for adjacent water blocks to avoid breaking into lakes or oceans
//            if (airBlockState == CAVE_AIR && isWaterAdjacent(chunkAccess, blockPos)) return;

            // Adjust block below if block removed is biome top block
//            if (blockState == biomeTopBlockState && canReplaceBlock(blockStateBelow, CAVE_AIR))
//                chunkAccess.setBlockState(blockPosBelow, biomeTopBlockState, false);

            // If we caused floating sand to form, replace it with sandstone
//            if (blockStateAbove == SAND)
//                chunkAccess.setBlockState(blockPosAbove, SANDSTONE, false);
//            else if (blockStateAbove == RED_SAND)
//                chunkAccess.setBlockState(blockPosAbove, RED_SANDSTONE, false);

            // Replace floating gravel with andesite, if enabled
//            if (replaceGravel && blockStateAbove == GRAVEL)
//                chunkAccess.setBlockState(blockPosAbove, ANDESITE, false);

            // Replace this block with air, effectively "digging" it out
            BlockState newBlockState = aquifer.computeSubstance(new DensityFunction.SinglePointContext(
                        blockPos.getX(), blockPos.getY(), blockPos.getZ()), 0.0);

            if (newBlockState == null) {
                return;
            }

            chunkAccess.setBlockState(blockPos, newBlockState, false);
            if (aquifer.shouldScheduleFluidUpdate() && !newBlockState.getFluidState().isEmpty()) {
                chunkAccess.markPosForPostprocessing(blockPos);
            }

//            if ($$8.isTrue()) {
//                $$6.setWithOffset($$5, Direction.DOWN);
//                if ($$2.getBlockState($$6).is(Blocks.DIRT)) {
//                    $$0.topMaterial($$3, $$2, $$6, !$$10.getFluidState().isEmpty()).ifPresent($$2x -> {
//                        $$2.setBlockState($$6, $$2x, false);
//                        if (!$$2x.getFluidState().isEmpty()) {
//                            $$2.markPosForPostprocessing($$6);
//                        }
//                    });
//                }
//            }
        }
    }

    public static void carveBlock(CarverConfiguration config, ChunkAccess chunkAccess, BlockPos blockPos, BlockState liquidBlockState, int liquidAltitude, CarvingMask carvingMask, Aquifer aquifer) {
        carveBlock(config, chunkAccess, blockPos, Blocks.AIR.defaultBlockState(), liquidBlockState, liquidAltitude, carvingMask, aquifer);
    }

    /**
     * Counterpart to carveBlock() for flooded caves.
     * Places magma and obsidian randomly 1 block above liquidAltitude.
     *
     * @param chunkAccess          the chunk containing the block
     * @param rand             Random used to place magma and obsidian.
     * @param blockPos         The block's position - can be with real (absolute) or chunk-local coordinates
     * @param liquidBlockState the BlockState to use for liquids. May be null if in buffer zone between liquid regions
     * @param liquidAltitude   altitude at and below which air is replaced with liquidBlockState
     * @param carvingMask      BitSet that keeps track of which blocks have already been dug.
     */
    public static void carveFloodedBlock(CarverConfiguration config, ChunkAccess chunkAccess, Random rand, BlockPos.MutableBlockPos blockPos,
                                         BlockState liquidBlockState, int liquidAltitude, boolean replaceGravel, CarvingMask carvingMask) {
        // Mark block as processed - for use by features
        carvingMask.set(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        // Dig flooded block
//        Holder<Biome> biome = chunkAccess.getNoiseBiome(blockPos.getX(), blockPos.getY(), blockPos.getZ());
//        BlockState biomeTopBlockState = biome.getGenerationSettings().getSurfaceBuilderConfig().getTop();
//        BlockState biomeFillerBlockState = biome.getGenerationSettings().getSurfaceBuilderConfig().getUnder();
        BlockState blockState = chunkAccess.getBlockState(blockPos);
        BlockState blockStateAbove = chunkAccess.getBlockState(blockPos.above());
        if (!canReplaceLiquidBlock(config, blockState, blockStateAbove)) {
            return;
        }

        if (liquidBlockState != null && liquidBlockState.getBlock() == Blocks.LAVA && blockPos.getY() == liquidAltitude + 1) { // Add magma and obsidian right above lava
            float f = rand.nextFloat();
            if (f < 0.25f) {
                chunkAccess.setBlockState(blockPos, Blocks.MAGMA_BLOCK.defaultBlockState(), false);
//                chunkAccess.scheduleTick(blockPos, Blocks.MAGMA_BLOCK, 0);
            } else {
                chunkAccess.setBlockState(blockPos, Blocks.OBSIDIAN.defaultBlockState(), false);
            }
        } else if (liquidBlockState != null && blockPos.getY() <= liquidAltitude) { // Replace any block below the liquid altitude with the liquid block passed in
            chunkAccess.setBlockState(blockPos, liquidBlockState, false);
        } else { // Normal carving
            chunkAccess.setBlockState(blockPos, WATER, false);

            int x = blockPos.getX();
            int y = blockPos.getY();
            int z = blockPos.getZ();

            // Schedule fluid tick if along chunk boundary. Helps avoid weird floating water
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                int j = blockPos.getX() + direction.getStepX();
                int k = blockPos.getZ() + direction.getStepZ();
                if (j >> 4 != chunkAccess.getPos().x || k >> 4 != chunkAccess.getPos().z || chunkAccess.getBlockState(blockPos.set(j, y, k)).isAir()) {
                    chunkAccess.setBlockState(blockPos, WATER, false);
//                    chunkAccess.getFluidsToBeTicked().scheduleTick(blockPos, WATER.getFluidState().getFluid(), 0);
                    break;
                }
            }

            blockPos.set(x, y, z); // Reset block pos to original pos

            // Replace floating gravel with andesite, if enabled
            if (replaceGravel && blockStateAbove == GRAVEL)
                chunkAccess.setBlockState(blockPos.above(), ANDESITE, false);
        }
    }

    public static void carveFloodedBlock(CarverConfiguration config, ChunkAccess chunkIn, Random rand, BlockPos.MutableBlockPos blockPos,
                                         BlockState liquidBlockState, int liquidAltitude, CarvingMask carvingMask) {
        carveFloodedBlock(config, chunkIn, rand, blockPos, liquidBlockState, liquidAltitude, false, carvingMask);
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
    public static boolean canReplaceBlock(CarverConfiguration config, BlockState blockState, BlockState blockStateAbove) {
        Block block = blockState.getBlock();

        // Avoid damaging trees
//        if (blockState.getMaterial() == Material.LEAVES || blockState.getMaterial() == Material.WOOD)
//            return false;

        // Avoid digging out under trees
//        if (blockStateAbove.getMaterial() == Material.WOOD)
//            return false;

        // This should hopefully avoid damaging villages
//        if (block == Blocks.FARMLAND || block == Blocks.GRASS_PATH) {
//            return false;
//        }

        // Accept stone-like blocks added from other mods
//        if (blockState.getMaterial() == Material.ROCK || blockState.getMaterial() == Material.CLAY || blockState.getMaterial() == Material.EARTH)
//            return true;

        // List of carvable blocks provided by vanilla
//        if (carvableBlocks.contains(block))
//            return true;

        // Only accept gravel and sand if water is not directly above it
//        return (block == Blocks.SAND || block == Blocks.GRAVEL) && blockStateAbove.getMaterial() != Material.WATER;
        return blockState.is(config.replaceable);
    }

    public static boolean canReplaceLiquidBlock(CarverConfiguration config, BlockState blockState, BlockState blockStateAbove) {
        Block block = blockState.getBlock();

        // Avoid damaging trees
//        if (blockState.getMaterial() == Material.LEAVES || blockState.getMaterial() == Material.WOOD)
//            return false;

        // Avoid digging out under trees
//        if (blockStateAbove.getMaterial() == Material.WOOD)
//            return false;

        // This should hopefully avoid damaging villages
//        if (block == Blocks.FARMLAND
//                || block == Blocks.GRASS_PATH) {
//            return false;
//        }

        // Accept stone-like blocks added from other mods
//        if (blockState.getMaterial() == Material.ROCK)
//            return true;

        return blockState.is(config.replaceable);
    }

    private static boolean isWaterAdjacent(ChunkAccess chunkAccess, BlockPos blockPos) {
        int localX = BetterCavesUtils.getLocal(blockPos.getX());
        int localZ = BetterCavesUtils.getLocal(blockPos.getZ());
        int y = blockPos.getY();

        return (y < 255 && chunkAccess.getBlockState(blockPos.above()).is(Blocks.WATER))
                || localZ > 0 && chunkAccess.getBlockState(blockPos.north()).is(Blocks.WATER)
                || localX < 15 && chunkAccess.getBlockState(blockPos.east()).is(Blocks.WATER)
                || localZ < 15 && chunkAccess.getBlockState(blockPos.south()).is(Blocks.WATER)
                || localX > 0 && chunkAccess.getBlockState(blockPos.west()).is(Blocks.WATER);
    }
}
