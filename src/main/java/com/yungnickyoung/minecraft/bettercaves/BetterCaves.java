package com.yungnickyoung.minecraft.bettercaves;

import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
@Mod(modid = BetterCaves.MODID, name = BetterCaves.NAME, version = BetterCaves.VERSION, useMetadata = true)
public class BetterCaves {
    // MOD INFORMATION CONSTANTS
    // These should not actually be used since 'useMetaData' is true.
    // See resources/mcmod.info for actual used information.
    public static final String MODID = "bettercaves";
    public static final String NAME = "Better Caves";
    public static final String VERSION = "0.0.1";

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = LogManager.getLogger();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some example code
        logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }
}