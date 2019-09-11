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

    @Config.Name("Enable Ravines")
    @Config.Comment("Set to true to enable ravine generation.")
    public boolean enableVanillaRavines = true;


    /* Couldnt get to work, ignore for now. Seems like Forge/MC is bugged for lakes gen? */
    @Config.Ignore
    @Config.Name("Enable Water Lakes")
    @Config.Comment("Set to true to enable small water lakes found on the surface and underground in vanilla" +
            " Minecraft.")
    public boolean enableWaterLakes = true;

    @Config.Ignore
    @Config.Name("Enable Lava Lakes")
    @Config.Comment("Set to true to enable small lava lakes found on the surface and underground in vanilla" +
            " Minecraft.")
    public boolean enableLavaLakes = true;
}
