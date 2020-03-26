package com.yungnickyoung.minecraft.bettercaves.config.cave;

import net.minecraftforge.common.config.Config;

public class ConfigVanillaCave {
    @Config.Name("Enable Vanilla Caves")
    @Config.Comment(
            "Set to true to enable vanilla cave generation alongside Better Caves.\n" +
            "    Note that even if this is enabled, vanilla caves will not generate unless Cave Spawn Chance\n" +
            "    is less than 100. Vanilla caves will spawn in the chunks with no Better Caves.\n" +
            "    WARNING - THIS WILL CAUSE CHUNKS TO BLEND INCORRECTLY WITH BETTER CAVES, RESULTING IN NOTICEABLE\n" +
            "    UNPROCESSED CHUNK FORMATIONS UNDERGROUND. ONLY USE IF YOU ARE OK WITH THIS.\n" +
            "Default: false")
    public boolean enableVanillaCaves = false;

    @Config.Name("Enable Ravines")
    @Config.Comment(
            "Set to true to enable ravine generation.\n" +
            "Default: true")
    public boolean enableVanillaRavines = true;

    /* Couldnt get to work, ignore for now. Seems like Forge is bugged for lakes gen? */
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
