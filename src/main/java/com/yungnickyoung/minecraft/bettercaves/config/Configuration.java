package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.common.ForgeConfigSpec;

final class Configuration {

    public final ConfigCaveGen configCaveGen;
    public final ConfigDebug configDebug;
    final ForgeConfigSpec.ConfigValue<Integer> lavaDepth;
    final ForgeConfigSpec.ConfigValue<Boolean> flattenBedrock;
    final ForgeConfigSpec.ConfigValue<String> lavaBlock;
    final ForgeConfigSpec.ConfigValue<String> waterBlock;

    Configuration(final ForgeConfigSpec.Builder builder) {
        builder.push("Better Caves");

        lavaDepth = builder
                .comment(" Lava (or water in water regions) spawns at and below this y-coordinate." +
                        "\n Default: 10")
                .worldRestart()
                .defineInRange("Liquid Altitude", 10, 0, 255);

        flattenBedrock = builder
                .comment(" Set this to true to replace the usual bedrock generation with a single flat layer." +
                        "\n Default: true")
                .worldRestart()
                .define("Flatten Bedrock", true);

        lavaBlock = builder
                .comment("The block used for lava generation at and below the Liquid Altitude. " +
                        "Defaults to regular lava if an invalid block is given." +
                        "\n Default: minecraft:flowing_lava")
                .worldRestart()
                .define("Lava Block", "minecraft:lava");

        waterBlock = builder
                .comment("The block used for water generation in  water caves/caverns at and below the Liquid Altitude. " +
                        "Defaults to regular water if an invalid block is given." +
                        "\n Default: minecraft:water")
                .worldRestart()
                .define("Water Block", "minecraft:water");

        configCaveGen = new ConfigCaveGen(builder);
        configDebug = new ConfigDebug(builder);

        builder.pop();
    }
}
