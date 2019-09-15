package com.yungnickyoung.minecraft.bettercaves.config.cave;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigVanillaCave {
    public final ForgeConfigSpec.ConfigValue<Boolean> enableVanillaCaves;
    public final ForgeConfigSpec.ConfigValue<Boolean> enableVanillaRavines;

    public ConfigVanillaCave(final ForgeConfigSpec.Builder builder) {
        builder.push("Type 2 Caves (Simplex)");

        enableVanillaCaves = builder
                .comment(" Set to true to enable vanilla cave generation alongside Better Caves. Note that even if this is" +
                        " enabled, vanilla caves  will not generate unless either Type 1 Caves or Type 2 Caves have Cave" +
                        " Frequency set to anything but VeryCommon.\n" +
                        " WARNING - THIS WILL CAUSE CHUNKS TO BLEND INCORRECTLY WITH BETTER CAVES, RESULTING IN NOTICEABLE UNPROCESSED" +
                        " CHUNK FORMATIONS UNDERGROUND. ONLY USE IF YOU ARE OK WITH THIS.")
                .worldRestart()
                .define("Enable Vanilla Caves", false);

        enableVanillaRavines = builder
                .comment(" Set to true to enable ravine generation.")
                .worldRestart()
                .define("Enable Ravines", true);

        builder.pop();
    }
}
