package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.common.config.Config;

@Config(modid = Settings.MOD_ID, name = Settings.NAME)
public class Configuration {
    @Config.Name("Cave Generation")
    @Config.Comment("Configure parameters controlling cave generation.")
    public static ConfigCaveGen caveSettings = new ConfigCaveGen();

    @Config.Name("Bedrock Generation")
    @Config.Comment("Configure how bedrock generates in the overworld and nether.")
    public static ConfigBedrockGen bedrockSettings = new ConfigBedrockGen();

    @Config.Name("Lava Depth")
    @Config.Comment("Lava spawns at and below this y-coordinate.")
    @Config.RangeInt(min = 1)
    @Config.RequiresWorldRestart
    public static int lavaDepth = 10;

    @Config.Ignore
    public static ConfigTest testSettings = new ConfigTest();
}
