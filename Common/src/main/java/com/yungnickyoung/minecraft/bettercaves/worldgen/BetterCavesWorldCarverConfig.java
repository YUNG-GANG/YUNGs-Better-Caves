package com.yungnickyoung.minecraft.bettercaves.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;

public class BetterCavesWorldCarverConfig extends CarverConfiguration {
    public static final Codec<BetterCavesWorldCarverConfig> CODEC = RecordCodecBuilder.create($$0 -> $$0.group(
                            Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter($$0x -> $$0x.probability),
                            HeightProvider.CODEC.fieldOf("y").forGetter($$0x -> $$0x.y),
                            FloatProvider.CODEC.fieldOf("yScale").forGetter($$0x -> $$0x.yScale),
                            VerticalAnchor.CODEC.fieldOf("lava_level").forGetter($$0x -> $$0x.lavaLevel),
                            CarverDebugSettings.CODEC.optionalFieldOf("debug_settings", CarverDebugSettings.DEFAULT).forGetter($$0x -> $$0x.debugSettings),
                            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("replaceable").forGetter($$0x -> $$0x.replaceable)
                    )
                    .apply($$0, BetterCavesWorldCarverConfig::new)
    );

    public BetterCavesWorldCarverConfig(float $$0, HeightProvider $$1, FloatProvider $$2, VerticalAnchor $$3, CarverDebugSettings $$4, HolderSet<Block> $$5) {
//        super(
//                1.0F,
//                UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(180)),
//                UniformFloat.of(0.1F, 0.9F),
//                VerticalAnchor.aboveBottom(8),
//                CarverDebugSettings.DEFAULT,
//                HolderSet.direct(Holder.direct(Blocks.STONE))
//        );
        super($$0, $$1, $$2, $$3, $$4, $$5);
    }
}
