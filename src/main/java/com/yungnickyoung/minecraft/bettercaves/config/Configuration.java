package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.common.ForgeConfigSpec;

final class Configuration {

    public final ConfigCaveGen configCaveGen;
    public final ConfigDebug configDebug;
    final ForgeConfigSpec.ConfigValue<Integer> lavaDepth;
    final ForgeConfigSpec.ConfigValue<Boolean> flattenBedrock;

    Configuration(final ForgeConfigSpec.Builder builder) {
        builder.push("Better Caves");

        lavaDepth = builder
                .comment(" Lava (or water in water biomes) spawns at and below this y-coordinate." +
                        "\n Default: 10")
                .worldRestart()
                .define("Lava Depth", 10);

        flattenBedrock = builder
                .comment(" Set this to true to replace the usual bedrock generation with a single flat layer." +
                        "\n Default: true")
                .worldRestart()
                .define("Flatten Bedrock", true);

        configCaveGen = new ConfigCaveGen(builder);
        configDebug = new ConfigDebug(builder);

        builder.pop();
    }
}
