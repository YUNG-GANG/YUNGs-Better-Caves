package com.yungnickyoung.minecraft.bettercaves.module;

import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.worldgen.BetterCavesWorldCarver;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

public class CarverModuleFabric {
    public static final MapCodec<BetterCavesWorldCarver> BETTER_CAVE = register("better_cave", BetterCavesWorldCarver.CODEC);

    private static <T extends WorldCarver> MapCodec<T> register(String path, MapCodec<T> carver) {
        return Registry.register(BuiltInRegistries.CARVER_TYPE, BetterCavesCommon.id(path), carver);
    }

    public static void init() {
    }
}
