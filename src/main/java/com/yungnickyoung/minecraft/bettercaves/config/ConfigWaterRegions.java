package com.yungnickyoung.minecraft.bettercaves.config;

import com.yungnickyoung.minecraft.bettercaves.enums.RegionSize;
import net.minecraftforge.common.config.Config;

public class ConfigWaterRegions {
    @Config.Name("Water Region Spawn Chance")
    @Config.Comment(
        "Percent chance of a region having water instead of lava at low altitudes." +
            "\nDefault: 40%")
    @Config.RangeDouble(min = 0, max = 100)
    @Config.RequiresWorldRestart
    public float waterRegionSpawnChance = 40;

    @Config.Name("Water Region Size")
    @Config.Comment(
        "Determines how large water regions are.\n" +
        "Default: Medium (recommended).")
    @Config.RequiresWorldRestart
    public RegionSize waterRegionSize = RegionSize.Medium;

    @Config.Name("Water Region Size Custom Value")
    @Config.Comment(
        "Custom value for water region size. Smaller value = larger regions. This value is very sensitive to change.\n" +
        "    ONLY WORKS IF Water Region Size IS Custom.\n" +
        "    Provided values:\n" +
        "        Small: 0.008\n" +
        "        Medium: 0.004\n" +
        "        Large: 0.0028\n" +
        "        ExtraLarge: 0.001\n" +
        "Default: 0.004")
    @Config.RangeDouble(min = 0, max = .05)
    @Config.RequiresWorldRestart
    public float waterRegionCustomSize = 0.004f;
}
