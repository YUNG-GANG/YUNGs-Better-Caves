package com.yungnickyoung.minecraft.bettercaves.mixin;

import com.yungnickyoung.minecraft.bettercaves.duck.IMasterControllerProvider;
import com.yungnickyoung.minecraft.bettercaves.worldgen.controller.MasterController;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerLevel.class)
public class ServerLevelMixin implements IMasterControllerProvider {
    @Unique
    private MasterController masterController;

    @Override
    @Unique
    public MasterController getMasterController() {
        return this.masterController;
    }

    @Override
    @Unique
    public void setMasterController(MasterController masterController) {
        this.masterController = masterController;
    }
}
