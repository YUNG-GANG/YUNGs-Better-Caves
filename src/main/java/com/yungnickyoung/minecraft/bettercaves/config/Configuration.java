package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.common.config.Config;

/**
 * Configuration options for Better Caves.
 * <p>
 * Fields not annotated with @Ignore are visible to users.
 * Some @Ignore fields are not used at all, while the rest are used but annotated as such
 * in order to keep them hidden from the user, as they are too delicate to allow the user
 * to mess with.
 * <p>
 * This class and all of its fields provide default config values, as well as an in-game config GUI.
 * The values here are not actually used directly - they are baked into a ConfigHolder each time a new
 * ConfigHolder is created. Separate ConfigHolders are created for each dimension. This allows any or all
 * config values to be overridden differently for each dimension.
 */
@Config(modid = BCSettings.MOD_ID, name = "bettercaves-1_12_2")
public class Configuration {
    @Config.Name("Underground Generation")
    @Config.Comment("Configure settings related to caves, caverns, ravines and more.")
    public static ConfigUndergroundGen caveSettings = new ConfigUndergroundGen();

    @Config.Name("Bedrock Generation")
    @Config.Comment("Configure how bedrock generates in the overworld and nether.")
    public static ConfigBedrockGen bedrockSettings = new ConfigBedrockGen();

    @Config.Name("Debug settings")
    @Config.Comment("Don't mess with these settings for normal gameplay.")
    public static ConfigDebug debugsettings = new ConfigDebug();

    @Config.Name("Whitelisted Dimension IDs")
    @Config.Comment("List of ID's of dimensions that will have Better Caves. Ignored if Global Whitelisting is enabled.")
    @Config.RequiresWorldRestart
    public static int[] whitelistedDimensionIDs = {0};

    @Config.Name("Enable Global Whitelist")
    @Config.Comment(
            "Automatically enables Better Caves in every possible dimension, except for the End.\n" +
            "    If this is enabled, the Whitelisted Dimension IDs option is ignored.\n" +
            "Default: false")
    @Config.RequiresWorldRestart
    public static boolean enableGlobalWhitelist = false;
}
