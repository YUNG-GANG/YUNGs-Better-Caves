package com.yungnickyoung.minecraft.bettercaves.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Interface for specifynig proxy functionality.
 *
 * This code taken from Jabelar's proxy tutorial. Check out their blog at http://jabelarminecraft.blogspot.com/.
 */
public interface IProxy {
    /**
     * FML life cycle event for Pre-Initialization. Historically (before registry events)
     * this was where blocks, items, etc. were registered. There are still things
     * like entities and networking which should still be registered here.
     *
     * @param event the event
     */
    void preInit(FMLPreInitializationEvent event);

    /**
     * FML life cycle event for Initialization. This phase is good for registering event listeners,
     * for registering things that depend on things in pre-init from other mods (like
     * recipes, advancements and such.)
     *
     * @param event the event
     */
    void init(FMLInitializationEvent event);

    /**
     * FML life cycle event Post Initialization. This phase is useful for doing
     * inter-mod stuff like checking which mods are loaded or if you want a complete view of things across
     * mods like having a list of all registered items to aid random item generation.
     *
     * @param event the event
     */
    void postInit(FMLPostInitializationEvent event);

    /**
     * FML life cycle event. Server commands should be registered here.
     *
     * @param event the event
     */
    void serverStarting(FMLServerStartingEvent event);

    /**
     * Returns a side-appropriate EntityPlayer for use during message handling.
     *
     * @param parContext the context
     * @return the player entity from context
     */
    EntityPlayer getPlayerEntityFromContext(MessageContext parContext);
}
