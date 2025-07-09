package com.yungnickyoung.minecraft.bettercaves.worldgen.controller;

import com.yungnickyoung.minecraft.bettercaves.worldgen.LiquidRegions;
import net.minecraft.server.level.ServerLevel;

import java.util.HashMap;
import java.util.Map;

public class LiquidRegionController {
    private static LiquidRegionController INSTANCE;

    public static LiquidRegionController getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LiquidRegionController();
        }
        return INSTANCE;
    }

    private final Map<ServerLevel, LiquidRegions> map = new HashMap<>();

    public LiquidRegions getLiquidRegionsForServerLevel(ServerLevel serverLevel) {
        return this.map.computeIfAbsent(serverLevel, LiquidRegions::new);
    }
}
