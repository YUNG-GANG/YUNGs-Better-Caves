package com.yungnickyoung.minecraft.bettercaves.worldgen.cavern;


import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.enums.CavernType;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseGen;
import com.yungnickyoung.minecraft.bettercaves.worldgen.CarverSettings;
import com.yungnickyoung.minecraft.bettercaves.worldgen.CarverUtils;
import com.yungnickyoung.minecraft.bettercaves.worldgen.ICarver;
import com.yungnickyoung.minecraft.yungsapi.noise.FastNoise;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;

import java.util.BitSet;
import java.util.Random;

/**
 * BetterCaves Cavern carver.
 * Caverns are large openings generated at the bottom of the world.
 */
public class CavernCarver implements ICarver {
    private CarverSettings settings;
    private NoiseGen noiseGen;
    private CavernType cavernType;
    private int bottomY;
    private int topY;

    public CavernCarver(final Builder builder) {
        settings = builder.getSettings();
        noiseGen = new NoiseGen(
                settings.getSeed(),
                settings.isFastNoise(),
                settings.getNoiseSettings(),
                settings.getNumGens(),
                settings.getyCompression(),
                settings.getXzCompression()
        );
        cavernType = builder.getCavernType();
        bottomY = builder.getBottomY();
        topY = builder.getTopY();
        if (bottomY > topY) {
            BetterCavesCommon.LOGGER.warn("Warning: Min altitude for caverns should not be greater than max altitude.");
            BetterCavesCommon.LOGGER.warn("Using default values...");
            this.bottomY = 1;
            this.topY = 35;
        }
    }

    public void carveColumn(CarverConfiguration config, ChunkAccess chunk, BlockPos colPos, int topY, float smoothAmp,
                            double[][] noises, BlockState liquidBlock, boolean flooded, CarvingMask carvingMask) {
        int localX = colPos.getX() & 0xF;
        int localZ = colPos.getZ() & 0xF;

        // Validate vars
//        if (bottomY < 0) bottomY = 0;
//        if (bottomY > 255) bottomY = 255;
//        if (topY < 2) topY = 2;
//        if (topY > 255) topY = 255;

        // Set altitude at which caverns start closing off on the top
        topY -= 2;
        int topTransitionBoundary = topY - 6;

        // Set altitude at which caverns start closing off on the bottom
        int bottomTransitionBoundary = bottomY + 3;
        if (cavernType == CavernType.FLOORED) { // Close off floored caverns more to create "floors"
            bottomTransitionBoundary = bottomY < settings.getLiquidAltitude() ? settings.getLiquidAltitude() + 8 : bottomY + 7;
        }

        // Validate transition boundaries
        topTransitionBoundary = Math.max(topTransitionBoundary, 1);
        bottomTransitionBoundary = Math.min(bottomTransitionBoundary, 255);

        BlockPos.MutableBlockPos localPos = new BlockPos.MutableBlockPos(localX, 1, localZ);

        /* =============== Dig out caves and caverns in this chunk, based on noise values =============== */
        for (int y = topY; y >= bottomY; y--) {
            if (y <= settings.getLiquidAltitude() && liquidBlock == null)
                break;

            boolean digBlock = false;

            // Compute a single noise value to represent all the noise values in the NoiseTuple
            float noise = 1;
            double[] noiseBlock = noises[y - bottomY];
            for (double n : noiseBlock)
                noise *= (float) n;

            // Adjust threshold if we're in the transition range to provide smoother transition into ceiling
            float noiseThreshold = settings.getNoiseThreshold();
            if (y >= topTransitionBoundary)
                noiseThreshold *= (float) (y - topY) / (topTransitionBoundary - topY);

            // Close off caverns at the bottom to hide bedrock and give some walkable area
            if (y < bottomTransitionBoundary)
                noiseThreshold *= (float) (y - bottomY) / (bottomTransitionBoundary - bottomY);

            // Adjust threshold along region borders to create smooth transition
            if (smoothAmp < 1)
                noiseThreshold *= smoothAmp;

            // Mark block for removal if the noise passes the threshold check
            if (noise < noiseThreshold)
                digBlock = true;

            localPos.set(localX, y, localZ);

            // Dig out the block if it passed the threshold check, using the debug visualizer if enabled
            if (settings.isEnableDebugVisualizer()) {
                CarverUtils.debugCarveBlock(chunk, localPos, settings.getDebugBlock(), digBlock);
            } else if (digBlock) {
                if (flooded) {
                    CarverUtils.carveFloodedBlock(config, chunk, new Random(), localPos, liquidBlock, settings.getLiquidAltitude(), settings.isReplaceFloatingGravel(), carvingMask);
                } else {
                    CarverUtils.carveBlock(config, chunk, localPos, liquidBlock, settings.getLiquidAltitude(), settings.isReplaceFloatingGravel(), carvingMask);
                }
            }
        }
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
        return bottomY;
    }

    public int getTopY() {
        return topY;
    }

    /**
     * Builder class for CavernCarver.
     * Fields may be built individually or loaded in bulk via the {@code ofTypeFromCarver} method
     */
    public static class Builder {
        private CarverSettings settings;
        private CavernType cavernType;
        private int bottomY;
        private int topY;

        public Builder(long seed) {
            settings = new CarverSettings(seed);
        }

        public CavernCarver build() {
            return new CavernCarver(this);
        }

        /**
         * Helps build a CavernCarver from a ConfigHolder based on its CavernType
         * @param cavernType the CavernType of this CavernCarver
         */
        public Builder ofTypeFromConfig(CavernType cavernType) {
            this.settings.setLiquidAltitude(BetterCavesCommon.CONFIG.undergroundGen.misc.liquidAltitude);
            this.settings.setReplaceFloatingGravel(BetterCavesCommon.CONFIG.undergroundGen.misc.replaceFloatingGravel);
            this.settings.getNoiseSettings().setFractalType(FastNoise.FractalType.RigidMulti);
//            this.settings.setEnableDebugVisualizer(config.debugVisualizer.get());
            this.settings.setEnableDebugVisualizer(false);
            this.settings.setFastNoise(true);
            this.cavernType = cavernType;
            switch (cavernType) {
                case LIQUID:
                    this.settings.setNoiseThreshold((float) BetterCavesCommon.CONFIG.undergroundGen.caverns.liquidCaverns.advancedSettings.noiseThreshold);
                    this.settings.getNoiseSettings().setNoiseType(FastNoise.NoiseType.valueOf(BetterCavesCommon.CONFIG.undergroundGen.caverns.liquidCaverns.advancedSettings.noiseType));
                    this.settings.getNoiseSettings().setOctaves(BetterCavesCommon.CONFIG.undergroundGen.caverns.liquidCaverns.advancedSettings.fractalOctaves);
                    this.settings.getNoiseSettings().setGain((float) BetterCavesCommon.CONFIG.undergroundGen.caverns.liquidCaverns.advancedSettings.fractalGain);
                    this.settings.getNoiseSettings().setFrequency((float) BetterCavesCommon.CONFIG.undergroundGen.caverns.liquidCaverns.advancedSettings.fractalFrequency);
                    this.settings.setNumGens(BetterCavesCommon.CONFIG.undergroundGen.caverns.liquidCaverns.advancedSettings.numGenerators);
                    this.settings.setyCompression((float) BetterCavesCommon.CONFIG.undergroundGen.caverns.liquidCaverns.yCompression);
                    this.settings.setXzCompression((float) BetterCavesCommon.CONFIG.undergroundGen.caverns.liquidCaverns.xzCompression);
                    this.settings.setPriority(BetterCavesCommon.CONFIG.undergroundGen.caverns.liquidCaverns.cavePriority);
                    this.bottomY = BetterCavesCommon.CONFIG.undergroundGen.caverns.liquidCaverns.cavernBottom;
                    this.topY = BetterCavesCommon.CONFIG.undergroundGen.caverns.liquidCaverns.cavernTop;
                    break;
                case FLOORED:
                    this.settings.setNoiseThreshold((float) BetterCavesCommon.CONFIG.undergroundGen.caverns.flooredCaverns.advancedSettings.noiseThreshold);
                    this.settings.getNoiseSettings().setNoiseType(FastNoise.NoiseType.valueOf(BetterCavesCommon.CONFIG.undergroundGen.caverns.flooredCaverns.advancedSettings.noiseType));
                    this.settings.getNoiseSettings().setOctaves(BetterCavesCommon.CONFIG.undergroundGen.caverns.flooredCaverns.advancedSettings.fractalOctaves);
                    this.settings.getNoiseSettings().setGain((float) BetterCavesCommon.CONFIG.undergroundGen.caverns.flooredCaverns.advancedSettings.fractalGain);
                    this.settings.getNoiseSettings().setFrequency((float) BetterCavesCommon.CONFIG.undergroundGen.caverns.flooredCaverns.advancedSettings.fractalFrequency);
                    this.settings.setNumGens(BetterCavesCommon.CONFIG.undergroundGen.caverns.flooredCaverns.advancedSettings.numGenerators);
                    this.settings.setyCompression((float) BetterCavesCommon.CONFIG.undergroundGen.caverns.flooredCaverns.yCompression);
                    this.settings.setXzCompression((float) BetterCavesCommon.CONFIG.undergroundGen.caverns.flooredCaverns.xzCompression);
                    this.settings.setPriority(BetterCavesCommon.CONFIG.undergroundGen.caverns.flooredCaverns.cavePriority);
                    this.bottomY = BetterCavesCommon.CONFIG.undergroundGen.caverns.flooredCaverns.cavernBottom;
                    this.topY = BetterCavesCommon.CONFIG.undergroundGen.caverns.flooredCaverns.cavernTop;
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

        /**
         * Set cavern type
         */
        public Builder cavernType(CavernType cavernType) {
            this.cavernType = cavernType;
            return this;
        }

        /**
         * Set cavern bottom Y coordinate
         */
        public Builder bottomY(int bottomY) {
            this.bottomY = bottomY;
            return  this;
        }

        /**
         * Set cavern top Y coordinate
         */
        public Builder topY(int topY) {
            this.topY = topY;
            return this;
        }

        /* ================================== Builder Getters ================================== */

        public CarverSettings getSettings() {
            return settings;
        }

        public CavernType getCavernType() {
            return cavernType;
        }

        public int getBottomY() {
            return bottomY;
        }

        public int getTopY() {
            return topY;
        }
    }
}
