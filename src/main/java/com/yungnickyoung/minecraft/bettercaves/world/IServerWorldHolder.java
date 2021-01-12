package com.yungnickyoung.minecraft.bettercaves.world;

import net.minecraft.world.server.ServerWorld;

public interface IServerWorldHolder {
    void setServerWorld(ServerWorld serverWorld);
    ServerWorld getServerWorld();
    void setCarved(boolean carved);
    boolean hasBeenCarved();
}