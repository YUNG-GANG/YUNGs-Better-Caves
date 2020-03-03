package com.yungnickyoung.minecraft.bettercaves.config.cave;

import com.yungnickyoung.minecraft.bettercaves.enums.RegionSize;
import net.minecraftforge.common.config.Config;

public class ConfigCaves {
    @Config.Name("Type 1 Caves")
    @Config.Comment("Parameters used in the generation of caves made with cubic noise. Type 1 Caves are a little " +
            "less spacious than Type 2 Caves, and have more long, winding tunnels instead of large rooms. Does not " +
            "affect caverns found at low altitudes.")
    public ConfigCubicCave cubicCave = new ConfigCubicCave();

    @Config.Name("Type 2 Caves")
    @Config.Comment("Parameters used in the generation of caves made with simplex noise. Type 2 Caves tend to have " +
            "more open, spacious rooms than Type 1 Caves, with shorter winding passages. Does not include the " +
            "large caverns found at low altitudes.")
    public ConfigSimplexCave simplexCave = new ConfigSimplexCave();

    @Config.Name("Vanilla Caves")
    @Config.Comment("Settings controlling vanilla Minecraft cave generation.")
    public ConfigVanillaCave vanillaCave = new ConfigVanillaCave();

    @Config.Name("Cave Region Size")
    @Config.Comment("Determines how large cave regions are. Controls how long a cave system of a certain cave type" +
            " extends before intersecting with a cave system of another type. Larger Biome Size = more " +
            "cave interconnectivity for a given area, but possibly less variation. Medium is recommended.")
    @Config.RequiresWorldRestart
    public RegionSize caveRegionSize = RegionSize.Medium;

    @Config.Name("Cave Region Size Custom Value")
    @Config.Comment("Custom value for cave region size. Only works if Cave Region Size is set to Custom. " +
            "Smaller value = larger regions. This value is very sensitive to change. \nProvided values:\n" +
            "Small: 0.01\n" +
            "Medium: 0.008\n" +
            "Large: 0.004\n" +
            "ExtraLarge: 0.002")
    @Config.RangeDouble(min = 0, max = .05)
    @Config.RequiresWorldRestart
    public float customRegionSize = 0.008f;

    @Config.Name("Cave Surface Cutoff Depth")
    @Config.Comment("The depth from a given point on the surface (or the Max Cave Altitude, whichever is " +
            "lower) at which caves start to close off. Decrease this to create more cave openings in the sides of " +
            "mountains. Increase to create less above-surface openings.")
    @Config.RangeInt(min = 0, max = 255)
    @Config.RequiresWorldRestart
    public int surfaceCutoff = 10;

    @Config.Name("Max Cave Altitude")
    @Config.Comment("The maximum altitude at which caves can generate")
    @Config.RangeInt(min = 0, max = 255)
    @Config.RequiresWorldRestart
    public int maxCaveAltitude = 128;
}
