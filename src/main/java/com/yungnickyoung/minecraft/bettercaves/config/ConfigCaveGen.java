package com.yungnickyoung.minecraft.bettercaves.config;

import com.yungnickyoung.minecraft.bettercaves.config.cave.*;
import com.yungnickyoung.minecraft.bettercaves.config.cavern.ConfigCaverns;
import com.yungnickyoung.minecraft.bettercaves.enums.CaveBiomeSize;
import net.minecraftforge.common.config.Config;

public class ConfigCaveGen {
    @Config.Name("Caves")
    @Config.Comment("Settings used in the generation of the caves in Better Caves.")
    public ConfigCaves caves = new ConfigCaves();

    @Config.Name("Caverns")
    @Config.Comment("Settings used in the generation of the caverns in Better Caves. Caverns are spacious caves that " +
            "spawn at low altitudes.")
    public ConfigCaverns caverns = new ConfigCaverns();

    @Config.Name("Water Biomes")
    @Config.Comment("Parameters used in the generation of water-based caves and caverns. These are similar to " +
            "the regular Better Caves and Caverns, but with water instead of lava.")
    public ConfigWaterBiomes waterBiomes = new ConfigWaterBiomes();

    @Config.Name("Whitelisted Dimension IDs")
    @Config.Comment("List of ID's of dimensions that will have Better Caves")
    @Config.RequiresWorldRestart
    public int[] whitelistedDimensionIDs = {0};
}
