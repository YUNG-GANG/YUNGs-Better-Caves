package com.yungnickyoung.minecraft.bettercaves.config.cave;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class ConfigVanillaCave {
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
    public int caveBottom = 1;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
    public int caveTop = 80;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int caveDensity = 14;

    @ConfigEntry.Gui.Tooltip
    public int cavePriority = 0;
}
