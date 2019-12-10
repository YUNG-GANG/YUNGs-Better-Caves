package com.yungnickyoung.minecraft.bettercaves.config.cave;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigSimplexCave {
    public final ForgeConfigSpec.ConfigValue<Integer> caveBottom;
    public final ForgeConfigSpec.ConfigValue<Double> yCompression;
    public final ForgeConfigSpec.ConfigValue<Double> xzCompression;
    public final ForgeConfigSpec.ConfigValue<String> caveFrequency;

    public ConfigSimplexCave(final ForgeConfigSpec.Builder builder) {
        builder.push("Type 2 Caves");

        caveBottom = builder
                .comment(" The minimum y-coordinate at which caves start generating." +
                        "\n Default: 1")
                .worldRestart()
                .defineInRange("Cave Bottom Altitude", 1, 0, 255);

        yCompression = builder
                .comment(" Changes height of caves. Lower value = taller caves with steeper drops." +
                        "\n Default: 3.0")
                .worldRestart()
                .defineInRange("Compression - Vertical", 3.0f, 0f, 20f);

        xzCompression = builder
                .comment(" Changes width of caves. Lower value = wider caves." +
                        "\n Default: 1.0")
                .worldRestart()
                .defineInRange("Compression - Horizontal", 1.0f, 0f, 20f);

        caveFrequency = builder
                .comment(" Determines how frequently Type 2 Caves spawn. If this is anything but VeryCommon (the default), " +
                        "some areas will not have caves at all.\n Accepted values: None, Rare, Common, VeryCommon" +
                        "\n Default: VeryCommon")
                .worldRestart()
                .define("Type 2 Cave Frequency", "VeryCommon");

        builder.pop();
    }
}
