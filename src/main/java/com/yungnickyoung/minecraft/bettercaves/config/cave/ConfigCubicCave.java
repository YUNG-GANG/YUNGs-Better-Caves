package com.yungnickyoung.minecraft.bettercaves.config.cave;

//import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

public class ConfigCubicCave {
//    @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
//    @ConfigEntry.Gui.Tooltip
    public int caveBottom = 1;

//    @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
//    @ConfigEntry.Gui.Tooltip
    public int caveTop = 80;

//    @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
//    @ConfigEntry.Gui.Tooltip(count = 4)
    public int caveSurfaceCutoff = 15;

//    @ConfigEntry.Gui.Tooltip
    public double yCompression = 5.0;

//    @ConfigEntry.Gui.Tooltip
    public double xzCompression = 1.6;

//    @ConfigEntry.BoundedDiscrete(min = 0, max = 10)
//    @ConfigEntry.Gui.Tooltip
    public int cavePriority = 10;

//    @ConfigEntry.Gui.Excluded
    public Advanced advancedSettings = new Advanced();

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
