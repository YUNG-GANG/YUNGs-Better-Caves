package com.yungnickyoung.minecraft.bettercaves.world;

import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenCaves;

import javax.annotation.ParametersAreNonnullByDefault;

public class BetterCaveGenerator extends MapGenCaves {

    private int lavaDepth = 10;

    public BetterCaveGenerator() {

    }

    @Override
    @ParametersAreNonnullByDefault
    public void generate(World worldIn, int chunkX, int chunkZ, ChunkPrimer primer) {
        Settings.LOGGER.info("generate() cave called: {} | {} | {} | {}", worldIn, chunkX, chunkZ, primer);

        this.world = worldIn;

        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                for (int localY = 0; localY < 80; localY++) {
                    IBlockState currentBlockState = primer.getBlockState(localX, localY, localZ);
                    IBlockState aboveBlockState = primer.getBlockState(localX, localY + 1, localZ);
                    boolean foundTopBlock = isTopBlock(primer, localX, localY, localZ, chunkX, chunkZ);
                    digBlock(primer, localX, localY, localZ, chunkX, chunkZ, foundTopBlock, currentBlockState, aboveBlockState);
                }
            }
        }
    }

    // Because it's private in MapGenCaves this is reimplemented
    // Determine if the block at the specified location is the top block for the biome, we take into account
    // Vanilla bugs to make sure that we generate the map the same way vanilla does.
    private boolean isTopBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ) {
        Biome biome = world.getBiome(new BlockPos(x + chunkX * 16, 0, z + chunkZ * 16));
        IBlockState state = data.getBlockState(x, y, z);
        return isExceptionBiome(biome) ? state.getBlock() == Blocks.GRASS : state == biome.topBlock;
    }

    // Exception biomes to make sure we generate like vanilla
    private boolean isExceptionBiome(net.minecraft.world.biome.Biome biome) {
        return biome == net.minecraft.init.Biomes.BEACH || biome == net.minecraft.init.Biomes.DESERT;
    }

    @Override
    protected void digBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop, IBlockState state, IBlockState up) {
        net.minecraft.world.biome.Biome biome = world.getBiome(new BlockPos(x + chunkX * 16, 0, z + chunkZ * 16));
        IBlockState top = biome.topBlock;
        IBlockState filler = biome.fillerBlock;

        if (this.canReplaceBlock(state, up) || state.getBlock() == top.getBlock() || state.getBlock() == filler.getBlock()) {
            if (y <= lavaDepth)
                data.setBlockState(x, y, z, Blocks.LAVA.getDefaultState());
            else {
                data.setBlockState(x, y, z, Blocks.AIR.getDefaultState());
                if (foundTop && data.getBlockState(x, y - 1, z).getBlock() == filler.getBlock())
                    data.setBlockState(x, y - 1, z, top);


                //replace floating sand with sandstone
                if(up == Blocks.SAND.getDefaultState()) {
                    data.setBlockState(x, y+1, z, BLK_SANDSTONE);
                } else if(up == Blocks.SAND.getStateFromMeta(1)) {
                    data.setBlockState(x, y+1, z, BLK_RED_SANDSTONE);
                }
            }
        }
    }
}
