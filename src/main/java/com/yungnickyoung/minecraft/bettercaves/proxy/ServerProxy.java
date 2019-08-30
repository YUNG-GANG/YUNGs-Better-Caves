package com.yungnickyoung.minecraft.bettercaves.proxy;

import com.yungnickyoung.minecraft.bettercaves.event.EventBetterCaveGen;
import com.yungnickyoung.minecraft.bettercaves.event.EventFlattenBedrock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Proxy for server code
 */
public class ServerProxy implements IProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
    }

    @Override
    public void init(FMLInitializationEvent event) {


    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        // register server commands here
    }

    @Override
    public EntityPlayer getPlayerEntityFromContext(MessageContext ctx) {
        return ctx.getServerHandler().player;
    }
}
