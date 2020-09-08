package com.yungnickyoung.minecraft.bettercaves.config;


import com.yungnickyoung.minecraft.bettercaves.config.cavern.ConfigCaverns;

/**
 * Configuration options for Better Caves.
 * <p>
 * This class and all of its fields provide default config values.
 * The values here are not actually used directly - they are baked into a ConfigHolder each time a new
 * ConfigHolder is created. Separate ConfigHolders are created for each dimension. This allows any or all
 * config values to be overridden differently for each dimension.
 */
public final class Configuration {

    public static final ConfigUndergroundGen caveSettings;
    public static final ConfigCaverns caverns;

    static {
        caveSettings = new ConfigUndergroundGen();
        caverns = new ConfigCaverns();
    }
}
