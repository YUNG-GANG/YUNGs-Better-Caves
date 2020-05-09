package com.yungnickyoung.minecraft.bettercaves.config.cavern;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigFlooredCavern {
    public final ForgeConfigSpec.ConfigValue<Integer> cavernBottom;
    public final ForgeConfigSpec.ConfigValue<Integer> cavernTop;
    public final ForgeConfigSpec.ConfigValue<Double> yCompression;
    public final ForgeConfigSpec.ConfigValue<Double> xzCompression;
    public final ForgeConfigSpec.ConfigValue<Integer> cavernPriority;
    public final ConfigFlooredCavern.Advanced advancedSettings;

    public ConfigFlooredCavern(final ForgeConfigSpec.Builder BUILDER) {
        BUILDER
            .comment(
                "##########################################################################################################\n" +
                "# Settings used in the generation of Floored Caverns found at low altitudes.\n" +
                "# These have much more ground to walk on than Liquid Caverns.\n" +
                "##########################################################################################################")
            .push("Floored Caverns");

        cavernBottom = BUILDER
            .comment(
                " The minimum y-coordinate at which Floored Caverns can generate.\n" +
                " Default: 1")
            .worldRestart()
            .defineInRange("Floored Cavern Minimum Altitude", 1, 0, 255);

        cavernTop = BUILDER
            .comment(
                " The maximum y-coordinate at which Floored Caverns can generate.\n" +
                "     Caverns will attempt to close off anyway if this value is greater than the surface's altitude.\n" +
                " Default: 35")
            .worldRestart()
            .defineInRange("Floored Cavern Maximum Altitude", 35, 0, 255);

        yCompression = BUILDER
            .comment(
                " Stretches caverns vertically. Lower value = more open caverns with larger features.\n" +
                " Default: 1.3")
            .worldRestart()
            .defineInRange("Compression - Vertical", 1.3, 0, 100);

        xzCompression = BUILDER
            .comment(
                " Stretches caverns horizontally. Lower value = more open caverns with larger features.\n" +
                " Default: 0.7")
            .worldRestart()
            .defineInRange("Compression - Horizontal", 0.7, 0, 100);

        cavernPriority = BUILDER
            .comment(
                " Determines how frequently Floored Caverns spawn. 0 = will not spawn at all.\n" +
                " Default: 10")
            .worldRestart()
            .defineInRange("Floored Cavern Priority", 10, 0, 10);

        advancedSettings = new ConfigFlooredCavern.Advanced();

        BUILDER.pop();
    }

    public static class Advanced {
        public final double noiseThreshold;
        public final int fractalOctaves;
        public final double fractalGain;
        public final double fractalFrequency;
        public final int numGenerators;
        public final String noiseType;

        public Advanced() {
            noiseThreshold = .6;
            fractalOctaves = 1;
            fractalGain = .3;
            fractalFrequency = .028;
            numGenerators = 2;
            noiseType = "SimplexFractal";
        }
    }
}
