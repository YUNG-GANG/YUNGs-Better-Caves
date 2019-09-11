package com.yungnickyoung.minecraft.bettercaves.event;

import com.yungnickyoung.minecraft.bettercaves.world.ravine.MapGenBetterRavine;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Replaces vanilla ravine generation with Better Caves ravine generation if enabled (which just disables ravines).
 * Should be registered to the {@code TERRAIN_GEN_BUS}.
 */
public class EventRavineGen {
    /**
     * Disables ravine gen events if config option is enabled
     * @param event Map generation event
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onRavineEvent(InitMapGenEvent event) {
        if (event.getType() == InitMapGenEvent.EventType.RAVINE && !event.getOriginalGen().getClass().equals(MapGenBetterRavine.class)) {
            event.setNewGen(new MapGenBetterRavine());
        }
    }
}