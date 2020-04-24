package com.yungnickyoung.minecraft.bettercaves.config;

/**
 * Settings and information for Better Caves.
 * All fields are {@code static}.
 *
 * For the user-facing configuration options, see the Configuration class.
 */
public class BCSettings {
    /** MOD INFORMATION CONSTANTS
     * These will not be used if USE_META_DATA is true. Instead, data will be used from resources/mcmod.info.
     * Strings like {@varName} are set from the gradle build script.
     */
    public static final boolean USE_META_DATA = true;
    public static final String MOD_ID = "{@modid}";
    public static final String NAME = "{@name}";
    public static final String VERSION = "{@version}";

    public static final String SERVER_PROXY = "com.yungnickyoung.minecraft.bettercaves.proxy.ServerProxy";
    public static final String CLIENT_PROXY = "com.yungnickyoung.minecraft.bettercaves.proxy.ClientProxy";

    public static final String CUSTOM_CONFIG_PATH = "bettercaves-1_12_2";

    public static final int SUB_CHUNK_SIZE = 4;
    public static final float[] START_COEFFS = new float[SUB_CHUNK_SIZE];
    public static final float[] END_COEFFS = new float[SUB_CHUNK_SIZE];

    static {
        // Calculate coefficients used for bilinear interpolation during noise calculation.
        // These are initialized one time here to avoid redundant computation later on.
        for (int n = 0; n < SUB_CHUNK_SIZE; n++) {
            START_COEFFS[n] = (float)(SUB_CHUNK_SIZE - 1 - n) / (SUB_CHUNK_SIZE - 1);
            END_COEFFS[n] = (float)(n) / (SUB_CHUNK_SIZE - 1);
        }
    }

    private BCSettings() {} // private constructor prevents instantiation
}
