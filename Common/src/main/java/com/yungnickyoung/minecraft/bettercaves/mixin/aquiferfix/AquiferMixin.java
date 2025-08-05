package com.yungnickyoung.minecraft.bettercaves.mixin.aquiferfix;

import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.worldgen.context.AquiferContext;
import com.yungnickyoung.minecraft.bettercaves.worldgen.liquidregion.LiquidRegions;
import com.yungnickyoung.minecraft.bettercaves.worldgen.liquidregion.LiquidRegionsController;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Aquifer.NoiseBasedAquifer.class)
public class AquiferMixin {
    /**
     * Replaces Aquifer-generated liquids at and below the liquidAltitude with the proper Better Caves liquid,
     * as defined by the LiquidRegions data for the current chunk.
     */
    @Inject(method = "computeSubstance", at = @At("RETURN"), cancellable = true)
    private void bettercaves$fixAquiferLiquids(DensityFunction.FunctionContext context, double d, CallbackInfoReturnable<BlockState> cir) {
        // Grab the AquiferContext from the current thread and fetch the ServerLevel from it
        AquiferContext aquiferContext = AquiferContext.peek();
        if (aquiferContext == null) {
            BetterCavesCommon.LOGGER.warn("AquiferContext is null in AquiferMixin, this should not happen!");
            return;
        }
        ServerLevel serverLevel = aquiferContext.getServerLevel();

        // Only modify aquifers if LiquidRegions are enabled for the current level
        if (!LiquidRegionsController.getInstance().hasSettingsForLevel(serverLevel)) {
            return;
        }

        BlockState blockState = cir.getReturnValue();
        if (blockState == null) return; // Only modify air or liquid blocks

        // Fetch the (previously generated) LiquidRegions data for the current chunk.
        // If the cached LiquidRegions data is missing for some reason, it will be generated again.
        ChunkPos chunkPos = new ChunkPos(new BlockPos(context.blockX(), context.blockY(), context.blockZ()));
        LiquidRegions liquidRegions = LiquidRegionsController.getInstance().getLiquidRegionsForServerLevel(serverLevel);
        LiquidRegions.CacheData cacheData = liquidRegions.getOrCreateLiquidBlocksForChunk(chunkPos);
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
}
