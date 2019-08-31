package com.yungnickyoung.minecraft.bettercaves;

// Better Caves
import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.event.EventBetterCaveGen;
import com.yungnickyoung.minecraft.bettercaves.event.EventFlattenBedrock;
import com.yungnickyoung.minecraft.bettercaves.proxy.IProxy;

// Minecraft Forge API
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

/**
 * Entry point for Better Caves
 */
@Mod(modid = Settings.MOD_ID, name = Settings.NAME, version = Settings.VERSION, useMetadata = Settings.USE_META_DATA)
public class BetterCaves {
    @SidedProxy(clientSide = Settings.CLIENT_PROXY, serverSide = Settings.SERVER_PROXY)
    public static IProxy proxy;

    /**
     * Pre-Initialization FML Life Cycle event handling method which is automatically
     * called by Forge. Runs before anything else. Read your config, create blocks, items, etc, and
     * register them with the game registry.
     *
     * @param event the event
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    /**
     * Initialization FML Life Cycle event handling method which is automatically
     * called by Forge. Build data structures needed, register network handlers and
     * world generators, etc.
     *
     * @param event the event
     */
    @EventHandler
    // Perform mod setup.
    // Build whatever data structures are needed, register network handlers, etc.
    public void init(FMLInitializationEvent event) {
        // Register world generation events
        MinecraftForge.EVENT_BUS.register(new EventFlattenBedrock()); // Flatten bedrock
        MinecraftForge.TERRAIN_GEN_BUS.register(new EventBetterCaveGen()); // Replace vanilla cave generation
        proxy.init(event);
    }

    /**
     * Post-Initialization FML Life Cycle event handling method which is automatically
     * called by Forge. Handle interaction with other mods; complete setup based on this.
     *
     * @param event the event
     */
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }


    /**
     * Server Starting FML Life Cycle event handling method which is automatically
     * called by Forge.
     *
     * @param event the event
     */
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }
}