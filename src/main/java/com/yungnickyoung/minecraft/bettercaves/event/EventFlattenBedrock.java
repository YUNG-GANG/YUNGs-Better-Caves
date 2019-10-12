package com.yungnickyoung.minecraft.bettercaves.event;

import com.yungnickyoung.minecraft.bettercaves.world.bedrock.FlattenBedrock;
import net.minecraft.world.chunk.Chunk;
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
        Chunk chunk = event.getWorld().getChunk(event.getChunkX(), event.getChunkZ());
        FlattenBedrock.flattenBedrock(chunk, dimension);
    }
}
