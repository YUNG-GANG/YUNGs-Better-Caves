package com.yungnickyoung.minecraft.bettercaves.world.carver.bedrock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

/**
 * Class containing static method for flattening bedrock.
 */
public class FlattenBedrock {
    private static final BlockState BEDROCK = Blocks.BEDROCK.defaultBlockState();

    /**
     * Flattens bedrock in a given chunk
     * @param bedrockLayerWidth Width of the bedrock layer, in blocks
     */
    public static void flattenBedrock(ChunkAccess chunk, int bedrockLayerWidth) {
        BlockState replacementBlock = Blocks.STONE.defaultBlockState();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        // Replace normal bedrock at bottom of map with stone
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 1; y < 5; y++) {
                    pos.set(x, y, z);
                    if (chunk.getBlockState(pos) == BEDROCK)
                        chunk.setBlockState(pos, replacementBlock, false);
                }
            }
        }

        // Create bedrock layer(s) at bottom of map
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 1; y < bedrockLayerWidth; y++) {
                    pos.set(x, y, z);
                    chunk.setBlockState(pos, BEDROCK, false);
                }
            }
        }
    }
}
