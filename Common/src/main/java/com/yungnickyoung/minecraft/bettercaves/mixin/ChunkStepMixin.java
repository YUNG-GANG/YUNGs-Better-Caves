package com.yungnickyoung.minecraft.bettercaves.mixin;

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

@Mixin(ChunkStep.class)
public class ChunkStepMixin {
    @Inject(method = "apply", at = @At("HEAD"))
    private void bettercaves$apply(WorldGenContext worldGenContext, StaticCache2D<GenerationChunkHolder> $$1, ChunkAccess $$2, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir) {
        AquiferContext.push(worldGenContext.level());
    }

    @Inject(method = "apply", at = @At("RETURN"))
    private void bettercaves$apply2(WorldGenContext worldGenContext, StaticCache2D<GenerationChunkHolder> $$1, ChunkAccess $$2, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir) {
        AquiferContext.pop();
    }
}
