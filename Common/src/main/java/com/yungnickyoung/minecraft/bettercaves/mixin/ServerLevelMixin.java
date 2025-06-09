package com.yungnickyoung.minecraft.bettercaves.mixin;

import com.yungnickyoung.minecraft.bettercaves.duck.IMasterControllerProvider;
import com.yungnickyoung.minecraft.bettercaves.worldgen.controller.MasterController;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ServerLevel.class)
public class ServerLevelMixin implements IMasterControllerProvider {
    @Unique
    private MasterController masterController;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void bettercaves$attachMasterController(MinecraftServer $$0, Executor $$1, LevelStorageSource.LevelStorageAccess $$2,
                                                  ServerLevelData $$3, ResourceKey $$4, LevelStem $$5, ChunkProgressListener $$6,
                                                  boolean $$7, long $$8, List $$9, boolean $$10, RandomSequences $$11, CallbackInfo ci) {
//        this.masterController = new MasterController();
    }

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
