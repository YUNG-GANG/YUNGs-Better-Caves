package com.yungnickyoung.minecraft.bettercaves.config;

/**
 * Class containing settings and information for Better Caves.
 * All fields are {@code static}.
 */
public class BCSettings {
    public static final String MOD_ID = "bettercaves";

    public static final String CUSTOM_CONFIG_PATH = "bettercaves-1_15_2";

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
