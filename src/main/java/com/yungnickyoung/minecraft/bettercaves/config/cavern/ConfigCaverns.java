package com.yungnickyoung.minecraft.bettercaves.config.cavern;

import com.yungnickyoung.minecraft.bettercaves.enums.RegionSize;
import net.minecraftforge.common.config.Config;

public class ConfigCaverns {
    @Config.Name("Lava Caverns")
    @Config.Comment("Parameters used in the generation of Lava Caverns found at low altitudes. These are caverns" +
            " where the floor is predominantly lava.")
    public ConfigLavaCavern lavaCavern = new ConfigLavaCavern();

    @Config.Name("Floored Caverns")
    @Config.Comment("Parameters used in the generation of Floored Caverns. These have much more ground to walk on" +
            " than Lava Caverns.")
    public ConfigFlooredCavern flooredCavern = new ConfigFlooredCavern();

    @Config.Name("Cavern Region Size")
    @Config.Comment("Determines how large cavern regions are. This controls the average size of caverns.")
    @Config.RequiresWorldRestart
    public RegionSize cavernRegionSize = RegionSize.Small;
}
