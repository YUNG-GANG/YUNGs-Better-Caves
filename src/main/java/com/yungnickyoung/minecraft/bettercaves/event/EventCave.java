package com.yungnickyoung.minecraft.bettercaves.event;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.world.BetterCaveGenerator;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Event for replacing default caves with Better Caves upon generation.
 */
public class EventCave {
    /**
     * Subscribes to {@code InitMapGenEvent}s replaces default cave gen with Better Caves cave gen
     * @param event Map generation event
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onCaveEvent(InitMapGenEvent event) {
        // Only replace cave gen if the original gen passed isn't a Better Cave
        Configuration.LOGGER.info("onCaveEvent called");
        if (event.getType() == InitMapGenEvent.EventType.CAVE && !event.getOriginalGen().getClass().equals(BetterCaveGenerator.class)) {
            Configuration.LOGGER.info("  onCaveEvent passed conditions");
            event.setNewGen(new BetterCaveGenerator());
        }
    }

}