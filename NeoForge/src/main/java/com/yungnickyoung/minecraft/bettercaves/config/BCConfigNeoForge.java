package com.yungnickyoung.minecraft.bettercaves.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class BCConfigNeoForge {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ConfigUndergroundGen caveSettings;
//    public static final ConfigBedrockGen bedrockSettings;
//    public static final ConfigDebug debugSettings;

    static {
        BUILDER.push("YUNG's Better Caves");

        caveSettings = new ConfigUndergroundGen(BUILDER);
//        bedrockSettings = new ConfigBedrockGen(BUILDER);
//        debugSettings = new ConfigDebug(BUILDER);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}