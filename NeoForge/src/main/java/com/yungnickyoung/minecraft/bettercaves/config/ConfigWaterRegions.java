package com.yungnickyoung.minecraft.bettercaves.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ConfigWaterRegions {
    public final ModConfigSpec.DoubleValue waterRegionSpawnChance;
    public final ModConfigSpec.DoubleValue waterRegionSize;

    public ConfigWaterRegions(final ModConfigSpec.Builder BUILDER) {
        BUILDER
                .comment(
                        "##########################################################################################################\n" +
                        "# Settings used in the generation of water regions.\n" +
                        "##########################################################################################################")
                .push("Water Regions");

        waterRegionSpawnChance = BUILDER
                .comment(
                        " Percent chance of a region having water instead of lava at low altitudes.\n" +
                                " Default: 40%")
                .worldRestart()
                .defineInRange("Water Region Spawn Chance", 40d, 0, 100);

        waterRegionSize = BUILDER
                .comment(
                        " Determines how large water regions are. Smaller value = larger regions.\n" +
                                " Default: 0.001")
                .worldRestart()
                .defineInRange("Water Region Size", 0.001, 0, .05);

        BUILDER.pop();
    }
}