package com.yungnickyoung.minecraft.bettercaves.mixin.aquiferfix;

import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.worldgen.context.AquiferContext;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;

/**
 * Mixin to push and pop the AquiferContext at the start and end of every chunk generation step.
 * This should ensure that the AquiferContext is available for aquifer-related operations
 * at all times during chunk generation (except when new threads are created dynamically - see {@link NoiseBasedChunkGeneratorMixin}).
 */
@Mixin(ChunkStatus.class)
public class ChunkStatusMixin {

    @Mutable
    @Shadow
    @Final
    private ChunkStatus.GenerationTask generationTask;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void bettercaves$wrapAquiferContext(ChunkStatus $$0, int $$1, boolean $$2, EnumSet $$3, ChunkStatus.ChunkType $$4, ChunkStatus.GenerationTask $$5, ChunkStatus.LoadingTask $$6, CallbackInfo ci) {
        ChunkStatus.GenerationTask originalTask = this.generationTask;
        this.generationTask = (chunkStatus, executor, serverLevel, chunkGnerator, structureTemplateManager, lightEngine, var7, chunks, chunk) -> {
            try {
                AquiferContext.push(serverLevel);
                return originalTask.doWork(chunkStatus, executor, serverLevel, chunkGnerator, structureTemplateManager, lightEngine, var7, chunks, chunk);
            } finally {
                AquiferContext.pop();
            }
        };
        BetterCavesCommon.LOGGER.debug("WRAPPED GENERATION TASK FOR CHUNK STATUS");
    }
}
