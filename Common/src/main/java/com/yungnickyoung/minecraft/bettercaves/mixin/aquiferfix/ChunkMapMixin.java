package com.yungnickyoung.minecraft.bettercaves.mixin.aquiferfix;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.yungnickyoung.minecraft.bettercaves.worldgen.liquidregion.LiquidRegionsController;
import net.minecraft.core.HolderGetter;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Wrap creation of the RandomState to attach LiquidRegions, if this dimension has them.
 *
 * @see RandomStateMixin
 */
@Mixin(ChunkMap.class)
public class ChunkMapMixin {
    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/RandomState;create(Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;Lnet/minecraft/core/HolderGetter;J)Lnet/minecraft/world/level/levelgen/RandomState;"))
    private RandomState bettercaves$setLiquidRegionsContext(final NoiseGeneratorSettings settings,
                                                            final HolderGetter<NormalNoise.NoiseParameters> noises,
                                                            final long seed,
                                                            final Operation<RandomState> original,
                                                            final ServerLevel level) {
        return LiquidRegionsController.getInstance().withSettings(level, () -> original.call(settings, noises, seed));
    }
}
