package com.yungnickyoung.minecraft.bettercaves.world.carver.cavern;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.enums.CavernType;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseGen;
import com.yungnickyoung.minecraft.bettercaves.world.carver.CarverSettings;
import com.yungnickyoung.minecraft.bettercaves.world.carver.CarverUtils;
import com.yungnickyoung.minecraft.bettercaves.world.carver.ICarver;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

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

    public CavernCarver(final CavernCarverBuilder builder) {
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
            BetterCaves.LOGGER.warn("Warning: Min altitude for caverns should not be greater than max altitude.");
            BetterCaves.LOGGER.warn("Using default values...");
            this.bottomY = 1;
            this.topY = 35;
        }
    }

    public void carveColumn(Chunk chunk, BlockPos colPos, int topY, float smoothAmp, double[][] noises, BlockState liquidBlock, boolean flooded, BitSet carvingMask) {
        int localX = colPos.getX() & 0xF;
        int localZ = colPos.getZ() & 0xF;

        // Validate vars
        if (bottomY < 0) bottomY = 0;
        if (bottomY > 255) bottomY = 255;
        if (topY < 2) topY = 2;
        if (topY > 255) topY = 255;

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

        BlockPos.Mutable localPos = new BlockPos.Mutable(localX, 1, localZ);

        /* =============== Dig out caves and caverns in this chunk, based on noise values =============== */
        for (int y = topY; y >= bottomY; y--) {
            if (y <= settings.getLiquidAltitude() && liquidBlock == null)
                break;

            boolean digBlock = false;

            // Compute a single noise value to represent all the noise values in the NoiseTuple
            float noise = 1;
            double[] noiseBlock = noises[y - bottomY];
            for (double n : noiseBlock)
                noise *= n;

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
                    CarverUtils.carveFloodedBlock(chunk, new Random(), localPos, liquidBlock, settings.getLiquidAltitude(), settings.isReplaceFloatingGravel(), carvingMask);
                } else {
                    CarverUtils.carveBlock(chunk, localPos, liquidBlock, settings.getLiquidAltitude(), settings.isReplaceFloatingGravel(), carvingMask);
                }            }
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
}
