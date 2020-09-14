package com.yungnickyoung.minecraft.bettercaves.config.cave;

import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

public class ConfigCaves {
    @ConfigEntry.Category("Type 1 Caves")
    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Gui.Tooltip
    public ConfigCubicCave type1Caves = new ConfigCubicCave();

    @ConfigEntry.Category("Type 2 Caves")
    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Gui.Tooltip
    public ConfigSimplexCave type2Caves = new ConfigSimplexCave();

    @ConfigEntry.Category("Surface Caves")
    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Gui.Tooltip
    public ConfigSurfaceCave surfaceCaves = new ConfigSurfaceCave();

    @ConfigEntry.Category("Vanilla Caves")
    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Gui.Tooltip
    public ConfigVanillaCave vanillaCaves = new ConfigVanillaCave();

    @ConfigEntry.Gui.Tooltip(count = 2)
    public double caveSpawnChance = 100.0;

    @ConfigEntry.Gui.Tooltip(count = 5)
    public String caveRegionSize = "Small";

    @ConfigEntry.Gui.Tooltip(count = 8)
    public double caveRegionSizeCustomValue = 0.008;
}
