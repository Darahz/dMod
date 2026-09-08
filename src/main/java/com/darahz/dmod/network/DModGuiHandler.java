package com.darahz.dmod.network;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class DModGuiHandler implements IGuiHandler {

    public static final int REMOTE_WORKBENCH = 0;

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == REMOTE_WORKBENCH) {
            return new ContainerRemoteWorkbench(player.inventory, world, x, y, z);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == REMOTE_WORKBENCH) {
            return new GuiCrafting(player.inventory, world, x, y, z);
        }
        return null;
    }
}
