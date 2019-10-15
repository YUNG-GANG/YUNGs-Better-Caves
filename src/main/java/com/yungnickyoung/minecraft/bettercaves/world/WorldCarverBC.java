package com.yungnickyoung.minecraft.bettercaves.world;


import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Pair;
import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.BetterCavesConfig;
import com.yungnickyoung.minecraft.bettercaves.enums.CaveType;
import com.yungnickyoung.minecraft.bettercaves.enums.CavernType;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCaveUtil;
import com.yungnickyoung.minecraft.bettercaves.world.cave.AbstractBC;
import com.yungnickyoung.minecraft.bettercaves.world.cave.CaveBC;
import com.yungnickyoung.minecraft.bettercaves.world.cave.CavernBC;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.*;
import net.minecraft.world.gen.feature.ProbabilityConfig;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class WorldCarverBC extends WorldCarver<ProbabilityConfig> {
    public static int counter = 0;
    public long oldSeed = 0;

    // The minecraft world
    private long seed = 0; // world seed

    // Cave types
    private AbstractBC caveCubic;
    private AbstractBC caveSimplex;

    // Cavern types
    private AbstractBC cavernLava;
    private AbstractBC cavernFloored;
    private AbstractBC cavernWater;

    private int surfaceCutoff;

    // Noise generators to group caves into cave "biomes" based on xz-coordinates.
    // Cavern Biome Controller uses simplex noise while the others use Voronoi regions (cellular noise)
    private FastNoise waterCavernController;
    private FastNoise cavernBiomeController;
    private FastNoise caveBiomeController;

    // Biome generation noise thresholds, based on user config
    private float cubicCaveThreshold;
    private float simplexCaveThreshold;
    private float lavaCavernThreshold;
    private float flooredCavernThreshold;
    private float waterBiomeThreshold;

    // Dictates the degree of smoothing along cavern biome boundaries
    private float transitionRange = .15f;

    // Config option for using water biomes
    private boolean enableWaterBiomes;

    // List used to avoid operating on a chunk more than once
    public Set<Pair<Integer, Integer>> coordList = new HashSet<>();

    public WorldCarverBC(Function<Dynamic<?>, ? extends ProbabilityConfig> p_i49929_1_, int p_i49929_2_) {
        super(p_i49929_1_, p_i49929_2_);
    }

    // Override the default carver's method to use Better Caves carving instead.
    @Override
    public boolean carve(IChunk chunkIn, Random rand, int seaLevel, int cX, int cZ, int chunkX, int chunkZ, BitSet carvingMask, ProbabilityConfig config) {
        // Since the ChunkGenerator calls this method many times per chunk (~300), we must
        // check for duplicates so we don't operate on the same chunk more than once.
        Pair<Integer, Integer> pair = new Pair<>(chunkX, chunkZ);
        if (coordList.contains(pair))
            return true;

        // Clear the list occasionally to prevent excessive memory usage.
        // This is a hacky solution, and may introduce bugs due to chunks being over- or under-processed
        if (coordList.size() > 10000) {
            coordList.clear();
            BetterCaves.LOGGER.warn("WARNING: BetterCaves chunk list reached max capacity!");
            BetterCaves.LOGGER.info("Clearing chunk list...");
        }

        coordList.add(pair);

        // Debug logging to see if any chunks may have been generated erroneously with the wrong seed
        if (seed != oldSeed) {
            BetterCaves.LOGGER.debug("CHUNKS LOADED SINCE SEED CHANGE: " + counter);
            counter = 0;
            oldSeed = seed;
        }

        counter++;

        // Flatten bedrock into single layer, if enabled in user config
        if (BetterCavesConfig.flattenBedrock) {
            for (int localX = 0; localX < 16; localX++) {
                for (int localZ = 0; localZ < 16; localZ++) {
                    for (int y = 1; y <= 4; y++) {
                        BlockPos blockPos = new BlockPos(localX, y, localZ);
                        if (chunkIn.getBlockState(blockPos) == Blocks.BEDROCK.getDefaultState())
                            chunkIn.setBlockState(blockPos, Blocks.STONE.getDefaultState(), false);
                    }
                }
            }
        }

        int maxSurfaceHeight = 128;
        int minSurfaceHeight = 60;

        // Cave generators - we will determine exactly what type these are based on the cave biome for each column
        AbstractBC cavernGen;
        AbstractBC caveGen;

        // These values probably could be hardcoded, but are kept as vars for future extensibility.
        // These values are later set to the correct cave type's config vars for
        // caveBottom and caveTop (only applicable for caverns, since caves perform some additional
        // operations to smoothly transition into the surface)
        int cavernBottomY;
        int cavernTopY;
        int caveBottomY;

        // We split chunks into 2x2 subchunks for surface height calculations
        for (int subX = 0; subX < 8; subX++) {
            for (int subZ = 0; subZ < 8; subZ++) {
                if (!BetterCavesConfig.enableDebugVisualizer)
                    maxSurfaceHeight = BetterCaveUtil.getMaxSurfaceHeightSubChunk(chunkIn, subX, subZ);

                // maxSurfaceHeight (also used for max cave altitude) cannot exceed Max Cave Altitude setting
                maxSurfaceHeight = Math.min(maxSurfaceHeight, BetterCavesConfig.maxCaveAltitude);

                for (int offsetX = 0; offsetX < 2; offsetX++) {
                    for (int offsetZ = 0; offsetZ < 2; offsetZ++) {
                        int localX = (subX * 2) + offsetX; // chunk-local x-coordinate (0-15, inclusive)
                        int localZ = (subZ * 2) + offsetZ; // chunk-local z-coordinate (0-15, inclusive)
                        int realX = (chunkX * 16) + localX;
                        int realZ = (chunkZ * 16) + localZ;

                        /* --------------------------- Configure Caves --------------------------- */

                        // Get noise values used to determine cave biome
                        float caveBiomeNoise = caveBiomeController.GetNoise(realX, realZ);

                        /* Determine cave type for this column. We have two thresholds, one for cubic caves and one for
                         * simplex caves. Since the noise value generated for the biome is between -1 and 1, we (by
                         * default) designate all negative values as cubic caves, and all positive as simplex. However,
                         * we allow the user to tweak the cutoff values based on the frequency they designate for each cave
                         * type, so we must also check for values between the two thresholds,
                         * e.g. if (cubicCaveThreshold <= noiseValue < simplexCaveThreshold).
                         * In this case, we dig no caves out of this chunk.
                         */
                        if (caveBiomeNoise < this.cubicCaveThreshold) {
                            caveGen = this.caveCubic;
                            caveBottomY = BetterCavesConfig.cubicCaveBottom;
                        } else if (caveBiomeNoise >= this.simplexCaveThreshold) {
                            caveGen = this.caveSimplex;
                            caveBottomY = BetterCavesConfig.simplexCaveBottom;
                        } else {
                            caveGen = null;
                            caveBottomY = 255;
                        }

                        /* --------------------------- Configure Caverns --------------------------- */

                        // Get noise values used to determine cavern biome
                        float cavernBiomeNoise = cavernBiomeController.GetNoise(realX, realZ);
                        float waterBiomeNoise = 99;

                        // Only bother calculating noise for water biome if enabled
                        if (enableWaterBiomes)
                            waterBiomeNoise = waterCavernController.GetNoise(realX, realZ);

                        // If water biome threshold check is passed, change lava block to water
                        BlockState lavaBlock = Blocks.LAVA.getDefaultState();
                        if (waterBiomeNoise < waterBiomeThreshold)
                            lavaBlock = Blocks.WATER.getDefaultState();

                        // Determine cavern type for this column. Caverns generate at low altitudes only.
                        if (cavernBiomeNoise < lavaCavernThreshold) {
                            if (this.enableWaterBiomes && waterBiomeNoise < this.waterBiomeThreshold) {
                                // Generate water cavern in this column
                                cavernGen = this.cavernWater;
                            } else {
                                // Generate lava cavern in this column
                                cavernGen = this.cavernLava;
                            }
                            // Water caverns use the same cave top/bottom as lava caverns
                            cavernBottomY = BetterCavesConfig.lavaCavernCaveBottom;
                            cavernTopY = BetterCavesConfig.lavaCavernCaveTop;
                        } else if (cavernBiomeNoise >= lavaCavernThreshold && cavernBiomeNoise <= flooredCavernThreshold) {
                            /* Similar to determining cave type above, we must check for values between the two adjusted
                             * thresholds, i.e. lavaCavernThreshold < noiseValue <= flooredCavernThreshold.
                             * In this case, we just continue generating the caves we were generating above, instead
                             * of generating a cavern.
                             */
                            cavernGen = caveGen;
                            cavernBottomY = caveBottomY;
                            cavernTopY = caveBottomY;
                        } else {
                            // Generate floored cavern in this column
                            cavernGen = this.cavernFloored;
                            cavernBottomY = BetterCavesConfig.flooredCavernCaveBottom;
                            cavernTopY = BetterCavesConfig.flooredCavernCaveTop;
                        }

                        // Extra check to provide close-off transitions on cavern edges
                        if (cavernBiomeNoise >= lavaCavernThreshold && cavernBiomeNoise <= lavaCavernThreshold + transitionRange) {
                            float smoothAmp = Math.abs((cavernBiomeNoise - (lavaCavernThreshold + transitionRange)) / transitionRange);
                            this.cavernLava.generateColumn(chunkX, chunkZ, chunkIn, localX, localZ, BetterCavesConfig.lavaCavernCaveBottom, BetterCavesConfig.lavaCavernCaveTop,
                                    maxSurfaceHeight, minSurfaceHeight, surfaceCutoff, lavaBlock, smoothAmp);
                        } else if (cavernBiomeNoise <= flooredCavernThreshold && cavernBiomeNoise >= flooredCavernThreshold - transitionRange) {
                            float smoothAmp = Math.abs((cavernBiomeNoise - (flooredCavernThreshold - transitionRange)) / transitionRange);
                            this.cavernFloored.generateColumn(chunkX, chunkZ, chunkIn, localX, localZ, BetterCavesConfig.flooredCavernCaveBottom, BetterCavesConfig.flooredCavernCaveTop,
                                    maxSurfaceHeight, minSurfaceHeight, surfaceCutoff, lavaBlock, smoothAmp);
                        }

                        /* --------------- Dig out caves and caverns for this column --------------- */
                        // Top (Cave) layer:
                        if (caveGen != null)
                            caveGen.generateColumn(chunkX, chunkZ, chunkIn, localX, localZ, caveBottomY, maxSurfaceHeight,
                                maxSurfaceHeight, minSurfaceHeight, surfaceCutoff, lavaBlock);
                        // Bottom (Cavern) layer:
                        if (cavernGen != null)
                            cavernGen.generateColumn(chunkX, chunkZ, chunkIn, localX, localZ, cavernBottomY, cavernTopY,
                                maxSurfaceHeight, minSurfaceHeight, surfaceCutoff, lavaBlock);
                    }
                }
            }
        }
        chunkIn.setModified(true);
        return true;
    }

    /**
     * @return threshold value for cubic cave spawn rate based on Config setting
     */
    private float calcCubicCaveThreshold() {
        switch (BetterCavesConfig.cubicCaveFreq) {
            case "None":
                return -99f;
            case "Rare":
                return -.6f;
            case "Common":
                return -.2f;
            default: // VeryCommon
                return 0;
        }
    }

    /**
     * @return threshold value for simplex cave spawn rate based on Config setting
     */
    private float calcSimplexCaveThreshold() {
        switch (BetterCavesConfig.simplexCaveFreq) {
            case "None":
                return 99f;
            case "Rare":
                return .6f;
            case "Common":
                return .2f;
            default: // VeryCommon
                return 0;
        }
    }

    /**
     * @return threshold value for lava cavern spawn rate based on Config setting
     */
    private float calcLavaCavernThreshold() {
        switch (BetterCavesConfig.lavaCavernCaveFreq) {
            case "None":
                return -99f;
            case "Rare":
                return -.8f;
            case "Common":
                return -.3f;
            case "VeryCommon":
                return -.1f;
            default: // Normal
                return -.4f;
        }
    }

    /**
     * @return threshold value for floored cavern spawn rate based on Config setting
     */
    private float calcFlooredCavernThreshold() {
        switch (BetterCavesConfig.flooredCavernCaveFreq) {
            case "None":
                return 99f;
            case "Rare":
                return .8f;
            case "Common":
                return .3f;
            case "VeryCommon":
                return .1f;
            default: // Normal
                return .4f;
        }
    }

    /**
     * @return threshold value for water biome spawn rate based on Config setting
     */
    private float calcWaterBiomeThreshold() {
        switch (BetterCavesConfig.waterBiomeFreq) {
            case "Rare":
                return -.4f;
            case "Common":
                return .1f;
            case "VeryCommon":
                return .3f;
            case "Always":
                return 99f;
            default: // Normal
                return -.15f;
        }
    }

    /**
     * Initialize Better Caves generators and cave biome controllers for this world.
     */
    public void initialize(long seed) {
        this.seed = seed;
        this.enableWaterBiomes = BetterCavesConfig.enableWaterBiomes;

        // Determine noise thresholds for cavern spawns based on user config
        this.lavaCavernThreshold = calcLavaCavernThreshold();
        this.flooredCavernThreshold = calcFlooredCavernThreshold();
        this.waterBiomeThreshold = calcWaterBiomeThreshold();

        // Determine noise thresholds for caverns based on user config
        this.cubicCaveThreshold = calcCubicCaveThreshold();
        this.simplexCaveThreshold = calcSimplexCaveThreshold();

        // Get user setting for surface cutoff depth used to close caves off towards the surface
        this.surfaceCutoff = BetterCavesConfig.surfaceCutoff;

        // Determine cave biome size
        float caveBiomeSize;
        switch (BetterCavesConfig.caveBiomeSize) {
            case "Small":
                caveBiomeSize = .007f;
                break;
            case "Large":
                caveBiomeSize = .0032f;
                break;
            case "ExtraLarge":
                caveBiomeSize = .001f;
                break;
            default: // Medium
                caveBiomeSize = .005f;
                break;
        }

        // Determine cavern biome size, as well as jitter to make Voronoi regions more varied in shape
        float cavernBiomeSize;
        float waterCavernBiomeSize = .003f;
        switch (BetterCavesConfig.cavernBiomeSize) {
            case "Small":
                cavernBiomeSize = .01f;
                break;
            case "Large":
                cavernBiomeSize = .005f;
                break;
            case "ExtraLarge":
                cavernBiomeSize = .001f;
                waterCavernBiomeSize = .0005f;
                break;
            default: // Medium
                cavernBiomeSize = .007f;
                break;
        }

        // Initialize Biome Controllers using world seed and user config option for biome size
        this.caveBiomeController = new FastNoise();
        this.caveBiomeController.SetSeed((int)seed + 222);
        this.caveBiomeController.SetFrequency(caveBiomeSize);
        this.caveBiomeController.SetNoiseType(FastNoise.NoiseType.Cellular);
        this.caveBiomeController.SetCellularDistanceFunction(FastNoise.CellularDistanceFunction.Natural);

        // Note that Cavern Biome Controller uses Simplex noise instead of Cellular
        this.cavernBiomeController = new FastNoise();
        this.cavernBiomeController.SetSeed((int)seed + 333);
        this.cavernBiomeController.SetFrequency(cavernBiomeSize);

        this.waterCavernController = new FastNoise();
        this.waterCavernController.SetSeed((int)seed + 444);
        this.waterCavernController.SetFrequency(waterCavernBiomeSize);
        this.waterCavernController.SetNoiseType(FastNoise.NoiseType.Cellular);
        this.waterCavernController.SetCellularDistanceFunction(FastNoise.CellularDistanceFunction.Natural);

        /* ---------- Initialize all Better Cave generators using config options ---------- */
        this.caveCubic = new CaveBC(
                seed,
                CaveType.CUBIC,
                BetterCavesConfig.cubicFractalOctaves,
                BetterCavesConfig.cubicFractalGain,
                BetterCavesConfig.cubicFractalFreq,
                BetterCavesConfig.cubicNumGenerators,
                BetterCavesConfig.cubicNoiseThreshold,
                BetterCavesConfig.cubicTurbulenceOctaves,
                BetterCavesConfig.cubicTurbulenceGain,
                BetterCavesConfig.cubicTurbulenceFreq,
                BetterCavesConfig.cubicEnableTurbulence,
                BetterCavesConfig.cubicYComp,
                BetterCavesConfig.cubicXZComp,
                BetterCavesConfig.cubicYAdjust,
                BetterCavesConfig.cubicYAdjustF1,
                BetterCavesConfig.cubicYAdjustF2,
                Blocks.OAK_PLANKS.getDefaultState()
        );

        this.caveSimplex = new CaveBC(
                seed,
                CaveType.SIMPLEX,
                BetterCavesConfig.simplexFractalOctaves,
                BetterCavesConfig.simplexFractalGain,
                BetterCavesConfig.simplexFractalFreq,
                BetterCavesConfig.simplexNumGenerators,
                BetterCavesConfig.simplexNoiseThreshold,
                BetterCavesConfig.simplexTurbulenceOctaves,
                BetterCavesConfig.simplexTurbulenceGain,
                BetterCavesConfig.simplexTurbulenceFreq,
                BetterCavesConfig.simplexEnableTurbulence,
                BetterCavesConfig.simplexYComp,
                BetterCavesConfig.simplexXZComp,
                BetterCavesConfig.simplexYAdjust,
                BetterCavesConfig.simplexYAdjustF1,
                BetterCavesConfig.simplexYAdjustF2,
                Blocks.COBBLESTONE.getDefaultState()
        );

        this.cavernLava = new CavernBC(
                seed,
                CavernType.LAVA,
                BetterCavesConfig.lavaCavernFractalOctaves,
                BetterCavesConfig.lavaCavernFractalGain,
                BetterCavesConfig.lavaCavernFractalFreq,
                BetterCavesConfig.lavaCavernNumGenerators,
                BetterCavesConfig.lavaCavernNoiseThreshold,
                BetterCavesConfig.lavaCavernYComp,
                BetterCavesConfig.lavaCavernXZComp,
                Blocks.REDSTONE_BLOCK.getDefaultState()
        );

        this.cavernFloored = new CavernBC(
                seed,
                CavernType.FLOORED,
                BetterCavesConfig.flooredCavernFractalOctaves,
                BetterCavesConfig.flooredCavernFractalGain,
                BetterCavesConfig.flooredCavernFractalFreq,
                BetterCavesConfig.flooredCavernNumGenerators,
                BetterCavesConfig.flooredCavernNoiseThreshold,
                BetterCavesConfig.flooredCavernYComp,
                BetterCavesConfig.flooredCavernXZComp,
                Blocks.GOLD_BLOCK.getDefaultState()
        );

        this.cavernWater = new CavernBC(
                seed,
                CavernType.WATER,
                BetterCavesConfig.waterCavernFractalOctaves,
                BetterCavesConfig.waterCavernFractalGain,
                BetterCavesConfig.waterCavernFractalFreq,
                BetterCavesConfig.waterCavernNumGenerators,
                BetterCavesConfig.waterCavernNoiseThreshold,
                BetterCavesConfig.waterCavernYComp,
                BetterCavesConfig.waterCavernXZComp,
                Blocks.LAPIS_BLOCK.getDefaultState()
        );

        BetterCaves.LOGGER.debug("BETTER CAVES WORLD CARVER INITIALIZED WITH SEED " + this.seed);
    }

    @Override
    public boolean shouldCarve(Random rand, int chunkX, int chunkZ, ProbabilityConfig config) {
        return true;
    }

    @Override
    protected boolean func_222708_a(double p_222708_1_, double p_222708_3_, double p_222708_5_, int p_222708_7_) {
        return false;
    }
}
