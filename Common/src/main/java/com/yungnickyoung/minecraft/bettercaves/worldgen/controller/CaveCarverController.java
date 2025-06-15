package com.yungnickyoung.minecraft.bettercaves.worldgen.controller;

import com.yungnickyoung.minecraft.bettercaves.BCSettings;
import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.enums.CaveType;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtils;
import com.yungnickyoung.minecraft.bettercaves.worldgen.CarverNoiseRange;
import com.yungnickyoung.minecraft.bettercaves.worldgen.ICarver;
import com.yungnickyoung.minecraft.bettercaves.worldgen.cave.CaveCarver;
import com.yungnickyoung.minecraft.yungsapi.noise.FastNoise;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import java.util.BitSet;
import java.util.List;
import java.util.function.Function;

public class CaveCarverController {
    private ServerLevel serverLevel;
//    private VanillaCaveCarver surfaceCaveCarver; // only used if surface caves enabled
    private FastNoise caveRegionSampler;
    private List<CarverNoiseRange> noiseRanges = new ArrayList<>();

    // Vars from config
    private boolean isDebugViewEnabled;
    private boolean isOverrideSurfaceDetectionEnabled;
    private boolean isSurfaceCavesEnabled;
    private boolean isFloodedUndergroundEnabled;

    public CaveCarverController(ServerLevel serverLevel) {
        this.serverLevel = serverLevel;
        this.isDebugViewEnabled = false;
        this.isOverrideSurfaceDetectionEnabled = BetterCavesCommon.CONFIG.undergroundGen.misc.overrideSurfaceDetection;
        this.isSurfaceCavesEnabled = true;
        this.isFloodedUndergroundEnabled = BetterCavesCommon.CONFIG.undergroundGen.misc.enableFloodedUnderground;
//        this.surfaceCaveCarver = new VanillaCaveCarverBuilder()
//                .bottomY(config.surfaceCaveBottom.get())
//                .topY(config.surfaceCaveTop.get())
//                .density(config.surfaceCaveDensity.get())
//                .liquidAltitude(config.liquidAltitude.get())
//                .replaceGravel(config.replaceFloatingGravel.get())
//                .floodedUnderground(config.enableFloodedUnderground.get())
//                .debugVisualizerEnabled(config.debugVisualizer.get())
//                .debugVisualizerBlock(Blocks.EMERALD_BLOCK.getDefaultState())
//                .build();

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
        List<ICarver> carvers = new ArrayList<>();
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
        // Vanilla caves
//        carvers.add(new VanillaCaveCarver.Builder()
//                .bottomY(BetterCavesCommon.CONFIG.undergroundGen.caves.vanillaCaves.caveBottom)
//                .topY(BetterCavesCommon.CONFIG.undergroundGen.caves.vanillaCaves.caveTop)
//                .density(BetterCavesCommon.CONFIG.undergroundGen.caves.vanillaCaves.caveDensity)
//                .priority(BetterCavesCommon.CONFIG.undergroundGen.caves.vanillaCaves.cavePriority)
//                .liquidAltitude(BetterCavesCommon.CONFIG.undergroundGen.misc.liquidAltitude)
//                .replaceGravel(BetterCavesCommon.CONFIG.undergroundGen.misc.replaceFloatingGravel)
//                .floodedUnderground(BetterCavesCommon.CONFIG.undergroundGen.misc.enableFloodedUnderground)
//                .debugVisualizerEnabled(false)
//                .debugVisualizerBlock(Blocks.BRICKS.defaultBlockState())
//                .build());

        // Remove carvers with no priority
        carvers.removeIf(carver -> carver.getPriority() == 0);

        // Initialize vars for calculating sampler noise thresholds
        float maxPossibleNoiseThreshold = (float) (BetterCavesCommon.CONFIG.undergroundGen.caves.caveSpawnChance * .01 * 2 - 1);
        int totalPriority = carvers.stream().map(ICarver::getPriority).reduce(0, Integer::sum);
        float totalRangeLength = maxPossibleNoiseThreshold - -1f;
        float currNoise = -1f;

        BetterCavesCommon.LOGGER.debug("CAVE INFORMATION");
        BetterCavesCommon.LOGGER.debug("--> MAX POSSIBLE THRESHOLD: {}", maxPossibleNoiseThreshold);
        BetterCavesCommon.LOGGER.debug("--> TOTAL PRIORITY: {}", totalPriority);
        BetterCavesCommon.LOGGER.debug("--> TOTAL RANGE LENGTH: {}", totalRangeLength);

        for (ICarver carver : carvers) {
            BetterCavesCommon.LOGGER.debug("--> CARVER");
            float noiseRangeLength = (float) carver.getPriority() / totalPriority * totalRangeLength;
            float rangeTop = currNoise + noiseRangeLength;
            CarverNoiseRange range = new CarverNoiseRange(currNoise, rangeTop, carver);
            currNoise = rangeTop;
            noiseRanges.add(range);

            BetterCavesCommon.LOGGER.debug("    --> RANGE FOUND: " + range);
        }
    }

    public void carveChunk(CarverConfiguration config, ChunkAccess chunkAccess, int[][] surfaceAltitudes,
                           BlockState[][] liquidBlocks, Function<BlockPos, Holder<Biome>> biomeProvider, CarvingMask carvingMask,
                           Aquifer aquifer
    ) {
        // Prevent unnecessary computation if caves are disabled
        if (noiseRanges.isEmpty() && !isSurfaceCavesEnabled) {
            return;
        }

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
//        boolean flooded;

        // Flag to keep track of whether we've already carved vanilla caves for this chunk, since
        // vanilla caves operate on a chunk-by-chunk basis rather than by column
        boolean shouldCarveVanillaCaves = false;

        // Since vanilla caves carve by chunk and not by column, we store an array
        // indicating which x-z coordinates are valid to be carved in
        boolean[][] validPositions = new boolean[16][16];

        // Break into subchunks for noise interpolation
        for (int subX = 0; subX < 16 / BCSettings.SUB_CHUNK_SIZE; subX++) {
            for (int subZ = 0; subZ < 16 / BCSettings.SUB_CHUNK_SIZE; subZ++) {
                int localStartX = subX * BCSettings.SUB_CHUNK_SIZE;
                int localStartZ = subZ * BCSettings.SUB_CHUNK_SIZE;
                int localEndX = localStartX + BCSettings.SUB_CHUNK_SIZE - 1;
                int localEndZ = localStartZ + BCSettings.SUB_CHUNK_SIZE - 1;
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
                for (int offsetX = 0; offsetX < BCSettings.SUB_CHUNK_SIZE; offsetX++) {
                    for (int offsetZ = 0; offsetZ < BCSettings.SUB_CHUNK_SIZE; offsetZ++) {
                        int localX = localStartX + offsetX;
                        int localZ = localStartZ + offsetZ;
                        BlockPos colPos = new BlockPos(chunkAccess.getPos().x * 16 + localX, 62, chunkAccess.getPos().z * 16 + localZ);

//                        flooded = isFloodedUndergroundEnabled
//                                && !isDebugViewEnabled
//                                && (biomeProvider.apply(colPos).is(BiomeTags.IS_OCEAN));
//                        if (flooded) {
//                            if ((BetterCavesUtils.isPosInWorld(mutablePos.set(colPos).move(Direction.EAST), serverLevel) && !biomeProvider.apply(mutablePos.set(colPos).move(Direction.EAST)).is(BiomeTags.IS_OCEAN)) ||
//                                    (BetterCavesUtils.isPosInWorld(mutablePos.set(colPos).move(Direction.WEST), serverLevel) && !biomeProvider.apply(mutablePos.set(colPos).move(Direction.WEST)).is(BiomeTags.IS_OCEAN)) ||
//                                    (BetterCavesUtils.isPosInWorld(mutablePos.set(colPos).move(Direction.NORTH), serverLevel) && !biomeProvider.apply(mutablePos.set(colPos).move(Direction.NORTH)).is(BiomeTags.IS_OCEAN)) ||
//                                    (BetterCavesUtils.isPosInWorld(mutablePos.set(colPos).move(Direction.SOUTH), serverLevel) && !biomeProvider.apply(mutablePos.set(colPos).move(Direction.SOUTH)).is(BiomeTags.IS_OCEAN))
//                            ) {
//                                continue;
//                            }
//                        }

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
//                            else if (range.getCarver() instanceof VanillaCaveCarver) {
//                                validPositions[localX][localZ] = true;
//                                shouldCarveVanillaCaves = true;
//                            }
                        }
                    }
                }
            }
        }
//        if (shouldCarveVanillaCaves) {
//            VanillaCaveCarver carver = null;
//            for (CarverNoiseRange range : noiseRanges) {
//                if (range.getCarver() instanceof VanillaCaveCarver) {
//                    carver = (VanillaCaveCarver) range.getCarver();
//                    break;
//                }
//            }
//            if (carver != null) {
//                carver.generate(serverLevel, chunkX, chunkZ, chunkAccess, true, liquidBlocks, biomeProvider, validPositions, airCarvingMask, liquidCarvingMask);
//            }
//        }
        // Generate surface caves if enabled
//        if (isSurfaceCavesEnabled) {
//            surfaceCaveCarver.generate(serverLevel, chunkX, chunkZ, chunkAccess, false, liquidBlocks, biomeProvider, airCarvingMask, liquidCarvingMask);
//        }
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

//    public void setServerLevel(ServerLevel serverLevel) {
//        this.serverLevel = serverLevel;
//        this.surfaceCaveCarver.setWorld(serverLevel);
//        for (CarverNoiseRange range : noiseRanges) {
//            if (range.getCarver() instanceof VanillaCaveCarver) {
//                ((VanillaCaveCarver) range.getCarver()).setWorld(serverLevel);
//                break;
//            }
//        }
//    }
}
