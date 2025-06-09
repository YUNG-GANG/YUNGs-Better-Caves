package com.yungnickyoung.minecraft.bettercaves.duck;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;

public interface IBetterBuilder {
    IBetterBuilder removeCarver(GenerationStep.Carving step, ResourceKey<ConfiguredWorldCarver<?>> key);
}
