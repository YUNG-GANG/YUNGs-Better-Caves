package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.common.ForgeConfigSpec;
//Holds our Ocean Config comments.
public class OceanConfig {
    final ForgeConfigSpec.ConfigValue<String> oceanFloorSetting;

    OceanConfig(final ForgeConfigSpec.Builder builder) {
        builder.push("Ocean Settings");

        oceanFloorSetting = builder
                .comment("Determines the amount of gravel to be replaced with Andesite on the ocean floor.\n Prevents lag that can occur in freshly loaded chunks with falling gravel in oceans." +
                        "There are 3 acceptable config values: \n'default', replaces any gravel blocks in cave ceiling/ocean floor. \n'vanilla', no change. \n'replaceall', Changes gravel on ocean floors to andesite & ores. \n LOWERCASE LETTERS ONLY")
                .worldRestart()
                .define("OceanFloor", "default");
        builder.pop();
    }
}
