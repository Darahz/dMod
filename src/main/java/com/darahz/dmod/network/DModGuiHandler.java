package com.darahz.dmod.network;

import com.darahz.dmod.DMod;
import com.darahz.dmod.client.GuiVoidSatchel;
import com.darahz.dmod.inventory.ContainerVoidSatchel;
import com.darahz.dmod.inventory.InventoryVoidSatchel;
import com.darahz.dmod.items.ItemVoidSatchel;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DModGuiHandler implements IGuiHandler {

    public static final int REMOTE_WORKBENCH = 0;
    /** For the satchel, x carries the player inventory slot holding it. */
    public static final int VOID_SATCHEL = 1;

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == REMOTE_WORKBENCH) {
            return new ContainerRemoteWorkbench(player.inventory, world, x, y, z);
        }
        if (id == VOID_SATCHEL) {
            final ItemStack satchel = satchelAt(player, x);
            DMod.debug("VoidSatchel: server element for slot " + x
                    + (satchel == null ? " -> NOT FOUND" : " -> ok"));
            return satchel == null ? null
                    : new ContainerVoidSatchel(player.inventory, new InventoryVoidSatchel(satchel), x);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == REMOTE_WORKBENCH) {
            return new GuiCrafting(player.inventory, world, x, y, z);
        }
        if (id == VOID_SATCHEL) {
            final ItemStack satchel = satchelAt(player, x);
            return satchel == null ? null
                    : new GuiVoidSatchel(player.inventory, new InventoryVoidSatchel(satchel), x);
        }
        return null;
    }

    private static ItemStack satchelAt(EntityPlayer player, int inventorySlot) {
        if (inventorySlot < 0 || inventorySlot >= player.inventory.mainInventory.length) {
            return null;
        }
        final ItemStack stack = player.inventory.getStackInSlot(inventorySlot);
        return stack != null && stack.getItem() instanceof ItemVoidSatchel ? stack : null;
    }
}
