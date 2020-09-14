package com.yungnickyoung.minecraft.bettercaves.config;

import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

public class ConfigBetterCaves {
    @ConfigEntry.Category("Bedrock Generation")
    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Gui.Tooltip
    public ConfigBedrockGen bedrockSettings = new ConfigBedrockGen();

    @ConfigEntry.Category("Underground Generation")
    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Gui.Tooltip
    public ConfigUndergroundGen undergroundSettings = new ConfigUndergroundGen();

    @ConfigEntry.Category("DEBUG Settings")
    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Gui.Tooltip
    public ConfigDebug debugSettings = new ConfigDebug();

    @ConfigEntry.Gui.Tooltip(count = 6)
    public String whitelistedDimensions = "[minecraft:overworld]";

    @ConfigEntry.Gui.Tooltip(count = 3)
    public boolean enableGlobalWhitelist = false;
}
