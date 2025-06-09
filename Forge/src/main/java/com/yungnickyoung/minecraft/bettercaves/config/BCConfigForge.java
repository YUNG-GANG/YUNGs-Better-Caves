package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class BCConfigForge {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ConfigGeneralForge general;

    static {
        BUILDER.push("YUNG's Better Caves");

        general = new ConfigGeneralForge(BUILDER);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}