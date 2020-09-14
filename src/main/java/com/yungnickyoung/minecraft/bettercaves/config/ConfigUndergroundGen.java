package com.yungnickyoung.minecraft.bettercaves.config;

import com.yungnickyoung.minecraft.bettercaves.config.cave.ConfigCaves;
import com.yungnickyoung.minecraft.bettercaves.config.cavern.ConfigCaverns;
import com.yungnickyoung.minecraft.bettercaves.config.ravine.ConfigRavine;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

public class ConfigUndergroundGen {
    @ConfigEntry.Category("Caves")
    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Gui.Tooltip
    public ConfigCaves caves = new ConfigCaves();

    @ConfigEntry.Category("Caverns")
    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Gui.Tooltip(count = 2)
    public ConfigCaverns caverns = new ConfigCaverns();

    @ConfigEntry.Category("Water Regions")
    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Gui.Tooltip
    public ConfigWaterRegions waterRegions = new ConfigWaterRegions();

    @ConfigEntry.Category("Ravines")
    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Gui.Tooltip
    public ConfigRavine ravines = new ConfigRavine();

    @ConfigEntry.Category("Miscellaneous")
    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Gui.Tooltip
    public ConfigMisc miscellaneous = new ConfigMisc();
}
