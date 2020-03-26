package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.common.config.Config;

public class ConfigMisc {
    @Config.Name("Liquid Altitude")
    @Config.Comment("Lava (or water in water regions) spawns at and below this y-coordinate.")
    @Config.RangeInt(min = 0, max = 255)
    @Config.RequiresWorldRestart
    public int liquidAltitude = 10;

    @Config.Name("Lava Block")
    @Config.Comment(
            "The block used for lava generation at and below the Liquid Altitude.\n" +
            "    Defaults to regular lava if an invalid block is given.\n" +
            "Default: minecraft:lava")
    @Config.RequiresWorldRestart
    public String lavaBlock = "minecraft:lava";

    @Config.Name("Water Block")
    @Config.Comment(
            "The block used for water generation in water caves/caverns at and below the Liquid Altitude.\n" +
            "    Defaults to regular water if an invalid block is given.\n" +
            "Default: minecraft:water")
    @Config.RequiresWorldRestart
    public String waterBlock = "minecraft:water";

    @Config.Name("Prevent Cascading Gravel")
    @Config.Comment(
            "Replace naturally generated floating gravel on the ocean floor with andesite.\n" +
            "    Can prevent lag due to cascading gravel falling into caverns under the ocean.\n" +
            "Default: true")
    @Config.RequiresWorldRestart
    public boolean replaceFloatingGravel = true;

    @Config.Name("Override Surface Detection")
    @Config.Comment(
            "Ignores surface detection for closing off caves and caverns, forcing them to spawn\n" +
            "    up until their max height. Useful for Nether-like dimensions with no real \"surface\".\n" +
            "Default: false")
    @Config.RequiresWorldRestart
    public boolean overrideSurfaceDetection = false;
}
