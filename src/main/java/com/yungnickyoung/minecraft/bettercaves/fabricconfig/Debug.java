package com.yungnickyoung.minecraft.bettercaves.fabricconfig;

import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;

public class Debug {

    @ConfigEntry.Gui.Tooltip()
    @Comment("The visualizer creates worlds where there are no blocks except those indicating where caves\n" +
            "and caverns would be carved out in a regular world. This is useful for visualizing the kinds of\n" +
            "caves and caverns your current config options will create.\n" +
            "Type 1 Cave: Wooden Planks\n" +
            "Type 2 Cave: Cobblestone\n" +
            "Lava Cavern: Redstone Block\n" +
            "Floored Cavern: Gold Block\n" +
            "Surface Cave: Emerald Block\n" +
            "Vanilla Cave: Bricks\n" +
            "Default: false")
    public boolean enableDebugVisualizer = false;
}