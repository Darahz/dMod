package com.darahz.dmod.inventory;

import com.darahz.dmod.items.ItemVoidSatchel;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Container for a Void Satchel, laid out on vanilla's hopper GUI (five slots).
 *
 * <p>The satchel lives in a player inventory slot and the container edits that
 * stack's NBT directly, so the slot holding it is locked for as long as the GUI
 * is open. Letting the player move or drop it mid-edit would leave the container
 * writing into a stack that is no longer there.
 */
public class ContainerVoidSatchel extends Container {

    private static final int HOTBAR_SIZE = 9;
    private static final int PLAYER_MAIN_START = InventoryVoidSatchel.SIZE;
    private static final int PLAYER_HOTBAR_START = PLAYER_MAIN_START + 27;

    private final InventoryVoidSatchel satchel;
    /** Index into the player's inventory, 0-35. */
    private final int lockedInventorySlot;
    private final int lockedContainerSlot;

    public ContainerVoidSatchel(InventoryPlayer playerInv, InventoryVoidSatchel satchel, int lockedInventorySlot) {
        this.satchel = satchel;
        this.lockedInventorySlot = lockedInventorySlot;
        this.lockedContainerSlot = toContainerSlot(lockedInventorySlot);
        satchel.openInventory();

        // Five filter slots, matching the hopper texture.
        for (int i = 0; i < InventoryVoidSatchel.SIZE; i++) {
            addSlotToContainer(new FilteredSlot(satchel, i, 44 + i * 18, 20));
        }

        // Player inventory, three rows then the hotbar.
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlotToContainer(new Slot(playerInv,
                        col + row * 9 + 9, 8 + col * 18, row * 18 + 51));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlotToContainer(new Slot(playerInv, col, 8 + col * 18, 109));
        }
    }

    public ItemStack getContainerStack() {
        return satchel.getContainerStack();
    }

    private static int toContainerSlot(int inventorySlot) {
        if (inventorySlot < HOTBAR_SIZE) {
            return PLAYER_HOTBAR_START + inventorySlot;
        }
        return PLAYER_MAIN_START + (inventorySlot - HOTBAR_SIZE);
    }

    /**
     * Closes the GUI if the satchel leaves its slot by any route we missed.
     *
     * <p>EntityPlayer.onUpdate closes any container whose canInteractWith is
     * false, every tick. Comparing ItemStacks by reference identity here was
     * enough to shut the GUI within a tick of opening it -- which looks exactly
     * like the GUI never opening at all. Check the slot still holds a satchel
     * instead; the slot is locked below, so it cannot become a different one.
     */
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        final ItemStack current = player.inventory.getStackInSlot(lockedInventorySlot);
        return current != null && current.getItem() instanceof ItemVoidSatchel;
    }

    @Override
    public ItemStack slotClick(int slotId, int clickedButton, int mode, EntityPlayer player) {
        if (slotId == lockedContainerSlot) {
            return null;
        }
        // Mode 2 is a number-key swap with a hotbar slot.
        if (mode == 2 && clickedButton == lockedInventorySlot && lockedInventorySlot < HOTBAR_SIZE) {
            return null;
        }
        return super.slotClick(slotId, clickedButton, mode, player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        if (index == lockedContainerSlot) {
            return null;
        }

        final Slot slot = (Slot) inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) {
            return null;
        }

        final ItemStack stack = slot.getStack();
        final ItemStack copy = stack.copy();

        if (index < InventoryVoidSatchel.SIZE) {
            // Out of the satchel, into the player.
            if (!mergeItemStack(stack, PLAYER_MAIN_START, inventorySlots.size(), true)) {
                return null;
            }
        } else {
            // Into the satchel, setting or topping up a filter.
            // mergeItemStack puts into any empty slot without consulting
            // Slot.isItemValid, so the filter has to be enforced here or a
            // shift-click would nest a satchel inside itself.
            if (!satchel.isItemValidForSlot(0, stack)) {
                return null;
            }
            if (!mergeItemStack(stack, 0, InventoryVoidSatchel.SIZE, false)) {
                return null;
            }
        }

        if (stack.stackSize == 0) {
            slot.putStack(null);
        } else {
            slot.onSlotChanged();
        }
        return copy;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        satchel.closeInventory();
    }

    /** Slot.isItemValid ignores the backing inventory, so delegate explicitly. */
    private static class FilteredSlot extends Slot {

        FilteredSlot(InventoryVoidSatchel inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return inventory.isItemValidForSlot(getSlotIndex(), stack);
        }
    }
}
