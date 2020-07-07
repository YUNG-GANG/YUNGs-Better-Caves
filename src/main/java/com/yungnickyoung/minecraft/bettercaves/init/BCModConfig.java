package com.yungnickyoung.minecraft.bettercaves.init;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.BCSettings;
import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.IOException;

public class BCModConfig {
    public static void init() {
        // Create custom config folder if not created already
        BetterCaves.customConfigDir = new File(FMLPaths.CONFIGDIR.get().toString(), BCSettings.CUSTOM_CONFIG_PATH);
        try {
            String filePath = BetterCaves.customConfigDir.getCanonicalPath();
            if (BetterCaves.customConfigDir.mkdir()) {
                BetterCaves.LOGGER.info("Creating directory for dimension-specific Better Caves configs at " + filePath);
            }
        } catch (IOException e) {
            BetterCaves.LOGGER.error("ERROR creating Better Caves config directory.");
        }

        // Register config with Forge
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, Configuration.SPEC, "bettercaves-1_16_1.toml");
    }
}
