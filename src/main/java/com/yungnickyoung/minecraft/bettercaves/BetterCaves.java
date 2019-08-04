package com.yungnickyoung.minecraft.bettercaves;

// Better Caves
import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.proxy.CommonProxy;

// Minecraft Forge API
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Entry point for Better Caves
 */
@Mod(modid = Settings.MOD_ID, name = Settings.NAME, version = Settings.VERSION, useMetadata = Settings.USE_META_DATA)
public class BetterCaves {
    @SidedProxy(clientSide = Settings.CLIENT_PROXY, serverSide = Settings.COMMON_PROXY)
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Settings.LOGGER.info("main pre-init");
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        Settings.LOGGER.info("main init");
    }
}