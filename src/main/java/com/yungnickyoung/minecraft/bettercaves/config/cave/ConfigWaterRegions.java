package com.yungnickyoung.minecraft.bettercaves.config.cave;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigWaterRegions {
    public final ConfigWaterCavern configWaterCavern;

    public final ForgeConfigSpec.ConfigValue<Boolean> enableWaterRegions;
    public final ForgeConfigSpec.ConfigValue<String> waterRegionFrequency;
    public final ForgeConfigSpec.ConfigValue<Double> customFrequency;

    public ConfigWaterRegions(final ForgeConfigSpec.Builder builder) {
        builder.push("Water Regions");

        enableWaterRegions = builder
                .comment(" Set to true for caves/caverns to have a chance of spawning as their water-based variants, instead" +
                        " of having lava." +
                        "\n Default: true")
                .worldRestart()
                .define("Enable Water Cave/Cavern Regions", true);

        waterRegionFrequency = builder
                .comment(" Determines how frequently water regions spawn\n" +
                        " Accepted values: Rare, Normal, Common, VeryCommon, Always, Custom" +
                        "\n Default: Normal")
                .worldRestart()
                .define("Water Region Frequency", "Normal");

        customFrequency = builder
                .comment("Custom value for water region frequency. Only works if Water Region Frequency is set to Custom. 0 = 0% chance of spawning, " +
                        "1.0 = 100% chance of spawning. The value may not scale linearly. \n Provided values:\n" +
                        " Rare: 0.3\n" +
                        " Normal: 0.425\n" +
                        " Common: 0.55\n" +
                        " VeryCommon: 0.65\n" +
                        " Always: 1.0")
                .worldRestart()
                .defineInRange("Water Region Frequency Custom Value", 1.0, 0, 1.0);

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
