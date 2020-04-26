package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class Configuration {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    static final ConfigCaveGen configCaveGen;
    static final ConfigDebug configDebug;
    static final ForgeConfigSpec.ConfigValue<Integer> liquidAltitude;
    static final ForgeConfigSpec.ConfigValue<Boolean> flattenBedrock;
    static final ForgeConfigSpec.ConfigValue<String> lavaBlock;
    static final ForgeConfigSpec.ConfigValue<String> waterBlock;

    static  {
        BUILDER.push("Better Caves");

        liquidAltitude = BUILDER
                .comment(" Lava (or water in water regions) spawns at and below this y-coordinate." +
                        "\n Default: 10")
                .worldRestart()
                .defineInRange("Liquid Altitude", 10, 0, 255);

        flattenBedrock = BUILDER
                .comment(" Set this to true to replace the usual bedrock generation with a single flat layer." +
                        "\n Default: true")
                .worldRestart()
                .define("Flatten Bedrock", true);

        lavaBlock = BUILDER
                .comment("The block used for lava generation at and below the Liquid Altitude. " +
                        "Defaults to regular lava if an invalid block is given." +
                        "\n Default: minecraft:flowing_lava")
                .worldRestart()
                .define("Lava Block", "minecraft:lava");

        waterBlock = BUILDER
                .comment("The block used for water generation in  water caves/caverns at and below the Liquid Altitude. " +
                        "Defaults to regular water if an invalid block is given." +
                        "\n Default: minecraft:water")
                .worldRestart()
                .define("Water Block", "minecraft:water");

        configCaveGen = new ConfigCaveGen(BUILDER);
        configDebug = new ConfigDebug(BUILDER);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
