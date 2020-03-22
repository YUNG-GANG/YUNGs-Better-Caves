package com.yungnickyoung.minecraft.bettercaves.world.carver.cave;

import com.yungnickyoung.minecraft.bettercaves.config.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.enums.CaveType;
import com.yungnickyoung.minecraft.bettercaves.enums.RegionSize;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseColumn;
import com.yungnickyoung.minecraft.bettercaves.world.carver.CarverNoiseRange;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;

import java.util.ArrayList;
import java.util.List;

public class CaveCarverController {
    private World world;
    private MapGenBase defaultCaveGen;
    private FastNoise caveRegionController;
    private List<CarverNoiseRange> noiseRanges = new ArrayList<>();

    // vars from config
    private boolean isVanillaCavesEnabled;
    private boolean isDebugViewEnabled;

    public CaveCarverController(World worldIn, ConfigHolder config, MapGenBase defaultCaveGen) {
        this.world = worldIn;
        this.defaultCaveGen = defaultCaveGen;
        this.isVanillaCavesEnabled = config.enableVanillaCaves.get();
        this.isDebugViewEnabled = config.debugVisualizer.get();

        // Configure cave region controller, which determines what type of cave should be
        // carved in any given region
        float caveRegionSize = calcCaveRegionSize(config.caveRegionSize.get(), config.caveRegionCustomSize.get());
        this.caveRegionController = new FastNoise();
        this.caveRegionController.SetSeed((int)worldIn.getSeed() + 222);
        this.caveRegionController.SetFrequency(caveRegionSize);
        this.caveRegionController.SetNoiseType(FastNoise.NoiseType.Cellular);
        this.caveRegionController.SetCellularDistanceFunction(FastNoise.CellularDistanceFunction.Natural);

        // Initialize all carvers using config options
        List<CaveCarver> carvers = new ArrayList<>();
        carvers.add(new CaveCarverBuilder(worldIn)
                .ofTypeFromConfig(CaveType.CUBIC, config)
                .debugVisualizerBlock(Blocks.PLANKS.getDefaultState())
                .build()
        );
        carvers.add(new CaveCarverBuilder(worldIn)
                .ofTypeFromConfig(CaveType.SIMPLEX, config)
                .debugVisualizerBlock(Blocks.COBBLESTONE.getDefaultState())
                .build()
        );

        float maxPossibleNoiseThreshold = config.caveSpawnChance.get() * .01f * 2 - 1;
        int totalPriority = carvers.stream().map(CaveCarver::getPriority).reduce(0, Integer::sum);
        float totalRangeLength = maxPossibleNoiseThreshold - -1f;
        float currNoise = -1f;
        carvers.removeIf(carver -> carver.getPriority() == 0);

        Settings.LOGGER.info("CAVE INFORMATION");
        Settings.LOGGER.info("--> MAX POSSIBLE THRESHOLD: " + maxPossibleNoiseThreshold);
        Settings.LOGGER.info("--> TOTAL PRIORITY: " + totalPriority);
        Settings.LOGGER.info("--> TOTAL RANGE LENGTH: " + totalRangeLength);

        for (CaveCarver carver : carvers) {
            Settings.LOGGER.info("--> CARVER");
            float noiseRangeLength = (float)carver.getPriority() / totalPriority * totalRangeLength;
            float rangeTop = currNoise + noiseRangeLength;
            CarverNoiseRange range = new CarverNoiseRange(currNoise, rangeTop, carver);
            currNoise = rangeTop;
            noiseRanges.add(range);

            Settings.LOGGER.info("    --> RANGE FOUND: " + range);
        }
    }

    public void carveChunk(ChunkPrimer primer, int chunkX, int chunkZ, int[][] surfaceAltitudes, IBlockState[][] liquidBlocks) {
        // Prevent unnecessary computation if caves are disabled
        if (noiseRanges.size() == 0) {
            if (isVanillaCavesEnabled) {
                defaultCaveGen.generate(world, chunkX, chunkZ, primer);
            }
            return;
        }

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
                for (int x = startX; x < endX; x++) {
                    for (int z = startZ; z < endZ; z++) {
                        maxHeight = Math.max(maxHeight, surfaceAltitudes[x][z]);
                    }
                }
                for (CarverNoiseRange range : noiseRanges) {
                    CaveCarver carver = (CaveCarver)range.getCarver();
                    maxHeight = Math.max(maxHeight, carver.getTopY());
                }

                for (int offsetX = 0; offsetX < Settings.SUB_CHUNK_SIZE; offsetX++) {
                    for (int offsetZ = 0; offsetZ < Settings.SUB_CHUNK_SIZE; offsetZ++) {
                        int localX = startX + offsetX;
                        int localZ = startZ + offsetZ;
                        BlockPos colPos = new BlockPos(chunkX * 16 + localX, 1, chunkZ * 16 + localZ);

                        int surfaceAltitude = surfaceAltitudes[localX][localZ];
                        IBlockState liquidBlock = liquidBlocks[localX][localZ];

                        // Get noise values used to determine cave region
                        float caveRegionNoise = caveRegionController.GetNoise(colPos.getX(), colPos.getZ());
                        boolean carved = false;

                        // Carve cave using matching carver
                        for (CarverNoiseRange range : noiseRanges) {
                            if (!range.contains(caveRegionNoise)) {
                                continue;
                            }
                            CaveCarver carver = (CaveCarver)range.getCarver();
                            int bottomY = carver.getBottomY();
                            int topY = isDebugViewEnabled ? 128 : Math.min(surfaceAltitude, carver.getTopY());
                            if (range.getNoiseCube() == null) {
                                range.setNoiseCube(carver.getNoiseGen().interpolateNoiseCube(startPos, endPos, bottomY, maxHeight));
                            }
                            NoiseColumn noiseColumn = range.getNoiseCube().get(offsetX).get(offsetZ);
                            carver.carveColumn(primer, colPos, topY, noiseColumn, liquidBlock);
                            carved = true;
                            break;
                        }

                        if (!carved) {
                            if (isVanillaCavesEnabled) {
                                defaultCaveGen.generate(world, chunkX, chunkZ, primer);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @return frequency value for cave region controller
     */
    private float calcCaveRegionSize(RegionSize caveRegionSize, float caveRegionCustomSize) {
        switch (caveRegionSize) {
            case Small:
                return .008f;
            case Large:
                return .0032f;
            case ExtraLarge:
                return .001f;
            case Custom:
                return caveRegionCustomSize;
            default: // Medium
                return .005f;
        }
    }
}
