package com.yungnickyoung.minecraft.bettercaves.config.cave;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigCubicCave {
    public final ForgeConfigSpec.ConfigValue<Integer> caveBottom;
    public final ForgeConfigSpec.ConfigValue<Integer> caveTop;
    public final ForgeConfigSpec.ConfigValue<Integer> caveSurfaceCutoff;
    public final ForgeConfigSpec.ConfigValue<Double> yCompression;
    public final ForgeConfigSpec.ConfigValue<Double> xzCompression;
    public final ForgeConfigSpec.ConfigValue<Integer> cavePriority;
    public final Advanced advancedSettings;

    public ConfigCubicCave(final ForgeConfigSpec.Builder BUILDER) {
        BUILDER
            .comment(
                "##########################################################################################################\n" +
                "# Settings used in the generation of type 1 caves, which are more worm-like.\n" +
                "##########################################################################################################")
            .push("Type 1 Caves");

        caveBottom = BUILDER
            .comment(
                " The minimum y-coordinate at which type 1 caves can generate.\n" +
                " Default: 1")
            .worldRestart()
            .defineInRange("Type 1 Cave Minimum Altitude", 1, 0, 255);

        caveTop = BUILDER
            .comment(
                " The maximum y-coordinate at which type 1 caves can generate.\n" +
                " Default: 80")
            .worldRestart()
            .defineInRange("Type 1 Cave Maximum Altitude", 80, 0, 255);

        caveSurfaceCutoff = BUILDER
            .comment(
                " The depth from a given point on the surface at which type 1 caves start to close off.\n" +
                "     Will use the Max Cave Altitude instead of surface height if it is lower.\n" +
                "     Will use the Max Cave Altitude no matter what if Override Surface Detection is enabled.\n" +
                " Default: 15 (recommended)")
            .worldRestart()
            .defineInRange("Type 1 Cave Surface Cutoff Depth", 15, 0, 255);

        yCompression = BUILDER
            .comment(
                " Stretches caves vertically. Lower value = taller caves with steeper drops.\n" +
                " Default: 5.0 (recommended)")
            .worldRestart()
            .defineInRange("Compression - Vertical", 5.0, 0, 100);

        xzCompression = BUILDER
            .comment(
                " Stretches caves horizontally. Lower value = wider caves.\n" +
                " Default: 1.6 (recommended)")
            .worldRestart()
            .defineInRange("Compression - Horizontal", 1.6, 0, 100);

        cavePriority = BUILDER
            .comment(
                " Determines how frequently Type 1 Caves spawn. 0 = will not spawn at all.\n" +
                " Default: 10")
            .worldRestart()
            .defineInRange("Type 1 Cave Priority", 10, 0, 10);

        advancedSettings = new Advanced();

        BUILDER.pop();
    }

    public static class Advanced {
        public final double noiseThreshold;
        public final int fractalOctaves;
        public final double fractalGain;
        public final double fractalFrequency;
        public final int numGenerators;
        public final boolean yAdjust;
        public final double yAdjustF1;
        public final double yAdjustF2;
        public final String noiseType;

        public Advanced() {
            noiseThreshold = .95;
            fractalOctaves = 1;
            fractalGain = .3;
            fractalFrequency = .03;
            numGenerators = 2;
            yAdjust = true;
            yAdjustF1 = .9;
            yAdjustF2 = .9;
            noiseType = "CubicFractal";
        }
    }
}
