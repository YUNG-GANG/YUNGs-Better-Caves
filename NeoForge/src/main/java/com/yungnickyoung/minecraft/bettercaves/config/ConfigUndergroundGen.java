package com.yungnickyoung.minecraft.bettercaves.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ConfigUndergroundGen {
    public final ConfigCaves caves;
    //    public final ConfigCaverns caverns;
    public final ConfigWaterRegions waterRegions;
//    public final ConfigRavine ravines;
    public final ConfigMisc miscellaneous;

    public ConfigUndergroundGen(final ModConfigSpec.Builder BUILDER) {
        BUILDER
                .comment(
                        "##########################################################################################################\n" +
                        "# Configure settings related to caves, caverns, ravines and more.\n" +
                        "##########################################################################################################")
                .push("Underground Generation");

        caves = new ConfigCaves(BUILDER);
//        caverns = new ConfigCaverns(BUILDER);
        waterRegions = new ConfigWaterRegions(BUILDER);
//        ravines = new ConfigRavine(BUILDER);
        miscellaneous = new ConfigMisc(BUILDER);

        BUILDER.pop();
    }
}
