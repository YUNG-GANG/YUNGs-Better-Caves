package com.yungnickyoung.minecraft.bettercaves.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseBasedChunkGenerator.class)
public class NoiseBasedChunkGeneratorMixin {
    @Inject(method = "createFluidPicker",
            at = @At("RETURN"), cancellable = true)
    private static void bettercaves$fixFluidPicker(NoiseGeneratorSettings settings, CallbackInfoReturnable<Aquifer.FluidPicker> cir) {
//        Aquifer.FluidPicker retVal = cir.getReturnValue();
//
//        cir.setReturnValue((x, y, z) -> {
//            ChunkPos chunkPos = new ChunkPos(new BlockPos(x, y, z));
//            LiquidRegionController.CacheData cacheData = LiquidRegionController.LIQUID_CACHE.get(chunkPos);
//
//            // Only modify if it's at or below the liquid altitude for this position
//            if (cacheData != null && y <= cacheData.liquidAltitude()) {
//                BlockState liquidBlock = cacheData.liquidBlocks()[x & 15][z & 15];
//                return new Aquifer.FluidStatus(cacheData.liquidAltitude(), liquidBlock);
//            } else {
//                return retVal.computeFluid(x, y, z);
//            }
//        });
    }
}
