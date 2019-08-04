package com.yungnickyoung.minecraft.bettercaves.proxy;

import com.yungnickyoung.minecraft.bettercaves.event.EventGeneral;
import com.yungnickyoung.minecraft.bettercaves.event.EventTerrain;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Proxy for common code (client or server)
 */
public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        // Register event handlers
        MinecraftForge.TERRAIN_GEN_BUS.register(new EventTerrain());
        MinecraftForge.EVENT_BUS.register(new EventGeneral());
    }
}
