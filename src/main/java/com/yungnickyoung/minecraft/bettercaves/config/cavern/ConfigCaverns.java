package com.yungnickyoung.minecraft.bettercaves.config.cavern;

import com.yungnickyoung.minecraft.bettercaves.config.cave.ConfigFlooredCavern;
import com.yungnickyoung.minecraft.bettercaves.config.cave.ConfigLavaCavern;
import com.yungnickyoung.minecraft.bettercaves.enums.CaveBiomeSize;
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

    @Config.Name("Cavern Biome Size")
    @Config.Comment("Determines how large cavern biomes are. This controls the average size of caverns.")
    @Config.RequiresWorldRestart
    public CaveBiomeSize cavernBiomeSize = CaveBiomeSize.Small;

    @Config.Name("Enable Smooth Cavern Edges")
    @Config.Comment("Smooths the transition boundary between Cave and Cavern biomes, reducing straight walls found" +
            " along the edges of caverns in order to create a more natural feel. Disable for pre-v2.0 behavior.")
    @Config.RequiresWorldRestart
    public boolean enableBoundarySmoothing = true;
}
