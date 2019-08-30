package com.yungnickyoung.minecraft.bettercaves.event;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Replaces ordinary bedrock generation with a flattened layer based on the user's specification.
 * Should be registered to the {@code EVENT_BUS}.
 */
public class EventFlattenBedrock {
    /**
     * Replaces usual bedrock generation with a flat layer of bedrock.
     * Accounts for both the Overworld and the Nether, based on user config settings.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPopulateChunkEventPre(PopulateChunkEvent.Pre event) {
        int dimension = event.getWorld().provider.getDimension();
        IBlockState replacementBlock;

        // Only process the overworld and nether. Make sure the corresponding Config option is set to true.
        if (dimension == 0) { // Dimension 0 is the overworld
            if (!Configuration.bedrockSettings.overworld.flattenBedrock) return;
            replacementBlock = Blocks.STONE.getDefaultState();
        } else if (dimension == -1) { // Dimension -1 is the nether
            if (!Configuration.bedrockSettings.nether.flattenBedrock) return;
            replacementBlock = Blocks.NETHERRACK.getDefaultState();
        } else {
            return;
        }

        Chunk chunk = event.getWorld().getChunkFromChunkCoords(event.getChunkX(), event.getChunkZ());

        // Replace any bedrock in either dimension with the appropriate block
        for (ExtendedBlockStorage chunkSection : chunk.getBlockStorageArray()) {
            if (chunkSection != null && !chunkSection.isEmpty()) {
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        for (int z = 0; z < 16; z++) {
                            if (chunkSection.get(x, y, z) == Blocks.BEDROCK.getDefaultState()) {
                                chunkSection.set(x, y, z, replacementBlock);
                            }
                        }
                    }
                }
            }
        }

        // Ensure the bottom X layers (and top X layers in the nether) are bedrock,
        // where X is dependent on the Configuration setting
        ExtendedBlockStorage[] sections = chunk.getBlockStorageArray();

        int numSections, topLeftover;
        if (dimension == 0) {
            numSections = Configuration.bedrockSettings.overworld.bedrockWidth / 16;
            topLeftover = Configuration.bedrockSettings.overworld.bedrockWidth % 16;
        } else {
            numSections = Configuration.bedrockSettings.nether.bedrockWidthBottom / 16;
            topLeftover = Configuration.bedrockSettings.nether.bedrockWidthBottom % 16;
        }

        /* ---- Replace bottom X layers in either dimension ---- */
        for (int section = 0; section < numSections; section++) {
            if (sections[section] != null && !sections[section].isEmpty()) {
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        for (int z = 0; z < 16; z++) {
                            sections[section].set(x, y, z, Blocks.BEDROCK.getDefaultState());
                        }
                    }
                }
            }
        }

        // Populate top partial section with bedrock
        if (sections[numSections] != null && !sections[numSections].isEmpty()) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < topLeftover; y++) {
                        sections[numSections].set(x, y, z, Blocks.BEDROCK.getDefaultState());
                    }
                }
            }
        }

        /* ---- Replace top X layers in the nether ---- */
        if (dimension == -1) {
            numSections = Configuration.bedrockSettings.nether.bedrockWidthTop / 16;
            topLeftover = Configuration.bedrockSettings.nether.bedrockWidthTop % 16;

            for (int section = 0; section < numSections; section++) {
                if (sections[8 - section - 1] != null && !sections[8 - section - 1].isEmpty()) {
                    for (int x = 0; x < 16; x++) {
                        for (int y = 0; y < 16; y++) {
                            for (int z = 0; z < 16; z++) {
                                sections[8 - section - 1].set(x, y, z, Blocks.BEDROCK.getDefaultState());
                            }
                        }
                    }
                }
            }

            // Populate bottom partial section with bedrock
            if (sections[8 - numSections - 1] != null && !sections[8 - numSections - 1].isEmpty()) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = 0; y < topLeftover; y++) {
                            sections[8 - numSections - 1].set(x, 15 - y, z, Blocks.BEDROCK.getDefaultState());
                        }
                    }
                }
            }
        }

        chunk.setModified(true);
    }
}
