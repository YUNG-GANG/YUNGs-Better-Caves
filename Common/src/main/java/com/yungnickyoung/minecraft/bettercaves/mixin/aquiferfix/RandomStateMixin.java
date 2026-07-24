package com.yungnickyoung.minecraft.bettercaves.mixin.aquiferfix;

import com.yungnickyoung.minecraft.bettercaves.duck.ILiquidRegionsProvider;
import com.yungnickyoung.minecraft.bettercaves.worldgen.liquidregion.LiquidRegions;
import com.yungnickyoung.minecraft.bettercaves.worldgen.liquidregion.LiquidRegionsController;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Create LiquidRegions and attach them to RandomState. RandomState is preserved throughout world generation for a given dimension,
 * and generally stores similar state, so this is a sensible place to put LiquidRegions.
 */
@Mixin(RandomState.class)
public class RandomStateMixin implements ILiquidRegionsProvider {
    private @Shadow @Final    PositionalRandomFactory random;
    private @Unique @Nullable LiquidRegions           liquidRegions;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void bettercaves$setLiquidRegions(final NoiseGeneratorSettings settings,
                                              final HolderGetter<NormalNoise.NoiseParameters> noises,
                                              final long seed,
                                              final CallbackInfo ci) {
        var liquidRegionsSettings = LiquidRegionsController.getInstance().getSettings();
        if (liquidRegionsSettings != null) {
            var forkedRandom = this.random.fromHashOf(new ResourceLocation(ResourceLocation.DEFAULT_NAMESPACE, "liquid_regions"));
            this.liquidRegions = new LiquidRegions(forkedRandom, liquidRegionsSettings);
        }
    }

    @Override public @Nullable LiquidRegions bettercaves$getLiquidRegions() {
        return this.liquidRegions;
    }
}
