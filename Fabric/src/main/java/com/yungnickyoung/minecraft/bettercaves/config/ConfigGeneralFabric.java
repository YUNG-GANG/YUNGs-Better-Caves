package com.yungnickyoung.minecraft.bettercaves.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class ConfigGeneralFabric {
    @ConfigEntry.Category("Liquid Regions")
    @ConfigEntry.Gui.CollapsibleObject
    public ConfigLiquidRegionsFabric liquidRegions = new ConfigLiquidRegionsFabric();

}
