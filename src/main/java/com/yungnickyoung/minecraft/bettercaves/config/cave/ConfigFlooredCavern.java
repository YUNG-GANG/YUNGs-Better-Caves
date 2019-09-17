package com.yungnickyoung.minecraft.bettercaves.config.cave;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigFlooredCavern {
    public final ForgeConfigSpec.ConfigValue<Integer> caveTop;
    public final ForgeConfigSpec.ConfigValue<Integer> caveBottom;
    public final ForgeConfigSpec.ConfigValue<Double> yCompression;
    public final ForgeConfigSpec.ConfigValue<Double> xzCompression;
    public final ForgeConfigSpec.ConfigValue<String> caveFrequency;

    public ConfigFlooredCavern(final ForgeConfigSpec.Builder builder) {
        builder.push("Floored Caverns");

        caveTop = builder
                .comment(" The top y-coordinate at which Floored Caverns start generating. Note that caverns will attempt " +
                        "to close off anyway if this value is greater than the surface y-coordinate." +
                        "\n Default: 30")
                .worldRestart()
                .define("Top Generation Altitude", 30);

        caveBottom = builder
                .comment(" The bottom y-coordinate at which Floored Caverns stop generating." +
                        "\n Default: 1")
                .worldRestart()
                .define("Bottom Generation Altitude", 1);

        yCompression = builder
                .comment(" Changes height of caves. Lower value = taller caves with steeper drops." +
                        "\n Default: 1.0")
                .worldRestart()
                .defineInRange("Vertical Compression", 1.0f, 0f, 20f);

        xzCompression = builder
                .comment(" Changes width of caves. Lower value = wider caves." +
                        "\n Default: 1.0")
                .worldRestart()
                .defineInRange("Horizontal Compression", 1.0f, 0f, 20f);

        caveFrequency = builder
                .comment(" Determines how frequently Floored Caverns spawn." +
                        "\n Accepted values: None, Rare, Normal, Common, VeryCommon" +
                        "\n Default: Normal")
                .worldRestart()
                .define("Floored Cavern Frequency", "Normal");

        builder.pop();
    }
}
