package com.yungnickyoung.minecraft.bettercaves.config;

import com.yungnickyoung.minecraft.bettercaves.config.cave.*;
import net.minecraftforge.common.config.Config;

public class ConfigCaveGen {
    @Config.Name("Type 1 Caves (Cubic)")
    @Config.Comment("Parameters used in the generation of caves generated with cubic noise. Does not include the" +
            " large caverns found at low altitudes.")
    public ConfigCubicCave cubicCave = new ConfigCubicCave();

    @Config.Name("Type 2 Caves (Simplex)")
    @Config.Comment("Parameters used in the generation of caves generated with simplex noise. Does not include the" +
            " large caverns found at low altitudes.")
    public ConfigSimplex3Cave simplexCave = new ConfigSimplex3Cave();

    @Config.Name("Lava Caverns")
    @Config.Comment("Parameters used in the generation of Lava Caverns found at low altitudes. These are caverns" +
            " where the floor is predominantly lava.")
    public ConfigLavaCavern lavaCavern = new ConfigLavaCavern();

    @Config.Name("Floored Caverns")
    @Config.Comment("Parameters used in the generation of Floored Caverns. These have much more ground to walk on" +
            " than Lava Caverns.")
    public ConfigFlooredCavern flooredCavern = new ConfigFlooredCavern();

    @Config.Name("Vanilla Caves")
    @Config.Comment("Settings controlling vanilla Minecraft cave generation.")
    public ConfigVanillaCave vanillaCave = new ConfigVanillaCave();

    @Config.Name("Cave Biome Size")
    @Config.Comment("Determines how large cave biomes are. This controls the average size of caverns. It also" +
            " determines how long a cave system of a certain cave type extends before intersecting with a cave" +
            " system of the other type.")
    @Config.RequiresWorldRestart
    public CaveBiomeSize caveBiomeSize = CaveBiomeSize.Medium;

    public enum CaveBiomeSize {
        Small, Medium, Large, ExtraLarge
    }
}
