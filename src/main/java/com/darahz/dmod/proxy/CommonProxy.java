package com.darahz.dmod.proxy;

import com.darahz.dmod.events.DroppedTearHandler;
import com.darahz.dmod.events.VoidSatchelPickupHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

/** Runs on both the client and the dedicated server. */
public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {}

    public void init(FMLInitializationEvent event) {
        // One instance on both buses: EntityJoinWorldEvent is a Forge event
        // while TickEvent.WorldTickEvent lives on the FML bus, and the handler
        // needs to see both.
        final DroppedTearHandler tears = new DroppedTearHandler();
        MinecraftForge.EVENT_BUS.register(tears);
        FMLCommonHandler.instance().bus().register(tears);

        // EntityItemPickupEvent is a Forge event only.
        MinecraftForge.EVENT_BUS.register(new VoidSatchelPickupHandler());
    }

    public void postInit(FMLPostInitializationEvent event) {}
}
