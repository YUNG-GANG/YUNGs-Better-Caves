package com.yungnickyoung.minecraft.bettercaves.config.cave;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigCaves {
    public final ConfigCubicCave cubicCave;
    public final ConfigSimplexCave simplexCave;
    public final ConfigSurfaceCave surfaceCave;
    public final ConfigVanillaCave vanillaCave;
    public final ForgeConfigSpec.ConfigValue<Double> caveSpawnChance;
    public final ForgeConfigSpec.ConfigValue<String> caveRegionSize;
    public final ForgeConfigSpec.ConfigValue<Double> customRegionSize;

    public ConfigCaves(final ForgeConfigSpec.Builder BUILDER) {
        BUILDER
            .comment(
                "##########################################################################################################\n" +
                "# Settings used in the generation of caves.\n" +
                "##########################################################################################################")
            .push("Caves");

        cubicCave = new ConfigCubicCave(BUILDER);
        simplexCave = new ConfigSimplexCave(BUILDER);
        surfaceCave = new ConfigSurfaceCave(BUILDER);
        vanillaCave = new ConfigVanillaCave(BUILDER);

        caveSpawnChance = BUILDER
            .comment(
                " Percent chance of caves spawning in a given region.\n" +
                " Default: caves spawn in 100% of regions.")
            .worldRestart()
            .defineInRange("Cave Spawn Chance", 100f, 0, 100f);

        caveRegionSize = BUILDER
            .comment(
                " Determines how large cave regions are.\n" +
                    "     Controls how long a cave system of a certain cave type extends before intersecting with a cave system of another type.\n" +
                    "     Larger = more cave interconnectivity for a given area, but less variation.\n" +
                    " Accepted values: Small, Medium, Large, ExtraLarge, Custom\n" +
                    " Default: Small (recommended).")
            .worldRestart()
            .define("Cave Region Size", "Small");

        customRegionSize = BUILDER
            .comment(
                " Custom value for cave region size. Smaller value = larger regions. This value is very sensitive to change.\n" +
                    "     ONLY WORKS IF Cave Region Size IS Custom.\n" +
                    "     Provided values:\n" +
                    "         Small: 0.008\n" +
                    "         Medium: 0.005\n" +
                    "         Large: 0.0032\n" +
                    "         ExtraLarge: 0.001\n" +
                    " Default: 0.008")
            .worldRestart()
            .defineInRange("Cave Region Size Custom Value", .008f, 0, .05f);

        BUILDER.pop();
    }
}
