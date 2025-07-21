package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class BCConfigForge {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ConfigLiquidRegionsForge liquidRegions;

    static {
        BUILDER.push("YUNG's Better Caves");

        liquidRegions = new ConfigLiquidRegionsForge(BUILDER);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}