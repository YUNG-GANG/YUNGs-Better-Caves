package com.yungnickyoung.minecraft.bettercaves.config;

import com.yungnickyoung.minecraft.bettercaves.config.cave.*;
import com.yungnickyoung.minecraft.bettercaves.config.cavern.ConfigCaverns;

public class ConfigUndergroundGen {
    public final ConfigCaves caves;
    public final ConfigCaverns caverns;


    public ConfigUndergroundGen() {
        caves = new ConfigCaves();
        caverns = new ConfigCaverns();
    }
}
