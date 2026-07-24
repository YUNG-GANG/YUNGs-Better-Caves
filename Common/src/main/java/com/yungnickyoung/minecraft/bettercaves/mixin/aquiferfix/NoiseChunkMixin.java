package com.yungnickyoung.minecraft.bettercaves.mixin.aquiferfix;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.yungnickyoung.minecraft.bettercaves.duck.ILiquidRegionsProvider;
import com.yungnickyoung.minecraft.bettercaves.worldgen.context.AquiferContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Wrap creation of Aquifers to add LiquidRegions, if this dimension has them.
 *
 * @see AquiferMixin
 */
@Mixin(NoiseChunk.class)
public class NoiseChunkMixin {
    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/Aquifer;create(Lnet/minecraft/world/level/levelgen/NoiseChunk;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/levelgen/NoiseRouter;Lnet/minecraft/world/level/levelgen/PositionalRandomFactory;IILnet/minecraft/world/level/levelgen/Aquifer$FluidPicker;)Lnet/minecraft/world/level/levelgen/Aquifer;"))
    private Aquifer bettercaves$setAquiferContext(final NoiseChunk noiseChunk,
                                                  final ChunkPos pos,
                                                  final NoiseRouter router,
                                                  final PositionalRandomFactory positionalRandomFactory,
                                                  final int minBlockY,
                                                  final int yBlockSize,
                                                  final Aquifer.FluidPicker fluidRule,
                                                  final Operation<Aquifer> original,
                                                  final int cellCountXZ,
                                                  final RandomState randomState) {
        var liquidRegions = ((ILiquidRegionsProvider) (Object) randomState).bettercaves$getLiquidRegions();
        if (liquidRegions == null) {
            return AquiferContext.callWithoutRegions(() -> original.call(noiseChunk, pos, router, positionalRandomFactory, minBlockY, yBlockSize, fluidRule));
        } else {
            return AquiferContext.call(liquidRegions, () -> original.call(noiseChunk, pos, router, positionalRandomFactory, minBlockY, yBlockSize, fluidRule));
        }
    }
}
