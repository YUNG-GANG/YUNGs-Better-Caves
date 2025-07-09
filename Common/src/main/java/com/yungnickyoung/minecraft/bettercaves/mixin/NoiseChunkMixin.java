package com.yungnickyoung.minecraft.bettercaves.mixin;

import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseChunk.class)
public class NoiseChunkMixin {
    @Inject(method = "forChunk", at = @At("RETURN"))
    private static void bettercaves$initAquifer1(ChunkAccess $$0, RandomState $$1, DensityFunctions.BeardifierOrMarker $$2, NoiseGeneratorSettings $$3, Aquifer.FluidPicker $$4, Blender $$5, CallbackInfoReturnable<NoiseChunk> cir) {
//        NoiseChunk noiseChunk = cir.getReturnValue();
//        ((IServerLevelProvider) noiseChunk.aquifer()).setServerLevel($$5.);
    }
}
