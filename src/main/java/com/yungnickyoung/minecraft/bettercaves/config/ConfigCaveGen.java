package com.yungnickyoung.minecraft.bettercaves.config;

import com.yungnickyoung.minecraft.bettercaves.config.cave.*;
import net.minecraftforge.common.config.Config;

public class ConfigCaveGen {
    @Config.Name("Caves")
    @Config.Comment("Parameters used in the generation of cave systems. Does not include the large caverns " +
            "found at low altitudes.")
    public ConfigSimplexCave simplexFractalCave = new ConfigSimplexCave();

    @Config.Name("Caverns")
    @Config.Comment("Parameters used in the generation of large caverns found at low altitudes.")
    public ConfigInvertedPerlinCavern invertedPerlinCavern = new ConfigInvertedPerlinCavern();

    @Config.Name("Lava Cavern Frequency")
    @Config.Comment("Determines how frequently Lava Caverns spawn.")
    public cavernFrequency lavaCavernFrequency = cavernFrequency.Rare;

    @Config.Name("Floored Cavern Frequency")
    @Config.Comment("Determines how frequently Floored Caverns spawn.")
    public cavernFrequency flooredCavernFrequency = cavernFrequency.Rare;

    public enum cavernFrequency {
         VeryRare, Rare, Common, VeryCommon
    }

    /* DEBUG OPTIONS -- UNUSED IN PRODUCTION */
    @Config.Ignore
    @Config.Name("Perlin Cavern Settings")
    @Config.Comment("Parameters used in the generation of inverted Perlin caverns")
    public ConfigPerlinCavern perlinCavern = new ConfigPerlinCavern();

    @Config.Ignore
    @Config.Name("Value Fractal Cave Settings")
    @Config.Comment("Parameters used in the generation of the value fractal caves")
    public ConfigValueFractalCave valueFractalCave = new ConfigValueFractalCave();

    @Config.Ignore
    @Config.Name("Cellular Cave Settings")
    @Config.Comment("Parameters used in the generation of the cellular caves")
    public ConfigCellularCave cellularCave = new ConfigCellularCave();

    @Config.Ignore
    @Config.Name("Perlin Cave Settings")
    @Config.Comment("Parameters used in the generation of the cellular caves")
    public ConfigPerlinCave perlinFractalCave = new ConfigPerlinCave();
}
