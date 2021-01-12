/*
 * 100% credit goes to TelepathicGrunt for this!
 *
 * Check out his mods on CurseForge:
 * https://www.curseforge.com/members/telepathicgrunt/projects
 */

package com.yungnickyoung.minecraft.bettercaves.mixin;

import com.mojang.datafixers.util.Either;
import com.yungnickyoung.minecraft.bettercaves.world.IServerWorldHolder;
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
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Mixin(ChunkStatus.class)
public class ChunkStatusMixin {
    @Inject(
        method = "doGenerationWork(Lnet/minecraft/world/server/ServerWorld;Lnet/minecraft/world/gen/ChunkGenerator;Lnet/minecraft/world/gen/feature/template/TemplateManager;Lnet/minecraft/world/server/ServerWorldLightManager;Ljava/util/function/Function;Ljava/util/List;)Ljava/util/concurrent/CompletableFuture;",
        at = @At(value = "HEAD")
    )
    private void setCarverWorld(ServerWorld worldIn,
                                ChunkGenerator chunkGeneratorIn,
                                TemplateManager templateManagerIn,
                                ServerWorldLightManager lightManager,
                                Function<IChunk, CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>>> loadingFunction,
                                List<IChunk> chunks,
                                CallbackInfoReturnable<CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>>> cir
    ) {
//        if (((ChunkStatus)(Object)this).getParent() == ChunkStatus.CARVERS) {
            // Set world into all chunks for the carver to be able to obtain
            chunks.forEach(chunk -> {
                ((IServerWorldHolder)chunk).setServerWorld(worldIn);
            });
//        }
    }
}