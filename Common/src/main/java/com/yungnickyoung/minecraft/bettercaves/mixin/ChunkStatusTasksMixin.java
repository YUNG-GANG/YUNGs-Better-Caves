package com.yungnickyoung.minecraft.bettercaves.mixin;

import com.yungnickyoung.minecraft.bettercaves.worldgen.CavegenContext;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatusTasks;
import net.minecraft.world.level.chunk.status.ChunkStep;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(ChunkStatusTasks.class)
public class ChunkStatusTasksMixin {
    @Inject(method = "generateCarvers", at = @At("HEAD"))
    private static void bettercaves$attachCavegenContext(WorldGenContext worldGenContext, ChunkStep $$1, StaticCache2D<GenerationChunkHolder> $$2, ChunkAccess $$3, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir) {
        CavegenContext.push(worldGenContext.level());
    }
}
