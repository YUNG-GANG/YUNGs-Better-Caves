package com.yungnickyoung.minecraft.bettercaves.mixin.aquiferfix;

import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.duck.ILiquidRegionsProvider;
import com.yungnickyoung.minecraft.bettercaves.worldgen.context.AquiferContext;
import com.yungnickyoung.minecraft.bettercaves.worldgen.liquidregion.LiquidRegions;
import com.yungnickyoung.minecraft.bettercaves.worldgen.liquidregion.LiquidRegionsController;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Apply LiquidRegions changes to aquifers.
 * This is where all the LiquidRegions stuff actually has an effect.
 * The LiquidRegions are passed in through {@link AquiferContext}.
 */
@NullMarked
@Mixin(Aquifer.NoiseBasedAquifer.class)
public class AquiferMixin implements ILiquidRegionsProvider {
    @Unique
    private @Nullable LiquidRegions liquidRegions;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void bettercaves$setLiquidRegions(final NoiseChunk noiseChunk,
                                              final ChunkPos pos,
                                              final NoiseRouter router,
                                              final PositionalRandomFactory positionalRandomFactory,
                                              final int minBlockY,
                                              final int yBlockSize,
                                              final Aquifer.FluidPicker globalFluidPicker,
                                              final CallbackInfo ci) {
        var context = AquiferContext.get();
        if (context == null) {
            BetterCavesCommon.LOGGER.error("Failed to fetch the AquiferContext. Liquid Regions for YUNG's Better Caves may not generate properly.");
            BetterCavesCommon.LOGGER.error("This is a mod compatibility issue. Please report it to the Better Caves GitHub issue tracker!");
        } else if (context.liquidRegions() != null) {
            this.liquidRegions = context.liquidRegions();
        }
    }

    /**
     * Replaces Aquifer-generated liquids at and below the liquidAltitude with the proper Better Caves liquid,
     * as defined by the LiquidRegions data for the current chunk.
     */
    @Inject(method = "computeSubstance", at = @At("RETURN"), cancellable = true)
    private void bettercaves$fixAquiferLiquids(DensityFunction.FunctionContext context,
                                               double d,
                                               CallbackInfoReturnable<@Nullable BlockState> cir) {
        // Only modify aquifers if LiquidRegions are enabled for the current level
        if (this.liquidRegions == null) {
            return;
        }

        BlockState blockState = cir.getReturnValue();
        if (blockState == null) return; // Only modify air or liquid blocks

        // Fetch the LiquidRegions data for the current chunk.
        // If the LiquidRegions data has been generated before, that cached result will be reused.
        ChunkPos chunkPos = new ChunkPos(new BlockPos(context.blockX(), context.blockY(), context.blockZ()));
        LiquidRegions.CacheData cacheData = this.liquidRegions.getOrCreateLiquidBlocksForChunk(chunkPos);
        if (cacheData == null) {
            BetterCavesCommon.LOGGER.warn("No LiquidRegions data found for chunk {} in AquiferMixin, this should not happen!", chunkPos);
            return;
        }

        if (context.blockY() > cacheData.liquidAltitude()) {
            return; // Only modify if it's at or below the liquid altitude for this position
        }

        int localX = context.blockX() & 15;
        int localZ = context.blockZ() & 15;
        BlockState liquidBlock = cacheData.liquidBlocks()[localX][localZ];

        // Only modify if the block is different from the liquid block it should be
        if (liquidBlock == null || !blockState.is(liquidBlock.getBlock())) {
            cir.setReturnValue(liquidBlock);
        }
    }

    @Override public @Nullable LiquidRegions bettercaves$getLiquidRegions() {
        return this.liquidRegions;
    }
}
