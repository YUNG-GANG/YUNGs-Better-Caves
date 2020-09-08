package com.yungnickyoung.minecraft.bettercaves.fabricconfig;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;

@Config(name = "better-caves")
public class BCConfig implements ConfigData {

    @ConfigEntry.Category("Bedrock_Generation")
    @Comment("Configure how bedrock generates.")
    @ConfigEntry.Gui.TransitiveObject
    public BCBedrockConfig bedrockConfig = new BCBedrockConfig();


    @ConfigEntry.Category("Underground_Generation")
    @Comment("Configure settings related to caves, caverns, ravines and more.")
    @ConfigEntry.Gui.TransitiveObject
    public BCUndergroundGeneration underGroundGeneration = new BCUndergroundGeneration();

    @ConfigEntry.Gui.Tooltip()
    @Comment("List of dimensions that will have Better Caves. Ignored if Global Whitelisting is enabled.\n" +
            "List must be comma-separated values enclosed in brackets.\n" +
            "Entries must have the mod namespace included.\n" +
            "For example: \"[minecraft:overworld, minecraft:the_nether, rats:ratlantis]\"\n" +
    "Default: \"minecraft:overworld\"")
    public String whiteListedDimensions = "[minecraft:overworld]";

    @ConfigEntry.Gui.Tooltip()
    @Comment("Automatically enables Better Caves in every possible dimension.\n" +
            "If this is enabled, the Whitelisted Dimension IDs option is ignored.\n" +
            "Default: false")
    public boolean globalWhiteList = false;


}
