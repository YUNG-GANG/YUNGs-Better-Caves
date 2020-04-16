package com.yungnickyoung.minecraft.bettercaves.world;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.BetterCavesConfig;
import com.yungnickyoung.minecraft.bettercaves.config.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.enums.CavernType;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseColumn;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseUtils;
import com.yungnickyoung.minecraft.bettercaves.world.carver.CarverNoiseRange;
import com.yungnickyoung.minecraft.bettercaves.world.carver.cavern.CavernCarver;
import com.yungnickyoung.minecraft.bettercaves.world.carver.cavern.CavernCarverBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

import java.util.ArrayList;
import java.util.List;

public class CavernCarverController {
    private long seed;
    private FastNoise cavernRegionController;
    private List<CarverNoiseRange> noiseRanges = new ArrayList<>();

    // Vars from config
    private boolean isDebugViewEnabled;
    private boolean isOverrideSurfaceDetectionEnabled;
    private boolean isFloodedUndergroundEnabled;

    public CavernCarverController(long seed, ConfigHolder config) {
        this.seed = seed;
        this.isDebugViewEnabled = BetterCavesConfig.enableDebugVisualizer;
        this.isOverrideSurfaceDetectionEnabled = BetterCavesConfig.overrideSurfaceDetection;
        this.isFloodedUndergroundEnabled = BetterCavesConfig.enableFloodedUnderground;

        // Configure cavern region controller, which determines what type of cavern should be carved in any given region
        float cavernRegionSize = calcCavernRegionSize(BetterCavesConfig.cavernRegionSize, BetterCavesConfig.cavernRegionCustomSize);
        this.cavernRegionController = new FastNoise();
        this.cavernRegionController.SetSeed((int)seed + 333);
        this.cavernRegionController.SetFrequency(cavernRegionSize);

        // Initialize all carvers using config options
        List<CavernCarver> carvers = new ArrayList<>();
        carvers.add(new CavernCarverBuilder(seed)
            .ofTypeFromConfig(CavernType.LIQUID, config)
            .debugVisualizerBlock(Blocks.REDSTONE_BLOCK.getDefaultState())
            .build()
        );
        carvers.add(new CavernCarverBuilder(seed)
            .ofTypeFromConfig(CavernType.FLOORED, config)
            .debugVisualizerBlock(Blocks.GOLD_BLOCK.getDefaultState())
            .build()
        );

        float spawnChance = BetterCavesConfig.cavernSpawnChance / 100f;
        int totalPriority = carvers.stream().map(CavernCarver::getPriority).reduce(0, Integer::sum);

        BetterCaves.LOGGER.debug("CAVERN INFORMATION");
        BetterCaves.LOGGER.debug("--> SPAWN CHANCE SET TO: " + spawnChance);
        BetterCaves.LOGGER.debug("--> TOTAL PRIORITY: " + totalPriority);

        carvers.removeIf(carver -> carver.getPriority() == 0);
        float totalDeadzonePercent = 1 - spawnChance;
        float deadzonePercent = carvers.size() > 1
            ? totalDeadzonePercent / (carvers.size() - 1)
            : totalDeadzonePercent;

        BetterCaves.LOGGER.debug("--> DEADZONE PERCENT: " + deadzonePercent + "(" + totalDeadzonePercent + " TOTAL)");

        float currNoise = -1f;

        for (CavernCarver carver : carvers) {
            BetterCaves.LOGGER.debug("--> CARVER");
            float rangeCDFPercent = (float)carver.getPriority() / totalPriority * spawnChance;
            float topNoise = NoiseUtils.simplexNoiseOffsetByPercent(currNoise, rangeCDFPercent);
            CarverNoiseRange range = new CarverNoiseRange(currNoise, topNoise, carver);
            noiseRanges.add(range);

            // Offset currNoise for deadzone region
            currNoise = NoiseUtils.simplexNoiseOffsetByPercent(topNoise, deadzonePercent);

            BetterCaves.LOGGER.debug("    --> RANGE PERCENT LENGTH WANTED: " + rangeCDFPercent);
            BetterCaves.LOGGER.debug("    --> RANGE FOUND: " + range);
        }
    }

    public void carveChunk(IChunk chunk, int chunkX, int chunkZ, int[][] surfaceAltitudes, BlockState[][] liquidBlocks) {
        // Prevent unnecessary computation if caverns are disabled
        if (noiseRanges.size() == 0) {
            return;
        }

        boolean flooded = false;
        float smoothAmpFactor = 1;

        for (int subX = 0; subX < 16 / Settings.SUB_CHUNK_SIZE; subX++) {
            for (int subZ = 0; subZ < 16 / Settings.SUB_CHUNK_SIZE; subZ++) {
                int startX = subX * Settings.SUB_CHUNK_SIZE;
                int startZ = subZ * Settings.SUB_CHUNK_SIZE;
                int endX = startX + Settings.SUB_CHUNK_SIZE - 1;
                int endZ = startZ + Settings.SUB_CHUNK_SIZE - 1;
                BlockPos startPos = new BlockPos(chunkX * 16 + startX, 1, chunkZ * 16 + startZ);
                BlockPos endPos = new BlockPos(chunkX * 16 + endX, 1, chunkZ * 16 + endZ);

                noiseRanges.forEach(range -> range.setNoiseCube(null));

                // Get max height in subchunk. This is needed for calculating the noise cube
                int maxHeight = 0;
                if (!isOverrideSurfaceDetectionEnabled) { // Only necessary if we aren't overriding surface detection
                    for (int x = startX; x < endX; x++) {
                        for (int z = startZ; z < endZ; z++) {
                            maxHeight = Math.max(maxHeight, surfaceAltitudes[x][z]);
                        }
                    }
                    for (CarverNoiseRange range : noiseRanges) {
                        CavernCarver carver = (CavernCarver) range.getCarver();
                        maxHeight = Math.max(maxHeight, carver.getTopY());
                    }
                }

                for (int offsetX = 0; offsetX < Settings.SUB_CHUNK_SIZE; offsetX++) {
                    for (int offsetZ = 0; offsetZ < Settings.SUB_CHUNK_SIZE; offsetZ++) {
                        int localX = startX + offsetX;
                        int localZ = startZ + offsetZ;
                        BlockPos colPos = new BlockPos(chunkX * 16 + localX, 1, chunkZ * 16 + localZ);

                        if (isFloodedUndergroundEnabled) {
                            flooded = isFloodedUndergroundEnabled && chunk.getBiome(colPos).getCategory() == Biome.Category.OCEAN;
                            smoothAmpFactor = calcFloodedSmoothAmp(flooded, chunk, colPos);
                            if (smoothAmpFactor <= 0) continue; // Wall between flooded and normal caves
                        }

                        int surfaceAltitude = surfaceAltitudes[localX][localZ];
                        BlockState liquidBlock = liquidBlocks[localX][localZ];

                        // Get noise values used to determine cavern region
                        float cavernRegionNoise = cavernRegionController.GetNoise(colPos.getX(), colPos.getZ());

                        // Carve cavern using matching carver
                        for (CarverNoiseRange range : noiseRanges) {
                            if (!range.contains(cavernRegionNoise)) {
                                continue;
                            }
                            CavernCarver carver = (CavernCarver)range.getCarver();
                            int bottomY = carver.getBottomY();
                            int topY = isDebugViewEnabled ? carver.getTopY() : Math.min(surfaceAltitude, carver.getTopY());
                            if (isOverrideSurfaceDetectionEnabled) {
                                topY = carver.getTopY();
                                maxHeight = carver.getTopY();
                            }
                            float smoothAmp = range.getSmoothAmp(cavernRegionNoise) * smoothAmpFactor;
                            if (range.getNoiseCube() == null) {
                                range.setNoiseCube(carver.getNoiseGen().interpolateNoiseCube(startPos, endPos, bottomY, maxHeight));
                            }
                            NoiseColumn noiseColumn = range.getNoiseCube().get(offsetX).get(offsetZ);
                            carver.carveColumn(chunk, colPos, topY, smoothAmp, noiseColumn, liquidBlock, flooded);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * @return frequency value for cavern region controller
     */
    private float calcCavernRegionSize(String cavernRegionSize, float cavernRegionCustomSize) {
        switch (cavernRegionSize) {
            case "Small":
                return .01f;
            case "Large":
                return .005f;
            case "ExtraLarge":
                return .001f;
            case "Custom":
                return cavernRegionCustomSize;
            case "Medium":
            default:
                return .007f;
        }
    }

    private float calcFloodedSmoothAmp(boolean flooded, IChunk chunk, BlockPos colPos) {
        if (flooded) {
            if (chunk.getBiome(colPos.east(2)).getCategory() != Biome.Category.OCEAN) {
                if (chunk.getBiome(colPos.east()).getCategory() != Biome.Category.OCEAN) {
                    return -1;
                }
                else {
                    return .7f;
                }
            }
            if (chunk.getBiome(colPos.north(2)).getCategory() != Biome.Category.OCEAN) {
                if (chunk.getBiome(colPos.north()).getCategory() != Biome.Category.OCEAN) {
                    return -1;
                }
                else {
                    return .7f;
                }
            }
            if (chunk.getBiome(colPos.west(2)).getCategory() != Biome.Category.OCEAN) {
                if (chunk.getBiome(colPos.west()).getCategory() != Biome.Category.OCEAN) {
                    return -1;
                }
                else {
                    return .7f;
                }                            }
            if (chunk.getBiome(colPos.south(2)).getCategory() != Biome.Category.OCEAN) {
                if (chunk.getBiome(colPos.south()).getCategory() != Biome.Category.OCEAN) {
                    return -1;
                }
                else {
                    return .7f;
                }
            }
        }
        else {
            if (chunk.getBiome(colPos.east(2)).getCategory() == Biome.Category.OCEAN) {
                if (chunk.getBiome(colPos.east()).getCategory() == Biome.Category.OCEAN) {
                    return -1f;
                }
                else {
                    return .7f;
                }
            }
            if (chunk.getBiome(colPos.south(2)).getCategory() == Biome.Category.OCEAN) {
                if (chunk.getBiome(colPos.south()).getCategory() == Biome.Category.OCEAN) {
                    return -1f;
                }
                else {
                    return .7f;
                }
            }
            if (chunk.getBiome(colPos.west(2)).getCategory() == Biome.Category.OCEAN) {
                if (chunk.getBiome(colPos.west()).getCategory() == Biome.Category.OCEAN) {
                    return -1f;
                }
                else {
                    return .7f;
                }
            }
            if (chunk.getBiome(colPos.north(2)).getCategory() == Biome.Category.OCEAN) {
                if (chunk.getBiome(colPos.north()).getCategory() == Biome.Category.OCEAN) {
                    return -1f;
                }
                else {
                    return .7f;
                }
            }
        }
        return 1;
    }
}
