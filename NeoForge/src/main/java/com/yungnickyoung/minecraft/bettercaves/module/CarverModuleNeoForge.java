package com.yungnickyoung.minecraft.bettercaves.module;

import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.worldgen.BetterCavesWorldCarver;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CarverModuleNeoForge {
    private static final DeferredRegister<MapCodec<? extends WorldCarver>> CARVERS = DeferredRegister.create(
            BuiltInRegistries.CARVER_TYPE, BetterCavesCommon.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends WorldCarver>, MapCodec<BetterCavesWorldCarver>> BETTER_CAVE = CARVERS.register(
            "better_cave",
            () -> BetterCavesWorldCarver.CODEC
    );

    public static void init(IEventBus eventBus) {
        CARVERS.register(eventBus);
    }
}
