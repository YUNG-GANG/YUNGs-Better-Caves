package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.fml.config.ModConfig;

/**
 * This bakes the config values to normal fields
 *
 * @author Cadiboo
 * It can be merged into the main BetterCavesConfig class, but is separate because of personal preference and to keep the code organised
 */
public final class ConfigHelper {

    // We store a reference to the ModConfigs here to be able to change the values in them from our code
    // (For example from a config GUI)
    private static ModConfig clientConfig;

    public static void bakeClient(final ModConfig config) {
        clientConfig = config;
        BetterCavesConfig.lavaDepth = ConfigHolder.CLIENT.lavaDepth.get();
    }

    private static void setValueAndSave(final ModConfig modConfig, final String path, final Object newValue) {
        modConfig.getConfigData().set(path, newValue);
        modConfig.save();
    }

}