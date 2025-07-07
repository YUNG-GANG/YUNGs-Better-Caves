package com.yungnickyoung.minecraft.bettercaves;

import com.yungnickyoung.minecraft.bettercaves.module.BiomeModificationsModuleFabric;
import com.yungnickyoung.minecraft.bettercaves.module.CarverModuleFabric;
import com.yungnickyoung.minecraft.bettercaves.module.ConfigModuleFabric;
import net.fabricmc.api.ModInitializer;

public class BetterCavesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        BetterCavesCommon.init();
        ConfigModuleFabric.init();
        CarverModuleFabric.init();
        BiomeModificationsModuleFabric.init();
    }
}
