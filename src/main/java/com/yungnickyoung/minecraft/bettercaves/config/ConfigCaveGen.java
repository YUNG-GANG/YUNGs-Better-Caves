package com.yungnickyoung.minecraft.bettercaves.config;

import com.yungnickyoung.minecraft.bettercaves.config.cave.*;
import net.minecraftforge.common.config.Config;

public class ConfigCaveGen {
    @Config.Name("Big Caves")
    @Config.Comment("Parameters used in the generation of big caves. Does not include the large caverns " +
            "found at low altitudes.")
    public ConfigBigSimplexCave bigSimplexCave = new ConfigBigSimplexCave();

    @Config.Name("Small Caves")
    @Config.Comment("Parameters used in the generation of small caves. Does not include the large caverns " +
            "found at low altitudes.")
    public ConfigSmallSimplexCave smallSimplexCave = new ConfigSmallSimplexCave();

    @Config.Name("Cramped Caves")
    @Config.Comment("Parameters used in the generation of cramped caves. Does not include the large caverns " +
            "found at low altitudes.")
    public ConfigCubicCave crampedCave = new ConfigCubicCave();

    @Config.Name("Simplex3 Caves")
    @Config.Comment("Parameters used in the generation of simplex3 caves. Does not include the large caverns " +
            "found at low altitudes.")
    public ConfigSimplex3Cave simplex3Cave = new ConfigSimplex3Cave();


    @Config.Name("Lava Caverns")
    @Config.Comment("Parameters used in the generation of Lava Caverns found at low altitudes.")
    public ConfigLavaCavern lavaCavern = new ConfigLavaCavern();

    @Config.Name("Floored Caverns")
    @Config.Comment("Parameters used in the generation of Floored Caverns.")
    public ConfigFlooredCavern flooredCavern = new ConfigFlooredCavern();

    @Config.Name("Cave Biome Size")
    @Config.Comment("Determines how large cave biomes are. This includes all caves and caverns of different types and" +
            "sizes.")
    @Config.RequiresWorldRestart
    public CaveBiomeSize caveBiomeSize = CaveBiomeSize.Medium;

    public enum CaveBiomeSize {
        Small, Medium, Large, ExtraLarge
    }
}
