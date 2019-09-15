package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.common.ForgeConfigSpec;

final class Configuration {
//    public static final ConfigCaveGen CAVE_GEN = new ConfigCaveGen(BUILDER);
//    public static final ConfigBedrockGen BEDROCK_GEN = new ConfigBedrockGen(BUILDER);
//    public static  final ConfigDebug DEBUG = new ConfigDebug(BUILDER);

    public final ConfigCaveGen configCaveGen;
    public final ConfigDebug configDebug;
    final ForgeConfigSpec.ConfigValue<Integer> lavaDepth;

    Configuration(final ForgeConfigSpec.Builder builder) {
        builder.push("Better Caves");

        lavaDepth = builder
                    .comment(" Lava (or water in water biomes) spawns at and below this y-coordinate.")
                    .worldRestart()
                    .define("Lava Depth", 10);

        configCaveGen = new ConfigCaveGen(builder);
        configDebug = new ConfigDebug(builder);

        builder.pop();



    }

}
