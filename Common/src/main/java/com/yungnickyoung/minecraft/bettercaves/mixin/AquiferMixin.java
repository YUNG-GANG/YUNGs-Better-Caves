package com.yungnickyoung.minecraft.bettercaves.mixin;

import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.worldgen.ExperimentalLiquidRegions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Aquifer.NoiseBasedAquifer.class)
public class AquiferMixin {
    @Inject(method = "computeSubstance",
            at = @At("RETURN"), cancellable = true)
    private void bettercaves$fixAquiferLiquids(DensityFunction.FunctionContext context, double d, CallbackInfoReturnable<BlockState> cir) {
        BlockState blockState = cir.getReturnValue();
        if (blockState == null || blockState.is(Blocks.AIR)) return; // Only modify liquids

        ChunkPos chunkPos = new ChunkPos(new BlockPos(context.blockX(), context.blockY(), context.blockZ()));
        ExperimentalLiquidRegions.CacheData cacheData = ExperimentalLiquidRegions.getInstance().cache.get(chunkPos);

        if (cacheData == null) {
            BetterCavesCommon.LOGGER.info("NULL ({} {} {}) {}", context.blockX(), context.blockY(), context.blockZ(), chunkPos);
            return;
        }

        if (context.blockY() > cacheData.liquidAltitude()) {
            return;
        }

        // Only modify if it's at or below the liquid altitude for this position
        int localX = context.blockX() & 15;
        int localZ = context.blockZ() & 15;
        BlockState liquidBlock = cacheData.liquidBlocks()[localX][localZ];

        // Only modify if the block is different from the liquid block it should be
        if (liquidBlock != null && !blockState.is(liquidBlock.getBlock())) {
            cir.setReturnValue(liquidBlock);
        }
    }
}
