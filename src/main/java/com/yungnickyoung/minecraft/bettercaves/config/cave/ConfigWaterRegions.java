package com.yungnickyoung.minecraft.bettercaves.config.cave;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigWaterRegions {
    public final ConfigWaterCavern configWaterCavern;

    public final ForgeConfigSpec.ConfigValue<Boolean> enableWaterRegions;
    public final ForgeConfigSpec.ConfigValue<String> waterRegionFrequency;

    public ConfigWaterRegions(final ForgeConfigSpec.Builder builder) {
        builder.push("Water Biomes");

        enableWaterRegions = builder
                .comment(" Set to true for caves/caverns to have a chance of spawning as their water-based variants, instead" +
                        " of having lava." +
                        "\n Default: true")
                .worldRestart()
                .define("Enable Water Cave/Cavern Regions", true);

        waterRegionFrequency = builder
                .comment(" Determines how frequently water regions spawn\n" +
                        " Accepted values: Rare, Normal, Common, VeryCommon, Always" +
                        "\n Default: Normal")
                .worldRestart()
                .define("Water Region Frequency", "Normal");

        configWaterCavern = new ConfigWaterCavern(builder);

        builder.pop();

    }

    public static class ConfigWaterCavern {
        public final ForgeConfigSpec.ConfigValue<Double> yCompression;
        public final ForgeConfigSpec.ConfigValue<Double> xzCompression;

        public ConfigWaterCavern(final ForgeConfigSpec.Builder builder) {
            builder.push("Water Caverns");

            yCompression = builder
                    .comment(" Changes height of formations in caverns. Lower value = more open caverns with larger features." +
                            "\n Default: 1.0")
                    .worldRestart()
                    .defineInRange("Compression - Vertical", 1.0f, 0f, 20f);

            xzCompression = builder
                    .comment(" Changes width of formations in caverns. Lower value = more open caverns with larger features." +
                            "\n Default: 1.0")
                    .worldRestart()
                    .defineInRange("Compression - Horizontal", 1.0f, 0f, 20f);

            builder.pop();
        }
    }
}
