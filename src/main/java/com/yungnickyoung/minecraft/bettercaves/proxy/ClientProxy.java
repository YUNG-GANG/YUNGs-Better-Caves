package com.yungnickyoung.minecraft.bettercaves.proxy;

import com.yungnickyoung.minecraft.bettercaves.event.EventConfigReload;
import net.minecraftforge.common.MinecraftForge;

/**
 * Proxy for client-only code.
 */
public class ClientProxy implements IProxy {
    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(new EventConfigReload());
    }
}
