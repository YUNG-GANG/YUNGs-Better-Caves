package com.yungnickyoung.minecraft.bettercaves.worldgen.carver;

import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseGen;
import com.yungnickyoung.minecraft.bettercaves.worldgen.BetterCavesWorldCarverConfig;
import com.yungnickyoung.minecraft.yungsapi.noise.FastNoise;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaveCarver extends AbstractCarver {
    private final NoiseGen noiseGen;
    private final int surfaceCutoff;
    private final int bottomY;
    private final int topY;

    /**
     * Set true to perform pre-processing on noise values, adjusting them to increase headroom in the y direction.
     */
    private final boolean enableYAdjust;

    /**
     * Adjustment value for the block immediately above. Must be between 0 and 1.0
     */
    private final float yAdjustF1;

    /**
     * Adjustment value for the block two blocks above. Must be between 0 and 1.0
     */
    private final float yAdjustF2;

    public static List<AbstractCarver> createCarversFromConfig(ServerLevel serverLevel,
                                                               BetterCavesWorldCarverConfig config,
                                                               BetterCavesWorldCarverConfig.CaveLayerSettings layerSettings) {
        List<AbstractCarver> carvers = new ArrayList<>();
        layerSettings.carvers().forEach(carverSettings -> {
            carvers.add(new Builder(serverLevel.getSeed())
                    .fromConfig(config, carverSettings)
                    .build());
        });
        return carvers;
    }

    private CaveCarver(final Builder builder) {
        super(builder.getSettings());
        this.noiseGen = new NoiseGen(
                this.settings.getSeed(),
                this.settings.isFastNoise(),
                this.settings.getNoiseSettings(),
                this.settings.getNumGens(),
                this.settings.getyCompression(),
                this.settings.getXzCompression()
        );
        this.surfaceCutoff = builder.getSurfaceCutoff();
        this.enableYAdjust = builder.isEnableYAdjust();
        this.yAdjustF1 = builder.getyAdjustF1();
        this.yAdjustF2 = builder.getyAdjustF2();
        if (builder.getBottomY() > builder.getTopY()) {
            BetterCavesCommon.LOGGER.warn("Warning: Min altitude for caves should not be greater than max altitude.");
            BetterCavesCommon.LOGGER.warn("Using default values...");
            throw new IllegalArgumentException(
                    "bettercaves: Cave Carver's bottomY cannot be greater than topY. Please check your config.");
        } else {
            this.bottomY = builder.getBottomY();
            this.topY = builder.getTopY();
        }
    }

    public void carveColumn(BetterCavesWorldCarverConfig config, ChunkAccess chunk, BlockPos colPos, int topY, double[][] noises,
                            BlockState liquidBlock, CarvingMask carvingMask, Aquifer aquifer) {
        int localX = colPos.getX() & 0xF;
        int localZ = colPos.getZ() & 0xF;

        // TODO - Validate topY and bottomY

        // Altitude at which caves start closing off so they aren't all open to the surface
        int transitionBoundary = topY - surfaceCutoff;

        // TODO = Validate transition boundary?
//        if (transitionBoundary < 1)
//            transitionBoundary = 1;

        // Pre-compute thresholds to ensure accuracy during pre-processing
        Map<Integer, Float> thresholds = generateThresholds(topY, bottomY, transitionBoundary);

        // Do some pre-processing on the noises to facilitate better cave generation.
        // Basically this makes caves taller to give players more headroom.
        // See the javadoc for the function for more info.
        if (this.enableYAdjust) {
            preprocessCaveNoiseCol(noises, topY, bottomY, thresholds, settings.getNumGens());
        }

        BlockPos.MutableBlockPos localPos = new BlockPos.MutableBlockPos(localX, 1, localZ);
        BlockPos.MutableBlockPos realPos = new BlockPos.MutableBlockPos(colPos.getX(), 1, colPos.getZ());

        // Dig out caves in this column, based on noise values
        for (int y = topY; y >= bottomY; y--) {
            if (y <= settings.getLiquidAltitude() && liquidBlock == null) {
                break;
            }

            double[] noiseBlock = noises[y - bottomY];
            boolean digBlock = true;

            for (double noise : noiseBlock) {
                if (noise < thresholds.get(y)) {
                    digBlock = false;
                    break;
                }
            }

            localPos.set(localX, y, localZ);
            realPos.setY(y);

            // Dig out the block if it passed the threshold check, using the debug visualizer if enabled
            if (settings.isEnableDebugVisualizer()) {
                this.debugCarveBlock(chunk, localPos, digBlock);
            } else if (digBlock) {
                this.carveBlock(config, chunk, realPos, liquidBlock, carvingMask, aquifer);
            }
        }
    }

    /**
     * Preprocessing performed on a column of noise to adjust its values before comparing them to the threshold.
     * This function adjusts the noise value of blocks based on the noise values of blocks below.
     * This has the effect of raising the ceilings of caves, giving the player more headroom.
     * Big shoutouts to the guys behind Worley's Caves for this great idea.
     *
     * @param noises     The column of noises as a map, mapping the y-coordinate of a block to its NoiseTuple
     * @param topY       Top y-coordinate of the noise column
     * @param bottomY    Bottom y-coordinate of the noise column
     * @param thresholds Map of y-coordinates to noise thresholds. This is the output of the generateThresholds method.
     * @param numGens    Number of noise values to create per block. This is equal to the number of floats held
     *                   in each NoiseTuple for each block in the noise column.
     */
    private void preprocessCaveNoiseCol(double[][] noises, int topY, int bottomY, Map<Integer, Float> thresholds, int numGens) {
        /* Adjust simplex noise values based on blocks above in order to give the player more headroom */
        for (int y = topY; y >= bottomY; y--) {
            int yIndex = y - bottomY;
            double[] noiseBlock = noises[yIndex];
            float threshold = thresholds.get(y);

            boolean valid = true;
            for (double noise : noiseBlock) {
                if (noise < threshold) {
                    valid = false;
                    break;
                }
            }

            // Adjust noise values of blocks above to give the player more head room
            if (valid) {
                float f1 = yAdjustF1;
                float f2 = yAdjustF2;

                // Adjust block one above
                if (y < topY) {
                    double[] tupleAbove = noises[yIndex + 1];
                    for (int i = 0; i < numGens; i++) {
                        tupleAbove[i] = ((1 - f1) * tupleAbove[i]) + (f1 * noiseBlock[i]);
                    }
                }

                // Adjust block two above
                if (y < topY - 1) {
                    double[] tupleTwoAbove = noises[yIndex + 2];
                    for (int i = 0; i < numGens; i++) {
                        tupleTwoAbove[i] = ((1 - f2) * tupleTwoAbove[i]) + (f2 * noiseBlock[i]);
                    }
                }
            }
        }
    }

    /**
     * Generate a map of y-coordinates to thresholds for a column of blocks.
     * This is useful because the threshold will decrease near the surface, and it is useful (and more accurate)
     * to have a precomputed threshold value when doing y-adjustments for caves.
     *
     * @param topY               Top y-coordinate of the column
     * @param bottomY            Bottom y-coordinate of the column
     * @param transitionBoundary The y-coordinate at which the caves start to close off
     * @return Map of y-coordinates to noise thresholds
     */
    private Map<Integer, Float> generateThresholds(int topY, int bottomY, int transitionBoundary) {
        Map<Integer, Float> thresholds = new HashMap<>();
        for (int realY = bottomY; realY <= topY; realY++) {
            float noiseThreshold = settings.getNoiseThreshold();
            if (realY >= transitionBoundary)
                noiseThreshold *= (1 + .3f * ((float) (realY - transitionBoundary) / (topY - transitionBoundary)));
            thresholds.put(realY, noiseThreshold);
        }

        return thresholds;
    }

    public NoiseGen getNoiseGen() {
        return noiseGen;
    }

    public CarverSettings getSettings() {
        return settings;
    }

    public int getSpawnWeight() {
        return settings.getSpawnWeight();
    }

    public int getBottomY() {
        return this.bottomY;
    }

    public int getTopY() {
        return this.topY;
    }

    public static class Builder {
        private final CarverSettings settings;
        private int surfaceCutoff;
        private int bottomY;
        private int topY;
        private boolean enableYAdjust;
        private float yAdjustF1;
        private float yAdjustF2;

        public Builder(long seed) {
            settings = new CarverSettings(seed);
        }

        public CaveCarver build() {
            return new CaveCarver(this);
        }

        private Builder fromConfig(BetterCavesWorldCarverConfig config, BetterCavesWorldCarverConfig.CaveLayerSettings.CaveSubCarverSettings subCarverSettings) {
            this.settings.setLiquidAltitude(BetterCavesCommon.CONFIG.liquidRegions.liquidAltitude);
            this.settings.setEnableDebugVisualizer(config.debugSettings.enabled());
            this.settings.getNoiseSettings().setFractalType(FastNoise.FractalType.RigidMulti);
            this.settings.setDebugBlock(subCarverSettings.debugCarveState());
            this.settings.setFastNoise(subCarverSettings.advanced().isFastNoise());
            this.settings.setNoiseThreshold((float) subCarverSettings.advanced().noiseThreshold());
            this.settings.getNoiseSettings().setNoiseType(FastNoise.NoiseType.valueOf(subCarverSettings.advanced().noiseType()));
            this.settings.getNoiseSettings().setOctaves(subCarverSettings.advanced().fractalOctaves());
            this.settings.getNoiseSettings().setGain((float) subCarverSettings.advanced().fractalGain());
            this.settings.getNoiseSettings().setFrequency((float) subCarverSettings.advanced().fractalFrequency());
            this.settings.setNumGens(subCarverSettings.advanced().numGenerators());
            this.settings.setXzCompression((float) subCarverSettings.xzCompression());
            this.settings.setyCompression((float) subCarverSettings.yCompression());
            this.settings.setSpawnWeight(subCarverSettings.spawnWeight());
            this.surfaceCutoff = subCarverSettings.surfaceCutoffDistance();
            this.bottomY = subCarverSettings.bottomY();
            this.topY = subCarverSettings.topY();
            this.enableYAdjust = subCarverSettings.advanced().yAdjust();
            this.yAdjustF1 = (float) subCarverSettings.advanced().yAdjustF1();
            this.yAdjustF2 = (float) subCarverSettings.advanced().yAdjustF2();
            return this;
        }

        public CarverSettings getSettings() {
            return settings;
        }

        public int getSurfaceCutoff() {
            return surfaceCutoff;
        }

        public int getBottomY() {
            return bottomY;
        }

        public int getTopY() {
            return topY;
        }

        public boolean isEnableYAdjust() {
            return enableYAdjust;
        }

        public float getyAdjustF1() {
            return yAdjustF1;
        }

        public float getyAdjustF2() {
            return yAdjustF2;
        }
    }
}
