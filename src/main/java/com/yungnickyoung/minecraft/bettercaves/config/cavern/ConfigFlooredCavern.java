package com.yungnickyoung.minecraft.bettercaves.config.cavern;


public class ConfigFlooredCavern {

    public final Advanced advancedSettings;

    public ConfigFlooredCavern() {
        advancedSettings = new Advanced();

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
