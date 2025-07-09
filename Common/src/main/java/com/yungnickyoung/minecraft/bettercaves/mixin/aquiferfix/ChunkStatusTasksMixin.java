package com.yungnickyoung.minecraft.bettercaves.mixin.aquiferfix;

import com.yungnickyoung.minecraft.bettercaves.worldgen.liquidregion.LiquidRegionsController;
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
    /**
     * Injects generation of liquid regions at the start of the first phase of chunk generation,
     * which is basically as early as possible.
     * These need to be generated before Aquifers are generated - see {@link AquiferMixin}.
     */
    @Inject(method = "generateStructureStarts", at = @At("HEAD"))
    private static void bettercaves$generateLiquidRegions(WorldGenContext worldGenContext, ChunkStep $$1, StaticCache2D<GenerationChunkHolder> $$2, ChunkAccess chunkAccess, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir) {
        LiquidRegionsController.getInstance()
                .getLiquidRegionsForServerLevel(worldGenContext.level())
                .getOrCreateLiquidBlocksForChunk(chunkAccess.getPos());
    }
}
