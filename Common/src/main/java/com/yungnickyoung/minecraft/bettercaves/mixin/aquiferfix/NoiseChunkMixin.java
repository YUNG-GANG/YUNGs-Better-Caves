package com.yungnickyoung.minecraft.bettercaves.mixin.aquiferfix;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.yungnickyoung.minecraft.bettercaves.duck.ILiquidRegionsProvider;
import com.yungnickyoung.minecraft.bettercaves.worldgen.context.AquiferContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.densityfunction.DensitySamplerSet;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Wrap creation of Aquifers to add LiquidRegions, if this dimension has them.
 *
 * @see AquiferMixin
 */
@Mixin(NoiseChunk.class)
public class NoiseChunkMixin {
    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/Aquifer$Config;create(Lnet/minecraft/world/level/levelgen/densityfunction/DensitySamplerSet;Lnet/minecraft/world/level/levelgen/PositionalRandomFactory;Lnet/minecraft/world/level/levelgen/densityfunction/DensityVolume;Lnet/minecraft/world/level/levelgen/Aquifer$FluidPicker;)Lnet/minecraft/world/level/levelgen/Aquifer;"))
    private Aquifer bettercaves$setAquiferContext(final Aquifer.Config config,
                                                   final DensitySamplerSet cachingSamplers,
                                                   final PositionalRandomFactory positionalRandomFactory,
                                                   final DensityVolume volume,
                                                   final Aquifer.FluidPicker fluidRule,
                                                   final Operation<Aquifer> original,
                                                   final RandomState randomState) {
        var liquidRegions = ((ILiquidRegionsProvider) (Object) randomState).bettercaves$getLiquidRegions();
        if (liquidRegions == null) {
            return AquiferContext.callWithoutRegions(() -> original.call(config, cachingSamplers, positionalRandomFactory, volume, fluidRule));
        } else {
            return AquiferContext.call(liquidRegions, () -> original.call(config, cachingSamplers, positionalRandomFactory, volume, fluidRule));
        }
    }
}
