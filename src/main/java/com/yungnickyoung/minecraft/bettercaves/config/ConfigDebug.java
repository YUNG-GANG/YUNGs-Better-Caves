package com.yungnickyoung.minecraft.bettercaves.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class ConfigDebug {
    @ConfigEntry.Gui.Tooltip(count = 10)
    public boolean enableDebugVisualizer = false;
}