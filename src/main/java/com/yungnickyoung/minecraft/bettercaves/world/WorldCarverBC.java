package com.yungnickyoung.minecraft.bettercaves.world;


import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Pair;
import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.BetterCavesConfig;
import com.yungnickyoung.minecraft.bettercaves.enums.CaveType;
import com.yungnickyoung.minecraft.bettercaves.enums.CavernType;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtil;
import com.yungnickyoung.minecraft.bettercaves.world.cave.AbstractBC;
import com.yungnickyoung.minecraft.bettercaves.world.cave.CaveBC;
import com.yungnickyoung.minecraft.bettercaves.world.cave.CavernBC;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.*;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraftforge.registries.ForgeRegistries;

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

    // Noise generators to group caves into cave regions based on xz-coordinates.
    // Cavern Region Controller uses simplex noise while the others use Voronoi regions (cellular noise)
    private FastNoise waterCavernController;
    private FastNoise cavernRegionController;
    private FastNoise caveRegionController;

    // Region generation noise thresholds, based on user config
    private float cubicCaveThreshold;
    private float simplexCaveThreshold;
    private float lavaCavernThreshold;
    private float flooredCavernThreshold;
    private float waterRegionThreshold;

    // Dictates the degree of smoothing along cavern region boundaries
    private float transitionRange = .15f;

    // Config option for using water regions
    private boolean enableWaterRegions;

    BlockState lavaBlock;
    BlockState waterBlock;

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

        // Cave generators - we will determine exactly what type these are based on the cave region for each column
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
                    maxSurfaceHeight = BetterCavesUtil.getMaxSurfaceAltitudeSubChunk(chunkIn, subX, subZ);

                // maxSurfaceHeight (also used for max cave altitude) cannot exceed Max Cave Altitude setting
                maxSurfaceHeight = Math.min(maxSurfaceHeight, BetterCavesConfig.maxCaveAltitude);

                for (int offsetX = 0; offsetX < 2; offsetX++) {
                    for (int offsetZ = 0; offsetZ < 2; offsetZ++) {
                        int localX = (subX * 2) + offsetX; // chunk-local x-coordinate (0-15, inclusive)
                        int localZ = (subZ * 2) + offsetZ; // chunk-local z-coordinate (0-15, inclusive)
                        int realX = (chunkX * 16) + localX;
                        int realZ = (chunkZ * 16) + localZ;

                        /* --------------------------- Configure Caves --------------------------- */

                        // Get noise values used to determine cave region
                        float caveRegionNoise = caveRegionController.GetNoise(realX, realZ);

                        /* Determine cave type for this column. We have two thresholds, one for cubic caves and one for
                         * simplex caves. Since the noise value generated for the region is between -1 and 1, we (by
                         * default) designate all negative values as cubic caves, and all positive as simplex. However,
                         * we allow the user to tweak the cutoff values based on the frequency they designate for each cave
                         * type, so we must also check for values between the two thresholds,
                         * e.g. if (cubicCaveThreshold <= noiseValue < simplexCaveThreshold).
                         * In this case, we dig no caves out of this chunk.
                         */
                        if (caveRegionNoise < this.cubicCaveThreshold) {
                            caveGen = this.caveCubic;
                            caveBottomY = BetterCavesConfig.cubicCaveBottom;
                        } else if (caveRegionNoise >= this.simplexCaveThreshold) {
                            caveGen = this.caveSimplex;
                            caveBottomY = BetterCavesConfig.simplexCaveBottom;
                        } else {
                            caveGen = null;
                            caveBottomY = 255;
                        }

                        /* --------------------------- Configure Caverns --------------------------- */

                        // Noise values used to determine cavern region
                        float cavernRegionNoise = cavernRegionController.GetNoise(realX, realZ);
                        float waterRegionNoise = 99;

                        // Only bother calculating noise for water region if enabled
                        if (enableWaterRegions)
                            waterRegionNoise = waterCavernController.GetNoise(realX, realZ);

                        // If water region threshold check is passed, change lava block to water
                        BlockState liquidBlock = lavaBlock;
                        if (waterRegionNoise < waterRegionThreshold)
                            liquidBlock = waterBlock;

                        // Determine cavern type for this column. Caverns generate at low altitudes only.
                        if (cavernRegionNoise < lavaCavernThreshold) {
                            if (this.enableWaterRegions && waterRegionNoise < this.waterRegionThreshold) {
                                // Generate water cavern in this column
                                cavernGen = this.cavernWater;
                            } else {
                                // Generate lava cavern in this column
                                cavernGen = this.cavernLava;
                            }
                            // Water caverns use the same cave top/bottom as lava caverns
                            cavernBottomY = BetterCavesConfig.lavaCavernCaveBottom;
                            cavernTopY = BetterCavesConfig.lavaCavernCaveTop;
                        } else if (cavernRegionNoise >= lavaCavernThreshold && cavernRegionNoise <= flooredCavernThreshold) {
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
                        if (cavernRegionNoise >= lavaCavernThreshold && cavernRegionNoise <= lavaCavernThreshold + transitionRange) {
                            float smoothAmp = Math.abs((cavernRegionNoise - (lavaCavernThreshold + transitionRange)) / transitionRange);
                            this.cavernLava.generateColumn(chunkX, chunkZ, chunkIn, localX, localZ, BetterCavesConfig.lavaCavernCaveBottom, BetterCavesConfig.lavaCavernCaveTop,
                                    maxSurfaceHeight, minSurfaceHeight, surfaceCutoff, liquidBlock, smoothAmp);
                        } else if (cavernRegionNoise <= flooredCavernThreshold && cavernRegionNoise >= flooredCavernThreshold - transitionRange) {
                            float smoothAmp = Math.abs((cavernRegionNoise - (flooredCavernThreshold - transitionRange)) / transitionRange);
                            this.cavernFloored.generateColumn(chunkX, chunkZ, chunkIn, localX, localZ, BetterCavesConfig.flooredCavernCaveBottom, BetterCavesConfig.flooredCavernCaveTop,
                                    maxSurfaceHeight, minSurfaceHeight, surfaceCutoff, liquidBlock, smoothAmp);
                        }

                        /* --------------- Dig out caves and caverns for this column --------------- */
                        // Top (Cave) layer:
                        if (caveGen != null)
                            caveGen.generateColumn(chunkX, chunkZ, chunkIn, localX, localZ, caveBottomY, maxSurfaceHeight,
                                maxSurfaceHeight, minSurfaceHeight, surfaceCutoff, liquidBlock);
                        // Bottom (Cavern) layer:
                        if (cavernGen != null)
                            cavernGen.generateColumn(chunkX, chunkZ, chunkIn, localX, localZ, cavernBottomY, cavernTopY,
                                maxSurfaceHeight, minSurfaceHeight, surfaceCutoff, liquidBlock);
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
     * @return threshold value for water region spawn rate based on Config setting
     */
    private float calcWaterRegionThreshold() {
        switch (BetterCavesConfig.waterRegionFreq) {
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
     * Initialize Better Caves generators and cave region controllers for this world.
     */
    public void initialize(long seed) {
        this.seed = seed;
        this.enableWaterRegions = BetterCavesConfig.enableWaterRegions;

        // Determine noise thresholds for cavern spawns based on user config
        this.lavaCavernThreshold = calcLavaCavernThreshold();
        this.flooredCavernThreshold = calcFlooredCavernThreshold();
        this.waterRegionThreshold = calcWaterRegionThreshold();

        // Determine noise thresholds for caverns based on user config
        this.cubicCaveThreshold = calcCubicCaveThreshold();
        this.simplexCaveThreshold = calcSimplexCaveThreshold();

        // Get user setting for surface cutoff depth used to close caves off towards the surface
        this.surfaceCutoff = BetterCavesConfig.surfaceCutoff;

        // Determine cave region size
        float caveRegionSize;
        switch (BetterCavesConfig.caveRegionSize) {
            case "Small":
                caveRegionSize = .007f;
                break;
            case "Large":
                caveRegionSize = .0032f;
                break;
            case "ExtraLarge":
                caveRegionSize = .001f;
                break;
            default: // Medium
                caveRegionSize = .005f;
                break;
        }

        // Determine cavern region size, as well as jitter to make Voronoi regions more varied in shape
        float cavernRegionSize;
        float waterCavernRegionSize = .003f;
        switch (BetterCavesConfig.cavernRegionSize) {
            case "Small":
                cavernRegionSize = .01f;
                break;
            case "Large":
                cavernRegionSize = .005f;
                break;
            case "ExtraLarge":
                cavernRegionSize = .001f;
                waterCavernRegionSize = .0005f;
                break;
            default: // Medium
                cavernRegionSize = .007f;
                break;
        }

        // Initialize Region Controllers using world seed and user config option for region size
        this.caveRegionController = new FastNoise();
        this.caveRegionController.SetSeed((int)seed + 222);
        this.caveRegionController.SetFrequency(caveRegionSize);
        this.caveRegionController.SetNoiseType(FastNoise.NoiseType.Cellular);
        this.caveRegionController.SetCellularDistanceFunction(FastNoise.CellularDistanceFunction.Natural);

        // Note that Cavern Region Controller uses Simplex noise instead of Cellular
        this.cavernRegionController = new FastNoise();
        this.cavernRegionController.SetSeed((int)seed + 333);
        this.cavernRegionController.SetFrequency(cavernRegionSize);

        this.waterCavernController = new FastNoise();
        this.waterCavernController.SetSeed((int)seed + 444);
        this.waterCavernController.SetFrequency(waterCavernRegionSize);
        this.waterCavernController.SetNoiseType(FastNoise.NoiseType.Cellular);
        this.waterCavernController.SetCellularDistanceFunction(FastNoise.CellularDistanceFunction.Natural);

        // Set lava block
        try {
            lavaBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(BetterCavesConfig.lavaBlock)).getDefaultState();
            BetterCaves.LOGGER.info("Using block '" + BetterCavesConfig.lavaBlock + "' as lava in cave generation...");
        } catch (Exception e) {
            BetterCaves.LOGGER.warn("Unable to use block '" + BetterCavesConfig.lavaBlock + "': " + e);
            BetterCaves.LOGGER.warn("Using vanilla lava instead...");
            lavaBlock = Blocks.LAVA.getDefaultState();
        }

        // Default to vanilla lava if lavaBlock is null or contains air (the default registry block) when air was not specified
        if (lavaBlock == null || (lavaBlock == Blocks.AIR.getDefaultState() && !BetterCavesConfig.lavaBlock.equals("minecraft:air"))) {
            BetterCaves.LOGGER.warn("Unable to use block '" + BetterCavesConfig.lavaBlock + "': null block returned. Using vanilla lava instead...");
            lavaBlock = Blocks.LAVA.getDefaultState();
        }

        // Set water block
        try {
            waterBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(BetterCavesConfig.waterBlock)).getDefaultState();
            BetterCaves.LOGGER.info("Using block '" + BetterCavesConfig.waterBlock + "' as water in cave generation...");
        } catch (Exception e) {
            BetterCaves.LOGGER.warn("Unable to use block '" + BetterCavesConfig.waterBlock + "': " + e);
            BetterCaves.LOGGER.warn("Using vanilla water instead...");
            waterBlock = Blocks.WATER.getDefaultState();
        }

        // Default to vanilla water if waterBlock is null or contains air (the default registry block) when air was not specified
        if (waterBlock == null || (waterBlock == Blocks.AIR.getDefaultState() && !BetterCavesConfig.waterBlock.equals("minecraft:air"))) {
            BetterCaves.LOGGER.warn("Unable to use block '" + BetterCavesConfig.waterBlock + "': null block returned. Using vanilla water instead...");
            waterBlock = Blocks.WATER.getDefaultState();
        }

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
