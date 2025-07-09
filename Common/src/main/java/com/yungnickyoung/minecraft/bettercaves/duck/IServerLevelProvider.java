package com.yungnickyoung.minecraft.bettercaves.duck;

import net.minecraft.server.level.ServerLevel;

public interface IServerLevelProvider {
    ServerLevel getServerLevel();
    void setServerLevel(ServerLevel serverLevel);
}
