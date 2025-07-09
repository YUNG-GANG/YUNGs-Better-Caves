package com.yungnickyoung.minecraft.bettercaves.worldgen.liquidregion;

import net.minecraft.server.level.ServerLevel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LiquidRegionsController {
    private static LiquidRegionsController INSTANCE;

    public static LiquidRegionsController getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LiquidRegionsController();
        }
        return INSTANCE;
    }

    private final ConcurrentMap<ServerLevel, LiquidRegions> map = new ConcurrentHashMap<>();

    public LiquidRegions getLiquidRegionsForServerLevel(ServerLevel serverLevel) {
        return this.map.computeIfAbsent(serverLevel, LiquidRegions::new);
    }
}
