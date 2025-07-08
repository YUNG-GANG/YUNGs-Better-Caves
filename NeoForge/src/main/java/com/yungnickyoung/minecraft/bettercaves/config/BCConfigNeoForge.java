package com.yungnickyoung.minecraft.bettercaves.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class BCConfigNeoForge {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ConfigLiquidRegionsNeoForge liquidRegions;

    static {
        BUILDER.push("YUNG's Better Caves");

        liquidRegions = new ConfigLiquidRegionsNeoForge(BUILDER);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}