package com.yungnickyoung.minecraft.bettercaves.world.ravine;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtils;
import com.yungnickyoung.minecraft.bettercaves.world.MapGenBetterCaves;
import com.yungnickyoung.minecraft.bettercaves.world.WaterRegionController;
import com.yungnickyoung.minecraft.bettercaves.world.carver.CarverUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenRavine;

/**
 * Overrides MapGenRavine, tweaking it to work with config options.
 */
public class MapGenBetterRavine extends MapGenRavine {
    private MapGenBetterCaves carver;
    private int liquidAltitude;
    private boolean isReplaceFloatingGravel;
    private boolean isFloodedRavinesEnabled;

    IBlockState[][] currChunkLiquidBlocks;
    int currChunkX, currChunkZ;

    @Override
    public void generate(World worldIn, int x, int z, ChunkPrimer primer) {
        if (carver == null) { // First call - lazy initialization
            this.initialize(worldIn);
        }
        if (carver != null) { // If carver exists, use its config to determine ravine spawning
            if (carver.config.enableVanillaRavines.get()) {
                super.generate(worldIn, x, z, primer);
            }
        } else { // If carver is for some reason not found, use the global Better Caves config setting
            if (Configuration.caveSettings.ravines.enableVanillaRavines) {
                super.generate(worldIn, x, z, primer);
            }
        }
    }

    @Override
    protected void digBlock(ChunkPrimer primer, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop) {
        IBlockState liquidBlockState;
        BlockPos pos = new BlockPos(x + chunkX * 16, y, z + chunkZ * 16);

        if (currChunkLiquidBlocks == null || chunkX != currChunkX || chunkZ != currChunkZ) {
            try {
                currChunkLiquidBlocks = carver.waterRegionController.getLiquidBlocksForChunk(chunkX, chunkZ);
                liquidBlockState = currChunkLiquidBlocks[BetterCavesUtils.getLocal(x)][BetterCavesUtils.getLocal(z)];
            } catch (Exception e) {
                liquidBlockState = Blocks.LAVA.getDefaultState();
            }
        }
        else {
            try {
                liquidBlockState = currChunkLiquidBlocks[BetterCavesUtils.getLocal(x)][BetterCavesUtils.getLocal(z)];
            } catch (Exception e) {
                liquidBlockState = Blocks.LAVA.getDefaultState();
            }
        }

        // Don't dig boundaries between flooded and unflooded openings.
        boolean flooded = isFloodedRavinesEnabled && world.getBiome(pos).getTempCategory() == Biome.TempCategory.OCEAN;
        if (flooded) {
            float smoothAmpFactor = WaterRegionController.getDistFactor(world, pos, 2, b -> b != Biome.TempCategory.OCEAN);
            if (smoothAmpFactor <= .25f) { // Wall between flooded and normal caves.
                return;
            }
        }

        IBlockState airBlockState = flooded ? Blocks.WATER.getDefaultState() : AIR;
        CarverUtils.digBlock(world, primer, pos, airBlockState, liquidBlockState, liquidAltitude, isReplaceFloatingGravel);
    }

    // Disable built-in water block checks.
    // Without this, ravines in water regions will be sliced up.
    @Override
    protected boolean isOceanBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ) {
        return false;
    }

    private void initialize(World worldIn) {
        this.world = worldIn;
        int dimensionID = worldIn.provider.getDimension();
        this.carver = BetterCaves.activeCarversMap.get(dimensionID);
        // Check for error loading carver
        if (carver == null) {
            Settings.LOGGER.error(String.format("Failed to find Better Caves carver in dimension %s during ravine generation!", world.provider.getDimensionType().toString()));
            Settings.LOGGER.error("Is another cave mod installed?");
        }
        this.liquidAltitude = carver.config.liquidAltitude.get();
        this.isReplaceFloatingGravel = carver.config.replaceFloatingGravel.get();
        this.isFloodedRavinesEnabled = carver.config.enableFloodedRavines.get();
    }
}
