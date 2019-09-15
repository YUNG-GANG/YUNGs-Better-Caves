package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigDebug {
    public final ForgeConfigSpec.ConfigValue<Boolean> debugVisualizer;

    public ConfigDebug(final ForgeConfigSpec.Builder builder) {
        builder.push("Debug Settings");

        debugVisualizer = builder
                .comment(" The visualizer creates worlds where there are no blocks except those indicating where caves" +
                        " and caverns would be carved out in a regular world. This is useful for visualizing the kinds of" +
                        "caves and caverns your current config options will create.\n" +
                        " Type 1 Cave: Quartz Block\n" +
                        " Type 2 Cave: Cobblestone\n" +
                        " Lava Cavern: Redstone Block\n" +
                        " Floored Cavern: Gold Block")
                .worldRestart()
                .define("Enable DEBUG Visualizer", false);

        builder.pop();
    }
}
