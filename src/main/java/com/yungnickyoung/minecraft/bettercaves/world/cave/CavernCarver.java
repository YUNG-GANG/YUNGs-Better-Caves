package com.yungnickyoung.minecraft.bettercaves.world.cave;

import com.yungnickyoung.minecraft.bettercaves.enums.CavernType;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseColumn;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseGen;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtil;
import com.yungnickyoung.minecraft.bettercaves.world.cave.builder.CavernCarverBuilder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.List;

/**
 * BetterCaves Cavern carver.
 * Caverns are large openings generated at the bottom of the world.
 */
public class CavernCarver {
    private CarverSettings settings;
    private NoiseGen noiseGen;
    private CavernType cavernType;

    public CavernCarver(final CavernCarverBuilder builder) {
        settings = builder.getSettings();
        noiseGen = new NoiseGen(
                settings.getWorld(),
                settings.getNoiseSettings(),
                settings.getTurbulenceSettings(),
                settings.getNumGens(),
                settings.isEnableTurbulence(),
                settings.getyCompression(),
                settings.getXzCompression()
        );
        cavernType = builder.getCavernType();
    }

    public void generateColumnWithNoise(ChunkPrimer primer, BlockPos colPos, int bottomY,
                                        int topY, int maxSurfaceHeight, int minSurfaceHeight,
                                        IBlockState liquidBlock, float smoothAmp, NoiseColumn noises, boolean liquidBuffer) {
        int localX = BetterCavesUtil.getLocal(colPos.getX());
        int localZ = BetterCavesUtil.getLocal(colPos.getZ());

        // Validate vars
        if (localX < 0 || localX > 15)
            return;
        if (localZ < 0 || localZ > 15)
            return;
        if (bottomY < 0)
            return;
        if (topY > 255)
            return;

        // Altitude at which caverns start closing off on the top
        int topTransitionBoundary = topY - 10;

        // Validate transition boundary
        if (topTransitionBoundary < 1)
            topTransitionBoundary = 1;

        // Altitude at which caverns start closing off on the bottom to create "floors"
        int bottomTransitionBoundary = 0;
        if (cavernType == CavernType.FLOORED)
            bottomTransitionBoundary = (bottomY <= 10) ? settings.getLiquidAltitude() + 4 : bottomY + 7;
        else if (cavernType == CavernType.WATER)
            bottomTransitionBoundary = bottomY + 3;

        /* =============== Dig out caves and caverns in this chunk, based on noise values =============== */
        for (int y = topY; y >= bottomY; y--) {
            if (y <= settings.getLiquidAltitude() && liquidBuffer)
                break;

            List<Float> noiseBlock;
            boolean digBlock = false;

            // Compute a single noise value to represent all the noise values in the NoiseTuple
            float noise = 1;
            noiseBlock = noises.get(y).getNoiseValues();
            for (float n : noiseBlock)
                noise *= n;

            // Adjust threshold if we're in the transition range to provide smoother transition into ceiling
            float noiseThreshold = settings.getNoiseThreshold();
            if (y >= topTransitionBoundary)
                noiseThreshold *= Math.max((float) (y - topY) / (topTransitionBoundary - topY), .3f);

            // Force close-off caverns if we're in ease-in depth range
            if (y >= minSurfaceHeight - 5)
                noiseThreshold *= (float) (y - topY) / (minSurfaceHeight - 5 - topY);

            // For floored caverns, close off caverns at the bottom to provide floors for the player to walk on
            if ((this.cavernType == CavernType.FLOORED || this.cavernType == CavernType.WATER) && y <= bottomTransitionBoundary)
                noiseThreshold *= Math.max((float) (y - bottomY) / (bottomTransitionBoundary - bottomY), .3f);

            // Adjust threshold along region borders to create smooth transition
            if (smoothAmp < 1)
                noiseThreshold *= smoothAmp;

            // Mark block for removal if the noise passes the threshold check
            if (noise < noiseThreshold)
                digBlock = true;

            BlockPos blockPos = new BlockPos(colPos.getX(), y, colPos.getZ());

            // Dig out the block if it passed the threshold check, using the debug visualizer if enabled
            if (settings.isEnableDebugVisualizer()) {
                CarverUtils.debugDigBlock(primer, blockPos, settings.getDebugBlock(), digBlock);
            }
            else if (digBlock) {
                CarverUtils.digBlock(settings.getWorld(), primer, blockPos, liquidBlock, settings.getLiquidAltitude(), settings.isReplaceFloatingGravel());
            }
        }
    }

    public NoiseGen getNoiseGen() {
        return noiseGen;
    }
}
