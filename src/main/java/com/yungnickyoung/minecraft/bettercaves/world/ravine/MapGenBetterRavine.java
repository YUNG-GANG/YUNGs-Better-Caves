package com.yungnickyoung.minecraft.bettercaves.world.ravine;

import com.yungnickyoung.minecraft.bettercaves.config.io.ConfigLoader;
import com.yungnickyoung.minecraft.bettercaves.config.util.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtils;
import com.yungnickyoung.minecraft.bettercaves.world.WaterRegionController;
import com.yungnickyoung.minecraft.bettercaves.world.carver.CarverUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.terraingen.InitMapGenEvent;

import javax.annotation.Nonnull;

/**
 * Overrides MapGenRavine, tweaking it to work with config options.
 */
public class MapGenBetterRavine extends MapGenRavine {
    private ConfigHolder config;
    private WaterRegionController waterRegionController;
    private MapGenBase defaultRavineGen;

    IBlockState[][] currChunkLiquidBlocks;
    int currChunkX, currChunkZ;

    public MapGenBetterRavine(InitMapGenEvent event) {
        this.defaultRavineGen = event.getOriginalGen();
    }

    @Override
    public void generate(World worldIn, int x, int z, @Nonnull ChunkPrimer primer) {
        // Only operate on whitelisted dimensions.
        if (!BetterCavesUtils.isDimensionWhitelisted(worldIn.provider.getDimension())) {
            defaultRavineGen.generate(worldIn, x, z, primer);
            return;
        }

        if (config == null) { // First call - lazy initialization
            this.initialize(worldIn);
        }

        if (config.enableVanillaRavines.get()) {
            super.generate(worldIn, x, z, primer);
        }
    }

    @Override
    protected void digBlock(ChunkPrimer primer, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop) {
        IBlockState liquidBlockState;
        BlockPos pos = new BlockPos(x + chunkX * 16, y, z + chunkZ * 16);

        if (currChunkLiquidBlocks == null || chunkX != currChunkX || chunkZ != currChunkZ) {
            try {
                currChunkLiquidBlocks = waterRegionController.getLiquidBlocksForChunk(chunkX, chunkZ);
                liquidBlockState = currChunkLiquidBlocks[BetterCavesUtils.getLocal(x)][BetterCavesUtils.getLocal(z)];
                currChunkX = chunkX;
                currChunkZ = chunkZ;
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
        boolean flooded = config.enableFloodedRavines.get() && BiomeDictionary.hasType(world.getBiome(pos), BiomeDictionary.Type.OCEAN) && y < world.getSeaLevel();
        if (flooded) {
            float smoothAmpFactor = BetterCavesUtils.biomeDistanceFactor(world, pos, 2, b -> !BiomeDictionary.hasType(b, BiomeDictionary.Type.OCEAN));
            if (smoothAmpFactor <= .25f) { // Wall between flooded and normal caves.
                return;
            }
        }

        IBlockState airBlockState = flooded ? Blocks.WATER.getDefaultState() : AIR;
        CarverUtils.digBlock(world, primer, pos, airBlockState, liquidBlockState, config.liquidAltitude.get(), config.replaceFloatingGravel.get());
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
        this.config = ConfigLoader.loadConfigFromFileForDimension(dimensionID);
        this.waterRegionController = new WaterRegionController(world, config);
    }
}
