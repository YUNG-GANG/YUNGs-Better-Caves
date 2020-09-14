package com.yungnickyoung.minecraft.bettercaves.world.carver.controller;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.BCSettings;
import com.yungnickyoung.minecraft.bettercaves.config.util.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.enums.CaveType;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseColumn;
import com.yungnickyoung.minecraft.bettercaves.util.ColPos;
import com.yungnickyoung.minecraft.bettercaves.world.carver.CarverNoiseRange;
import com.yungnickyoung.minecraft.bettercaves.world.carver.ICarver;
import com.yungnickyoung.minecraft.bettercaves.world.carver.cave.CaveCarver;
import com.yungnickyoung.minecraft.bettercaves.world.carver.cave.CaveCarverBuilder;
import com.yungnickyoung.minecraft.bettercaves.world.carver.vanilla.VanillaCaveCarver;
import com.yungnickyoung.minecraft.bettercaves.world.carver.vanilla.VanillaCaveCarverBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

import static com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtils.isPosInWorld;

public class CaveCarverController {
    private StructureWorldAccess world;
    private VanillaCaveCarver surfaceCaveCarver; // only used if surface caves enabled
    private FastNoise caveRegionController;
    private List<CarverNoiseRange> noiseRanges = new ArrayList<>();

    // Vars from config
    private boolean isDebugViewEnabled;
    private boolean isOverrideSurfaceDetectionEnabled;
    private boolean isSurfaceCavesEnabled;
    private boolean isFloodedUndergroundEnabled;

    public CaveCarverController(StructureWorldAccess worldIn, ConfigHolder config) {
        this.world = worldIn;
        this.isDebugViewEnabled = config.debugVisualizer.get();
        this.isOverrideSurfaceDetectionEnabled = config.overrideSurfaceDetection.get();
        this.isSurfaceCavesEnabled = config.isSurfaceCavesEnabled.get();
        this.isFloodedUndergroundEnabled = config.enableFloodedUnderground.get();
        this.surfaceCaveCarver = new VanillaCaveCarverBuilder()
            .bottomY(config.surfaceCaveBottom.get())
            .topY(config.surfaceCaveTop.get())
            .density(config.surfaceCaveDensity.get())
            .liquidAltitude(config.liquidAltitude.get())
            .replaceGravel(config.replaceFloatingGravel.get())
            .floodedUnderground(config.enableFloodedUnderground.get())
            .debugVisualizerEnabled(config.debugVisualizer.get())
            .debugVisualizerBlock(Blocks.EMERALD_BLOCK.getDefaultState())
            .build();

        // Configure cave region controller, which determines what type of cave should be
        // carved in any given region
        float caveRegionSize = calcCaveRegionSize(config.caveRegionSize.get(), config.caveRegionCustomSize.get().floatValue());
        this.caveRegionController = new FastNoise();
        this.caveRegionController.SetSeed((int)worldIn.getSeed() + 222);
        this.caveRegionController.SetFrequency(caveRegionSize);
        this.caveRegionController.SetNoiseType(FastNoise.NoiseType.Cellular);
        this.caveRegionController.SetCellularDistanceFunction(FastNoise.CellularDistanceFunction.Natural);

        // Initialize all carvers using config options
        List<ICarver> carvers = new ArrayList<>();
        // Type 1 caves
        carvers.add(new CaveCarverBuilder(worldIn.getSeed())
            .ofTypeFromConfig(CaveType.CUBIC, config)
            .debugVisualizerBlock(Blocks.OAK_PLANKS.getDefaultState())
            .build()
        );
        // Type 2 caves
        carvers.add(new CaveCarverBuilder(worldIn.getSeed())
            .ofTypeFromConfig(CaveType.SIMPLEX, config)
            .debugVisualizerBlock(Blocks.COBBLESTONE.getDefaultState())
            .build()
        );
        // Vanilla caves
        carvers.add(new VanillaCaveCarverBuilder()
            .bottomY(config.vanillaCaveBottom.get())
            .topY(config.vanillaCaveTop.get())
            .density(config.vanillaCaveDensity.get())
            .priority(config.vanillaCavePriority.get())
            .liquidAltitude(config.liquidAltitude.get())
            .replaceGravel(config.replaceFloatingGravel.get())
            .floodedUnderground(config.enableFloodedUnderground.get())
            .debugVisualizerEnabled(config.debugVisualizer.get())
            .debugVisualizerBlock(Blocks.BRICKS.getDefaultState())
            .build());

        // Remove carvers with no priority
        carvers.removeIf(carver -> carver.getPriority() == 0);

        // Initialize vars for calculating controller noise thresholds
        float maxPossibleNoiseThreshold = config.caveSpawnChance.get().floatValue() * .01f * 2 - 1;
        int totalPriority = carvers.stream().map(ICarver::getPriority).reduce(0, Integer::sum);
        float totalRangeLength = maxPossibleNoiseThreshold - -1f;
        float currNoise = -1f;

        BetterCaves.LOGGER.debug("CAVE INFORMATION");
        BetterCaves.LOGGER.debug("--> MAX POSSIBLE THRESHOLD: " + maxPossibleNoiseThreshold);
        BetterCaves.LOGGER.debug("--> TOTAL PRIORITY: " + totalPriority);
        BetterCaves.LOGGER.debug("--> TOTAL RANGE LENGTH: " + totalRangeLength);

        for (ICarver carver : carvers) {
            BetterCaves.LOGGER.debug("--> CARVER");
            float noiseRangeLength = (float)carver.getPriority() / totalPriority * totalRangeLength;
            float rangeTop = currNoise + noiseRangeLength;
            CarverNoiseRange range = new CarverNoiseRange(currNoise, rangeTop, carver);
            currNoise = rangeTop;
            noiseRanges.add(range);

            BetterCaves.LOGGER.debug("    --> RANGE FOUND: " + range);
        }
    }

    public void carveChunk(Chunk chunk, int chunkX, int chunkZ, int[][] surfaceAltitudes, BlockState[][] liquidBlocks, Map<Long, Biome> biomeMap, BitSet airCarvingMask, BitSet liquidCarvingMask) {
        // Prevent unnecessary computation if caves are disabled
        if (noiseRanges.size() == 0 && !isSurfaceCavesEnabled) {
            return;
        }

        ColPos.Mutable mutablePos = new ColPos.Mutable();
        boolean flooded;

        // Flag to keep track of whether or not we've already carved vanilla caves for this chunk, since
        // vanilla caves operate on a chunk-by-chunk basis rather than by column
        boolean shouldCarveVanillaCaves = false;

        // Since vanilla caves carve by chunk and not by column, we store an array
        // indicating which x-z coordinates are valid to be carved in
        boolean[][] validPositions = new boolean[16][16];

        // Break into subchunks for noise interpolation
        for (int subX = 0; subX < 16 / BCSettings.SUB_CHUNK_SIZE; subX++) {
            for (int subZ = 0; subZ < 16 / BCSettings.SUB_CHUNK_SIZE; subZ++) {
                int startX = subX * BCSettings.SUB_CHUNK_SIZE;
                int startZ = subZ * BCSettings.SUB_CHUNK_SIZE;
                int endX = startX + BCSettings.SUB_CHUNK_SIZE - 1;
                int endZ = startZ + BCSettings.SUB_CHUNK_SIZE - 1;
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
                        maxHeight = Math.max(maxHeight, range.getCarver().getTopY());
                    }
                }

                // Offset within subchunk
                for (int offsetX = 0; offsetX < BCSettings.SUB_CHUNK_SIZE; offsetX++) {
                    for (int offsetZ = 0; offsetZ < BCSettings.SUB_CHUNK_SIZE; offsetZ++) {
                        int localX = startX + offsetX;
                        int localZ = startZ + offsetZ;
                        ColPos colPos = new ColPos(chunkX * 16 + localX, chunkZ * 16 + localZ);

                        flooded = isFloodedUndergroundEnabled && !isDebugViewEnabled && biomeMap.get(colPos.toLong()).getCategory() == Biome.Category.OCEAN;
                        if (flooded) {
                            if (
                                (isPosInWorld(mutablePos.set(colPos).move(Direction.EAST), world) && biomeMap.get(mutablePos.set(colPos).move(Direction.EAST).toLong()).getCategory() != Biome.Category.OCEAN) ||
                                (isPosInWorld(mutablePos.set(colPos).move(Direction.WEST), world) && biomeMap.get(mutablePos.set(colPos).move(Direction.WEST).toLong()).getCategory() != Biome.Category.OCEAN) ||
                                (isPosInWorld(mutablePos.set(colPos).move(Direction.NORTH), world) && biomeMap.get(mutablePos.set(colPos).move(Direction.NORTH).toLong()).getCategory() != Biome.Category.OCEAN) ||
                                (isPosInWorld(mutablePos.set(colPos).move(Direction.SOUTH), world) && biomeMap.get(mutablePos.set(colPos).move(Direction.SOUTH).toLong()).getCategory() != Biome.Category.OCEAN)
                            ) {
                                continue;
                            }
                        }

                        int surfaceAltitude = surfaceAltitudes[localX][localZ];
                        BlockState liquidBlock = liquidBlocks[localX][localZ];

                        // Get noise values used to determine cave region
                        float caveRegionNoise = caveRegionController.GetNoise(colPos.getX(), colPos.getZ());

                        // Carve cave using matching carver
                        for (CarverNoiseRange range : noiseRanges) {
                            if (!range.contains(caveRegionNoise)) {
                                continue;
                            }
                            if (range.getCarver() instanceof CaveCarver) {
                                CaveCarver carver = (CaveCarver) range.getCarver();
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
                                NoiseColumn noiseColumn = range.getNoiseCube().get(offsetX).get(offsetZ);
                                carver.carveColumn(chunk, colPos, topY, noiseColumn, liquidBlock, flooded, flooded ? liquidCarvingMask : airCarvingMask);
                                break;
                            }
                            else if (range.getCarver() instanceof VanillaCaveCarver) {
                                validPositions[localX][localZ] = true;
                                shouldCarveVanillaCaves = true;
                            }
                        }
                    }
                }
            }
        }
        if (shouldCarveVanillaCaves) {
            VanillaCaveCarver carver = null;
            for (CarverNoiseRange range : noiseRanges) {
                if (range.getCarver() instanceof VanillaCaveCarver) {
                    carver = (VanillaCaveCarver) range.getCarver();
                    break;
                }
            }
            if (carver != null) {
                carver.generate(world, chunkX, chunkZ, chunk, true, liquidBlocks, biomeMap, validPositions, airCarvingMask, liquidCarvingMask);
            }
        }
        // Generate surface caves if enabled
        if (isSurfaceCavesEnabled) {
            surfaceCaveCarver.generate(world, chunkX, chunkZ, chunk, false, liquidBlocks, biomeMap, airCarvingMask, liquidCarvingMask);
        }
    }

    /**
     * @return frequency value for cave region controller
     */
    private float calcCaveRegionSize(String caveRegionSize, float caveRegionCustomSize) {
        switch (caveRegionSize.toLowerCase()) {
            case "small":
                return .008f;
            case "large":
                return .0032f;
            case "extralarge":
                return .001f;
            case "custom":
                return caveRegionCustomSize;
            case "medium":
            default:
                return .005f;
        }
    }

    public void setWorld(StructureWorldAccess worldIn) {
        this.world = worldIn;
        this.surfaceCaveCarver.setWorld(worldIn);
        for (CarverNoiseRange range : noiseRanges) {
            if (range.getCarver() instanceof VanillaCaveCarver) {
                ((VanillaCaveCarver)range.getCarver()).setWorld(worldIn);
                break;
            }
        }
    }
}
