package com.yungnickyoung.minecraft.bettercaves.config.cave;

import com.yungnickyoung.minecraft.bettercaves.enums.WaterRegionFrequency;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import net.minecraftforge.common.config.Config;

public class ConfigWaterRegions {
    @Config.Name("Water Region Frequency")
    @Config.Comment("Determines how frequently water regions spawn.")
    @Config.RequiresWorldRestart
    public WaterRegionFrequency waterRegionFrequency = WaterRegionFrequency.Normal;

    @Config.Name("Water Region Frequency Custom Value")
    @Config.Comment("Custom value for water region frequency. Only works if Water Region Frequency is set to Custom. 0 = 0% chance of spawning, " +
            "1.0 = 100% chance of spawning. The value does not scale linearly. \nProvided values:\n" +
            "None: 0\n" +
            "Rare: 0.3\n" +
            "Normal: 0.425\n" +
            "Common: 0.55\n" +
            "VeryCommon: 0.65\n" +
            "Always: 1.0")
    @Config.RangeDouble(min = 0, max = 1)
    @Config.RequiresWorldRestart
    public float customFrequency = 0.425f;
}
