package com.yungnickyoung.minecraft.bettercaves.config.cavern;

//import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

public class ConfigLiquidCavern {
//    @ConfigEntry.Gui.Tooltip
//    @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
    public int cavernBottom = 1;

//    @ConfigEntry.Gui.Tooltip
//    @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
    public int cavernTop = 35;

//    @ConfigEntry.Gui.Tooltip
    public double yCompression = 1.3;

//    @ConfigEntry.Gui.Tooltip
    public double xzCompression = 0.7;

//    @ConfigEntry.Gui.Tooltip
//    @ConfigEntry.BoundedDiscrete(min = 0, max = 10)
    public int cavernPriority = 10;

//    @ConfigEntry.Gui.Excluded
    public Advanced advancedSettings = new Advanced();

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
            fractalFrequency = .03;
            numGenerators = 2;
            noiseType = "SimplexFractal";
        }
    }
}
