package com.yungnickyoung.minecraft.bettercaves.mixin;

import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = NoiseRouterData.class)
public class NoiseRouterDataMixin {
//    @Inject(method = "underground", at = @At("HEAD"), cancellable = true)
//    private static void bettercaves$removeVanillaNoiseCaves(HolderGetter<DensityFunction> $$0,
//                                                            HolderGetter<NormalNoise.NoiseParameters> $$1,
//                                                            DensityFunction $$2,
//                                                            CallbackInfoReturnable<DensityFunction> cir) {
//        BetterCavesCommon.LOGGER.info("HELLO");
//        BetterCavesCommon.LOGGER.info(cir.getReturnValue());
//        cir.setReturnValue(DensityFunctions.constant(1000000.0D));
//    }
//
//    @Inject(method = "overworld", at = @At("RETURN"), cancellable = true)
//    private static void bettercaves$overworld(HolderGetter<DensityFunction> $$0, HolderGetter<NormalNoise.NoiseParameters> $$1, boolean $$2, boolean $$3, CallbackInfoReturnable<NoiseRouter> cir) {
//        BetterCavesCommon.LOGGER.info("OVERWORLDDD");
//        NoiseRouter retValue = cir.getReturnValue();
//        NoiseRouter newValue = new NoiseRouter(
//                retValue.barrierNoise(),
//                retValue.fluidLevelFloodednessNoise(),
//                retValue.fluidLevelSpreadNoise(),
//                retValue.lavaNoise(),
//                retValue.temperature(),
//                retValue.vegetation(),
//                retValue.continents(),
//                retValue.erosion(),
//                retValue.depth(),
//                retValue.ridges(),
//                retValue.initialDensityWithoutJaggedness(),
//                DensityFunctions.constant(1000000), //retValue.finalDensity(),
//                retValue.veinGap(),
//                retValue.veinRidged(),
//                retValue.veinGap()
//        );
//        cir.setReturnValue(newValue);
//    }
}
