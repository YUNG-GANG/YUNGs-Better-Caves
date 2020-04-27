package com.yungnickyoung.minecraft.bettercaves.config.cavern;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigCaverns {
    public final ConfigLiquidCavern liquidCavern;
    public final ConfigFlooredCavern flooredCavern;
    public final ForgeConfigSpec.ConfigValue<Double> cavernSpawnChance;
    public final ForgeConfigSpec.ConfigValue<String> cavernRegionSize;
    public final ForgeConfigSpec.ConfigValue<Double> customRegionSize;

    public ConfigCaverns(final ForgeConfigSpec.Builder BUILDER) {
        BUILDER
            .comment(
                "##########################################################################################################\n" +
                "# Settings used in the generation of caverns. Caverns are spacious caves at low altitudes.\n" +
                "##########################################################################################################")
            .push("Caverns");

        liquidCavern = new ConfigLiquidCavern(BUILDER);
        flooredCavern = new ConfigFlooredCavern(BUILDER);

        cavernSpawnChance = BUILDER
            .comment(
                " Percent chance of caverns spawning in a given region.\n" +
                " Default: caverns spawn in 25% of regions.")
            .worldRestart()
            .defineInRange("Cavern Spawn Chance", 25d, 0, 100);

        cavernRegionSize = BUILDER
            .comment(
                " Determines how large cavern regions are. This controls the average size of caverns.\n" +
                " Accepted values: Small, Medium, Large, ExtraLarge, Custom\n" +
                " Default: Small (recommended).")
            .worldRestart()
            .define("Cavern Region Size", "Small");

        customRegionSize = BUILDER
            .comment(
                " Custom value for cavern region size. Only works if Cavern Region Size is set to Custom. " +
                "     Smaller value = larger regions. This value is very sensitive to change.\n" +
                "     Provided values:\n" +
                "         Small: 0.01\n" +
                "         Medium: 0.007\n" +
                "         Large: 0.005\n" +
                "         ExtraLarge: 0.001\n" +
                " Default: 0.01")
            .worldRestart()
            .defineInRange("Cavern Region Size Custom Value", .01, 0, .05);

        BUILDER.pop();
    }
}
