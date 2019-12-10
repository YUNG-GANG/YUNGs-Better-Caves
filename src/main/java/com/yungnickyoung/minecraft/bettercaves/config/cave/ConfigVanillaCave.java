package com.yungnickyoung.minecraft.bettercaves.config.cave;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigVanillaCave {
    public final ForgeConfigSpec.ConfigValue<Boolean> enableRavines;
    public final ForgeConfigSpec.ConfigValue<Boolean> enableUnderwaterRavines;

    public ConfigVanillaCave(final ForgeConfigSpec.Builder builder) {
        builder.push("Vanilla Generation");

        enableRavines = builder
                .comment(" Set to true to enable normal vanilla ravine generation." +
                        "\n Default: true")
                .worldRestart()
                .define("Enable Ravines", true);

        enableUnderwaterRavines = builder
                .comment(" Set to true to enable underwater vanilla ravine generation in ocean biomes." +
                        "\n Default: true")
                .worldRestart()
                .define("Enable Underwater Ravines", true);

        builder.pop();
    }
}