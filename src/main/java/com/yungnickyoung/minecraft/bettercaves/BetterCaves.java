package com.yungnickyoung.minecraft.bettercaves;

// Better Caves
import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.proxy.CommonProxy;

// Minecraft API
import net.minecraft.init.Blocks;

// Minecraft Forge API
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Entry point for Better Caves
 */
@Mod(modid = Configuration.MOD_ID, name = Configuration.NAME, version = Configuration.VERSION, useMetadata = true)
public class BetterCaves {
    @SidedProxy(clientSide = Configuration.CLIENT_PROXY, serverSide = Configuration.COMMON_PROXY)
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Configuration.LOGGER.info("pre-init, diamond >> {}", Blocks.DIAMOND_BLOCK.getRegistryName());
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some example code
        Configuration.LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }
}