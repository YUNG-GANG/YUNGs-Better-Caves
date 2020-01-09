package com.yungnickyoung.minecraft.bettercaves.world.cave;

import com.yungnickyoung.minecraft.bettercaves.config.BetterCavesConfig;
import com.yungnickyoung.minecraft.bettercaves.enums.CavernType;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseGen;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseTuple;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;

import java.util.List;
import java.util.Map;

public class CavernBC extends AbstractBC {
    private NoiseGen noiseGen;
    private CavernType cavernType;

    public CavernBC(long seed, CavernType cavernType, int fOctaves, float fGain, float fFreq, int numGens, float threshold,
                    double yComp, double xzComp, BlockState vBlock) {
        super(seed, fOctaves, fGain, fFreq, numGens, threshold, 0, 0, 0, false,
                yComp, xzComp, false, 1, 1, vBlock);

        this.cavernType = cavernType;

        // Determine noise to use based on cavern type
        switch (cavernType) {
            case LAVA:
                this.noiseType = FastNoise.NoiseType.PerlinFractal;
                break;
            case FLOORED:
                this.noiseType = FastNoise.NoiseType.PerlinFractal;
                break;
            default:
            case WATER:
                this.noiseType = FastNoise.NoiseType.PerlinFractal;
                break;
        }

        noiseGen = new NoiseGen(
                FastNoise.NoiseType.PerlinFractal,
                seed,
                this.fractalOctaves,
                this.fractalGain,
                this.fractalFreq,
                this.turbOctaves,
                this.turbGain,
                this.turbFreq,
                this.enableTurbulence,
                this.yCompression,
                this.xzCompression
        );
    }

    @Override
    public void generateColumn(int chunkX, int chunkZ, IChunk chunkIn, int localX, int localZ, int bottomY,
                               int topY, int maxSurfaceHeight, int minSurfaceHeight, int surfaceCutoff,
                               BlockState lavaBlock, float smoothAmp) {
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
        int topTransitionBoundary = topY - 7;

        // Validate transition boundary
        if (topTransitionBoundary < 1)
            topTransitionBoundary = 1;

        // Altitude at which caverns start closing off on the bottom to create "floors"
        int bottomTransitionBoundary = 0;
        if (cavernType == CavernType.FLOORED)
            bottomTransitionBoundary = (bottomY <= 10) ? BetterCavesConfig.lavaDepth + 4 : bottomY + 7;
        else if (cavernType == CavernType.WATER)
            bottomTransitionBoundary = bottomY + 3;

        // Generate noise for caverns.
        // The noise for an individual block is represented by a NoiseTuple, which is essentially an n-tuple of
        // floats, where n is equal to the number of generators passed to the function
        Map<Integer, NoiseTuple> noises =
                noiseGen.generateNoiseCol(chunkX, chunkZ, bottomY, topY, this.numGens, localX, localZ);

        /* =============== Dig out caves and caverns in this chunk, based on noise values =============== */
        for (int realY = topY; realY >= bottomY; realY--) {
            List<Float> noiseBlock;
            boolean digBlock = false;

            // Compute a single noise value to represent all the noise values in the NoiseTuple
            float noise = 1;
            noiseBlock = noises.get(realY).getNoiseValues();
            for (float n : noiseBlock)
                noise *= n;

            // Adjust threshold if we're in the transition range to provide smoother transition into ceiling
            float noiseThreshold = this.noiseThreshold;
            if (realY >= topTransitionBoundary)
                noiseThreshold *= Math.max((float) (realY - topY) / (topTransitionBoundary - topY), .5f);

            // Force close-off caverns if we're in ease-in depth range
            if (realY >= minSurfaceHeight - 5)
                noiseThreshold *= (float) (realY - topY) / (minSurfaceHeight - 5 - topY);

            // For floored caverns, close off caverns at the bottom to provide floors for the player to walk on
            if ((this.cavernType == CavernType.FLOORED || this.cavernType == CavernType.WATER) && realY <= bottomTransitionBoundary)
                noiseThreshold *= Math.max((float) (realY - bottomY) / (bottomTransitionBoundary - bottomY), .5f);

            // Adjust threshold along region borders to create smooth transition
            if (smoothAmp < 1)
                noiseThreshold *= smoothAmp;

            // Mark block for removal if the noise passes the threshold check
            if (noise < noiseThreshold)
                digBlock = true;

            // Consider digging out the block if it passed the threshold check, using the debug visualizer if enabled
            if (BetterCavesConfig.enableDebugVisualizer)
                visualizeDigBlock(digBlock, this.vBlock, chunkIn, localX, realY, localZ);
            else if (digBlock) {
                if (this.cavernType == CavernType.WATER) {
                    BlockPos blockPos = new BlockPos(localX, realY, localZ);
                    // Make sure we replace any lava possibly generated from caves with water to avoid having lava under the water
                    if (chunkIn.getBlockState(blockPos).getFluidState().isTagged(FluidTags.LAVA) && realY <= BetterCavesConfig.lavaDepth)
                        chunkIn.setBlockState(blockPos, Blocks.WATER.getDefaultState(), false);
                    else
                        this.digBlock(chunkIn, lavaBlock, chunkX, chunkZ, localX, localZ, realY);
                } else
                    this.digBlock(chunkIn, lavaBlock, chunkX, chunkZ, localX, localZ, realY);
            }
        }
    }

    @Override
    public void generateColumn(int chunkX, int chunkZ, IChunk chunkIn, int localX, int localZ, int bottomY,
                               int topY, int maxSurfaceHeight, int minSurfaceHeight, int surfaceCutoff,
                               BlockState lavaBlock) {
        generateColumn(chunkX, chunkZ, chunkIn, localX, localZ, bottomY, topY, maxSurfaceHeight, minSurfaceHeight, surfaceCutoff, lavaBlock, 1);
    }
}