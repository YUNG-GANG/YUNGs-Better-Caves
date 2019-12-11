package com.yungnickyoung.minecraft.bettercaves.config.cave;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigCubicCave {
    public final ForgeConfigSpec.ConfigValue<Integer> caveBottom;
    public final ForgeConfigSpec.ConfigValue<Double> yCompression;
    public final ForgeConfigSpec.ConfigValue<Double> xzCompression;
    public final ForgeConfigSpec.ConfigValue<String> caveFrequency;
    public final ForgeConfigSpec.ConfigValue<Double> customFrequency;

    public ConfigCubicCave(final ForgeConfigSpec.Builder builder) {
        builder.push("Type 1 Caves");

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
                .comment(" Determines how frequently Type 1 Caves spawn. If this is anything but VeryCommon (the default), " +
                        "some areas will not have caves at all.\n Accepted values: None, Rare, Common, VeryCommon, Custom" +
                        "\n Default: VeryCommon")
                .worldRestart()
                .define("Type 1 Cave Frequency", "VeryCommon");

        customFrequency = builder
                .comment(" Custom value for cave frequency. Only works if Type 1 Cave Frequency is set to Custom. 0 = 0% chance of spawning, " +
                        "1.0 = 50% chance of spawning (which is the max value). The value may not scale linearly. \n Provided values:\n" +
                        " None: 0\n" +
                        " Rare: 0.4\n" +
                        " Common: 0.8\n" +
                        " VeryCommon: 1.0")
                .worldRestart()
                .defineInRange("Type 1 Cave Frequency Custom Value", 1.0, 0, 1.0);

        builder.pop();
    }
}
