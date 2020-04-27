package com.yungnickyoung.minecraft.bettercaves.config.cave;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigVanillaCave {
    public final ForgeConfigSpec.ConfigValue<Integer> caveBottom;
    public final ForgeConfigSpec.ConfigValue<Integer> caveTop;
    public final ForgeConfigSpec.ConfigValue<Integer> caveDensity;
    public final ForgeConfigSpec.ConfigValue<Integer> cavePriority;

    public ConfigVanillaCave(final ForgeConfigSpec.Builder BUILDER) {
        BUILDER
            .comment(
                "##########################################################################################################\n" +
                "# Settings controlling vanilla Minecraft cave generation.\n" +
                "##########################################################################################################")
            .push("Vanilla Caves");

        caveBottom = BUILDER
            .comment(
                " The minimum y-coordinate at which vanilla caves can generate.\n" +
                " Default: 8")
            .worldRestart()
            .defineInRange("Vanilla Cave Minimum Altitude", 8, 0, 255);

        caveTop = BUILDER
            .comment(
                " The maximum y-coordinate at which vanilla caves can generate.\n" +
                " Default: 128")
            .worldRestart()
            .defineInRange("Vanilla Cave Maximum Altitude", 128, 0, 255);

        caveDensity = BUILDER
            .comment(
                " The density of vanilla caves. Higher = more caves, closer together. \n" +
                " Default: 14 (value used in vanilla)")
            .worldRestart()
            .defineInRange("Vanilla Cave Density", 14, 0, 100);

        cavePriority = BUILDER
            .comment(
                " Determines how frequently vanilla caves spawn. 0 = will not spawn at all.\n" +
                " Default: 0")
            .worldRestart()
            .defineInRange("Vanilla Cave Priority", 0, 0, 10);

        BUILDER.pop();
    }
}