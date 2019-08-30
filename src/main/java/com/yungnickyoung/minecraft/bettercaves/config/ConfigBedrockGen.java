package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.common.config.Config;

public class ConfigBedrockGen {
    public Overworld overworld = new Overworld();
    public Nether nether = new Nether();

    public static class Overworld {
        @Config.Name("Flatten Bedrock")
        @Config.Comment("Set this to true to replace the usual bedrock generation pattern with flat layers.")
        @Config.RequiresWorldRestart
        public boolean flattenBedrock = true;

        @Config.Name("Bedrock Layer Width")
        @Config.Comment("The width of the bedrock layer. Only works if Flatten Bedrock is true.")
        @Config.RequiresWorldRestart
        @Config.SlidingOption
        @Config.RangeInt(min = 0, max = 256)
        public int bedrockWidth = 1;
    }

    public static class Nether {
        @Config.Name("Flatten Bedrock")
        @Config.Comment("Set this to true to replace the usual bedrock generation pattern with flat layers.")
        @Config.RequiresWorldRestart
        public boolean flattenBedrock = true;

        @Config.Name("Bedrock Layer Width - Bottom")
        @Config.Comment("The width of the bedrock layer at the bottom of the nether. Only works if Flatten Bedrock is true.")
        @Config.RequiresWorldRestart
        @Config.SlidingOption
        @Config.RangeInt(min = 0, max = 64)
        public int bedrockWidthBottom = 1;

        @Config.Name("Bedrock Layer Width - Top")
        @Config.Comment("The width of the bedrock layer at the top of the nether. Only works if Flatten Bedrock is true.")
        @Config.RequiresWorldRestart
        @Config.SlidingOption
        @Config.RangeInt(min = 0, max = 64)
        public int bedrockWidthTop = 1;
    }
}