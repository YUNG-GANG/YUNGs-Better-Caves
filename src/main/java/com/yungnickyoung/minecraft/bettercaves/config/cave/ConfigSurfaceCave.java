package com.yungnickyoung.minecraft.bettercaves.config.cave;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigSurfaceCave {
    public final ForgeConfigSpec.ConfigValue<Boolean> enableSurfaceCaves;
    public final ForgeConfigSpec.ConfigValue<Integer> caveBottom;
    public final ForgeConfigSpec.ConfigValue<Integer> caveTop;
    public final ForgeConfigSpec.ConfigValue<Integer> caveDensity;

    public ConfigSurfaceCave(final ForgeConfigSpec.Builder BUILDER) {
        BUILDER
            .comment(
                "##########################################################################################################\n" +
                "# Settings used in the generation of vanilla-like caves near the surface.\n" +
                "##########################################################################################################")
            .push("Surface Caves");

        enableSurfaceCaves = BUILDER
            .comment(
                " Set to true to enable vanilla-like caves which provide nice, natural-looking openings at the surface.\n" +
                " Default: true")
            .worldRestart()
            .define("Enable Surface Caves", true);

        caveBottom = BUILDER
            .comment(
                " The minimum y-coordinate at which surface caves can generate.\n" +
                " Default: 40")
            .worldRestart()
            .defineInRange("Surface Cave Minimum Altitude", 40, 0, 255);

        caveTop = BUILDER
            .comment(
                " The maximum y-coordinate at which surface caves can generate.\n" +
                " Default: 128")
            .worldRestart()
            .defineInRange("Surface Cave Maximum Altitude", 128, 0, 255);

        caveDensity = BUILDER
            .comment(
                " The density of surface caves. Higher = more caves, closer together. \n" +
                " Default: 17")
            .worldRestart()
            .defineInRange("Surface Cave Density", 17, 0, 100);

        BUILDER.pop();
    }
}
