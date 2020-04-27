package com.yungnickyoung.minecraft.bettercaves.config.ravine;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigRavine {
    public final ForgeConfigSpec.ConfigValue<Boolean> enableVanillaRavines;
    public final ForgeConfigSpec.ConfigValue<Boolean> enableFloodedRavines;

    public ConfigRavine(final ForgeConfigSpec.Builder BUILDER) {
        BUILDER
            .comment(
                "##########################################################################################################\n" +
                "# Settings used for ravine generation.\n" +
                "##########################################################################################################")
            .push("Ravines");

        enableVanillaRavines = BUILDER
            .comment(
                " Set to true to enable ravine generation.\n" +
                " Default: true")
            .worldRestart()
            .define("Enable Ravines", true);

        enableFloodedRavines = BUILDER
            .comment(
                " Set to true to enable flooded ravines in ocean biomes.\n" +
                " Default: true")
            .worldRestart()
            .define("Enable Flooded Ravines", true);

        BUILDER.pop();
    }
}
