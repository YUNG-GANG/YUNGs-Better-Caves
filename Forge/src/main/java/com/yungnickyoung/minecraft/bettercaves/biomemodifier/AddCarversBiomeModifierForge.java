package com.yungnickyoung.minecraft.bettercaves.biomemodifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

public class AddCarversBiomeModifierForge implements BiomeModifier {
    public static Codec<AddCarversBiomeModifierForge> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(modifier -> modifier.biomes),
            ConfiguredWorldCarver.LIST_CODEC.fieldOf("carvers").forGetter(modifier -> modifier.carvers)
    ).apply(builder, AddCarversBiomeModifierForge::new));

    private final HolderSet<Biome> biomes;
    private final HolderSet<ConfiguredWorldCarver<?>> carvers;

    public AddCarversBiomeModifierForge(HolderSet<Biome> biomes, HolderSet<ConfiguredWorldCarver<?>> carvers) {
        this.biomes = biomes;
        this.carvers = carvers;
    }

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (biomes.contains(biome) && phase == Phase.ADD) {
            carvers.forEach(carver -> builder.getGenerationSettings().addCarver(GenerationStep.Carving.AIR, carver));
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}
