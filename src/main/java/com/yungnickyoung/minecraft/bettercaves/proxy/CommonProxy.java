package com.yungnickyoung.minecraft.bettercaves.proxy;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.event.EventCave;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Proxy for common code (client or server)
 */
public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        // Register cave generation event listener
        MinecraftForge.TERRAIN_GEN_BUS.register(new EventCave());
    }
}
