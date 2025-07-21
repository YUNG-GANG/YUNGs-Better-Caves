package com.yungnickyoung.minecraft.bettercaves.mixin.aquiferfix;

import com.mojang.datafixers.util.Either;
import com.yungnickyoung.minecraft.bettercaves.worldgen.context.AquiferContext;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

/**
 * Mixin to push and pop the AquiferContext at the start and end of every chunk generation step.
 * This should ensure that the AquiferContext is available for aquifer-related operations
 * at all times during chunk generation (except when new threads are created dynamically - see {@link NoiseBasedChunkGeneratorMixin}).
 */
@Mixin(ChunkStatus.class)
public class ChunkStatusMixin {
    @Inject(method = "generate", at = @At("HEAD"))
    private void bettercaves$pushAquiferContext(Executor $$0, ServerLevel serverLevel, ChunkGenerator $$2, StructureTemplateManager $$3, ThreadedLevelLightEngine $$4, Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> $$5, List<ChunkAccess> $$6, CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> cir) {
        AquiferContext.push(serverLevel);
    }

    @Inject(method = "generate", at = @At("RETURN"))
    private void bettercaves$popAquiferContext(Executor $$0, ServerLevel $$1, ChunkGenerator $$2, StructureTemplateManager $$3, ThreadedLevelLightEngine $$4, Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> $$5, List<ChunkAccess> $$6, CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> cir) {
        AquiferContext.pop();
    }
}
