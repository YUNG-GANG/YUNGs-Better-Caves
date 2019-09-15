package com.yungnickyoung.minecraft.bettercaves.config.cave;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigWaterBiomes {
    public final ConfigWaterCavern configWaterCavern;

    public final ForgeConfigSpec.ConfigValue<Boolean> enableWaterBiomes;
    public final ForgeConfigSpec.ConfigValue<String> waterBiomeFrequency;

    public ConfigWaterBiomes(final ForgeConfigSpec.Builder builder) {
        builder.push("Water Biomes");

        enableWaterBiomes = builder
                .comment(" Set to true for caves/caverns to have a chance of spawning as their water-based variants, instead" +
                        " of having lava")
                .worldRestart()
                .define("Enable Water Cave/Cavern Biomes", true);

        waterBiomeFrequency = builder
                .comment(" Determines how frequently water biomes spawn\n" +
                        " Accepted values: Rare, Normal, Common, VeryCommon, Always")
                .worldRestart()
                .define("Water Biome Frequency", "Normal");

        configWaterCavern = new ConfigWaterCavern(builder);

        builder.pop();

    }

    public static class ConfigWaterCavern {
        public final ForgeConfigSpec.ConfigValue<Double> yCompression;
        public final ForgeConfigSpec.ConfigValue<Double> xzCompression;

        public ConfigWaterCavern(final ForgeConfigSpec.Builder builder) {
            builder.push("Water Caverns");

            yCompression = builder
                    .comment(" Changes height of caves. Lower value = taller caves with steeper drops.")
                    .worldRestart()
                    .defineInRange("Vertical Compression", 1.0f, 0f, 20f);

            xzCompression = builder
                    .comment(" Changes width of caves. Lower value = wider caves.")
                    .worldRestart()
                    .defineInRange("Horizontal Compression", 1.0f, 0f, 20f);

            builder.pop();
        }
    }
}
