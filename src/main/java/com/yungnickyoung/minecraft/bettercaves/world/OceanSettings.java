package com.yungnickyoung.minecraft.bettercaves.world;

import com.yungnickyoung.minecraft.bettercaves.config.BetterCavesConfig;
//This class houses the Switch for Ocean Flooring and soon the Ocean Cave Flooding settings.
public class OceanSettings {

    public void oceanFloor() {

        switch (BetterCavesConfig.oceanFloorSetting) {
            case "vanilla":
                break;
            case "default":
                break;
            case "replaceAll":
                break;
        }

    }
}