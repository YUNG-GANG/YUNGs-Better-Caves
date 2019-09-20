package com.yungnickyoung.minecraft.bettercaves;

import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod.EventBusSubscriber(modid = Settings.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ForgeEventSubscriber {

    private static final Logger LOGGER = LogManager.getLogger(Settings.MOD_ID + " Forge Event Subscriber");
    private static boolean flag = false;

    @SubscribeEvent
    public void onWorldTickEvent(final RegisterDimensionsEvent event) {
        if (flag) return;
        flag = true;
        event.getEntry(ResourceLocation.create("dimension_type:OverworldDimension", ';'));

        IWorld world = event.getWorld();
//        if (world.isRemote()) return;
        LOGGER.info(event);
        LOGGER.info("--> Initializing Better Caves carver with seed: " + world.getSeed());

        // Initialize Better Caves with world seed
        BetterCaves.BETTER_CAVE.initialize(world);
    }
}
