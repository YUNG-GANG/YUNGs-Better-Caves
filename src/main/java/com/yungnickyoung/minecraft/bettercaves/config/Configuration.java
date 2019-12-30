package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.common.config.Config;

/**
 * Configuration options for Better Caves.
 * Fields not annotated with @Ignore are visible to users.
 * Some @Ignore fields are not used at all, while the rest are used but annotated as such
 * in order to keep them hidden from the user, as they are too delicate to allow the user
 * to mess with.
 */
@Config(modid = Settings.MOD_ID, name = Settings.NAME)
public class Configuration {
    @Config.Name("Cave & Cavern Generation")
    @Config.Comment("Configure parameters controlling cave generation.")
    public static ConfigCaveGen caveSettings = new ConfigCaveGen();

    @Config.Name("Bedrock Generation")
    @Config.Comment("Configure how bedrock generates in the overworld and nether.")
    public static ConfigBedrockGen bedrockSettings = new ConfigBedrockGen();

    @Config.Name("Debug settings")
    @Config.Comment("Don't mess with these settings for normal gameplay.")
    public static ConfigDebug debugsettings = new ConfigDebug();

    @Config.Name("Liquid Altitude")
    @Config.Comment("Lava (or water in water regions) spawns at and below this y-coordinate.")
    @Config.RangeInt(min = 0, max = 255)
    @Config.RequiresWorldRestart
    public static int liquidAltitude = 10;

    @Config.Name("Lava Block")
    @Config.Comment("The block used for lava generation at and below the Liquid Altitude. " +
            "Defaults to regular lava if an invalid block is given.")
    @Config.RequiresWorldRestart
    public static String lavaBlock = "minecraft:flowing_lava";

    @Config.Name("Water Block")
    @Config.Comment("The block used for water generation in water caves/caverns at and below the Liquid Altitude. " +
            "Defaults to regular water if an invalid block is given.")
    @Config.RequiresWorldRestart
    public static String waterblock = "minecraft:flowing_water";

    @Config.Ignore
    public static ConfigTest testSettings = new ConfigTest();
}
