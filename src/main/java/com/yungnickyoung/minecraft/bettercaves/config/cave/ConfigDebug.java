package com.yungnickyoung.minecraft.bettercaves.config.cave;

import net.minecraftforge.common.config.Config;

public class ConfigDebug {
    @Config.Name("Enable DEBUG Visualizer")
    @Config.Comment("The visualizer creates worlds where there are no blocks except those indicating where caves" +
            " and caverns would be carved out in a regular world. This is useful for visualizing the kinds of" +
            "caves and caverns your current config options will create.")
    public boolean debugVisualizer = false;
}
