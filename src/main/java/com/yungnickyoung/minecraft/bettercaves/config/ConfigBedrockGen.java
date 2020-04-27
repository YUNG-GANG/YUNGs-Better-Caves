package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigBedrockGen {
    public final ForgeConfigSpec.ConfigValue<Boolean> flattenBedrock;
    public final ForgeConfigSpec.ConfigValue<Integer> bedrockWidth;

    public ConfigBedrockGen(final ForgeConfigSpec.Builder BUILDER) {
        BUILDER
            .comment(
                "##########################################################################################################\n" +
                "# Configure how bedrock generates.\n" +
                "##########################################################################################################")
            .push("Bedrock Generation");

        flattenBedrock = BUILDER
            .comment(
                " Replaces the usual bedrock generation pattern with flat layers.\n" +
                "     Activates in all whitelisted dimension, where applicable. The End is unaffected.\n" +
                " Default: true")
            .worldRestart()
            .define("Flatten Bedrock", true);

        bedrockWidth = BUILDER
            .comment(" The width of the bedrock layer. Only works if Flatten Bedrock is true.")
            .worldRestart()
            .defineInRange("Bedrock Layer Width", 1, 0, 256);

        BUILDER.pop();
    }
}
