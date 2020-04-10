package com.yungnickyoung.minecraft.bettercaves.config.ravine;

import net.minecraftforge.common.config.Config;

public class ConfigRavine {
    @Config.Name("Enable Ravines")
    @Config.Comment(
        "Set to true to enable ravine generation.\n" +
            "Default: true")
    public boolean enableVanillaRavines = true;

    @Config.Name("Enable Flooded Ravines")
    @Config.Comment(
        "Set to true to enable flooded ravines in ocean biomes.\n" +
            "Default: true")
    public boolean enableFloodedRavines = true;
}
