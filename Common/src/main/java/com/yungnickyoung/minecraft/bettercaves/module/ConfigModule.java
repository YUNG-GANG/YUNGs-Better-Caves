package com.yungnickyoung.minecraft.bettercaves.module;

public class ConfigModule {
    public LiquidRegions liquidRegions = new LiquidRegions();

    public static class LiquidRegions {
        public double liquidRegionSize = 0.001;
        public double waterRegionSpawnChance = 40.0;
        public int liquidAltitude = -55;
    }
}
