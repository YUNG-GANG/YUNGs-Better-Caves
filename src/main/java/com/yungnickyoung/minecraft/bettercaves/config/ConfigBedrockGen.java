package com.yungnickyoung.minecraft.bettercaves.config;

import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

@Config(name = "better-caves")
public class ConfigBedrockGen {
    @ConfigEntry.Gui.Tooltip(count = 2)
    public Boolean flattenBedrock = true;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
    public int bedrockWidth = 1;
}
