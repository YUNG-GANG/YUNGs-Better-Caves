package com.yungnickyoung.minecraft.bettercaves.worldgen.cave;

import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.enums.CaveType;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseGen;
import com.yungnickyoung.minecraft.bettercaves.worldgen.CarverSettings;
import com.yungnickyoung.minecraft.bettercaves.worldgen.CarverUtils;
import com.yungnickyoung.minecraft.bettercaves.worldgen.ICarver;
import com.yungnickyoung.minecraft.yungsapi.noise.FastNoise;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CaveCarver implements ICarver {
    private CarverSettings settings;
    private NoiseGen noiseGen;
    private int surfaceCutoff;
    private int bottomY;
    private int topY;

    /**
     * Set true to perform pre-processing on noise values, adjusting them to increase ...
     * ... headroom in the y direction.
     */
    private final boolean enableYAdjust;

    /** Adjustment value for the block immediately above. Must be between 0 and 1.0 */
    private final float yAdjustF1;

    /** Adjustment value for the block two blocks above. Must be between 0 and 1.0 */
    private final float yAdjustF2;

    public CaveCarver(final Builder builder) {
        settings = builder.getSettings();
        noiseGen = new NoiseGen(
                settings.getSeed(),
                settings.isFastNoise(),
                settings.getNoiseSettings(),
                settings.getNumGens(),
                settings.getyCompression(),
                settings.getXzCompression()
        );
        surfaceCutoff = builder.getSurfaceCutoff();
        enableYAdjust = builder.isEnableYAdjust();
        yAdjustF1 = builder.getyAdjustF1();
        yAdjustF2 = builder.getyAdjustF2();
        if (builder.getBottomY() > builder.getTopY()) {
            BetterCavesCommon.LOGGER.warn("Warning: Min altitude for caves should not be greater than max altitude.");
            BetterCavesCommon.LOGGER.warn("Using default values...");
            this.bottomY = 1;
            this.topY = 80;
        } else {
            bottomY = builder.getBottomY();
            topY = builder.getTopY();
        }
    }

    public void carveColumn(CarverConfiguration config, ChunkAccess chunk, BlockPos colPos, int topY, double[][] noises,
                            BlockState liquidBlock, CarvingMask carvingMask, Aquifer aquifer) {
        int localX = colPos.getX() & 0xF;
        int localZ = colPos.getZ() & 0xF;

        // Validate vars
//        if (bottomY < 0) bottomY = 0;
//        if (bottomY > 255) bottomY = 255;
//        if (topY < 0) topY = 0;
//        if (topY > 255) topY = 255;

        // Altitude at which caves start closing off so they aren't all open to the surface
        int transitionBoundary = topY - surfaceCutoff;

        // Validate transition boundary
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
                CarverUtils.debugCarveBlock(chunk, localPos, settings.getDebugBlock(), digBlock);
            } else if (digBlock) {
//                if (flooded) {
//                    CarverUtils.carveFloodedBlock(config, chunk, new Random(), localPos, liquidBlock, settings.getLiquidAltitude(), carvingMask);
//                } else {
                    CarverUtils.carveBlock(config, chunk, realPos, liquidBlock, settings.getLiquidAltitude(), carvingMask, aquifer);
//                }
            }
        }
    }

    /**
     * Preprocessing performed on a column of noise to adjust its values before comparing them to the threshold.
     * This function adjusts the noise value of blocks based on the noise values of blocks below.
     * This has the effect of raising the ceilings of caves, giving the player more headroom.
     * Big shoutouts to the guys behind Worley's Caves for this great idea.
     * @param noises The column of noises as a map, mapping the y-coordinate of a block to its NoiseTuple
     * @param topY Top y-coordinate of the noise column
     * @param bottomY Bottom y-coordinate of the noise column
     * @param thresholds Map of y-coordinates to noise thresholds. This is the output of the generateThresholds method.
     * @param numGens Number of noise values to create per block. This is equal to the number of floats held
     *                in each NoiseTuple for each block in the noise column.
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
     * @param topY Top y-coordinate of the column
     * @param bottomY Bottom y-coordinate of the column
     * @param transitionBoundary The y-coordinate at which the caves start to close off
     * @return Map of y-coordinates to noise thresholds
     */
    private Map<Integer, Float> generateThresholds(int topY, int bottomY, int transitionBoundary) {
        Map<Integer, Float> thresholds = new HashMap<>();
        for (int realY = bottomY; realY <= topY; realY++) {
            float noiseThreshold = settings.getNoiseThreshold();
            if (realY >= transitionBoundary)
                noiseThreshold *= (1 + .3f * ((float)(realY - transitionBoundary) / (topY - transitionBoundary)));
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

    public int getPriority() {
        return settings.getPriority();
    }

    public int getBottomY() {
        return this.bottomY;
    }

    public int getTopY() {
        return this.topY;
    }

    /**
     * Builder class for CaveCarver.
     * Fields may be built individually or loaded in bulk via the {@code ofTypeFromCarver} method
     */
    public static class Builder {
        private CarverSettings settings;
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

        /**
         * Helps build a CaveCarver from a ConfigHolder based on its CaveType
         * @param caveType the CaveType of this CaveCarver
         */
        public Builder ofTypeFromConfig(CaveType caveType) {
            this.settings.setLiquidAltitude(BetterCavesCommon.CONFIG.undergroundGen.misc.liquidAltitude);
            this.settings.setReplaceFloatingGravel(BetterCavesCommon.CONFIG.undergroundGen.misc.replaceFloatingGravel);
//            this.settings.setEnableDebugVisualizer(config.debugVisualizer.get());
            this.settings.setEnableDebugVisualizer(false);
            this.settings.getNoiseSettings().setFractalType(FastNoise.FractalType.RigidMulti);
            switch (caveType) {
                case CUBIC:
                    this.settings.setFastNoise(true);
                    this.settings.setNoiseThreshold((float) BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.advancedSettings.noiseThreshold);
                    this.settings.getNoiseSettings().setNoiseType(FastNoise.NoiseType.valueOf(BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.advancedSettings.noiseType));
                    this.settings.getNoiseSettings().setOctaves(BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.advancedSettings.fractalOctaves);
                    this.settings.getNoiseSettings().setGain((float) BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.advancedSettings.fractalGain);
                    this.settings.getNoiseSettings().setFrequency((float) BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.advancedSettings.fractalFrequency);
                    this.settings.setNumGens(BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.advancedSettings.numGenerators);
                    this.settings.setXzCompression((float) BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.xzCompression);
                    this.settings.setyCompression((float) BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.yCompression);
                    this.settings.setPriority(BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.cavePriority);
                    this.surfaceCutoff = BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.caveSurfaceCutoff;
                    this.bottomY = BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.caveBottom;
                    this.topY = BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.caveTop;
                    this.enableYAdjust = BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.advancedSettings.yAdjust;
                    this.yAdjustF1 = (float) BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.advancedSettings.yAdjustF1;
                    this.yAdjustF2 = (float) BetterCavesCommon.CONFIG.undergroundGen.caves.cubicCaves.advancedSettings.yAdjustF2;
                    break;
                case SIMPLEX:
                    this.settings.setFastNoise(false);
                    this.settings.setNoiseThreshold((float) BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.advancedSettings.noiseThreshold);
                    this.settings.getNoiseSettings().setNoiseType(FastNoise.NoiseType.valueOf(BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.advancedSettings.noiseType));
                    this.settings.getNoiseSettings().setOctaves(BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.advancedSettings.fractalOctaves);
                    this.settings.getNoiseSettings().setGain((float) BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.advancedSettings.fractalGain);
                    this.settings.getNoiseSettings().setFrequency((float) BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.advancedSettings.fractalFrequency);
                    this.settings.setNumGens(BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.advancedSettings.numGenerators);
                    this.settings.setXzCompression((float) BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.xzCompression);
                    this.settings.setyCompression((float) BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.yCompression);
                    this.settings.setPriority(BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.cavePriority);
                    this.surfaceCutoff = BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.caveSurfaceCutoff;
                    this.bottomY = BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.caveBottom;
                    this.topY = BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.caveTop;
                    this.enableYAdjust = BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.advancedSettings.yAdjust;
                    this.yAdjustF1 = (float) BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.advancedSettings.yAdjustF1;
                    this.yAdjustF2 = (float) BetterCavesCommon.CONFIG.undergroundGen.caves.simplexCaves.advancedSettings.yAdjustF2;
                    break;
            }
            return this;
        }

        /* ================================== Builder Setters ================================== */
        /**
         * @param noiseType The type of noise this carver will use
         */
        public Builder noiseType(FastNoise.NoiseType noiseType) {
            settings.getNoiseSettings().setNoiseType(noiseType);
            return this;
        }

        /**
         * @param fractalOctaves Number of fractal octaves to use in ridged multifractal noise generation
         */
        public Builder fractalOctaves(int fractalOctaves) {
            settings.getNoiseSettings().setOctaves(fractalOctaves);
            return this;
        }

        /**
         * @param fractalGain Amount of gain to use in ridged multifractal noise generation
         */
        public Builder fractalGain(float fractalGain) {
            settings.getNoiseSettings().setGain(fractalGain);
            return this;
        }

        /**
         * @param fractalFreq Frequency to use in ridged multifractal noise generation
         */
        public Builder fractalFrequency(float fractalFreq) {
            settings.getNoiseSettings().setFrequency(fractalFreq);
            return this;
        }

        /**
         * @param numGens Number of noise values to calculate for a given block
         */
        public Builder numberOfGenerators(int numGens) {
            settings.setNumGens(numGens);
            return this;
        }

        /**
         * @param yCompression Vertical cave gen compression. Use 1.0 for default generation
         */
        public Builder verticalCompression(float yCompression) {
            settings.setyCompression(yCompression);
            return this;
        }

        /**
         * @param xzCompression Horizontal cave gen compression. Use 1.0 for default generation
         */
        public Builder horizontalCompression(float xzCompression) {
            settings.setXzCompression(xzCompression);
            return this;
        }

        /**
         * @param surfaceCutoff Cave surface cutoff depth
         */
        public Builder surfaceCutoff(int surfaceCutoff) {
            this.surfaceCutoff = surfaceCutoff;
            return this;
        }

        /**
         * @param bottomY Cave bottom y-coordinate
         */
        public Builder bottomY(int bottomY) {
            this.bottomY = bottomY;
            return this;
        }

        /**
         * @param topY Cave top y-coordinate
         */
        public Builder topY(int topY) {
            this.topY = topY;
            return this;
        }

        /**
         * @param yAdjustF1 Adjustment value for the block immediately above. Must be between 0 and 1.0
         */
        public Builder verticalAdjuster1(float yAdjustF1) {
            this.yAdjustF1 = yAdjustF1;
            return this;
        }

        /**
         * @param yAdjustF2 Adjustment value for the block two blocks above. Must be between 0 and 1.0
         */
        public Builder verticalAdjuster2(float yAdjustF2) {
            this.yAdjustF2 = yAdjustF2;
            return this;
        }

        /**
         * @param enableYAdjust Whether or not to adjust/increase the height of caves.
         */
        public Builder enableVerticalAdjustment(boolean enableYAdjust) {
            this.enableYAdjust = enableYAdjust;
            return this;
        }

        /**
         * @param noiseThreshold Noise threshold to determine whether or not a given block will be dug out
         */
        public Builder noiseThreshold(float noiseThreshold) {
            settings.setNoiseThreshold(noiseThreshold);
            return this;
        }

        /**
         * @param vBlock Block used for this cave type in the debug visualizer
         */
        public Builder debugVisualizerBlock(BlockState vBlock) {
            settings.setDebugBlock(vBlock);
            return this;
        }

        /**
         * @param liquidAltitude altitude at and below which air is replaced with liquid
         */
        public Builder liquidAltitude(int liquidAltitude) {
            settings.setLiquidAltitude(liquidAltitude);
            return this;
        }

        /**
         * Enable the debug visualizer
         */
        public Builder enableDebugVisualizer(boolean enableDebugVisualizer) {
            settings.setEnableDebugVisualizer(enableDebugVisualizer);
            return this;
        }

        /* ================================== Builder Getters ================================== */

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
