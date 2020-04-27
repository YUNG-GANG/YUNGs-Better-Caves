package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigMisc {
    public final ForgeConfigSpec.ConfigValue<Integer> liquidAltitude;
    public final ForgeConfigSpec.ConfigValue<String> lavaBlock;
    public final ForgeConfigSpec.ConfigValue<String> waterBlock;
    public final ForgeConfigSpec.ConfigValue<Boolean> replaceFloatingGravel;
    public final ForgeConfigSpec.ConfigValue<Boolean> overrideSurfaceDetection;
    public final ForgeConfigSpec.ConfigValue<Boolean> enableFloodedUnderground;

    public ConfigMisc(final ForgeConfigSpec.Builder BUILDER) {
        BUILDER
            .comment(
                "##########################################################################################################\n" +
                "# Miscellaneous settings used in cave and cavern generation.\n" +
                "##########################################################################################################")
            .push("Miscellaneous");

        liquidAltitude = BUILDER
            .comment(
                " Lava (or water in water regions) spawns at and below this y-coordinate.\n" +
                " Default: 10")
            .worldRestart()
            .defineInRange("Liquid Altitude", 10, 0, 255);

        lavaBlock = BUILDER
            .comment(
                " The block used for lava generation at and below the Liquid Altitude.\n" +
                "     Defaults to regular lava if an invalid block is given.\n" +
                " Default: minecraft:lava")
            .worldRestart()
            .define("Lava Block", "minecraft:lava");

        waterBlock = BUILDER
            .comment(
                " The block used for water generation in water caves/caverns at and below the Liquid Altitude.\n" +
                "     Defaults to regular water if an invalid block is given.\n" +
                " Default: minecraft:water")
            .worldRestart()
            .define("Water Block", "minecraft:water");

        replaceFloatingGravel = BUILDER
            .comment(
                " Replace naturally generated floating gravel on the ocean floor with andesite.\n" +
                "     Can prevent lag due to cascading gravel falling into caverns under the ocean.\n" +
                " Default: true")
            .worldRestart()
            .define("Prevent Cascading Gravel", true);

        overrideSurfaceDetection = BUILDER
            .comment(
                " Ignores surface detection for closing off caves and caverns, forcing them to spawn\n" +
                "     up until their max height. Useful for Nether-like dimensions with no real \"surface\".\n" +
                " Default: false")
            .worldRestart()
            .define("Override Surface Detection", false);

        enableFloodedUnderground = BUILDER
            .comment(
                " Set to true to enable flooded underground in ocean biomes.\n" +
                " Default: true")
            .worldRestart()
            .define("Enable Flooded Underground", true);

        BUILDER.pop();
    }
}
