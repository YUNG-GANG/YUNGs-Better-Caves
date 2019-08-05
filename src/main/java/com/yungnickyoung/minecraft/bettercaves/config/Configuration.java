package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.common.config.Config;

@Config(modid = Settings.MOD_ID, name = Settings.NAME)
public class Configuration {

    @Config.Name("Cave Generation Parameters")
    public static ConfigCaveGen cavegen = new ConfigCaveGen();

    public static class ConfigCaveGen {
        @Config.Name("F Threshold")
        @Config.Comment("The threshold for the F function")
        @Config.RangeDouble(min = 0)
        @Config.RequiresWorldRestart
        public double FThreshold = 20;

        @Config.Name("Lava Depth")
        @Config.Comment("The y-coordinate below which lava replaces air. Default 10")
        @Config.RangeInt(min = 1)
        @Config.RequiresWorldRestart
        public int lavaDepth = 10;
    }

}
