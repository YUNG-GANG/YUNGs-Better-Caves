package com.yungnickyoung.minecraft.bettercaves.module;

import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.worldgen.BetterCavesWorldCarver;
import com.yungnickyoung.minecraft.bettercaves.worldgen.BetterCavesWorldCarverConfig;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CarverModuleForge {
    private static final DeferredRegister<WorldCarver<?>> CARVERS = DeferredRegister.create(
            ForgeRegistries.WORLD_CARVERS, BetterCavesCommon.MOD_ID);

    public static final RegistryObject<WorldCarver<?>> BETTER_CAVE = CARVERS.register("better_cave",
            () -> new BetterCavesWorldCarver(BetterCavesWorldCarverConfig.CODEC));

    public static void init() {
        CARVERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
