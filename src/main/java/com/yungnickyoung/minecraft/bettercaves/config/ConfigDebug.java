package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigDebug {
    public final ForgeConfigSpec.ConfigValue<Boolean> debugVisualizer;

    public ConfigDebug(final ForgeConfigSpec.Builder BUILDER) {
        BUILDER
            .comment(
                "##########################################################################################################\n" +
                "# Don't mess with these settings for normal gameplay.\n" +
                "##########################################################################################################")
            .push("DEBUG Settings");

        debugVisualizer = BUILDER
                .comment(
                    " The visualizer creates worlds where there are no blocks except those indicating where caves\n" +
                    "     and caverns would be carved out in a regular world. This is useful for visualizing the kinds of\n" +
                    "     caves and caverns your current config options will create.\n" +
                    "     Type 1 Cave: Wooden Planks\n" +
                    "     Type 2 Cave: Cobblestone\n" +
                    "     Lava Cavern: Redstone Block\n" +
                    "     Floored Cavern: Gold Block\n" +
                    "     Surface Cave: Emerald Block\n" +
                    "     Vanilla Cave: Bricks\n" +
                    " Default: false")
                .worldRestart()
                .define("Enable DEBUG Visualizer", false);

        BUILDER.pop();
    }
}
