package com.yungnickyoung.minecraft.bettercaves.config;

import com.yungnickyoung.minecraft.bettercaves.config.cave.*;
import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigCaveGen {
    public final ConfigCubicCave configCubicCave;
    public final ConfigSimplexCave configSimplexCave;
    public final ConfigLavaCavern configLavaCavern;
    public final ConfigFlooredCavern configFlooredCavern;
    public final ConfigVanillaCave configVanillaCave;
    public final ConfigWaterBiomes configWaterBiomes;

    public final ForgeConfigSpec.ConfigValue<String> caveBiomeSize;
    public final ForgeConfigSpec.ConfigValue<String> cavernBiomeSize;
    public final ForgeConfigSpec.ConfigValue<Integer> surfaceCutoff;

    public ConfigCaveGen(final ForgeConfigSpec.Builder builder) {
        builder.push("Cave Generation");

        caveBiomeSize = builder
                .comment(" Determines how large cave biomes are.\n Controls how long a cave system of a certain cave type" +
                        " extends before intersecting with a cave system of another type.\n Larger Biome Size = more " +
                        "cave interconnectivity.\n Accepted values: Small, Medium, Large, ExtraLarge")
                .worldRestart()
                .define("Cave Biome Size", "Large");

        cavernBiomeSize = builder
                .comment(" Determines how large cavern biomes are. This controls the average size of caverns." +
                        "\n Accepted values: Small, Medium, Large, ExtraLarge")
                .worldRestart()
                .define("Cavern Biome Size", "Small");

        surfaceCutoff = builder
                .comment(" This is the number of blocks from a given point on the surface that caves start to close off." +
                        "\n Decrease this to create more cave openings in the sides of mountains. Increase to create less" +
                        " above-surface openings.")
                .worldRestart()
                .define("Cave Surface Cutoff Depth", 10);

        configWaterBiomes = new ConfigWaterBiomes(builder);
        configVanillaCave = new ConfigVanillaCave(builder);
        configFlooredCavern = new ConfigFlooredCavern(builder);
        configLavaCavern = new ConfigLavaCavern(builder);
        configSimplexCave = new ConfigSimplexCave(builder);
        configCubicCave = new ConfigCubicCave(builder);

        builder.pop();
    }
}