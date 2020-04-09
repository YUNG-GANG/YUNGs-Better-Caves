package com.yungnickyoung.minecraft.bettercaves.config.cave;

import com.yungnickyoung.minecraft.bettercaves.enums.RegionSize;
import net.minecraftforge.common.config.Config;

public class ConfigCaves {
    @Config.Name("Type 1 Caves")
    @Config.Comment("Settings used in the generation of type 1 caves, which are more worm-like.")
    public ConfigCubicCave cubicCave = new ConfigCubicCave();

    @Config.Name("Type 2 Caves")
    @Config.Comment("Settings used in the generation of type 2 caves, which tend to be more open and spacious.")
    public ConfigSimplexCave simplexCave = new ConfigSimplexCave();

    @Config.Name("Surface Caves")
    @Config.Comment("Settings used in the generation of vanilla-like caves near the surface.")
    public ConfigSurfaceCave surfaceCave = new ConfigSurfaceCave();

    @Config.Name("Vanilla Caves")
    @Config.Comment("Settings controlling vanilla Minecraft cave generation.")
    public ConfigVanillaCave vanillaCave = new ConfigVanillaCave();

    @Config.Name("Cave Spawn Chance")
    @Config.Comment(
            "Percent chance of caves spawning in a given region.\n" +
            "Default: caves spawn in 100% of regions.")
    @Config.RangeDouble(min = 0, max = 100)
    @Config.RequiresWorldRestart
    public float caveSpawnChance = 100;

    @Config.Name("Cave Region Size")
    @Config.Comment(
            "Determines how large cave regions are.\n" +
            "    Controls how long a cave system of a certain cave type extends before intersecting with a cave system of another type.\n" +
            "    Larger = more cave interconnectivity for a given area, but less variation.\n" +
            "Default: Small (recommended).")
    @Config.RequiresWorldRestart
    public RegionSize caveRegionSize = RegionSize.Small;

    @Config.Name("Cave Region Size Custom Value")
    @Config.Comment(
            "Custom value for cave region size. Smaller value = larger regions. This value is very sensitive to change.\n" +
            "    ONLY WORKS IF Cave Region Size IS Custom.\n" +
            "    Provided values:\n" +
            "        Small: 0.008\n" +
            "        Medium: 0.005\n" +
            "        Large: 0.0032\n" +
            "        ExtraLarge: 0.001\n" +
            "Default: 0.008")
    @Config.RangeDouble(min = 0, max = .05)
    @Config.RequiresWorldRestart
    public float customRegionSize = 0.008f;
}
