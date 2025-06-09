package com.yungnickyoung.minecraft.bettercaves.config;

import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name= BetterCavesCommon.MOD_ID + "-fabric-1_21")
public class BCConfigFabric implements ConfigData {
    @ConfigEntry.Category("Better Caves")
    @ConfigEntry.Gui.TransitiveObject
    public ConfigGeneralFabric general = new ConfigGeneralFabric();
}
