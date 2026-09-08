package com.darahz.dmod;

import com.darahz.dmod.init.ModBlocks;
import com.darahz.dmod.init.ModItems;
import com.darahz.dmod.init.ModRecipes;
import com.darahz.dmod.network.DModNetwork;
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

    public static void info(String msg) {
        if (logger != null) {
            logger.info(msg);
        }
    }

    /** Shows up in logs/fml-*-latest.log in a dev run; silent in production. */
    public static void debug(String msg) {
        if (logger != null) {
            logger.debug(msg);
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        ModItems.register();
        ModBlocks.register();
        DModNetwork.init();
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ModRecipes.register();
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
