package com.yungnickyoung.minecraft.bettercaves.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Class containing settings and information for Better Caves.
 * All fields are {@code static}.
 */
public class Settings {
    public static final String MOD_ID = "bettercaves";

    public static final boolean DEBUG_LOG_ENABLED = false;
    public static final boolean DEBUG_WORLD_GEN = false;

    private Settings() {} // private constructor prevents instantiation
}
