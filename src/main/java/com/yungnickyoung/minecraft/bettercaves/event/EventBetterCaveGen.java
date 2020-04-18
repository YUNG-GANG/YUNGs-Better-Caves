package com.yungnickyoung.minecraft.bettercaves.event;

import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.world.MapGenBetterCaves;
import com.yungnickyoung.minecraft.bettercaves.world.mineshaft.MapGenBetterMineshaft;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Replaces vanilla cave generation with Better Caves generation.
 * Should be registered to the {@code TERRAIN_GEN_BUS}.
 */
public class EventBetterCaveGen {
    /**
     * Replaces cave gen and mineshaft gen
     *
     * @param event Map generation event
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onInitMapGenEvent(InitMapGenEvent event) {
        // Replace cave gen with Better Caves if the original gen passed isn't from Better Caves
        if (
            (event.getType() == InitMapGenEvent.EventType.CAVE || event.getType() == InitMapGenEvent.EventType.NETHER_CAVE)
                && !event.getOriginalGen().getClass().equals(MapGenBetterCaves.class)
        ) {
            Settings.LOGGER.debug("INITMAPGENEVENT");
            event.setNewGen(new MapGenBetterCaves(event));
        }
        // Replace mineshaft gen with Better Caves if the original gen passed in isn't from Better Caves
        else if (
            event.getType() == InitMapGenEvent.EventType.MINESHAFT
                && !event.getOriginalGen().getClass().equals(MapGenBetterMineshaft.class)
                && event.getOriginalGen() == event.getNewGen() // only modify vanilla gen to allow other mods to modify mineshafts
        ) {
            event.setNewGen(new MapGenBetterMineshaft());
        }
    }
}
