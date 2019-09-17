package com.yungnickyoung.minecraft.bettercaves.config.cave;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigVanillaCave {
    public final ForgeConfigSpec.ConfigValue<Boolean> enableVanillaRavines;
    public final ForgeConfigSpec.ConfigValue<Boolean> enableVanillaUnderwaterRavines;

    public ConfigVanillaCave(final ForgeConfigSpec.Builder builder) {
        builder.push("Vanilla Gen");

        enableVanillaRavines = builder
                .comment(" Set to true to enable vanilla ravine generation." +
                        "\n Default: true")
                .worldRestart()
                .define("Enable Ravines", true);

        enableVanillaUnderwaterRavines = builder
                .comment(" Set to true to enable vanilla underwater ravine generation." +
                        "\n Default: true")
                .worldRestart()
                .define("Enable Underwater Ravines", true);

        builder.pop();
    }
}
