package com.darahz.dmod.items;

import java.util.List;

import com.darahz.dmod.DMod;
import com.darahz.dmod.DModTab;
import com.darahz.dmod.Reference;
import com.darahz.dmod.helpers.KeyboardHelper;
import com.darahz.dmod.inventory.InventoryVoidSatchel;
import com.darahz.dmod.network.DModGuiHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * A five-slot pouch. Whatever you put in a slot becomes a filter: picking that
 * item up tops the slot up to a full stack, and everything past that is
 * destroyed rather than clogging your inventory.
 */
public class ItemVoidSatchel extends Item {

    public ItemVoidSatchel() {
        setUnlocalizedName("dmod.voidsatchel");
        setTextureName(Reference.RES + "voidsatchel");
        setCreativeTab(DModTab.INSTANCE);
        setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            // The inventory slot index is passed as x so the container knows
            // which stack it is editing.
            DMod.debug("VoidSatchel: opening GUI for slot " + player.inventory.currentItem);
            player.openGui(DMod.instance, DModGuiHandler.VOID_SATCHEL, world,
                    player.inventory.currentItem, 0, 0);
        }
        return stack;
    }

    @Override
    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        final InventoryVoidSatchel inventory = new InventoryVoidSatchel(stack);

        boolean empty = true;
        for (int i = 0; i < InventoryVoidSatchel.SIZE; i++) {
            final ItemStack held = inventory.getStackInSlot(i);
            if (held == null) {
                continue;
            }
            empty = false;
            final int limit = Math.min(inventory.getInventoryStackLimit(), held.getMaxStackSize());
            final boolean full = held.stackSize >= limit;
            tooltip.add((full ? EnumChatFormatting.RED : EnumChatFormatting.GRAY) + "  "
                    + StatCollector.translateToLocal(held.getUnlocalizedName() + ".name")
                    + EnumChatFormatting.DARK_GRAY + " " + held.stackSize + "/" + limit
                    + (full ? EnumChatFormatting.RED + " (voiding)" : ""));
        }

        if (empty) {
            tooltip.add(EnumChatFormatting.DARK_GRAY + "  Empty");
        }

        if (KeyboardHelper.isHoldingShift()) {
            tooltip.add("Right click to open.");
            tooltip.add("An item placed in a slot becomes that slot's filter.");
            tooltip.add("Picked-up matches fill the slot to one stack,");
            tooltip.add(EnumChatFormatting.RED + "then any further pickups are destroyed.");
        } else {
            tooltip.add(EnumChatFormatting.GREEN + "Hold shift for more info.");
        }
    }
}
