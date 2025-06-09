package com.yungnickyoung.minecraft.bettercaves.module;

import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.worldgen.BetterCavesWorldCarver;
import com.yungnickyoung.minecraft.bettercaves.worldgen.BetterCavesWorldCarverConfig;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

public class CarverModuleFabric {
    public static final WorldCarver<BetterCavesWorldCarverConfig> BETTER_CAVE = register("better_cave",
            new BetterCavesWorldCarver(BetterCavesWorldCarverConfig.CODEC));

    private static <C extends CarverConfiguration, F extends WorldCarver<C>> F register(String path, F carver) {
        return Registry.register(BuiltInRegistries.CARVER, BetterCavesCommon.id(path), carver);
    }

    public static void init() {
    }
}
