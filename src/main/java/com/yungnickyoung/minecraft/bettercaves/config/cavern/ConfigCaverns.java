package com.yungnickyoung.minecraft.bettercaves.config.cavern;

import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

public class ConfigCaverns {
    @ConfigEntry.Category("Liquid Caverns")
    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Gui.Tooltip(count = 2)
    public ConfigLiquidCavern liquidCaverns = new ConfigLiquidCavern();

    @ConfigEntry.Category("Floored Caverns")
    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Gui.Tooltip(count = 2)
    public ConfigFlooredCavern flooredCaverns = new ConfigFlooredCavern();

    @ConfigEntry.Gui.Tooltip(count = 2)
    public double cavernSpawnChance = 25.0;

    @ConfigEntry.Gui.Tooltip(count = 3)
    public String cavernRegionSize = "Small";

    @ConfigEntry.Gui.Tooltip(count = 8)
    public double cavernRegionSizeCustomValue = 0.01;
}
