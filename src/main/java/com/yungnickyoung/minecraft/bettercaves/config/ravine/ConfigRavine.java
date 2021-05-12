package com.yungnickyoung.minecraft.bettercaves.config.ravine;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class ConfigRavine {
    @ConfigEntry.Gui.Tooltip
    public boolean enableRavines = true;

    @ConfigEntry.Gui.Tooltip
    public boolean enableFloodedRavines = true;
}
