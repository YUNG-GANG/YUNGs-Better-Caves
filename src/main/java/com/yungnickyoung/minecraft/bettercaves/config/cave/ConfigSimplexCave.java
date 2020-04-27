package com.yungnickyoung.minecraft.bettercaves.config.cave;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigSimplexCave {
    public final ForgeConfigSpec.ConfigValue<Integer> caveBottom;
    public final ForgeConfigSpec.ConfigValue<Integer> caveTop;
    public final ForgeConfigSpec.ConfigValue<Integer> caveSurfaceCutoff;
    public final ForgeConfigSpec.ConfigValue<Double> yCompression;
    public final ForgeConfigSpec.ConfigValue<Double> xzCompression;
    public final ForgeConfigSpec.ConfigValue<Integer> cavePriority;
    public final ConfigSimplexCave.Advanced advancedSettings;

    public ConfigSimplexCave(final ForgeConfigSpec.Builder BUILDER) {
        BUILDER
            .comment(
                "##########################################################################################################\n" +
                "# Settings used in the generation of type 2 caves, which tend to be more open and spacious.\n" +
                "##########################################################################################################")
            .push("Type 2 Caves");

        caveBottom = BUILDER
            .comment(
                " The minimum y-coordinate at which type 2 caves can generate.\n" +
                " Default: 1")
            .worldRestart()
            .defineInRange("Type 2 Cave Minimum Altitude", 1, 0, 255);

        caveTop = BUILDER
            .comment(
                " The maximum y-coordinate at which type 2 caves can generate.\n" +
                " Default: 80")
            .worldRestart()
            .defineInRange("Type 2 Cave Maximum Altitude", 80, 0, 255);

        caveSurfaceCutoff = BUILDER
            .comment(
                " The depth from a given point on the surface at which type 2 caves start to close off.\n" +
                "     Will use the Max Cave Altitude instead of surface height if it is lower.\n" +
                "     Will use the Max Cave Altitude no matter what if Override Surface Detection is enabled.\n" +
                " Default: 15 (recommended)")
            .worldRestart()
            .defineInRange("Type 2 Cave Surface Cutoff Depth", 15, 0, 255);

        yCompression = BUILDER
            .comment(
                " Stretches caves vertically. Lower value = taller caves with steeper drops.\n" +
                " Default: 2.2 (recommended)")
            .worldRestart()
            .defineInRange("Compression - Vertical", 2.2, 0, 100);

        xzCompression = BUILDER
            .comment(
                " Stretches caves horizontally. Lower value = wider caves.\n" +
                " Default: 0.9 (recommended)")
            .worldRestart()
            .defineInRange("Compression - Horizontal", 0.9, 0, 100);

        cavePriority = BUILDER
            .comment(
                " Determines how frequently Type 2 Caves spawn. 0 = will not spawn at all.\n" +
                " Default: 5")
            .worldRestart()
            .defineInRange("Type 2 Cave Priority", 5, 0, 10);

        advancedSettings = new ConfigSimplexCave.Advanced(BUILDER);

        BUILDER.pop();
    }

    public static class Advanced {
        public final ForgeConfigSpec.ConfigValue<Double> noiseThreshold;
        public final ForgeConfigSpec.ConfigValue<Integer> fractalOctaves;
        public final ForgeConfigSpec.ConfigValue<Double> fractalGain;
        public final ForgeConfigSpec.ConfigValue<Double> fractalFrequency;
        public final ForgeConfigSpec.ConfigValue<Integer> numGenerators;
        public final ForgeConfigSpec.ConfigValue<Boolean> yAdjust;
        public final ForgeConfigSpec.ConfigValue<Double> yAdjustF1;
        public final ForgeConfigSpec.ConfigValue<Double> yAdjustF2;
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
                    " Noise threshold for determining which blocks get mined out as part of cave generation\n" +
                    "     Blocks with generated noise values greater than this threshold will be dug out.\n" +
                    " Default: 0.82")
                .worldRestart()
                .defineInRange("Noise Threshold", .82, -1, 1);

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
                    "     This determines how spread out or tightly knit cave systems are.\n" +
                    " Default: 0.03")
                .worldRestart()
                .define("Fractal Frequency", .025);

            numGenerators = BUILDER
                .comment(
                    " The number of noise generation functions used.\n" +
                    "     The intersection of these functions is used to calculate a single noise value.\n" +
                    "     Increasing this may decrease performance.\n" +
                    " Default: 2")
                .worldRestart()
                .define("Number of Generators", 2);

            yAdjust = BUILDER
                .comment(
                    " Enable y-adjustment, giving players more headroom in caves.\n" +
                    " Default: true")
                .worldRestart()
                .define("Enable y-adjustment", true);

            yAdjustF1 = BUILDER
                .comment(
                    " Adjustment factor affecting the block immediately above a given block.\n" +
                    "     Higher value will tend to increase the headroom in caves.\n" +
                    " Default: 0.95")
                .worldRestart()
                .defineInRange("y-adjustment Variable 1", .95, 0, 1);

            yAdjustF2 = BUILDER
                .comment(
                    " Adjustment factor affecting the block two blocks above a given block.\n" +
                    "     Higher value will tend to increase the headroom in caves.\n" +
                    " Default: 0.5")
                .worldRestart()
                .defineInRange("y-adjustment Variable 2", .5, 0, 1);

            noiseType = BUILDER
                .comment(" This value is currently unused for Type 2 caves. ")
                .worldRestart()
                .define("Noise Type", "SimplexFractal");

            BUILDER.pop();
        }
    }
}
