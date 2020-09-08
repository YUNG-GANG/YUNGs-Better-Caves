package com.yungnickyoung.minecraft.bettercaves.config.cavern;


public class ConfigCaverns {
    public final ConfigLiquidCavern liquidCavern;
    public final ConfigFlooredCavern flooredCavern;

    public ConfigCaverns() {
        liquidCavern = new ConfigLiquidCavern();
        flooredCavern = new ConfigFlooredCavern();
    }
}
