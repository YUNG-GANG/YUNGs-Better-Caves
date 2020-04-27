package com.yungnickyoung.minecraft.bettercaves.config.cavern;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigLiquidCavern {
    public final ForgeConfigSpec.ConfigValue<Integer> cavernBottom;
    public final ForgeConfigSpec.ConfigValue<Integer> cavernTop;
    public final ForgeConfigSpec.ConfigValue<Double> yCompression;
    public final ForgeConfigSpec.ConfigValue<Double> xzCompression;
    public final ForgeConfigSpec.ConfigValue<Integer> cavernPriority;
    public final ConfigLiquidCavern.Advanced advancedSettings;

    public ConfigLiquidCavern(final ForgeConfigSpec.Builder BUILDER) {
        BUILDER
            .comment(
                "##########################################################################################################\n" +
                "# Settings used in the generation of Liquid Caverns found at low altitudes.\n" +
                "# These are caverns where the floor is predominantly water or lava.\n" +
                "##########################################################################################################")
            .push("Liquid Caverns");

        cavernBottom = BUILDER
            .comment(
                " The minimum y-coordinate at which Liquid Caverns can generate.\n" +
                " Default: 1")
            .worldRestart()
            .defineInRange("Liquid Cavern Minimum Altitude", 1, 0, 255);

        cavernTop = BUILDER
            .comment(
                " The maximum y-coordinate at which Liquid Caverns can generate.\n" +
                "     Caverns will attempt to close off anyway if this value is greater than the surface's altitude.\n" +
                " Default: 35")
            .worldRestart()
            .defineInRange("Liquid Cavern Maximum Altitude", 35, 0, 255);

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
                " Determines how frequently Liquid Caverns spawn. 0 = will not spawn at all.\n" +
                " Default: 10")
            .worldRestart()
            .defineInRange("Liquid Cavern Priority", 10, 0, 10);

        advancedSettings = new ConfigLiquidCavern.Advanced(BUILDER);

        BUILDER.pop();
    }

    public static class Advanced {
        public final ForgeConfigSpec.ConfigValue<Double> noiseThreshold;
        public final ForgeConfigSpec.ConfigValue<Integer> fractalOctaves;
        public final ForgeConfigSpec.ConfigValue<Double> fractalGain;
        public final ForgeConfigSpec.ConfigValue<Double> fractalFrequency;
        public final ForgeConfigSpec.ConfigValue<Integer> numGenerators;
        public final ForgeConfigSpec.ConfigValue<String> noiseType;

        public Advanced(final ForgeConfigSpec.Builder BUILDER) {
            BUILDER
                .comment(
                    "##########################################################################################################\n" +
                    "# Don't mess with these if you don't know what you're doing.\n" +
                    "##########################################################################################################")
                .push("Advanced Settings");

            noiseThreshold = BUILDER
                .comment(
                    " Noise threshold for determining which blocks get mined out as part of cavern generation\n" +
                    "     Blocks with generated noise values lower than this threshold will be dug out.\n" +
                    " Default: 0.6")
                .worldRestart()
                .defineInRange("Noise Threshold", .6, -1, 1);

            fractalOctaves = BUILDER
                .comment(
                    " The number of octaves used for ridged multi-fractal noise generation.\n" +
                    " Default: 1")
                .worldRestart()
                .define("Fractal Octaves", 1);

            fractalGain = BUILDER
                .comment(
                    " The gain for successive octaves of ridged multi-fractal noise generation.\n" +
                    " Default: 0.3")
                .worldRestart()
                .define("Fractal Gain", .3);

            fractalFrequency = BUILDER
                .comment(
                    " The frequency for ridged multi-fractal noise generation.\n" +
                    "     This determines how spread out or tightly knit the formations in caverns are.\n" +
                    " Default: 0.03")
                .worldRestart()
                .define("Fractal Frequency", .03);

            numGenerators = BUILDER
                .comment(
                    " The number of noise generation functions used.\n" +
                    "     The intersection of these functions is used to calculate a single noise value.\n" +
                    "     Increasing this may decrease performance.\n" +
                    " Default: 2")
                .worldRestart()
                .define("Number of Generators", 2);

            noiseType = BUILDER
                .comment(
                    " Type of noise to use for this cave. \n" +
                    " Accepted Values: Value, ValueFractal, Perlin, PerlinFractal, Simplex, SimplexFractal, Cellular, WhiteNoise, Cubic, CubicFractal\n" +
                    " Default: SimplexFractal")
                .worldRestart()
                .define("Noise Type", "SimplexFractal");

            BUILDER.pop();
        }
    }
}
