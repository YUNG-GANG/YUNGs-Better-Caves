package com.yungnickyoung.minecraft.bettercaves.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class containing meta information for Better Caves.
 * This class may not be instantiated - all of its members are {@code static}, and thus may be accessed
 * using, for example, {@code Configuration.MODID}.
 */
public class Configuration {
    // MOD INFORMATION CONSTANTS
    // These will not be used if 'useMetaData' is true. Instead, data will be used from resources/mcmod.info
    public static final String MOD_ID = "bettercaves";
    public static final String NAME = "Better Caves";
    public static final String VERSION = "0.0.1";

    public static final String COMMON_PROXY = "com.yungnickyoung.minecraft.bettercaves.proxy.CommonProxy";
    public static final String CLIENT_PROXY = "com.yungnickyoung.minecraft.bettercaves.proxy.ClientProxy";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
}
