package com.yungnickyoung.minecraft.bettercaves.worldgen.carver;


import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.enums.CavernType;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseGen;
import com.yungnickyoung.minecraft.bettercaves.worldgen.BetterCavesWorldCarverConfig;
import com.yungnickyoung.minecraft.yungsapi.noise.FastNoise;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;

/**
 * BetterCaves Cavern carver.
 * Caverns are large openings generated at the bottom of the world.
 */
public class CavernCarver extends AbstractCarver {
    private final NoiseGen noiseGen;
    private final CavernType cavernType;
    private int bottomY;
    private int topY;

    public CavernCarver(final Builder builder) {
        super(builder.getSettings());
        this.noiseGen = new NoiseGen(
                this.settings.getSeed(),
                this.settings.isFastNoise(),
                this.settings.getNoiseSettings(),
                this.settings.getNumGens(),
                this.settings.getyCompression(),
                this.settings.getXzCompression()
        );
        this.cavernType = builder.getCavernType();
        if (bottomY > topY) {
            BetterCavesCommon.LOGGER.warn("Warning: Min altitude for caverns should not be greater than max altitude.");
            BetterCavesCommon.LOGGER.warn("Using default values...");
            // TODO change how this validation works?
            this.bottomY = -63;
            this.topY = -28;
        } else {
            this.bottomY = builder.getBottomY();
            this.topY = builder.getTopY();
        }
    }

    public void carveColumn(BetterCavesWorldCarverConfig config, ChunkAccess chunk, BlockPos colPos, int topY, float smoothAmp,
                            double[][] noises, BlockState liquidBlock, CarvingMask carvingMask,
                            Aquifer aquifer) {
        int localX = colPos.getX() & 0xF;
        int localZ = colPos.getZ() & 0xF;

        // TODO - Validate topY and bottomY

        // Set altitude at which caverns start closing off on the top
        topY -= 2;
        int topTransitionBoundary = topY - 6;

        // Set altitude at which caverns start closing off on the bottom
        int bottomTransitionBoundary = bottomY + 3;
        if (cavernType == CavernType.FLOORED) { // Close off floored caverns more to create "floors"
            bottomTransitionBoundary = bottomY < settings.getLiquidAltitude() ? settings.getLiquidAltitude() + 8 : bottomY + 7;
        }

        // TODO - Validate transition boundaries?
//        topTransitionBoundary = Math.max(topTransitionBoundary, 1);
//        bottomTransitionBoundary = Math.min(bottomTransitionBoundary, 255);

        BlockPos.MutableBlockPos localPos = new BlockPos.MutableBlockPos(localX, 1, localZ);
        BlockPos.MutableBlockPos realPos = new BlockPos.MutableBlockPos(colPos.getX(), 1, colPos.getZ());

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
            realPos.setY(y);

            // Dig out the block if it passed the threshold check, using the debug visualizer if enabled
            if (settings.isEnableDebugVisualizer()) {
                this.debugCarveBlock(chunk, localPos, digBlock);
            } else if (digBlock) {
                this.carveBlock(config, chunk, realPos, liquidBlock, carvingMask, aquifer);
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
        private final CarverSettings settings;
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
         *
         * @param cavernType the CavernType of this CavernCarver
         */
        public Builder ofTypeFromConfig(CavernType cavernType, BetterCavesWorldCarverConfig config) {
            this.settings.setLiquidAltitude(config.liquidRegions.liquidAltitude());
            this.settings.getNoiseSettings().setFractalType(FastNoise.FractalType.RigidMulti);
            this.settings.setEnableDebugVisualizer(config.debugSettings.enabled());
            this.settings.setFastNoise(true);
            this.cavernType = cavernType;
            switch (cavernType) {
                case LIQUID:
                    this.settings.setNoiseThreshold((float) config.caverns.liquidCaverns().advanced().noiseThreshold());
                    this.settings.getNoiseSettings().setNoiseType(FastNoise.NoiseType.valueOf(config.caverns.liquidCaverns().advanced().noiseType()));
                    this.settings.getNoiseSettings().setOctaves(config.caverns.liquidCaverns().advanced().fractalOctaves());
                    this.settings.getNoiseSettings().setGain((float) config.caverns.liquidCaverns().advanced().fractalGain());
                    this.settings.getNoiseSettings().setFrequency((float) config.caverns.liquidCaverns().advanced().fractalFrequency());
                    this.settings.setNumGens(config.caverns.liquidCaverns().advanced().numGenerators());
                    this.settings.setyCompression((float) config.caverns.liquidCaverns().yCompression());
                    this.settings.setXzCompression((float) config.caverns.liquidCaverns().xzCompression());
                    this.settings.setPriority(config.caverns.liquidCaverns().cavePriority());
                    this.bottomY = config.caverns.liquidCaverns().cavernBottom();
                    this.topY = config.caverns.liquidCaverns().cavernTop();
                    break;
                case FLOORED:
                    this.settings.setNoiseThreshold((float) config.caverns.flooredCaverns().advanced().noiseThreshold());
                    this.settings.getNoiseSettings().setNoiseType(FastNoise.NoiseType.valueOf(config.caverns.flooredCaverns().advanced().noiseType()));
                    this.settings.getNoiseSettings().setOctaves(config.caverns.flooredCaverns().advanced().fractalOctaves());
                    this.settings.getNoiseSettings().setGain((float) config.caverns.flooredCaverns().advanced().fractalGain());
                    this.settings.getNoiseSettings().setFrequency((float) config.caverns.flooredCaverns().advanced().fractalFrequency());
                    this.settings.setNumGens(config.caverns.flooredCaverns().advanced().numGenerators());
                    this.settings.setyCompression((float) config.caverns.flooredCaverns().yCompression());
                    this.settings.setXzCompression((float) config.caverns.flooredCaverns().xzCompression());
                    this.settings.setPriority(config.caverns.flooredCaverns().cavePriority());
                    this.bottomY = config.caverns.flooredCaverns().cavernBottom();
                    this.topY = config.caverns.flooredCaverns().cavernTop();
                    break;
            }
            return this;
        }

        /* ================================== Builder Setters ================================== */

        /**
         * @param vBlock Block used for this cave type in the debug visualizer
         */
        public Builder debugVisualizerBlock(BlockState vBlock) {
            settings.setDebugBlock(vBlock);
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
