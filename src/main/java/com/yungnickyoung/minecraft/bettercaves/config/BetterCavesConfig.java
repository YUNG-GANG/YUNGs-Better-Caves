package com.yungnickyoung.minecraft.bettercaves.config;

import com.yungnickyoung.minecraft.bettercaves.enums.CaveBiomeSize;

/**
 * This holds the baked (runtime) values for our config.
 * These values should never be from changed outside this package.
 */
public final class BetterCavesConfig {
    public static int lavaDepth;

    // Cave gen vars
    public static String caveBiomeSize;
    public static String cavernBiomeSize;
    public static int    surfaceCutoff;

    // Cubic Cave vars
    public static int     cubicCaveBottom;
    public static double  cubicYComp;
    public static double  cubicXZComp;
    public static String  cubicCaveFreq;
    public static float   cubicNoiseThreshold    = 0.95f;
    public static int     cubicFractalOctaves    = 1;
    public static float   cubicFractalGain       = .3f;
    public static float   cubicFractalFreq       = .03f;
    public static boolean cubicEnableTurbulence  = false;
    public static int     cubicTurbulenceOctaves = 3;
    public static float   cubicTurbulenceGain    = 45f;
    public static float   cubicTurbulenceFreq    = .01f;
    public static int     cubicNumGenerators     = 2;
    public static boolean cubicYAdjust           = true;
    public static float   cubicYAdjustF1         = .9f;
    public static float   cubicYAdjustF2         = .8f;

    // Simplex Cave vars
    public static int     simplexCaveBottom;
    public static double  simplexYComp;
    public static double  simplexXZComp;
    public static String  simplexCaveFreq;
    public static float   simplexNoiseThreshold    = 0.86f;
    public static int     simplexFractalOctaves    = 1;
    public static float   simplexFractalGain       = .3f;
    public static float   simplexFractalFreq       = .017f;
    public static boolean simplexEnableTurbulence  = false;
    public static int     simplexTurbulenceOctaves = 3;
    public static float   simplexTurbulenceGain    = 45f;
    public static float   simplexTurbulenceFreq    = .01f;
    public static int     simplexNumGenerators     = 2;
    public static boolean simplexYAdjust           = true;
    public static float   simplexYAdjustF1         = .95f;
    public static float   simplexYAdjustF2         = .9f;

    // Lava Cavern vars
    public static int     lavaCavernCaveTop;
    public static int     lavaCavernCaveBottom;
    public static double  lavaCavernYComp;
    public static double  lavaCavernXZComp;
    public static String  lavaCavernCaveFreq;
    public static float   lavaCavernNoiseThreshold = 0.7f;
    public static int     lavaCavernFractalOctaves = 1;
    public static float   lavaCavernFractalGain    = .3f;
    public static float   lavaCavernFractalFreq    = .03f;
    public static int     lavaCavernNumGenerators  = 2;

    // Lava Cavern vars
    public static int     flooredCavernCaveTop;
    public static int     flooredCavernCaveBottom;
    public static double  flooredCavernYComp;
    public static double  flooredCavernXZComp;
    public static String  flooredCavernCaveFreq;
    public static float   flooredCavernNoiseThreshold = 0.7f;
    public static int     flooredCavernFractalOctaves = 1;
    public static float   flooredCavernFractalGain    = .3f;
    public static float   flooredCavernFractalFreq    = .03f;
    public static int     flooredCavernNumGenerators  = 2;

    // Vanilla gen vars
    public static boolean enableVanillaRavines;
    public static boolean enableVanillaUnderwaterRavines;

    // Water biome vars
    public static boolean enableWaterBiomes;
    public static String waterBiomeFreq;

    // Water Cavern vars
    public static double  waterCavernYComp;
    public static double  waterCavernXZComp;
    public static float   waterCavernNoiseThreshold = 0.75f;
    public static int     waterCavernFractalOctaves = 1;
    public static float   waterCavernFractalGain    = .3f;
    public static float   waterCavernFractalFreq    = .03f;
    public static int     waterCavernNumGenerators  = 2;

    // Debug vars
    public static boolean enableDebugVisualizer;
}