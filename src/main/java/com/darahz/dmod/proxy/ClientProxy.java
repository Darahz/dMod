package com.darahz.dmod.proxy;

import com.darahz.dmod.client.ReprogrammerHud;
import com.darahz.dmod.events.ReprogrammerInputHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

/** Client-only code -- renderers, HUD overlays, input handling. */
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        final ReprogrammerHud hud = new ReprogrammerHud();
        MinecraftForge.EVENT_BUS.register(hud);
        MinecraftForge.EVENT_BUS.register(new ReprogrammerInputHandler(hud));
        FMLCommonHandler.instance().bus().register(hud);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }
}
