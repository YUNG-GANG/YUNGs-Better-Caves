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

    @Config.Name("Lava Caverns")
    @Config.Comment("Parameters used in the generation of Lava Caverns found at low altitudes.")
    public ConfigLavaCavern lavaCavern = new ConfigLavaCavern();

    @Config.Name("Floored Caverns")
    @Config.Comment("Parameters used in the generation of Floored Caverns.")
    public ConfigFlooredCavern flooredCavern = new ConfigFlooredCavern();
}
