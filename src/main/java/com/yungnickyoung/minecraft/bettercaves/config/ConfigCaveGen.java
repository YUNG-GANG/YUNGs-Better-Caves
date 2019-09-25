package com.yungnickyoung.minecraft.bettercaves.config;

import com.yungnickyoung.minecraft.bettercaves.config.cave.*;
import com.yungnickyoung.minecraft.bettercaves.enums.CaveBiomeSize;
import net.minecraftforge.common.config.Config;

public class ConfigCaveGen {
    @Config.Name("Type 1 Caves (Cubic)")
    @Config.Comment("Parameters used in the generation of caves generated with cubic noise. Does not include the" +
            " large caverns found at low altitudes.")
    public ConfigCubicCave cubicCave = new ConfigCubicCave();

    @Config.Name("Type 2 Caves (Simplex)")
    @Config.Comment("Parameters used in the generation of caves generated with simplex noise. Does not include the" +
            " large caverns found at low altitudes.")
    public ConfigSimplexCave simplexCave = new ConfigSimplexCave();

    @Config.Name("Lava Caverns")
    @Config.Comment("Parameters used in the generation of Lava Caverns found at low altitudes. These are caverns" +
            " where the floor is predominantly lava.")
    public ConfigLavaCavern lavaCavern = new ConfigLavaCavern();

    @Config.Name("Floored Caverns")
    @Config.Comment("Parameters used in the generation of Floored Caverns. These have much more ground to walk on" +
            " than Lava Caverns.")
    public ConfigFlooredCavern flooredCavern = new ConfigFlooredCavern();

    @Config.Name("Water Biomes")
    @Config.Comment("Parameters used in the generation of water-based caves and caverns. These are similar to " +
            "the regular Better Caves and Caverns, but with water instead of lava.")
    public ConfigWaterBiomes waterBiomes = new ConfigWaterBiomes();

    @Config.Name("Vanilla Caves")
    @Config.Comment("Settings controlling vanilla Minecraft cave generation.")
    public ConfigVanillaCave vanillaCave = new ConfigVanillaCave();

    @Config.Name("Cave Biome Size")
    @Config.Comment("Determines how large cave biomes are. Controls how long a cave system of a certain cave type" +
            " extends before intersecting with a cave system of another type. Larger Biome Size = more " +
            "cave interconnectivity.")
    @Config.RequiresWorldRestart
    public CaveBiomeSize caveBiomeSize = CaveBiomeSize.Large;

    @Config.Name("Cavern Biome Size")
    @Config.Comment("Determines how large cavern biomes are. This controls the average size of caverns.")
    @Config.RequiresWorldRestart
    public CaveBiomeSize cavernBiomeSize = CaveBiomeSize.Small;

    @Config.Name("Cave Surface Cutoff Depth")
    @Config.Comment("The depth from a given point on the surface (or the Max Cave Altitude, whichever is " +
            "lower) at which caves start to close off. Decrease this to create more cave openings in the sides of " +
            "mountains. Increase to create less above-surface openings.")
    @Config.RequiresWorldRestart
    public int surfaceCutoff = 10;

    @Config.Name("Max Cave Altitude")
    @Config.Comment("The maximum altitude at which caves can generate")
    @Config.RequiresWorldRestart
    public int maxCaveAltitude = 128;

    @Config.Name("Whitelisted Dimension IDs")
    @Config.Comment("List of ID's of dimensions that will have Better Caves")
    @Config.RequiresWorldRestart
    public int[] whitelistedDimensionIDs = {0};
}
