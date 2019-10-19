package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.common.config.Config;

public class ConfigBedrockGen {
    @Config.Name("Flatten Bedrock")
    @Config.Comment("Set this to true to replace the usual bedrock generation pattern with flat layers." +
            " Activates in all whitelisted dimension, where applicable. The Nether and End are unaffected.")
    @Config.RequiresWorldRestart
    public boolean flattenBedrock = true;

    @Config.Name("Bedrock Layer Width")
    @Config.Comment("The width of the bedrock layer. Only works if Flatten Bedrock is true.")
    @Config.RequiresWorldRestart
    @Config.RangeInt(min = 0, max = 256)
    public int bedrockWidth = 1;
}