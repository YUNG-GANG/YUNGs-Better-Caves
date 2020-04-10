package com.yungnickyoung.minecraft.bettercaves.world.ravine;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.world.MapGenBetterCaves;
import com.yungnickyoung.minecraft.bettercaves.world.carver.CarverUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenRavine;

/**
 * Overrides MapGenRavine, disabling ravine generation if the config option
 * is set to false.
 */
public class MapGenBetterRavine extends MapGenRavine {
    private MapGenBetterCaves carver;
    private int liquidAltitude;
    private boolean isReplaceFloatingGravel;
    private boolean isFloodedRavinesEnabled;

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
        IBlockState liquidBlockState, airBlockState;
        BlockPos pos = new BlockPos(x + chunkX * 16, y, z + chunkZ * 16);
        try {
            liquidBlockState = carver.waterRegionController.getLiquidBlockAtPos(pos);
        } catch (Exception e) {
            liquidBlockState = Blocks.LAVA.getDefaultState();
        }
        airBlockState = (isFloodedRavinesEnabled && world.getBiome(pos).getTempCategory() == Biome.TempCategory.OCEAN)
            ? Blocks.WATER.getDefaultState()
            : AIR;
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
        this.liquidAltitude = carver.config.liquidAltitude.get();
        this.isReplaceFloatingGravel = carver.config.replaceFloatingGravel.get();
        this.isFloodedRavinesEnabled = carver.config.enableFloodedRavines.get();
    }
}
