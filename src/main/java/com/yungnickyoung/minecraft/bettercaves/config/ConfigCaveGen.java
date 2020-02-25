package com.yungnickyoung.minecraft.bettercaves.config;

import com.yungnickyoung.minecraft.bettercaves.config.cave.*;
import com.yungnickyoung.minecraft.bettercaves.config.cavern.ConfigCaverns;
import net.minecraftforge.common.config.Config;

public class ConfigCaveGen {
    @Config.Name("Caves")
    @Config.Comment("Settings used in the generation of the caves in Better Caves.")
    public ConfigCaves caves = new ConfigCaves();

    @Config.Name("Caverns")
    @Config.Comment("Settings used in the generation of the caverns in Better Caves. Caverns are spacious caves that " +
            "spawn at low altitudes.")
    public ConfigCaverns caverns = new ConfigCaverns();

    @Config.Name("Water Regions")
    @Config.Comment("Parameters used in the generation of water-based caves and caverns. These are similar to " +
            "the regular Better Caves and Caverns, but with water instead of lava.")
    public ConfigWaterRegions waterRegions = new ConfigWaterRegions();

    @Config.Name("Liquid Altitude")
    @Config.Comment("Lava (or water in water regions) spawns at and below this y-coordinate.")
    @Config.RangeInt(min = 0, max = 255)
    @Config.RequiresWorldRestart
    public int liquidAltitude = 10;

    @Config.Name("Lava Block")
    @Config.Comment("The block used for lava generation at and below the Liquid Altitude. " +
            "Defaults to regular lava if an invalid block is given.")
    @Config.RequiresWorldRestart
    public String lavaBlock = "minecraft:lava";

    @Config.Name("Water Block")
    @Config.Comment("The block used for water generation in water caves/caverns at and below the Liquid Altitude. " +
            "Defaults to regular water if an invalid block is given.")
    @Config.RequiresWorldRestart
    public String waterBlock = "minecraft:water";

    @Config.Name("Prevent Cascading Gravel")
    @Config.Comment("Replace naturally generated floating gravel on the ocean floor with andesite. " +
            "Can prevent lag due to cascading gravel falling into caverns under the ocean.")
    @Config.RequiresWorldRestart
    public boolean replaceFloatingGravel = false;
}
