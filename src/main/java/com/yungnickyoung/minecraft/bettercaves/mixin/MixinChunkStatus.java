/*
 * Many thanks to TelepathicGrunt and Won-Ton for helping me with this!
 */
package com.yungnickyoung.minecraft.bettercaves.mixin;

import com.mojang.datafixers.util.Either;
import com.yungnickyoung.minecraft.bettercaves.world.carver.CarvingContext;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Mixin(ChunkStatus.class)
public class MixinChunkStatus {
    @Inject(method = "runGenerationTask", at = @At(value = "HEAD"))
    private void pushCarvingContext(
        ServerWorld world,
        ChunkGenerator chunkGenerator,
        StructureManager structureManager,
        ServerLightingProvider lightingProvider,
        Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> loadingFunction,
        List<Chunk> chunks,
        CallbackInfoReturnable<CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> cir
    ) {
        // Limit context to CARVERS (air carving stage)
        if (Objects.equals(this, ChunkStatus.CARVERS)) {
            // Refer to ChunkStatus::runGenerationTask for method of getting the main chunk.
            CarvingContext.push(world, chunks.get(chunks.size() / 2));
        }
    }
}
