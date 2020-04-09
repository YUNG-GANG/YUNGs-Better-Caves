package com.yungnickyoung.minecraft.bettercaves.config.cave;

import net.minecraftforge.common.config.Config;

public class ConfigSurfaceCave {
    @Config.Name("Enable Surface Caves")
    @Config.Comment(
        "Set to true to enable vanilla-like caves which provide nice, natural-looking openings at the surface.\n" +
        "Default: true")
    public boolean enableSurfaceCaves = true;

    @Config.Name("Surface Cave Minimum Altitude")
    @Config.Comment(
        "The minimum y-coordinate at which surface caves can generate.\n" +
            "Default: 40")
    @Config.RangeInt(min = 0, max = 255)
    @Config.RequiresWorldRestart
    public int caveBottom = 40;

    @Config.Name("Surface Cave Maximum Altitude")
    @Config.Comment(
        "The maximum y-coordinate at which surface caves can generate.\n" +
            "Default: 128")
    @Config.RangeInt(min = 0, max = 255)
    @Config.RequiresWorldRestart
    public int caveTop = 128;

    @Config.Name("Surface Cave Density")
    @Config.Comment(
        "The density of surface caves. Higher = more caves, closer together. \n" +
            "Default: 10")
    @Config.RangeInt(min = 0, max = 100)
    @Config.RequiresWorldRestart
    public int caveDensity = 10;
}