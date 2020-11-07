package com.yungnickyoung.minecraft.bettercaves.config.cave;

//import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

public class ConfigSimplexCave {
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
    public double yCompression = 2.2;

//    @ConfigEntry.Gui.Tooltip
    public double xzCompression = 0.9;

//    @ConfigEntry.BoundedDiscrete(min = 0, max = 10)
//    @ConfigEntry.Gui.Tooltip
    public int cavePriority = 5;

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
