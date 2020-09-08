package com.yungnickyoung.minecraft.bettercaves.config.cave;


public class ConfigCubicCave {
    public final Advanced advancedSettings;

    public ConfigCubicCave() {
        advancedSettings = new Advanced();
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
