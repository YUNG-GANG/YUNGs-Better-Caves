package com.yungnickyoung.minecraft.bettercaves.config.cave;

import net.minecraftforge.common.config.Config;

public class ConfigVanillaCave {
    @Config.Name("Enable Vanilla Caves")
    @Config.Comment("Set to true to enable vanilla cave generation alongside Better Caves. Note that even if this is" +
            " enabled, vanilla caves  will not generate unless either Type 1 Caves or Type 2 Caves have Cave" +
            " Frequency set to anything but VeryCommon.\n" +
            "WARNING - THIS WILL CAUSE CHUNKS TO BLEND INCORRECTLY WITH BETTER CAVES, RESULTING IN NOTICEABLE UNPROCESSED" +
            " CHUNK FORMATIONS UNDERGROUND. ONLY USE IF YOU ARE OK WITH THIS.")
    public boolean enableVanillaCaves = false;
}
