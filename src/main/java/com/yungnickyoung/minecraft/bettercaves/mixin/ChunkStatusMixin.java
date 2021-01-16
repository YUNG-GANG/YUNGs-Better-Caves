/*
 * Many thanks to TelepathicGrunt and Won-Ton for helping me with this!
 */
package com.yungnickyoung.minecraft.bettercaves.mixin;

import com.mojang.datafixers.util.Either;
import com.yungnickyoung.minecraft.bettercaves.world.carver.CarvingContext;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.ServerWorldLightManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Mixin(ChunkStatus.class)
public class ChunkStatusMixin {
    @Inject(method = "doGenerationWork", at = @At(value = "HEAD"))
    private void pushCarvingContext(ServerWorld worldIn,
                                ChunkGenerator chunkGeneratorIn,
                                TemplateManager templateManagerIn,
                                ServerWorldLightManager lightManager,
                                Function<IChunk, CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>>> loadingFunction,
                                List<IChunk> chunks,
                                CallbackInfoReturnable<CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>>> cir) {
        // Limit context to CARVERS (air carving stage)
        if (Objects.equals(this, ChunkStatus.CARVERS)) {
            // Refer to ChunkStatus::doGenerationWork for method of getting the main chunk.
            CarvingContext.push(worldIn, chunks.get(chunks.size() / 2));
        }
    }
}