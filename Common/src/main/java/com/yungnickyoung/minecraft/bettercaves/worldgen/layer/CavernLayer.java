package com.yungnickyoung.minecraft.bettercaves.worldgen.layer;

import com.yungnickyoung.minecraft.bettercaves.BCConstants;
import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseUtils;
import com.yungnickyoung.minecraft.bettercaves.worldgen.BetterCavesWorldCarverConfig;
import com.yungnickyoung.minecraft.bettercaves.worldgen.carver.AbstractCarver;
import com.yungnickyoung.minecraft.bettercaves.worldgen.carver.CarverNoiseRange;
import com.yungnickyoung.minecraft.bettercaves.worldgen.carver.CavernCarver;
import com.yungnickyoung.minecraft.yungsapi.math.ColPos;
import com.yungnickyoung.minecraft.yungsapi.noise.FastNoise;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CavernLayer {
    private final FastNoise cavernRegionSampler;
    private final List<CarverNoiseRange> noiseRanges = new ArrayList<>();

    // Vars from config
    private final BetterCavesWorldCarverConfig config;
    private final boolean isDebugViewEnabled;
    private final boolean isOverrideSurfaceDetectionEnabled;

    public CavernLayer(ServerLevel serverLevel, BetterCavesWorldCarverConfig config,
                       BetterCavesWorldCarverConfig.CavernLayerSettings layerSettings) {
        this.config = config;
        this.isDebugViewEnabled = config.debugSettings.enabled();
        this.isOverrideSurfaceDetectionEnabled = config.misc.overrideSurfaceDetection();

        // Configure cavern region sampler, which determines what type of cavern should be carved in any given region
        double cavernRegionSize = layerSettings.cavernRegionSizeFrequency();
        this.cavernRegionSampler = new FastNoise();
        this.cavernRegionSampler.SetSeed((int) serverLevel.getSeed() + 333);
        this.cavernRegionSampler.SetFrequency((float) cavernRegionSize);

        // Initialize all carvers using config options
        List<AbstractCarver> carvers = CavernCarver.createCarversFromConfig(serverLevel, config, layerSettings);

        float spawnChance = (float) (layerSettings.cavernSpawnChance() / 100f);
        int totalSpawnWeight = carvers.stream().map(AbstractCarver::getSpawnWeight).reduce(0, Integer::sum);

        BetterCavesCommon.LOGGER.debug("CAVERN INFORMATION");
        BetterCavesCommon.LOGGER.debug("--> SPAWN CHANCE SET TO: {}", spawnChance);
        BetterCavesCommon.LOGGER.debug("--> TOTAL SPAWN WEIGHT: {}", totalSpawnWeight);

        carvers.removeIf(carver -> carver.getSpawnWeight() == 0);
        float totalDeadzonePercent = 1 - spawnChance;
        float deadzonePercent = carvers.size() > 1
                ? totalDeadzonePercent / (carvers.size() - 1)
                : totalDeadzonePercent;

        BetterCavesCommon.LOGGER.debug("--> DEADZONE PERCENT: {}({} TOTAL)", deadzonePercent, totalDeadzonePercent);

        float currNoise = -1f;

        for (AbstractCarver carver : carvers) {
            BetterCavesCommon.LOGGER.debug("--> CARVER");
            float rangeCDFPercent = (float) carver.getSpawnWeight() / totalSpawnWeight * spawnChance;
            float topNoise = NoiseUtils.simplexNoiseOffsetByPercent(currNoise, rangeCDFPercent);
            CarverNoiseRange range = new CarverNoiseRange(currNoise, topNoise, carver);
            noiseRanges.add(range);

            // Offset currNoise for deadzone region
            currNoise = NoiseUtils.simplexNoiseOffsetByPercent(topNoise, deadzonePercent);

            BetterCavesCommon.LOGGER.debug("    --> RANGE PERCENT LENGTH WANTED: {}", rangeCDFPercent);
            BetterCavesCommon.LOGGER.debug("    --> RANGE FOUND: {}", range);
        }
    }

    public void carveChunk(ChunkAccess chunkAccess, int[][] surfaceAltitudes,
                           BlockState[][] liquidBlocks, Function<BlockPos, Holder<Biome>> biomeProvider, CarvingMask carvingMask,
                           Aquifer aquifer) {
        // Prevent unnecessary computation if caverns are disabled
        if (this.noiseRanges.isEmpty()) return;

        float smoothAmpFloodFactor = 1;

        // Break into subchunks for noise interpolation
        for (int subX = 0; subX < 16 / BCConstants.SUB_CHUNK_SIZE; subX++) {
            for (int subZ = 0; subZ < 16 / BCConstants.SUB_CHUNK_SIZE; subZ++) {
                int localSubStartX = subX * BCConstants.SUB_CHUNK_SIZE;
                int localSubStartZ = subZ * BCConstants.SUB_CHUNK_SIZE;
                int localSubEndX = localSubStartX + BCConstants.SUB_CHUNK_SIZE - 1;
                int localSubEndZ = localSubStartZ + BCConstants.SUB_CHUNK_SIZE - 1;
                BlockPos startPos = new BlockPos(chunkAccess.getPos().x * 16 + localSubStartX, 1, chunkAccess.getPos().z * 16 + localSubStartZ);
                BlockPos endPos = new BlockPos(chunkAccess.getPos().x * 16 + localSubEndX, 1, chunkAccess.getPos().z * 16 + localSubEndZ);

                // Get max height in subchunk. This is needed for calculating the noise cube.
                int maxHeight = Integer.MIN_VALUE;
                if (!this.isOverrideSurfaceDetectionEnabled) {
                    // Only need to consider surface altitudes if we aren't overriding surface detection
                    for (int x = localSubStartX; x < localSubEndX; x++) {
                        for (int z = localSubStartZ; z < localSubEndZ; z++) {
                            maxHeight = Math.max(maxHeight, surfaceAltitudes[x][z]);
                        }
                    }
                    for (CarverNoiseRange range : this.noiseRanges) {
                        CavernCarver carver = (CavernCarver) range.getCarver();
                        maxHeight = Math.max(maxHeight, carver.getTopY());
                    }
                } else {
                    for (CarverNoiseRange range : this.noiseRanges) {
                        maxHeight = Math.max(maxHeight, range.getCarver().getTopY());
                    }
                }

                // Construct noise cube for each range
                Map<CarverNoiseRange, double[][][][]> noiseCubes = new HashMap<>();
                for (CarverNoiseRange range : this.noiseRanges) {
                    double[][][][] noiseCube = range.getCarver().getNoiseGen().interpolateNoiseCube(startPos, endPos, range.getCarver().getBottomY(), maxHeight);
                    noiseCubes.put(range, noiseCube);
                }

                // Process current subchunk
                for (int offsetX = 0; offsetX < BCConstants.SUB_CHUNK_SIZE; offsetX++) {
                    for (int offsetZ = 0; offsetZ < BCConstants.SUB_CHUNK_SIZE; offsetZ++) {
                        int localX = localSubStartX + offsetX;
                        int localZ = localSubStartZ + offsetZ;
                        ColPos colPos = new ColPos(chunkAccess.getPos().x * 16 + localX, chunkAccess.getPos().z * 16 + localZ);

                        int surfaceAltitude = surfaceAltitudes[localX][localZ];
                        BlockState liquidBlock = liquidBlocks[localX][localZ];

                        // Get noise values used to determine cavern region
                        float cavernRegionNoise = this.cavernRegionSampler.GetNoise(colPos.getX(), colPos.getZ());

                        // Carve cavern using matching carver
                        for (CarverNoiseRange range : this.noiseRanges) {
                            if (!range.contains(cavernRegionNoise)) continue;

                            int topY = this.isDebugViewEnabled || this.isOverrideSurfaceDetectionEnabled
                                    ? range.getCarver().getTopY()
                                    : Math.min(surfaceAltitude, range.getCarver().getTopY());

                            float smoothAmp = range.getSmoothAmp(cavernRegionNoise);
                            double[][] noiseColumn = noiseCubes.get(range)[offsetX][offsetZ];
                            ((CavernCarver) range.getCarver()).carveColumn(config, chunkAccess, colPos, topY, smoothAmp, noiseColumn, liquidBlock, carvingMask, aquifer);
                            break;
                        }
                    }
                }
            }
        }
    }
}
