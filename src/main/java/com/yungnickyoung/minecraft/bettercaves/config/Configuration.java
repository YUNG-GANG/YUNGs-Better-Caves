package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.common.config.Config;

@Config(modid = Settings.MOD_ID, name = Settings.NAME)
public class Configuration {

    @Config.Name("Cave Generation Parameters")
    public static ConfigCaveGen cavegen = new ConfigCaveGen();

    private static class ConfigCaveGen {
        @Config.Name("Test Double")
        @Config.Comment("This is a test double. Value must be -1.0 to 1.0.")
        @Config.RangeDouble(min = -1.0f, max = 1.0f)
        @Config.RequiresWorldRestart
        public double testDouble = .5f;
    }

}
