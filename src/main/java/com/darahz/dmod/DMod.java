package com.darahz.dmod;

import com.darahz.dmod.proxy.CommonProxy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import org.apache.logging.log4j.Logger;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION)
public class DMod {

    @Mod.Instance(Reference.MODID)
    public static DMod instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.SERVER_PROXY)
    public static CommonProxy proxy;

    private static Logger logger;

    public static Logger logger() {
        return logger;
    }

    /** Register blocks, items, and config here. */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit(event);
    }

    /** Register recipes, world gen, and event handlers here. */
    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    /** Anything that depends on other mods being loaded goes here. */
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
