package com.yungnickyoung.minecraft.bettercaves.mixin;

import com.yungnickyoung.minecraft.bettercaves.worldgen.context.AquiferContext;
import com.yungnickyoung.minecraft.bettercaves.worldgen.controller.LiquidRegionController;
import com.yungnickyoung.minecraft.bettercaves.worldgen.LiquidRegions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
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
//    @Unique
//    private ServerLevel serverLevel;

    @Inject(method = "computeSubstance",
            at = @At("RETURN"), cancellable = true)
    private void bettercaves$fixAquiferLiquids(DensityFunction.FunctionContext context, double d, CallbackInfoReturnable<BlockState> cir) {
        // Only modify aquifers in the overworld.
        // TODO - support other dimensions via config
        AquiferContext aquiferContext = AquiferContext.peek();
        if (aquiferContext == null) {
            return;
        } else {
            int i = 1;
        }
        ServerLevel serverLevel = AquiferContext.peek().getServerLevel();

        if (!serverLevel.dimension().location().equals(ResourceLocation.withDefaultNamespace("overworld"))) {
            return;
        }

        BlockState blockState = cir.getReturnValue();
        if (blockState == null || blockState.is(BlockTags.AIR)) {
            return; // Only modify liquids
        }

        ChunkPos chunkPos = new ChunkPos(new BlockPos(context.blockX(), context.blockY(), context.blockZ()));
        LiquidRegions liquidRegions = LiquidRegionController.getInstance().getLiquidRegionsForServerLevel(serverLevel);
        LiquidRegions.CacheData cacheData = liquidRegions.getLiquidBlocksForChunk(chunkPos);

        if (cacheData == null) {
//            BetterCavesCommon.LOGGER.info("NULL ({} {} {}) {}", context.blockX(), context.blockY(), context.blockZ(), chunkPos);
            return;
        }

        if (context.blockY() > cacheData.liquidAltitude()) {
            return; // Only modify if it's at or below the liquid altitude for this position
        }

        int localX = context.blockX() & 15;
        int localZ = context.blockZ() & 15;
        BlockState liquidBlock = cacheData.liquidBlocks()[localX][localZ];

        // Only modify if the block is different from the liquid block it should be
        if (liquidBlock != null && !blockState.is(liquidBlock.getBlock())) {
            cir.setReturnValue(liquidBlock);
        }
    }

//    @Unique
//    @Override
//    public ServerLevel getServerLevel() {
//        return this.serverLevel;
//    }
//
//    @Unique
//    @Override
//    public void setServerLevel(ServerLevel serverLevel) {
//        this.serverLevel = serverLevel;
//    }
}
