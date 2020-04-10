package com.yungnickyoung.minecraft.bettercaves.config.cave;

import net.minecraftforge.common.config.Config;

public class ConfigVanillaCave {
    @Config.Name("Vanilla Cave Minimum Altitude")
    @Config.Comment(
        "The minimum y-coordinate at which vanilla caves can generate.\n" +
         "Default: 8")
    @Config.RangeInt(min = 0, max = 255)
    @Config.RequiresWorldRestart
    public int caveBottom = 8;

    @Config.Name("Vanilla Cave Maximum Altitude")
    @Config.Comment(
        "The maximum y-coordinate at which vanilla caves can generate.\n" +
         "Default: 128")
    @Config.RangeInt(min = 0, max = 255)
    @Config.RequiresWorldRestart
    public int caveTop = 128;

    @Config.Name("Vanilla Cave Density")
    @Config.Comment(
        "The density of vanilla caves. Higher = more caves, closer together. \n" +
            "Default: 14 (value used in vanilla)")
    @Config.RangeInt(min = 0, max = 100)
    @Config.RequiresWorldRestart
    public int caveDensity = 14;

    @Config.Name("Vanilla Cave Priority")
    @Config.Comment(
        "Determines how frequently vanilla caves spawn. 0 = will not spawn at all.\n" +
            "Default: 0")
    @Config.RangeInt(min = 0, max = 10)
    @Config.RequiresWorldRestart
    public int cavePriority = 0;
}
