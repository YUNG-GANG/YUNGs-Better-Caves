package com.yungnickyoung.minecraft.bettercaves.duck;

import com.yungnickyoung.minecraft.bettercaves.worldgen.controller.MasterController;

public interface IMasterControllerProvider {
    MasterController getMasterController();
    void setMasterController(MasterController masterController);
}
