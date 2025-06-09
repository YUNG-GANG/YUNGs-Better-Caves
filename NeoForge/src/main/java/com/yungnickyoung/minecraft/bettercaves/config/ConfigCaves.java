package com.yungnickyoung.minecraft.bettercaves.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ConfigCaves {
    public final ConfigCubicCaves cubicCave;
    public final ConfigSimplexCaves simplexCave;
    public final ConfigSurfaceCaves surfaceCave;
    public final ConfigVanillaCaves vanillaCave;
    public final ModConfigSpec.DoubleValue caveSpawnChance;
    public final ModConfigSpec.ConfigValue<String> caveRegionSize;
    public final ModConfigSpec.DoubleValue customRegionSize;


    public ConfigCaves(final ModConfigSpec.Builder BUILDER) {
        BUILDER
                .comment(
                        "##########################################################################################################\n" +
                                "# Settings used in the generation of caves.\n" +
                                "##########################################################################################################")
                .push("Caves");

        cubicCave = new ConfigCubicCaves(BUILDER);
        simplexCave = new ConfigSimplexCaves(BUILDER);
        surfaceCave = new ConfigSurfaceCaves(BUILDER);
        vanillaCave = new ConfigVanillaCaves(BUILDER);

        caveSpawnChance = BUILDER
                .comment(
                        " Percent chance of caves spawning in a given region.\n" +
                                " Default: caves spawn in 100% of regions.")
                .worldRestart()
                .defineInRange("Cave Spawn Chance", 100f, 0, 100f);

        caveRegionSize = BUILDER
                .comment(
                        " Determines how large cave regions are.\n" +
                                "     Controls the average size of a cave system.\n" +
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
