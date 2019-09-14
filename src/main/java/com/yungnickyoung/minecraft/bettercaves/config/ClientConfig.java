package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.common.ForgeConfigSpec;

final class ClientConfig {
//    public static final ConfigCaveGen CAVE_GEN = new ConfigCaveGen(BUILDER);
//    public static final ConfigBedrockGen BEDROCK_GEN = new ConfigBedrockGen(BUILDER);
//    public static  final ConfigDebug DEBUG = new ConfigDebug(BUILDER);

    final ForgeConfigSpec.ConfigValue<Integer> lavaDepth;

    ClientConfig(final ForgeConfigSpec.Builder builder) {
        builder.push("test 123");
        lavaDepth = builder
                    .comment("Test int in client config")
                    .translation(Settings.MOD_ID + ".config.lavaDepth")
                    .worldRestart()
                    .define("lavaDepth", 0);
        builder.pop();
    }

}
