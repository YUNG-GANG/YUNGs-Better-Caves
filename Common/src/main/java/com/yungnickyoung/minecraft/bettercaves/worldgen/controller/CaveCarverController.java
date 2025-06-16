package com.yungnickyoung.minecraft.bettercaves.worldgen.controller;

import com.yungnickyoung.minecraft.bettercaves.BCConstants;
import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.enums.CaveType;
import com.yungnickyoung.minecraft.bettercaves.worldgen.carver.CarverNoiseRange;
import com.yungnickyoung.minecraft.bettercaves.worldgen.carver.AbstractCarver;
import com.yungnickyoung.minecraft.bettercaves.worldgen.carver.CaveCarver;
import com.yungnickyoung.minecraft.yungsapi.noise.FastNoise;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
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

public class CaveCarverController {
    private final FastNoise caveRegionSampler;
    private final List<CarverNoiseRange> noiseRanges = new ArrayList<>();

    // Vars from config
    private final boolean isDebugViewEnabled;
    private final boolean isOverrideSurfaceDetectionEnabled;

    public CaveCarverController(ServerLevel serverLevel) {
        this.isDebugViewEnabled = false;
        this.isOverrideSurfaceDetectionEnabled = BetterCavesCommon.CONFIG.undergroundGen.misc.overrideSurfaceDetection;

        // Configure cave region sampler, which determines what type of cave should be
        // carved in any given region
        float caveRegionSize = calcCaveRegionSize(
                BetterCavesCommon.CONFIG.undergroundGen.caves.caveRegionSize,
                (float) BetterCavesCommon.CONFIG.undergroundGen.caves.customRegionSize);
        this.caveRegionSampler = new FastNoise();
        this.caveRegionSampler.SetSeed((int) serverLevel.getSeed() + 222);
        this.caveRegionSampler.SetFrequency(caveRegionSize);
        this.caveRegionSampler.SetNoiseType(FastNoise.NoiseType.Cellular);
        this.caveRegionSampler.SetCellularDistanceFunction(FastNoise.CellularDistanceFunction.Natural);

        // Initialize all carvers using config options
        List<AbstractCarver> carvers = new ArrayList<>();
        // Type 1 caves
        carvers.add(new CaveCarver.Builder(serverLevel.getSeed())
                .ofTypeFromConfig(CaveType.CUBIC)
                .debugVisualizerBlock(Blocks.OAK_PLANKS.defaultBlockState())
                .build()
        );
        // Type 2 caves
        carvers.add(new CaveCarver.Builder(serverLevel.getSeed())
                .ofTypeFromConfig(CaveType.SIMPLEX)
                .debugVisualizerBlock(Blocks.COBBLESTONE.defaultBlockState())
                .build()
        );

        // Remove carvers with no priority
        carvers.removeIf(carver -> carver.getPriority() == 0);

        // Initialize vars for calculating sampler noise thresholds
        float maxPossibleNoiseThreshold = (float) (BetterCavesCommon.CONFIG.undergroundGen.caves.caveSpawnChance * .01 * 2 - 1);
        int totalPriority = carvers.stream().map(AbstractCarver::getPriority).reduce(0, Integer::sum);
        float totalRangeLength = maxPossibleNoiseThreshold - (-1f);
        float currNoise = -1f;

        BetterCavesCommon.LOGGER.debug("CAVE INFORMATION");
        BetterCavesCommon.LOGGER.debug("--> MAX POSSIBLE THRESHOLD: {}", maxPossibleNoiseThreshold);
        BetterCavesCommon.LOGGER.debug("--> TOTAL PRIORITY: {}", totalPriority);
        BetterCavesCommon.LOGGER.debug("--> TOTAL RANGE LENGTH: {}", totalRangeLength);

        for (AbstractCarver carver : carvers) {
            BetterCavesCommon.LOGGER.debug("--> CARVER");
            float noiseRangeLength = (float) carver.getPriority() / totalPriority * totalRangeLength;
            float rangeTop = currNoise + noiseRangeLength;
            CarverNoiseRange range = new CarverNoiseRange(currNoise, rangeTop, carver);
            currNoise = rangeTop;
            noiseRanges.add(range);
            BetterCavesCommon.LOGGER.debug("    --> RANGE FOUND: {}", range);
        }
    }

    public void carveChunk(CarverConfiguration config, ChunkAccess chunkAccess, int[][] surfaceAltitudes,
                           BlockState[][] liquidBlocks, Function<BlockPos, Holder<Biome>> biomeProvider, CarvingMask carvingMask,
                           Aquifer aquifer) {
        // Prevent unnecessary computation if caves are disabled
        if (noiseRanges.isEmpty()) {
            return;
        }

        // Break into subchunks for noise interpolation
        for (int subX = 0; subX < 16 / BCConstants.SUB_CHUNK_SIZE; subX++) {
            for (int subZ = 0; subZ < 16 / BCConstants.SUB_CHUNK_SIZE; subZ++) {
                int localStartX = subX * BCConstants.SUB_CHUNK_SIZE;
                int localStartZ = subZ * BCConstants.SUB_CHUNK_SIZE;
                int localEndX = localStartX + BCConstants.SUB_CHUNK_SIZE - 1;
                int localEndZ = localStartZ + BCConstants.SUB_CHUNK_SIZE - 1;
                BlockPos startPos = new BlockPos(chunkAccess.getPos().x * 16 + localStartX, 1, chunkAccess.getPos().z * 16 + localStartZ);
                BlockPos endPos = new BlockPos(chunkAccess.getPos().x * 16 + localEndX, 1, chunkAccess.getPos().z * 16 + localEndZ);

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
                        maxHeight = Math.max(maxHeight, range.getCarver().getTopY());
                    }
                }

                // Offset within subchunk
                for (int offsetX = 0; offsetX < BCConstants.SUB_CHUNK_SIZE; offsetX++) {
                    for (int offsetZ = 0; offsetZ < BCConstants.SUB_CHUNK_SIZE; offsetZ++) {
                        int localX = localStartX + offsetX;
                        int localZ = localStartZ + offsetZ;
                        BlockPos colPos = new BlockPos(chunkAccess.getPos().x * 16 + localX, 62, chunkAccess.getPos().z * 16 + localZ);

                        int surfaceAltitude = surfaceAltitudes[localX][localZ];
                        BlockState liquidBlock = liquidBlocks[localX][localZ];

                        // Get noise values used to determine cave region
                        float caveRegionNoise = caveRegionSampler.GetNoise(colPos.getX(), colPos.getZ());

                        // Carve cave using matching carver
                        for (CarverNoiseRange range : noiseRanges) {
                            if (!range.contains(caveRegionNoise)) {
                                continue;
                            }
                            if (range.getCarver() instanceof CaveCarver carver) {
                                int bottomY = carver.getBottomY();
                                int topY = Math.min(surfaceAltitude, carver.getTopY());
                                if (isOverrideSurfaceDetectionEnabled) {
                                    topY = carver.getTopY();
                                    maxHeight = carver.getTopY();
                                }
                                if (isDebugViewEnabled) {
                                    topY = 128;
                                    maxHeight = 128;
                                }
                                if (range.getNoiseCube() == null) {
                                    range.setNoiseCube(carver.getNoiseGen().interpolateNoiseCube(startPos, endPos, bottomY, maxHeight));
                                }
                                double[][] noiseColumn = range.getNoiseCube()[offsetX][offsetZ];
                                carver.carveColumn(config, chunkAccess, colPos, topY, noiseColumn, liquidBlock, carvingMask, aquifer);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @return frequency value for cave region sampler
     */
    private float calcCaveRegionSize(String caveRegionSize, float caveRegionCustomSize) {
        return switch (caveRegionSize) {
            case "Small" -> .008f;
            case "Large" -> .0032f;
            case "ExtraLarge" -> .001f;
            case "Custom" -> caveRegionCustomSize;
            default -> .005f;
        };
    }
}
