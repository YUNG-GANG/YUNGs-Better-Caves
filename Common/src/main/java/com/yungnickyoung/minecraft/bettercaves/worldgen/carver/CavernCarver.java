package com.yungnickyoung.minecraft.bettercaves.worldgen.carver;


import com.yungnickyoung.minecraft.bettercaves.BetterCavesCommon;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseGen;
import com.yungnickyoung.minecraft.bettercaves.worldgen.BetterCavesWorldCarverConfig;
import com.yungnickyoung.minecraft.yungsapi.math.ColPos;
import com.yungnickyoung.minecraft.yungsapi.noise.FastNoise;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;

import java.util.ArrayList;
import java.util.List;

/**
 * BetterCaves Cavern carver.
 * Caverns are large openings generated at the bottom of the world.
 */
public class CavernCarver extends AbstractCarver {
    private final NoiseGen noiseGen;
    private int bottomY;
    private int topY;
    private final boolean isFloored;

    public static List<AbstractCarver> createCarversFromConfig(ServerLevel serverLevel,
                                                               BetterCavesWorldCarverConfig config,
                                                               BetterCavesWorldCarverConfig.CavernLayerSettings layerSettings) {
        List<AbstractCarver> carvers = new ArrayList<>();
        layerSettings.carvers().forEach(carverSettings -> {
            carvers.add(new Builder(serverLevel)
                    .fromConfig(config, carverSettings)
                    .build());
        });
        return carvers;
    }

    public CavernCarver(final Builder builder) {
        super(builder.getSettings());
        this.noiseGen = new NoiseGen(
                this.settings.getSeed(),
                this.settings.isFastNoise(),
                this.settings.getNoiseSettings(),
                this.settings.getNumGens(),
                this.settings.getYCompression(),
                this.settings.getXzCompression()
        );
        if (bottomY > topY) {
            BetterCavesCommon.LOGGER.warn("Warning: Min altitude for caverns should not be greater than max altitude.");
            BetterCavesCommon.LOGGER.warn("Using default values...");
            // TODO change how this validation works?
            throw new IllegalArgumentException(
                    "bettercaves: Cavern Carver's bottomY cannot be greater than topY. Please check your config.");
        } else {
            this.bottomY = builder.getBottomY();
            this.topY = builder.getTopY();
        }
        this.isFloored = builder.isFloored();
    }

    public void carveColumn(BetterCavesWorldCarverConfig config, ChunkAccess chunk, ColPos colPos, int topY, float smoothAmp,
                            double[][] noiseColumn, BlockState liquidBlock, CarvingMask carvingMask,
                            Aquifer aquifer) {
        int localX = colPos.getX() & 0xF;
        int localZ = colPos.getZ() & 0xF;

        // TODO - Validate topY and bottomY

        // Set altitude at which caverns start closing off on the top
        topY -= 2;
        int topTransitionBoundary = topY - 6;

        // Set altitude at which caverns start closing off on the bottom
        int bottomTransitionBoundary = bottomY + 3;

        // Close off floored caverns more to create "floors"
        if (this.isFloored) {
            bottomTransitionBoundary = bottomY < settings.getLiquidAltitude() ? settings.getLiquidAltitude() + 8 : bottomY + 7;
        }

        // TODO - Validate transition boundaries?
//        topTransitionBoundary = Math.max(topTransitionBoundary, 1);
//        bottomTransitionBoundary = Math.min(bottomTransitionBoundary, 255);

        BlockPos.MutableBlockPos localPos = new BlockPos.MutableBlockPos(localX, 1, localZ);
        BlockPos.MutableBlockPos realPos = new BlockPos.MutableBlockPos(colPos.getX(), 1, colPos.getZ());

        // Dig out caverns in this column, based on noise values
        for (int y = topY; y >= bottomY; y--) {
            if (y <= settings.getLiquidAltitude() && liquidBlock == null) break;

            boolean digBlock = false;

            // Compute a single noise value to represent all the noise values in the NoiseTuple
            float noise = 1;
            double[] noiseBlock = noiseColumn[y - bottomY];
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

    @Override
    public NoiseGen getNoiseGen() {
        return noiseGen;
    }

    public CarverSettings getSettings() {
        return settings;
    }

    public int getSpawnWeight() {
        return settings.getSpawnWeight();
    }

    @Override
    public int getBottomY() {
        return bottomY;
    }

    @Override
    public int getTopY() {
        return topY;
    }

    public static class Builder {
        private final CarverSettings settings;
        private int bottomY;
        private int topY;
        private boolean isFloored;

        public Builder(ServerLevel serverLevel) {
            settings = new CarverSettings(serverLevel);
        }

        public CavernCarver build() {
            return new CavernCarver(this);
        }

        private Builder fromConfig(BetterCavesWorldCarverConfig config, BetterCavesWorldCarverConfig.CavernLayerSettings.CavernSubCarverSettings subCarverSettings) {
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
            this.settings.setYCompression((float) subCarverSettings.yCompression());
            this.settings.setXzCompression((float) subCarverSettings.xzCompression());
            this.settings.setSpawnWeight(subCarverSettings.spawnWeight());
            this.bottomY = subCarverSettings.bottomY();
            this.topY = subCarverSettings.topY();
            this.isFloored = subCarverSettings.isFloored();
            return this;
        }

        public CarverSettings getSettings() {
            return settings;
        }

        public int getBottomY() {
            return bottomY;
        }

        public int getTopY() {
            return topY;
        }

        public  boolean isFloored() {
            return isFloored;
        }
    }
}
