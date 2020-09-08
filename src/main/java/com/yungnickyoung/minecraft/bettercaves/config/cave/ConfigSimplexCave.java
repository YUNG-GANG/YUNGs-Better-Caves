package com.yungnickyoung.minecraft.bettercaves.config.cave;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigSimplexCave {
    public final ForgeConfigSpec.ConfigValue<Integer> caveBottom;
    public final ForgeConfigSpec.ConfigValue<Integer> caveTop;
    public final ForgeConfigSpec.ConfigValue<Integer> caveSurfaceCutoff;
    public final ForgeConfigSpec.ConfigValue<Double> yCompression;
    public final ForgeConfigSpec.ConfigValue<Double> xzCompression;
    public final ForgeConfigSpec.ConfigValue<Integer> cavePriority;
    public final Advanced advancedSettings;

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
            noiseThreshold = .82;
            fractalOctaves = 1;
            fractalGain = .3;
            fractalFrequency = .025;
            numGenerators = 2;
            yAdjust = true;
            yAdjustF1 = .95;
            yAdjustF2 = .5;
            noiseType = "SimplexFractal";
        }
    }
}
