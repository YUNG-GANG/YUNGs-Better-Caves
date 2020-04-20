package com.yungnickyoung.minecraft.bettercaves.world.bedrock;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;

/**
 * Class containing static method for flattening bedrock.
 */
public class FlattenBedrock {
    private static final BlockState BEDROCK = Blocks.BEDROCK.getDefaultState();

    /**
     * Flattens bedrock in a given chunk
     * @param bedrockLayerWidth Width of the bedrock layer, in blocks
     */
    public static void flattenBedrock(IChunk chunk, int bedrockLayerWidth) {
        BlockState replacementBlock = Blocks.STONE.getDefaultState();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        // Replace normal bedrock at bottom of map with stone
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 1; y < 5; y++) {
                    pos.setPos(x, y, z);
                    if (chunk.getBlockState(pos) == BEDROCK)
                        chunk.setBlockState(pos, replacementBlock, false);
                }
            }
        }

        // Create bedrock layer(s) at bottom of map
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 1; y < bedrockLayerWidth; y++) {
                    pos.setPos(x, y, z);
                    chunk.setBlockState(pos, BEDROCK, false);
                }
            }
        }
    }
}
