package com.yungnickyoung.minecraft.bettercaves.world.bedrock;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.config.dimension.ConfigHolder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.ChunkPrimer;

/**
 * Class containing static method for flattening bedrock.
 */
public class FlattenBedrock {
    /**
     * Flattens bedrock in a given chunk according to user Configuration settings.
     * @param primer The chunk's ChunkPrimer
     */
    public static void flattenBedrock(ChunkPrimer primer, ConfigHolder config) {
        if (!config.flattenBedrock.get()) return;
        IBlockState replacementBlock = Blocks.STONE.getDefaultState();

        // Replace normal bedrock at bottom of map with stone
        for (int x = 0; x < 16; x++)
            for (int z = 0; z < 16; z++)
                for (int y = 0; y < 5; y++)
                    if (primer.getBlockState(x, y, z) == Blocks.BEDROCK.getDefaultState())
                        primer.setBlockState(x, y, z, replacementBlock);

        // Create bedrock layer(s) at bottom of map
        int numLayers = config.bedrockWidth.get();

        for (int x = 0; x < 16; x++)
            for (int z = 0; z < 16; z++)
                for (int y = 0; y < numLayers; y++)
                    primer.setBlockState(x, y, z, Blocks.BEDROCK.getDefaultState());
    }
}
