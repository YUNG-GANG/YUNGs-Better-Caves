package com.yungnickyoung.minecraft.bettercaves.mixin;

import com.yungnickyoung.minecraft.bettercaves.worldgen.context.CavegenContext;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Pushes the ServerLevel into the {@link CavegenContext} at the start of the carving phase of chunk gen,
 * for superflat worlds.
 */
@Mixin(FlatLevelSource.class)
public class FlatLevelSourceMixin {
    @Inject(method = "applyCarvers", at = @At("HEAD"))
    private void bettercaves$attachCavegenContextSuperflat(WorldGenRegion worldGenRegion, long par2, RandomState par3, BiomeManager par4, StructureManager par5, ChunkAccess par6, GenerationStep.Carving par7, CallbackInfo ci) {
        CavegenContext.push(worldGenRegion.getLevel());
    }
}
