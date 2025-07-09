package com.yungnickyoung.minecraft.bettercaves.mixin;

import com.yungnickyoung.minecraft.bettercaves.mixin.accessor.StructureManagerAccessor;
import com.yungnickyoung.minecraft.bettercaves.worldgen.context.AquiferContext;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseBasedChunkGenerator.class)
public abstract class NoiseBasedChunkGeneratorMixin {
    @Shadow
    protected abstract NoiseChunk createNoiseChunk(ChunkAccess $$0, StructureManager $$1, Blender $$2, RandomState $$3);

    @Inject(method = "doFill", at = @At("HEAD"))
    private void bettercaves$doFill(Blender $$0, StructureManager structureManager, RandomState $$2, ChunkAccess $$3, int $$4, int $$5, CallbackInfoReturnable<ChunkAccess> cir) {
        AquiferContext aquiferContext = AquiferContext.peek();
        if (aquiferContext == null) {
            LevelAccessor levelAccessor = ((StructureManagerAccessor) structureManager).getLevel();
            if (levelAccessor instanceof WorldGenRegion region) {
                AquiferContext.push(region.getLevel());
            } else {
                return;
            }
        } else {
            int i = 1;
        }
    }

    @Inject(method = "doFill", at = @At("RETURN"))
    private void bettercaves$doFill2(Blender $$0, StructureManager structureManager, RandomState $$2, ChunkAccess $$3, int $$4, int $$5, CallbackInfoReturnable<ChunkAccess> cir) {
        AquiferContext.pop();
    }

    @Inject(method = "createFluidPicker", at = @At("RETURN"), cancellable = true)
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

    /**
     * Attach the server level to the Aquifer, for use in AquiferMixin.
     */
//    @Inject(method = "applyCarvers", at = @At("HEAD"))
//    private void bettercaves$attachAquiferData1(WorldGenRegion worldGenRegion, long $$1, RandomState $$2, BiomeManager $$3, StructureManager $$4, ChunkAccess chunkAccess, GenerationStep.Carving $$6, CallbackInfo ci) {
//        NoiseChunk noiseChunk = chunkAccess.getOrCreateNoiseChunk(c -> this.createNoiseChunk(c, $$4, Blender.of(worldGenRegion), $$2));
//        Aquifer aquifer = noiseChunk.aquifer();
//        ((IServerLevelProvider) aquifer).setServerLevel(worldGenRegion.getLevel()); // Set the server level for AquiferMixin
//    }

    /**
     * Attach the server level to the Aquifer, for use in AquiferMixin.
     */
//    @Inject(method = "iterateNoiseColumn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/NoiseChunk;getInterpolatedState()Lnet/minecraft/world/level/block/state/BlockState;"),
//    locals = LocalCapture.CAPTURE_FAILHARD)
//    private void bettercaves$attachAquiferData2(LevelHeightAccessor $$0, RandomState $$1, int $$2, int $$3, MutableObject<NoiseColumn> $$4, Predicate<BlockState> $$5, CallbackInfoReturnable<OptionalInt> cir, @Local NoiseChunk noiseChunk) {
//        Aquifer aquifer = noiseChunk.aquifer();
//        ((IServerLevelProvider) aquifer).setServerLevel(worldGenRegion.getLevel()); // Set the server level for AquiferMixin
//    }
}
