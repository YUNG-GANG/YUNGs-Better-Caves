package com.yungnickyoung.minecraft.bettercaves.worldgen.carver;

import com.google.common.collect.ImmutableSet;
import com.yungnickyoung.minecraft.bettercaves.worldgen.BetterCavesWorldCarverConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;

public abstract class AbstractCarver {
    protected final CarverSettings settings;

    public abstract int getSpawnWeight();
    public abstract int getTopY();

    private final ImmutableSet<BlockState> DEBUG_BLOCKS = ImmutableSet.of(
            Blocks.GOLD_BLOCK.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(),
            Blocks.COBBLESTONE.defaultBlockState(), Blocks.REDSTONE_BLOCK.defaultBlockState(),
            Blocks.EMERALD_BLOCK.defaultBlockState(), Blocks.BRICKS.defaultBlockState());

    public AbstractCarver(CarverSettings settings) {
        this.settings = settings;
    }

    /**
     * Digs out the current block, default implementation removes stone, filler, and top block.
     * Sets the block to liquidBlockState if y is less then the liquidAltitude in the Config, and air otherwise.
     * If setting to air, it also checks to see if we've broken the surface, and if so,
     * tries to make the floor the biome's top block.
     *
     * @param config           The CarverConfiguration.
     * @param chunkAccess      The chunk containing the block
     * @param blockPos         The block's position to carve.
     * @param airBlockState    The BlockState to use for air.
     * @param liquidBlockState The BlockState to use for liquids. May be null if in buffer zone between liquid regions
     * @param carvingMask      Keeps track of which blocks have already been carved.
     * @param aquifer          The Aquifer.
     */
    protected void carveBlock(BetterCavesWorldCarverConfig config, ChunkAccess chunkAccess, BlockPos blockPos, BlockState airBlockState,
                              BlockState liquidBlockState, CarvingMask carvingMask, Aquifer aquifer) {
        // Mark block as processed - for use by features
        carvingMask.set(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        // Only continue if the block is replaceable
        if (!chunkAccess.getBlockState(blockPos).is(config.misc.replaceable())) {
            return;
        }

        if (airBlockState.isAir() && blockPos.getY() <= settings.getLiquidAltitude()) { // Replace any block below the liquid altitude with the liquid block passed in
            if (liquidBlockState != null) {
                chunkAccess.setBlockState(blockPos, liquidBlockState, false);
            }
        } else {
            BlockState newBlockState = aquifer.computeSubstance(new DensityFunction.SinglePointContext(
                    blockPos.getX(), blockPos.getY(), blockPos.getZ()), 0.0);

            if (newBlockState == null) {
                return;
            }

            chunkAccess.setBlockState(blockPos, newBlockState, false);
            if (aquifer.shouldScheduleFluidUpdate() && !newBlockState.getFluidState().isEmpty()) {
                chunkAccess.markPosForPostprocessing(blockPos);
            }

            // TODO
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

    protected void carveBlock(BetterCavesWorldCarverConfig config, ChunkAccess chunkAccess, BlockPos blockPos, BlockState liquidBlockState, CarvingMask carvingMask, Aquifer aquifer) {
        carveBlock(config, chunkAccess, blockPos, Blocks.AIR.defaultBlockState(), liquidBlockState, carvingMask, aquifer);
    }

    /**
     * DEBUG method for visualizing cave systems. Used as a replacement for the {@code digBlock} method if the
     * debugVisualizer config option is enabled.
     *
     * @param chunkIn    Chunk containing the block
     * @param blockPos   block position
     * @param digBlock   true if the block should be "dug"
     */
    protected void debugCarveBlock(ChunkAccess chunkIn, BlockPos blockPos, boolean digBlock) {
        if (DEBUG_BLOCKS.contains(chunkIn.getBlockState(blockPos))) return;

        if (digBlock) {
            chunkIn.setBlockState(blockPos, this.settings.getDebugBlock(), false);
        } else {
            chunkIn.setBlockState(blockPos, Blocks.AIR.defaultBlockState(), false);
        }
    }
}