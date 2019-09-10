package com.yungnickyoung.minecraft.bettercaves.event;

import com.yungnickyoung.minecraft.bettercaves.world.ravine.MapGenBetterRavine;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Replaces vanilla ravine generation with Better Caves ravine generation if enabled (which essentially disables ravines).
 * Should be registered to the {@code TERRAIN_GEN_BUS}.
 */
public class EventBetterRavineGen {
    /**
     * Replaces ravine gen events with Better Caves ravine gen (nothing)
     * @param event Map generation event
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onCaveEvent(InitMapGenEvent event) {
        // Only replace cave gen if the original gen passed isn't a Better Cave
        if (event.getType() == InitMapGenEvent.EventType.RAVINE && !event.getOriginalGen().getClass().equals(MapGenBetterRavine.class)) {
            event.setNewGen(new MapGenBetterRavine());
        }
    }
}