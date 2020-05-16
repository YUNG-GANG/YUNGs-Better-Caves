package com.yungnickyoung.minecraft.bettercaves;

// Better Caves
import com.yungnickyoung.minecraft.bettercaves.config.BCSettings;
import com.yungnickyoung.minecraft.bettercaves.event.EventBetterCaveGen;
import com.yungnickyoung.minecraft.bettercaves.proxy.IProxy;

// Minecraft Forge API
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Entry point for Better Caves
 */
@Mod(modid = BCSettings.MOD_ID, name = BCSettings.NAME, version = BCSettings.VERSION, useMetadata = BCSettings.USE_META_DATA, acceptableRemoteVersions = "*")
public class BetterCaves {
    public static final Logger LOGGER = LogManager.getLogger(BCSettings.MOD_ID);

    /** File referring to the overarching directory for custom dimension configs **/
    public static File customConfigDir;

    @SidedProxy(clientSide = BCSettings.CLIENT_PROXY, serverSide = BCSettings.SERVER_PROXY)
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
        proxy.preInit();

        // Create custom dimension config directory if doesn't already exist
        customConfigDir = new File(Loader.instance().getConfigDir(), BCSettings.CUSTOM_CONFIG_PATH);
        try {
            String filePath = customConfigDir.getCanonicalPath();
            if (customConfigDir.mkdir())
                BetterCaves.LOGGER.info("Creating directory for dimension-specific Better Caves configs at " + filePath);
        } catch (IOException e) {
            BetterCaves.LOGGER.warn("ERROR creating Better Caves config directory.");
        }
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
        MinecraftForge.TERRAIN_GEN_BUS.register(new EventBetterCaveGen()); // Replace vanilla cave generation
    }
}