package com.yungnickyoung.minecraft.bettercaves.worldgen.layer;

import com.yungnickyoung.minecraft.bettercaves.BCConstants;
import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.worldgen.BetterCavesWorldCarverConfig;
import com.yungnickyoung.minecraft.bettercaves.worldgen.carver.AbstractCarver;
import com.yungnickyoung.minecraft.bettercaves.worldgen.carver.CarverNoiseRange;
import com.yungnickyoung.minecraft.bettercaves.worldgen.carver.CaveCarver;
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

public class CaveLayer {
    private final FastNoise caveRegionSampler;
    private final List<CarverNoiseRange> noiseRanges = new ArrayList<>();

    // Vars from config
    private final BetterCavesWorldCarverConfig config;
    private final boolean isDebugViewEnabled;
    private final boolean isOverrideSurfaceDetectionEnabled;

    public CaveLayer(ServerLevel serverLevel, BetterCavesWorldCarverConfig config,
                     BetterCavesWorldCarverConfig.CaveLayerSettings layerSettings) {
        this.config = config;
        this.isDebugViewEnabled = config.debugSettings.enabled();
        this.isOverrideSurfaceDetectionEnabled = config.misc.overrideSurfaceDetection();

        // Configure cave region sampler, which determines what type of cave should be
        // carved in any given region
        double caveRegionSize = layerSettings.caveRegionSizeFrequency();
        this.caveRegionSampler = new FastNoise();
        this.caveRegionSampler.SetSeed((int) serverLevel.getSeed() + 222);
        this.caveRegionSampler.SetFrequency((float) caveRegionSize);
        this.caveRegionSampler.SetNoiseType(FastNoise.NoiseType.Cellular);
        this.caveRegionSampler.SetCellularDistanceFunction(FastNoise.CellularDistanceFunction.Natural);

        // Initialize all carvers using config options
        List<AbstractCarver> carvers = CaveCarver.createCarversFromConfig(serverLevel, config, layerSettings);

        // Remove carvers with no spawn weight to prevent unnecessary computation
        carvers.removeIf(carver -> carver.getSpawnWeight() == 0);

        // Initialize vars for calculating sampler noise thresholds
        float maxPossibleNoiseThreshold = (float) (layerSettings.caveSpawnChance() * .01 * 2 - 1);
        int totalSpawnWeight = carvers.stream().map(AbstractCarver::getSpawnWeight).reduce(0, Integer::sum);
        float totalRangeLength = maxPossibleNoiseThreshold - (-1f);
        float currNoise = -1f;

        BetterCavesCommon.LOGGER.debug("CAVE INFORMATION");
        BetterCavesCommon.LOGGER.debug("--> MAX POSSIBLE THRESHOLD: {}", maxPossibleNoiseThreshold);
        BetterCavesCommon.LOGGER.debug("--> TOTAL SPAWN WEIGHT: {}", totalSpawnWeight);
        BetterCavesCommon.LOGGER.debug("--> TOTAL RANGE LENGTH: {}", totalRangeLength);

        for (AbstractCarver carver : carvers) {
            BetterCavesCommon.LOGGER.debug("--> CARVER");
            float noiseRangeLength = (float) carver.getSpawnWeight() / totalSpawnWeight * totalRangeLength;
            float rangeTop = currNoise + noiseRangeLength;
            CarverNoiseRange range = new CarverNoiseRange(currNoise, rangeTop, carver);
            currNoise = rangeTop;
            noiseRanges.add(range);
            BetterCavesCommon.LOGGER.debug("    --> RANGE FOUND: {}", range);
        }
    }

    public void carveChunk(ChunkAccess chunkAccess, int[][] surfaceAltitudes,
                           BlockState[][] liquidBlocks, Function<BlockPos, Holder<Biome>> biomeProvider, CarvingMask carvingMask,
                           Aquifer aquifer) {
        // Prevent unnecessary computation if caves are disabled
        if (this.noiseRanges.isEmpty()) return;

        // Break into subchunks for noise interpolation
        for (int subX = 0; subX < 16 / BCConstants.SUB_CHUNK_SIZE; subX++) {
            for (int subZ = 0; subZ < 16 / BCConstants.SUB_CHUNK_SIZE; subZ++) {
                int localSubStartX = subX * BCConstants.SUB_CHUNK_SIZE;
                int localSubStartZ = subZ * BCConstants.SUB_CHUNK_SIZE;
                int localSubEndX = localSubStartX + BCConstants.SUB_CHUNK_SIZE - 1;
                int localSubEndZ = localSubStartZ + BCConstants.SUB_CHUNK_SIZE - 1;
                BlockPos startPos = new BlockPos(chunkAccess.getPos().x() * 16 + localSubStartX, 1, chunkAccess.getPos().z() * 16 + localSubStartZ);
                BlockPos endPos = new BlockPos(chunkAccess.getPos().x() * 16 + localSubEndX, 1, chunkAccess.getPos().z() * 16 + localSubEndZ);

                // Get max height in subchunk. This is needed for calculating the noise cube.
                int maxHeight = Integer.MIN_VALUE;
                if (!this.isOverrideSurfaceDetectionEnabled) {
                    // Only need to consider surface altitudes if we aren't overriding surface detection
                    for (int x = localSubStartX; x <= localSubEndX; x++) {
                        for (int z = localSubStartZ; z <= localSubEndZ; z++) {
                            maxHeight = Math.max(maxHeight, surfaceAltitudes[x][z]);
                        }
                    }
                    for (CarverNoiseRange range : this.noiseRanges) {
                        maxHeight = Math.max(maxHeight, range.getCarver().getTopY());
                    }
                } else {
                    for (CarverNoiseRange range : this.noiseRanges) {
                        maxHeight = Math.max(maxHeight, range.getCarver().getTopY());
                    }
                }
                if (this.isDebugViewEnabled) {
                    maxHeight = this.config.debugSettings.topY();
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
                        ColPos colPos = new ColPos(chunkAccess.getPos().x() * 16 + localX, chunkAccess.getPos().z() * 16 + localZ);

                        int surfaceAltitude = surfaceAltitudes[localX][localZ];
                        BlockState liquidBlock = liquidBlocks[localX][localZ];

                        // Get noise values used to determine cave region
                        float caveRegionNoise = this.caveRegionSampler.GetNoise(colPos.getX(), colPos.getZ());

                        // Carve cave using matching carver
                        for (CarverNoiseRange range : this.noiseRanges) {
                            if (!range.contains(caveRegionNoise)) continue;

                            int topY = this.isOverrideSurfaceDetectionEnabled
                                    ? range.getCarver().getTopY()
                                    : Math.min(surfaceAltitude, range.getCarver().getTopY());
                            if (this.isDebugViewEnabled) topY = this.config.debugSettings.topY();

                            double[][] noiseColumn = noiseCubes.get(range)[offsetX][offsetZ];
                            ((CaveCarver) range.getCarver()).carveColumn(this.config, chunkAccess, colPos, topY, noiseColumn, liquidBlock, carvingMask, aquifer);
                            break;
                        }
                    }
                }
            }
        }
    }
}
