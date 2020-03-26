package com.yungnickyoung.minecraft.bettercaves.config.cavern;

import com.yungnickyoung.minecraft.bettercaves.enums.RegionSize;
import net.minecraftforge.common.config.Config;

public class ConfigCaverns {
    @Config.Name("Liquid Caverns")
    @Config.Comment(
            "Settings used in the generation of Liquid Caverns found at low altitudes.\n" +
            "    These are caverns where the floor is predominantly water or lava.")
    public ConfigLiquidCavern liquidCavern = new ConfigLiquidCavern();

    @Config.Name("Floored Caverns")
    @Config.Comment(
            "Parameters used in the generation of Floored Caverns.\n" +
            "    These have much more ground to walk on than Liquid Caverns.")
    public ConfigFlooredCavern flooredCavern = new ConfigFlooredCavern();

    @Config.Name("Cavern Spawn Chance")
    @Config.Comment(
            "Percent chance of caverns spawning in a given region.\n" +
            "Default: caverns spawn in 25% of regions.")
    @Config.RangeDouble(min = 0, max = 100)
    @Config.RequiresWorldRestart
    public float cavernSpawnChance = 25;

    @Config.Name("Cavern Region Size")
    @Config.Comment(
            "Determines how large cavern regions are. This controls the average size of caverns.\n" +
            "Default: Small")
    @Config.RequiresWorldRestart
    public RegionSize cavernRegionSize = RegionSize.Small;

    @Config.Name("Cavern Region Size Custom Value")
    @Config.Comment(
            "Custom value for cavern region size. Only works if Cavern Region Size is set to Custom. " +
            "    Smaller value = larger regions. This value is very sensitive to change.\n" +
            "    Provided values:\n" +
            "        Small: 0.01\n" +
            "        Medium: 0.007\n" +
            "        Large: 0.005\n" +
            "        ExtraLarge: 0.001\n" +
            "Default: 0.01")
    @Config.RangeDouble(min = 0, max = .05)
    @Config.RequiresWorldRestart
    public float customRegionSize = 0.01f;
}
