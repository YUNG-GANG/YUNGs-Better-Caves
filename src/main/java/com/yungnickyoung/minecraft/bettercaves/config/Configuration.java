package com.yungnickyoung.minecraft.bettercaves.config;

import com.yungnickyoung.minecraft.bettercaves.world.MapGenBetterCaves;
import net.minecraftforge.common.config.Config;

@Config(modid = Settings.MOD_ID, name = Settings.NAME)
public class Configuration {
    @Config.Name("Cave Generation Settings")
    @Config.Comment("Configure parameters controlling cave generation.")
    public static ConfigCaveGen caveSettings = new ConfigCaveGen();

    @Config.Name("Bedrock Generation")
    @Config.Comment("Configure how bedrock generates in the overworld and nether.")
    public static ConfigBedrockGen bedrockSettings = new ConfigBedrockGen();

    @Config.Name("Lava Depth")
    @Config.Comment("The y-coordinate at which lava replaces air. Default: 10")
    @Config.RangeInt(min = 1)
    @Config.RequiresWorldRestart
    public static int lavaDepth = 10;

    /* DEBUG */
    @Config.Name("Cave Type")
    @Config.Comment("The type of cave generation to use. Default: SimplexIPComboCavern")
    public static MapGenBetterCaves.CaveType caveType = MapGenBetterCaves.CaveType.SimplexIPComboCavern;
}
