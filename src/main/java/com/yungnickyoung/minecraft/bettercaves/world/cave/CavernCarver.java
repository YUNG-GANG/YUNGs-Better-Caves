package com.yungnickyoung.minecraft.bettercaves.world.cave;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.config.dimension.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.enums.CavernType;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseGen;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseTuple;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.List;
import java.util.Map;

public class CavernCarver extends UndergroundCarver {
    private NoiseGen noiseGen;
    private CavernType cavernType;

    public CavernCarver(final CavernCarverBuilder builder) {
        super(builder);
        this.cavernType = builder.cavernType;
        noiseGen = new NoiseGen(
                this.noiseType,
                this.world,
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
    public void generateColumn(int chunkX, int chunkZ, ChunkPrimer primer, int localX, int localZ, int bottomY,
                               int topY, int maxSurfaceHeight, int minSurfaceHeight, int surfaceCutoff,
                               IBlockState lavaBlock, float smoothAmp) {
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
            bottomTransitionBoundary = (bottomY <= 10) ? Configuration.liquidAltitude + 4 : bottomY + 7;
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
            if (Configuration.debugsettings.debugVisualizer)
                visualizeDigBlock(digBlock, this.debugBlock, primer, localX, realY, localZ);
            else if (digBlock) {
                if (this.cavernType == CavernType.WATER) {
                    // Make sure we replace any lava possibly generated from caves with water to avoid having lava under the water
                    if (primer.getBlockState(localX, realY, localZ).getMaterial() == Material.LAVA && realY <= Configuration.liquidAltitude)
                        primer.setBlockState(localX, realY, localZ, Blocks.WATER.getDefaultState());
                    else
                        this.digBlock(primer, lavaBlock, chunkX, chunkZ, localX, localZ, realY);
                } else
                    this.digBlock(primer, lavaBlock, chunkX, chunkZ, localX, localZ, realY);
            }
        }
    }

    @Override
    public void generateColumn(int chunkX, int chunkZ, ChunkPrimer primer, int localX, int localZ, int bottomY,
                               int topY, int maxSurfaceHeight, int minSurfaceHeight, int surfaceCutoff,
                               IBlockState lavaBlock) {
        generateColumn(chunkX, chunkZ, primer, localX, localZ, bottomY, topY, maxSurfaceHeight, minSurfaceHeight, surfaceCutoff, lavaBlock, 1);
    }

    public static class CavernCarverBuilder extends UndergroundCarverBuilder {
        CavernType cavernType;

        public CavernCarverBuilder(World world) {
            super(world);
        }

        @Override
        public UndergroundCarver build() {
            return new CavernCarver(this);
        }

        public CavernCarverBuilder ofTypeFromConfig(CavernType cavernType, ConfigHolder config) {
            this.cavernType = cavernType;
            switch (cavernType) {
                case LAVA:
                    this.noiseType = FastNoise.NoiseType.PerlinFractal;
                    this.fractalOctaves = config.lavaCavernFractalOctaves.get();
                    this.fractalGain = config.lavaCavernFractalGain.get();
                    this.fractalFreq = config.lavaCavernFractalFrequency.get();
                    this.numGens = config.lavaCavernNumGenerators.get();
                    this.yCompression = config.lavaCavernYCompression.get();
                    this.xzCompression = config.lavaCavernXZCompression.get();
                    this.noiseThreshold = config.lavaCavernNoiseThreshold.get();
                    break;
                case FLOORED:
                    this.noiseType = FastNoise.NoiseType.PerlinFractal;
                    this.fractalOctaves = config.flooredCavernFractalOctaves.get();
                    this.fractalGain = config.flooredCavernFractalGain.get();
                    this.fractalFreq = config.flooredCavernFractalFrequency.get();
                    this.numGens = config.flooredCavernNumGenerators.get();
                    this.yCompression = config.flooredCavernYCompression.get();
                    this.xzCompression = config.flooredCavernXZCompression.get();
                    this.noiseThreshold = config.flooredCavernNoiseThreshold.get();
                    break;
                case WATER:
                    this.noiseType = FastNoise.NoiseType.PerlinFractal;
                    this.fractalOctaves = config.waterCavernFractalOctaves.get();
                    this.fractalGain = config.waterCavernFractalGain.get();
                    this.fractalFreq = config.waterCavernFractalFrequency.get();
                    this.numGens = config.waterCavernNumGenerators.get();
                    this.yCompression = config.waterCavernYCompression.get();
                    this.xzCompression = config.waterCavernXZCompression.get();
                    this.noiseThreshold = config.waterCavernNoiseThreshold.get();
                    break;
            }
            return this;
        }
    }
}
