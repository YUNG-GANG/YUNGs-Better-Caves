package com.yungnickyoung.minecraft.bettercaves.config;

/**
 * This holds the baked (runtime) values for our config.
 * These values should never be from changed outside this package.
 */
public final class BetterCavesConfig {
    // General
    public static int liquidAltitude;
    public static String lavaBlock;
    public static String waterBlock;

    // Cave gen vars
    public static String caveRegionSize;
    public static String cavernRegionSize;
    public static int    surfaceCutoff;
    public static int    maxCaveAltitude;

    // Cubic Cave vars
    public static int     cubicCaveBottom;
    public static double  cubicYComp;
    public static double  cubicXZComp;
    public static String  cubicCaveFreq;
    public static double   cubicCustomFrequency;
    public static float   cubicNoiseThreshold    = 0.95f;
    public static int     cubicFractalOctaves    = 1;
    public static float   cubicFractalGain       = .3f;
    public static float   cubicFractalFreq       = .03f;
    public static int     cubicNumGenerators     = 2;
    public static boolean cubicYAdjust           = true;
    public static float   cubicYAdjustF1         = .9f;
    public static float   cubicYAdjustF2         = .9f;

    // Simplex Cave vars
    public static int     simplexCaveBottom;
    public static double  simplexYComp;
    public static double  simplexXZComp;
    public static String  simplexCaveFreq;
    public static double   simplexCustomFrequency;
    public static float   simplexNoiseThreshold    = 0.82f;
    public static int     simplexFractalOctaves    = 1;
    public static float   simplexFractalGain       = .3f;
    public static float   simplexFractalFreq       = .025f;
    public static int     simplexNumGenerators     = 2;
    public static boolean simplexYAdjust           = true;
    public static float   simplexYAdjustF1         = .95f;
    public static float   simplexYAdjustF2         = .5f;

    // Liquid Cavern vars
    public static int     liquidCavernTop;
    public static int     liquidCavernBottom;
    public static double  liquidCavernYComp;
    public static double  liquidCavernXZComp;
    public static String  liquidCavernFreq;
    public static double  liquidCavernCustomFreq;
    public static float   liquidCavernNoiseThreshold = 0.6f;
    public static int     liquidCavernFractalOctaves = 1;
    public static float   liquidCavernFractalGain = .3f;
    public static float   liquidCavernFractalFreq = .03f;
    public static int     liquidCavernNumGenerators = 2;

    // Floored Cavern vars
    public static int flooredCavernTop;
    public static int flooredCavernBottom;
    public static double  flooredCavernYComp;
    public static double  flooredCavernXZComp;
    public static String  flooredCavernCaveFreq;
    public static double   flooredCavernCustomFrequency;
    public static float   flooredCavernNoiseThreshold = 0.6f;
    public static int     flooredCavernFractalOctaves = 1;
    public static float   flooredCavernFractalGain    = .3f;
    public static float   flooredCavernFractalFreq    = .028f;
    public static int     flooredCavernNumGenerators  = 2;

    // Water Cavern vars
    public static double  waterCavernYComp;
    public static double  waterCavernXZComp;

    // Water Region vars
    public static boolean enableWaterRegions;
    public static String  waterRegionFreq;
    public static double  waterRegionCustomFreq;

    // Bedrock flattening vars
    public static boolean flattenBedrock;

    // Vanilla features vars
    public static boolean enableRavines;
    public static boolean enableUnderwaterRavines;

    // Debug vars
    public static boolean enableDebugVisualizer;

    // COMPLETELY NEW
    public static boolean replaceFloatingGravel = true;
    public static boolean overrideSurfaceDetection = true;
    public static boolean enableSurfaceCaves = true;
    public static boolean enableFloodedUnderground = true;
    public static boolean enableFloodedRavines = true;
    public static float caveRegionCustomSize = 0f;
    public static float caveSpawnChance = 100f;
    public static float cavernRegionCustomSize = 0f;
    public static float cavernSpawnChance = 25f;
    public static float waterRegionSpawnChance = 40f;
    public static int bedrockWidth = 1;
    public static int[] whitelistedDimensions = {0};
    public static boolean enableGlobalWhitelisting = true;

}