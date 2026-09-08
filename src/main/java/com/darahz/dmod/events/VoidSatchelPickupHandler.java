package com.darahz.dmod.events;

import com.darahz.dmod.inventory.ContainerVoidSatchel;
import com.darahz.dmod.inventory.InventoryVoidSatchel;
import com.darahz.dmod.items.ItemVoidSatchel;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

/**
 * Routes picked-up items into a Void Satchel's matching filter slot, and
 * destroys whatever will not fit.
 */
public class VoidSatchelPickupHandler {

    @SubscribeEvent
    public void onItemPickup(EntityItemPickupEvent event) {
        final EntityPlayer player = event.entityPlayer;
        if (player == null || player.worldObj.isRemote) {
            return;
        }

        final ItemStack incoming = event.item.getEntityItem();
        if (incoming == null || incoming.stackSize <= 0) {
            return;
        }
        // A satchel must never eat a satchel.
        if (incoming.getItem() instanceof ItemVoidSatchel) {
            return;
        }

        for (final ItemStack candidate : player.inventory.mainInventory) {
            if (candidate == null || !(candidate.getItem() instanceof ItemVoidSatchel)) {
                continue;
            }
            // Don't write into a satchel the player currently has open, or the
            // GUI's in-memory copy would overwrite what we just stored.
            if (isOpen(player, candidate)) {
                continue;
            }
            if (absorb(candidate, incoming)) {
                // Result.ALLOW makes vanilla play the pickup sound and kill the
                // entity; it only calls setDead when the stack is emptied, which
                // absorb() has already done.
                event.setResult(Result.ALLOW);
                return;
            }
        }
    }

    private static boolean isOpen(EntityPlayer player, ItemStack satchel) {
        return player.openContainer instanceof ContainerVoidSatchel
                && ((ContainerVoidSatchel) player.openContainer).getContainerStack() == satchel;
    }

    /**
     * @return true when the satchel claimed the pickup, in which case
     *         {@code incoming} has been emptied.
     */
    private static boolean absorb(ItemStack satchel, ItemStack incoming) {
        final InventoryVoidSatchel inventory = new InventoryVoidSatchel(satchel);

        for (int i = 0; i < InventoryVoidSatchel.SIZE; i++) {
            final ItemStack filter = inventory.getStackInSlot(i);
            if (!matches(filter, incoming)) {
                continue;
            }

            final int limit = Math.min(inventory.getInventoryStackLimit(), filter.getMaxStackSize());
            final int room = limit - filter.stackSize;
            if (room > 0) {
                final int moved = Math.min(room, incoming.stackSize);
                filter.stackSize += moved;
                incoming.stackSize -= moved;
            }

            // Whatever is left over is destroyed -- that is the whole point.
            incoming.stackSize = 0;
            inventory.markDirty();
            return true;
        }
        return false;
    }

    /** Filters match on item and damage; stack size and NBT are ignored. */
    private static boolean matches(ItemStack filter, ItemStack incoming) {
        return filter != null
                && filter.getItem() == incoming.getItem()
                && filter.getItemDamage() == incoming.getItemDamage();
    }
}
