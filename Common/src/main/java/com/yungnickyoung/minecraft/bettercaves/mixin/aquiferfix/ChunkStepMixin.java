package com.yungnickyoung.minecraft.bettercaves.mixin.aquiferfix;

import com.yungnickyoung.minecraft.bettercaves.worldgen.context.AquiferContext;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStep;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

/**
 * Mixin to push and pop the AquiferContext at the start and end of every chunk generation step.
 * This should ensure that the AquiferContext is available for aquifer-related operations
 * at all times during chunk generation (except when new threads are created dynamically - see {@link NoiseBasedChunkGeneratorMixin}.
 */
@Mixin(ChunkStep.class)
public class ChunkStepMixin {
    @Inject(method = "apply", at = @At("HEAD"))
    private void bettercaves$pushAquiferContext(WorldGenContext worldGenContext, StaticCache2D<GenerationChunkHolder> $$1, ChunkAccess $$2, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir) {
        AquiferContext.push(worldGenContext.level());
    }

    @Inject(method = "apply", at = @At("RETURN"))
    private void bettercaves$popAquiferContext(WorldGenContext worldGenContext, StaticCache2D<GenerationChunkHolder> $$1, ChunkAccess $$2, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir) {
        AquiferContext.pop();
    }
}
