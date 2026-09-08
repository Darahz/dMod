package com.darahz.dmod.network;

import com.darahz.dmod.DMod;
import com.darahz.dmod.Reference;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public final class DModNetwork {

    public static SimpleNetworkWrapper channel;

    public static void init() {
        channel = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);
        channel.registerMessage(MessageReprogrammerAdjust.Handler.class,
                MessageReprogrammerAdjust.class, 0, Side.SERVER);

        NetworkRegistry.INSTANCE.registerGuiHandler(DMod.instance, new DModGuiHandler());
    }

    private DModNetwork() {}
}
