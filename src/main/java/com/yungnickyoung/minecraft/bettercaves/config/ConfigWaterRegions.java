package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigWaterRegions {
    public final ForgeConfigSpec.ConfigValue<Double> waterRegionSpawnChance;
    public final ForgeConfigSpec.ConfigValue<String> waterRegionSize;
    public final ForgeConfigSpec.ConfigValue<Double> waterRegionCustomSize;

    public ConfigWaterRegions(final ForgeConfigSpec.Builder BUILDER) {
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
                " Determines how large water regions are.\n" +
                " Default: Medium (recommended).")
            .worldRestart()
            .define("Water Region Size", "Medium");

        waterRegionCustomSize = BUILDER
            .comment(
                " Custom value for water region size. Smaller value = larger regions. This value is very sensitive to change.\n" +
                "     ONLY WORKS IF Water Region Size IS Custom.\n" +
                "     Provided values:\n" +
                "         Small: 0.008\n" +
                "         Medium: 0.004\n" +
                "         Large: 0.0028\n" +
                "         ExtraLarge: 0.001\n" +
                " Default: 0.004")
            .worldRestart()
            .defineInRange("Water Region Size Custom Value", .004, 0, .05);

        BUILDER.pop();
    }
}
