package com.yungnickyoung.minecraft.bettercaves.mixin;

import com.yungnickyoung.minecraft.bettercaves.worldgen.context.CavegenContext;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoiseBasedChunkGenerator.class)
public class NoiseBasedChunkGeneratorMixin {
    @Inject(method = "generateCarvers", at = @At("HEAD"))
    private void bettercaves$attachCarverContext(ChunkAccess chunk, Blender blender, NoiseChunk noiseChunk,
                                                  RandomState randomState, BiomeManager biomeManager,
                                                  @Nullable net.minecraft.server.level.WorldGenRegion carverBiomeRegion,
                                                  MaterialRule materialRule, CallbackInfo ci) {
        CavegenContext.attach(chunk, noiseChunk.aquifer());
    }
}
