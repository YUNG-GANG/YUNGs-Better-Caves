package com.yungnickyoung.minecraft.bettercaves.world.cave;

import com.yungnickyoung.minecraft.bettercaves.noise.NoiseColumn;
import com.yungnickyoung.minecraft.bettercaves.noise.NoiseGen;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtil;
import com.yungnickyoung.minecraft.bettercaves.world.cave.builder.CaveCarverBuilder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.List;
import java.util.Map;

/**
 * Builder class for CaveCarver.
 * Fields may be built individually or loaded in bulk via the {@code ofTypeFromCarver} method
 */
public class CaveCarver extends UndergroundCarver {
    private int surfaceCutoff;

    public CaveCarver(final CaveCarverBuilder builder) {
        super(builder);
        surfaceCutoff = builder.getSurfaceCutoff();
        noiseGen = new NoiseGen(
                this.world,
                this.noiseSettings,
                this.turbulenceSettings,
                this.numGens,
                this.enableTurbulence,
                this.yCompression,
                this.xzCompression
        );
    }

    @Override
    public void generateColumnWithNoise(ChunkPrimer primer, BlockPos colPos, int bottomY,
                                        int topY, int maxSurfaceHeight, int minSurfaceHeight,
                                        IBlockState liquidBlock, NoiseColumn noises, boolean liquidBuffer) {
        int localX = BetterCavesUtil.getLocal(colPos.getX());
        int localZ = BetterCavesUtil.getLocal(colPos.getZ());

        // Validate vars
        if (localX < 0 || localX > 15)
            return;
        if (localZ < 0 || localZ > 15)
            return;
        if (bottomY < 0 || bottomY > 255)
            return;
        if (topY < 0 || topY > 255)
            return;

        // Altitude at which caves start closing off so they aren't all open to the surface
        int transitionBoundary = maxSurfaceHeight - surfaceCutoff;

        // Validate transition boundary
        if (transitionBoundary < 1)
            transitionBoundary = 1;

        // Pre-compute thresholds to ensure accuracy during pre-processing
        Map<Integer, Float> thresholds = generateThresholds(topY, bottomY, transitionBoundary);

        // Do some pre-processing on the noises to facilitate better cave generation.
        // Basically this makes caves taller to give players more headroom.
        // See the javadoc for the function for more info.
        if (this.enableYAdjust)
            preprocessCaveNoiseCol(noises, topY, bottomY, thresholds, this.numGens);

        /* =============== Dig out caves and caverns in this column, based on noise values =============== */
        for (int y = topY; y >= bottomY; y--) {
            if (y <= liquidAltitude && liquidBuffer)
                break;

            List<Float> noiseBlock = noises.get(y).getNoiseValues();
            boolean digBlock = true;

            for (float noise : noiseBlock) {
                if (noise < thresholds.get(y)) {
                    digBlock = false;
                    break;
                }
            }

            BlockPos blockPos = new BlockPos(colPos.getX(), y, colPos.getZ());

            // Consider digging out the block if it passed the threshold check, using the debug visualizer if enabled
            if (this.enableDebugVisualizer)
                visualizeDigBlock(primer, blockPos, digBlock, this.debugBlock);
            else if (digBlock)
                this.digBlock(primer, blockPos, liquidBlock, liquidAltitude);
        }

        /* ============ Post-Processing to remove any singular floating blocks in the ease-in range ============ */
        IBlockState BlockStateAir = Blocks.AIR.getDefaultState();
        for (int y = transitionBoundary + 1; y < topY; y++) {
            if (y < 1 || y > 255)
                break;

            IBlockState currBlock = primer.getBlockState(localX, y, localZ);

            if (BetterCavesUtil.canReplaceBlock(currBlock, BlockStateAir)
                    && primer.getBlockState(localX, y + 1, localZ) == BlockStateAir
                    && primer.getBlockState(localX, y - 1, localZ) == BlockStateAir
            )
                this.digBlock(primer, colPos, liquidBlock, liquidAltitude);
        }
    }
}