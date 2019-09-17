package com.yungnickyoung.minecraft.bettercaves;

import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import net.minecraft.world.IWorld;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod.EventBusSubscriber(modid = Settings.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ForgeEventSubscriber {

    private static final Logger LOGGER = LogManager.getLogger(Settings.MOD_ID + " Forge Event Subscriber");

    /**
     * Initializes Better Caves cave generator with the world seed upon world start
     * @param event CreateSpawnPosition event, called right when a world is loaded
     */
    @SubscribeEvent
    public static void onWorldTickEvent(final WorldEvent.CreateSpawnPosition event) {
        IWorld world = event.getWorld();
        LOGGER.info("--> Initializing Better Caves carver with seed: " + world.getSeed());

        // Initialize Better Caves with world seed
        ModEventSubscriber.BETTER_CAVE.initialize(world);
    }
}
