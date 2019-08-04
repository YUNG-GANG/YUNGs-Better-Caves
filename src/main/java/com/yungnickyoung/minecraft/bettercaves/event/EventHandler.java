package com.yungnickyoung.minecraft.bettercaves.event;

import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.world.BetterCaveGenerator;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Event handler for replacing default caves with Better Caves upon generation.
 */
public class EventHandler {
    /**
     * Replaces cave gen events with Better Caves cave gen
     * @param event Map generation event
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onCaveEvent(InitMapGenEvent event) {
        Settings.LOGGER.info("onCaveEvent called");
        // Only replace cave gen if the original gen passed isn't a Better Cave
        if (event.getType() == InitMapGenEvent.EventType.CAVE && !event.getOriginalGen().getClass().equals(BetterCaveGenerator.class)) {
            Settings.LOGGER.info("  onCaveEvent passed conditions");
            event.setNewGen(new BetterCaveGenerator());
        }
    }

    /**
     * Keeps config settings synchronized
     * @param event Config change event
     */
    @SubscribeEvent
    public static void onConfigReload(ConfigChangedEvent.OnConfigChangedEvent event) {
        // Only mess with config syncing if it is this mod being changed
        if (Settings.MOD_ID.equals(event.getModID()))
            ConfigManager.sync(Settings.MOD_ID, Config.Type.INSTANCE);
    }
}