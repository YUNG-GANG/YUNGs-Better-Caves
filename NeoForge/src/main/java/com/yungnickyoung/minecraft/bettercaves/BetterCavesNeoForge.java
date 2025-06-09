package com.yungnickyoung.minecraft.bettercaves;

import com.yungnickyoung.minecraft.bettercaves.module.CarverModuleNeoForge;
import com.yungnickyoung.minecraft.bettercaves.module.ConfigModuleNeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(BetterCavesCommon.MOD_ID)
public class BetterCavesNeoForge {
    public static IEventBus loadingContextEventBus;

    public BetterCavesNeoForge(IEventBus eventBus, ModContainer container) {
        BetterCavesNeoForge.loadingContextEventBus = eventBus;

        BetterCavesCommon.init();
        ConfigModuleNeoForge.init(container);
        CarverModuleNeoForge.init(eventBus);
    }
}