package com.yungnickyoung.minecraft.bettercaves.mixin;

import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.duck.IBetterBuilder;
import com.yungnickyoung.minecraft.bettercaves.mixin.accessor.BiomeGenerationSettingsBuilderAccessor;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Map;

@Mixin(BiomeGenerationSettings.PlainBuilder.class)
public class BiomeGenerationSettingsBuilderMixin implements IBetterBuilder {
    @Override
    @Unique
    public IBetterBuilder removeCarver(GenerationStep.Carving step, ResourceKey<ConfiguredWorldCarver<?>> key) {
        Map<GenerationStep.Carving, List<Holder<ConfiguredWorldCarver<?>>>> carvers = ((BiomeGenerationSettingsBuilderAccessor) this).getCarvers();
        if (carvers.containsKey(step)) {
            List<Holder<ConfiguredWorldCarver<?>>> carverList = carvers.get(step);
            if (carverList != null) {
                carverList.removeIf(carver -> carver.is(key));
                BetterCavesCommon.LOGGER.info("Removed carver {} from step {}", key, step);
            }
        }
        return this;
    }
}
