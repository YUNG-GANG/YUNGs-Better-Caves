package com.yungnickyoung.minecraft.bettercaves.module;

import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.worldgen.BetterCavesWorldCarver;
import com.yungnickyoung.minecraft.bettercaves.worldgen.BetterCavesWorldCarverConfig;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

public class CarverModule {
//    public static final WorldCarver<BetterCavesWorldCarverConfig> BETTER_CAVE = register("better_cave",
//            new BetterCavesWorldCarver(BetterCavesWorldCarverConfig.CODEC));

    public static void init() {}

    private static <C extends CarverConfiguration, F extends WorldCarver<C>> F register(String location, F carver) {
        return Registry.register(BuiltInRegistries.CARVER, BetterCavesCommon.id(location), carver);
    }

//    ResourceKey<ConfiguredWorldCarver<?>> BETTER_CAVE_KEY = createKey("better_cave");

//    private static ResourceKey<ConfiguredWorldCarver<?>> createKey(String location) {
//        return ResourceKey.create(Registries.CONFIGURED_CARVER, ResourceLocation.withDefaultNamespace(location));
//    }
}
