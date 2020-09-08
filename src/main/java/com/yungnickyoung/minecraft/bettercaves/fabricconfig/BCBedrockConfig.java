package com.yungnickyoung.minecraft.bettercaves.fabricconfig;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;

@Config(name = "better-caves")
public class BCBedrockConfig {

    @ConfigEntry.Gui.Tooltip()
    @Comment("Replaces the usual bedrock generation pattern with flat layers.\n" +
            "Activates in all whitelisted dimension, where applicable. The End is unaffected.\n" +
            "Default: true")
    public Boolean flattenBedrock = true;

    @ConfigEntry.Gui.Tooltip()
    @Comment("The width of the bedrock layer. Only works if Flatten Bedrock is true." +
            "\nRange: 0 ~ 256")
    public int bedrockLayerWidth = 1;




}
