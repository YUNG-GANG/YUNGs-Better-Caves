package com.yungnickyoung.minecraft.bettercaves.proxy;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.event.EventConfigReload;
import com.yungnickyoung.minecraft.bettercaves.event.EventFlattenBedrock;
import com.yungnickyoung.minecraft.bettercaves.event.EventBetterCaveGen;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Proxy for client-only code.
 */
public class ClientProxy implements IProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new EventConfigReload());
    }

    @Override
    public void init(FMLInitializationEvent event) {

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
    }

    @Override
    public EntityPlayer getPlayerEntityFromContext(MessageContext ctx) {
        return (ctx.side.isClient() ? Minecraft.getMinecraft().player : BetterCaves.proxy.getPlayerEntityFromContext(ctx));
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        // This will never get called on client side
    }
}
