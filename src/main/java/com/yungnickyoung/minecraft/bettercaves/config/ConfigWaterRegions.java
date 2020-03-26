package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.common.config.Config;

public class ConfigWaterRegions {
    @Config.Name("Water Region Spawn Chance")
    @Config.Comment(
            "Percent chance of a region having water instead of lava at low altitudes." +
            "\nDefault: 40%")
    @Config.RangeDouble(min = 0, max = 100)
    @Config.RequiresWorldRestart
    public float waterRegionSpawnChance = 40;
}
