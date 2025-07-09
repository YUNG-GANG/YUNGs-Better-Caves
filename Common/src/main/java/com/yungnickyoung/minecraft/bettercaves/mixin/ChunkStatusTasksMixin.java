package com.yungnickyoung.minecraft.bettercaves.mixin;

import com.yungnickyoung.minecraft.bettercaves.worldgen.context.CavegenContext;
import com.yungnickyoung.minecraft.bettercaves.worldgen.controller.LiquidRegionController;
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

    /**
     * Injects generation of liquid regions as early as possible.
     * These need to be generated before Aquifers are generated - see @AquiferMixin.
     */
    @Inject(method = "generateStructureStarts", at = @At("HEAD"))
    private static void bettercaves$generateLiquidRegions(WorldGenContext worldGenContext, ChunkStep $$1, StaticCache2D<GenerationChunkHolder> $$2, ChunkAccess chunkAccess, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir) {
        LiquidRegionController.getInstance().getLiquidRegionsForServerLevel(worldGenContext.level()).generateLiquidBlocksForChunk(chunkAccess);
//        AquiferContext.push(worldGenContext.level());

//        NoiseChunk noiseChunk = chunkAccess.getOrCreateNoiseChunk(c -> this.createNoiseChunk(c, $$4, Blender.of(worldGenRegion), $$2));
//        Aquifer aquifer = noiseChunk.aquifer();
//        ((IServerLevelProvider) aquifer).setServerLevel(worldGenRegion.getLevel()); // Set the server level for AquiferMixin
    }
}
