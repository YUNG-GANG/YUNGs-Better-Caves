package com.yungnickyoung.minecraft.bettercaves.mixin;

import com.yungnickyoung.minecraft.bettercaves.duck.IBetterBuilder;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BiomeDefaultFeatures.class, priority = 2000)
public class BiomeDefaultFeaturesMixin {
//    @Inject(method = "addDefaultCarversAndLakes", at = @At("RETURN"))
//    private static void bettercaves$adjustDefaultCarversAndLakes(BiomeGenerationSettings.Builder builder, CallbackInfo ci) {
//        if (builder instanceof IBetterBuilder betterBuilder) {
//            betterBuilder.removeCarver(GenerationStep.Carving.AIR, Carvers.CAVE);
//            betterBuilder.removeCarver(GenerationStep.Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND);
//            betterBuilder.removeCarver(GenerationStep.Carving.AIR, Carvers.CANYON);
//        }
//    }
}
