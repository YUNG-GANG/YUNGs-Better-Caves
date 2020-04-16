package com.yungnickyoung.minecraft.bettercaves.config.cave;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigLavaCavern {
    public final ForgeConfigSpec.ConfigValue<Integer> caveTop;
    public final ForgeConfigSpec.ConfigValue<Integer> caveBottom;
    public final ForgeConfigSpec.ConfigValue<Double> yCompression;
    public final ForgeConfigSpec.ConfigValue<Double> xzCompression;
    public final ForgeConfigSpec.ConfigValue<String> caveFrequency;
    public final ForgeConfigSpec.ConfigValue<Double> customFrequency;

    public ConfigLavaCavern(final ForgeConfigSpec.Builder builder) {
        builder.push("Lava Caverns");

        caveTop = builder
                .comment(" The top cutoff y-coordinate of Lava Caverns. Note that caverns will attempt " +
                        "to close off anyway if this value is greater than the surface y-coordinate." +
                        "\n Default: 30")
                .worldRestart()
                .defineInRange("Cavern Top Altitude", 35, 0, 255);

        caveBottom = builder
                .comment(" The bottom cutoff y-coordinate at which Lava Caverns stop generating." +
                        "\n Default: 1")
                .worldRestart()
                .defineInRange("Cavern Bottom Altitude", 1, 0, 255);

        yCompression = builder
                .comment(" Changes height of formations in caverns. Lower value = more open caverns with larger features." +
                        "\n Default: 1.0")
                .worldRestart()
                .defineInRange("Compression - Vertical", 1.3f, 0f, 20f);

        xzCompression = builder
                .comment(" Changes width of formations in caverns. Lower value = more open caverns with larger features." +
                        "\n Default: 1.0")
                .worldRestart()
                .defineInRange("Compression - Horizontal", 0.7f, 0f, 20f);

        caveFrequency = builder
                .comment(" Determines how frequently Lava Caverns spawn." +
                        "\n Accepted values: None, Rare, Normal, Common, VeryCommon, Custom" +
                        "\n Default: Normal")
                .worldRestart()
                .define("Lava Cavern Frequency", "Normal");

        customFrequency = builder
                .comment("Custom value for cavern frequency. Only works if Lava Cavern Frequency is set to Custom. 0 = 0% chance of spawning, " +
                        "1.0 = 50% chance of spawning (which is the max value). The value does not scale linearly. \n Provided values:\n" +
                        " None: 0\n" +
                        " Rare: 0.2\n" +
                        " Normal: 0.6\n" +
                        " Common: 0.7\n" +
                        " VeryCommon: 0.9")
                .worldRestart()
                .defineInRange("Lava Cavern Frequency Custom Value", 1.0, 0, 1.0);

        builder.pop();
    }
}
