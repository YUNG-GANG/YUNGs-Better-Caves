package com.yungnickyoung.minecraft.bettercaves;

import com.yungnickyoung.minecraft.bettercaves.module.ConfigModuleForge;
import net.minecraftforge.fml.common.Mod;

@Mod(BetterCavesCommon.MOD_ID)
public class BetterCavesForge {
    public BetterCavesForge() {
        BetterCavesCommon.init();
        ConfigModuleForge.init();
    }
}