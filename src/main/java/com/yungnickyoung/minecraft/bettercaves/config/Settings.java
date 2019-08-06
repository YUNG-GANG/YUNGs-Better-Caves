package com.yungnickyoung.minecraft.bettercaves.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Class containing information for Better Caves.
 * All fields are {@code static}.
 */
public class Settings {
    // MOD INFORMATION CONSTANTS
    // These will not be used if USE_META_DETA is true. Instead, data will be used from resources/mcmod.info
    public static final boolean USE_META_DATA = true;
    public static final String MOD_ID = "{@modid}";
    public static final String NAME = "{@name}";
    public static final String VERSION = "{@version}";

    public static final String COMMON_PROXY = "com.yungnickyoung.minecraft.bettercaves.proxy.CommonProxy";
    public static final String CLIENT_PROXY = "com.yungnickyoung.minecraft.bettercaves.proxy.ClientProxy";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final boolean DEBUG_LOG_ENABLED = false;

    private Settings() {} // private constructor prevents instantiation
}
