package com.yungnickyoung.minecraft.bettercaves.event;

import com.yungnickyoung.minecraft.bettercaves.world.MapGenBetterCaves;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Handlers for events registered to the {@code TERRAIN_GEN_BUS}.
 */
public class EventTerrain {
    /**
     * Replaces cave gen events with Better Caves cave gen
     * @param event Map generation event
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onCaveEvent(InitMapGenEvent event) {
        // Only replace cave gen if the original gen passed isn't a Better Cave
        if (event.getType() == InitMapGenEvent.EventType.CAVE && !event.getOriginalGen().getClass().equals(MapGenBetterCaves.class)) {
            event.setNewGen(new MapGenBetterCaves());
        }
    }
}