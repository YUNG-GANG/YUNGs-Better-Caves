package com.yungnickyoung.minecraft.bettercaves.worldgen.controller;

import com.yungnickyoung.minecraft.bettercaves.BCSettings;
import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.enums.CavernType;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseUtils;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtils;
import com.yungnickyoung.minecraft.bettercaves.worldgen.CarverNoiseRange;
import com.yungnickyoung.minecraft.bettercaves.worldgen.cavern.CavernCarver;
import com.yungnickyoung.minecraft.yungsapi.noise.FastNoise;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CavernCarverController {
    private ServerLevel serverLevel;
    private FastNoise cavernRegionSampler;
    private List<CarverNoiseRange> noiseRanges = new ArrayList<>();

    // Vars from config
    private boolean isDebugViewEnabled;
    private boolean isOverrideSurfaceDetectionEnabled;
    private boolean isFloodedUndergroundEnabled;

    public CavernCarverController(ServerLevel serverLevel) {
        this.serverLevel = serverLevel;
        this.isDebugViewEnabled = false; //config.debugVisualizer.get();
        this.isOverrideSurfaceDetectionEnabled = BetterCavesCommon.CONFIG.undergroundGen.misc.overrideSurfaceDetection;
        this.isFloodedUndergroundEnabled = BetterCavesCommon.CONFIG.undergroundGen.misc.enableFloodedUnderground;

        // Configure cavern region sampler, which determines what type of cavern should be carved in any given region
        float cavernRegionSize = calcCavernRegionSize(
                BetterCavesCommon.CONFIG.undergroundGen.caverns.cavernRegionSize,
                (float) BetterCavesCommon.CONFIG.undergroundGen.caverns.customRegionSize);
        this.cavernRegionSampler = new FastNoise();
        this.cavernRegionSampler.SetSeed((int) this.serverLevel.getSeed() + 333);
        this.cavernRegionSampler.SetFrequency(cavernRegionSize);

        // Initialize all carvers using config options
        List<CavernCarver> carvers = new ArrayList<>();
        carvers.add(new CavernCarver.Builder(this.serverLevel.getSeed())
                .ofTypeFromConfig(CavernType.LIQUID)
                .debugVisualizerBlock(Blocks.REDSTONE_BLOCK.defaultBlockState())
                .build()
        );
        carvers.add(new CavernCarver.Builder(this.serverLevel.getSeed())
                .ofTypeFromConfig(CavernType.FLOORED)
                .debugVisualizerBlock(Blocks.GOLD_BLOCK.defaultBlockState())
                .build()
        );

        float spawnChance = (float) (BetterCavesCommon.CONFIG.undergroundGen.caverns.cavernSpawnChance / 100f);
        int totalPriority = carvers.stream().map(CavernCarver::getPriority).reduce(0, Integer::sum);

        BetterCavesCommon.LOGGER.debug("CAVERN INFORMATION");
        BetterCavesCommon.LOGGER.debug("--> SPAWN CHANCE SET TO: {}", spawnChance);
        BetterCavesCommon.LOGGER.debug("--> TOTAL PRIORITY: {}", totalPriority);

        carvers.removeIf(carver -> carver.getPriority() == 0);
        float totalDeadzonePercent = 1 - spawnChance;
        float deadzonePercent = carvers.size() > 1
                ? totalDeadzonePercent / (carvers.size() - 1)
                : totalDeadzonePercent;

        BetterCavesCommon.LOGGER.debug("--> DEADZONE PERCENT: {}({} TOTAL)", deadzonePercent, totalDeadzonePercent);

        float currNoise = -1f;

        for (CavernCarver carver : carvers) {
            BetterCavesCommon.LOGGER.debug("--> CARVER");
            float rangeCDFPercent = (float)carver.getPriority() / totalPriority * spawnChance;
            float topNoise = NoiseUtils.simplexNoiseOffsetByPercent(currNoise, rangeCDFPercent);
            CarverNoiseRange range = new CarverNoiseRange(currNoise, topNoise, carver);
            noiseRanges.add(range);

            // Offset currNoise for deadzone region
            currNoise = NoiseUtils.simplexNoiseOffsetByPercent(topNoise, deadzonePercent);

            BetterCavesCommon.LOGGER.debug("    --> RANGE PERCENT LENGTH WANTED: {}", rangeCDFPercent);
            BetterCavesCommon.LOGGER.debug("    --> RANGE FOUND: {}", range);
        }
    }

    public void carveChunk(CarverConfiguration config, ChunkAccess chunkAccess, int[][] surfaceAltitudes,
                           BlockState[][] liquidBlocks, Function<BlockPos, Holder<Biome>> biomeProvider, CarvingMask carvingMask,
                           Aquifer aquifer) {
        // Prevent unnecessary computation if caverns are disabled
        if (noiseRanges.isEmpty()) {
            return;
        }

//        boolean flooded = false;
        float smoothAmpFloodFactor = 1;

        for (int subX = 0; subX < 16 / BCSettings.SUB_CHUNK_SIZE; subX++) {
            for (int subZ = 0; subZ < 16 / BCSettings.SUB_CHUNK_SIZE; subZ++) {
                int localStartX = subX * BCSettings.SUB_CHUNK_SIZE;
                int localStartZ = subZ * BCSettings.SUB_CHUNK_SIZE;
                int localEndX = localStartX + BCSettings.SUB_CHUNK_SIZE - 1;
                int localEndZ = localStartZ + BCSettings.SUB_CHUNK_SIZE - 1;
                BlockPos startPos = new BlockPos(chunkAccess.getPos().x * 16 + localStartX, 1, chunkAccess.getPos().z * 16 + localStartZ);
                BlockPos endPos = new BlockPos(chunkAccess.getPos().x * 16 + localEndX, 1, chunkAccess.getPos().z * 16 + localEndZ);

                // Reset noise cubes for this subchunk
                noiseRanges.forEach(range -> range.setNoiseCube(null));

                // Get max height in subchunk. This is needed for calculating the noise cube
                int maxHeight = 0;
                if (!isOverrideSurfaceDetectionEnabled) { // Only necessary if we aren't overriding surface detection
                    for (int x = localStartX; x < localEndX; x++) {
                        for (int z = localStartZ; z < localEndZ; z++) {
                            maxHeight = Math.max(maxHeight, surfaceAltitudes[x][z]);
                        }
                    }
                    for (CarverNoiseRange range : noiseRanges) {
                        CavernCarver carver = (CavernCarver) range.getCarver();
                        maxHeight = Math.max(maxHeight, carver.getTopY());
                    }
                }

                for (int offsetX = 0; offsetX < BCSettings.SUB_CHUNK_SIZE; offsetX++) {
                    for (int offsetZ = 0; offsetZ < BCSettings.SUB_CHUNK_SIZE; offsetZ++) {
                        int localX = localStartX + offsetX;
                        int localZ = localStartZ + offsetZ;
                        BlockPos colPos = new BlockPos(chunkAccess.getPos().x * 16 + localX, 1, chunkAccess.getPos().z * 16 + localZ);

//                        if (isFloodedUndergroundEnabled && !isDebugViewEnabled) {
//                            flooded = biomeProvider.apply(colPos).is(BiomeTags.IS_OCEAN);
//                            smoothAmpFloodFactor = BetterCavesUtils.getDistFactor(serverLevel, biomeProvider, colPos, 2,
//                                    flooded ? BetterCavesUtils.isNotOcean : BetterCavesUtils.isOcean);
//                            if (smoothAmpFloodFactor <= .25) { // Wall between flooded and normal caves.
//                                continue; // Continue to prevent unnecessary noise calculation
//                            }
//                        }

                        int surfaceAltitude = surfaceAltitudes[localX][localZ];
                        BlockState liquidBlock = liquidBlocks[localX][localZ];

                        // Get noise values used to determine cavern region
                        float cavernRegionNoise = cavernRegionSampler.GetNoise(colPos.getX(), colPos.getZ());

                        // Carve cavern using matching carver
                        for (CarverNoiseRange range : noiseRanges) {
                            if (!range.contains(cavernRegionNoise)) {
                                continue;
                            }
                            CavernCarver carver = (CavernCarver) range.getCarver();
                            int bottomY = carver.getBottomY();
                            int topY = isDebugViewEnabled ? carver.getTopY() : Math.min(surfaceAltitude, carver.getTopY());
                            if (isOverrideSurfaceDetectionEnabled) {
                                topY = carver.getTopY();
                                maxHeight = carver.getTopY();
                            }
                            float smoothAmp = range.getSmoothAmp(cavernRegionNoise) * smoothAmpFloodFactor;
                            if (range.getNoiseCube() == null) {
                                range.setNoiseCube(carver.getNoiseGen().interpolateNoiseCube(startPos, endPos, bottomY, maxHeight));
                            }
                            double[][] noiseColumn = range.getNoiseCube()[offsetX][offsetZ];
                            carver.carveColumn(config, chunkAccess, colPos, topY, smoothAmp, noiseColumn, liquidBlock, carvingMask, aquifer);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * @return frequency value for cavern region sampler
     */
    private float calcCavernRegionSize(String cavernRegionSize, float cavernRegionCustomSize) {
        return switch (cavernRegionSize) {
            case "Small" -> .01f;
            case "Large" -> .005f;
            case "ExtraLarge" -> .001f;
            case "Custom" -> cavernRegionCustomSize;
            default -> .007f;
        };
    }
}
