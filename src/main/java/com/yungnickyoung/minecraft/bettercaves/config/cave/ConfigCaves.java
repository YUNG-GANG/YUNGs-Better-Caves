package com.yungnickyoung.minecraft.bettercaves.config.cave;


public class ConfigCaves {
    public final ConfigCubicCave cubicCave;
    public final ConfigSimplexCave simplexCave;

    public ConfigCaves() {
        cubicCave = new ConfigCubicCave();
        simplexCave = new ConfigSimplexCave();
    }
}
