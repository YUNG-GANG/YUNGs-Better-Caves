package com.yungnickyoung.minecraft.bettercaves.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class ConfigMisc {
    @ConfigEntry.Gui.Tooltip
    public int liquidAltitude = 10;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public String lavaBlock = "minecraft:lava";

    @ConfigEntry.Gui.Tooltip(count = 2)
    public String waterBlock = "minecraft:water";

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean preventCascadingGravel = true;

    @ConfigEntry.Gui.Tooltip
    public boolean enableFloodedUnderground = true;

    @ConfigEntry.Gui.Tooltip(count = 3)
    public boolean overrideSurfaceDetection = false;
}
