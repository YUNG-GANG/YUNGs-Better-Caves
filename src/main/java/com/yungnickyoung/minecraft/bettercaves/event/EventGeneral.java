package com.yungnickyoung.minecraft.bettercaves.event;

import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Handlers for events registered to the {@code EVENT_BUS}.
 */
public class EventGeneral {
    /**
     * Keeps Better Caves config settings synchronized
     * @param event Config change event
     */
    @SubscribeEvent
    public void onConfigReload(ConfigChangedEvent.OnConfigChangedEvent event) {
        // Only mess with config syncing if it is this mod being changed
        if (Settings.MOD_ID.equals(event.getModID()))
            ConfigManager.sync(Settings.MOD_ID, Config.Type.INSTANCE);
    }
}
