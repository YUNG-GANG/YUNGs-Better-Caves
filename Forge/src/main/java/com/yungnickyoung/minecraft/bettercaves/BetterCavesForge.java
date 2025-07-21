package com.yungnickyoung.minecraft.bettercaves;

import com.yungnickyoung.minecraft.bettercaves.module.BiomeModifierModuleForge;
import com.yungnickyoung.minecraft.bettercaves.module.CarverModuleForge;
import com.yungnickyoung.minecraft.bettercaves.module.ConfigModuleForge;
import net.minecraftforge.fml.common.Mod;

@Mod(BetterCavesCommon.MOD_ID)
public class BetterCavesForge {
    public BetterCavesForge() {
        BetterCavesCommon.init();
        ConfigModuleForge.init();
        CarverModuleForge.init();
        BiomeModifierModuleForge.init();
    }
}