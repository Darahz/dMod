package com.darahz.dmod.inventory;

import com.darahz.dmod.items.ItemVoidSatchel;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * The five filter slots of a Void Satchel, stored inside the satchel's own
 * ItemStack NBT rather than in a tile entity.
 *
 * <p>Each slot doubles as a filter and a buffer: whatever item sits in a slot
 * is the thing that slot captures, and it accumulates up to that item's max
 * stack size. Anything beyond that is destroyed on pickup.
 */
public class InventoryVoidSatchel implements IInventory {

    public static final int SIZE = 5;
    public static final String NAME_KEY = "container.dmod.voidsatchel";

    private static final String TAG_ITEMS = "Items";
    private static final String TAG_SLOT = "Slot";

    /** The satchel itself; all changes are written back into its NBT. */
    private final ItemStack container;
    private final ItemStack[] contents = new ItemStack[SIZE];

    public InventoryVoidSatchel(ItemStack container) {
        this.container = container;
        readFromContainer();
    }

    public ItemStack getContainerStack() {
        return container;
    }

    private void readFromContainer() {
        final NBTTagCompound nbt = container.getTagCompound();
        if (nbt == null || !nbt.hasKey(TAG_ITEMS, 9)) {
            return;
        }
        final NBTTagList list = nbt.getTagList(TAG_ITEMS, 10);
        for (int i = 0; i < list.tagCount(); i++) {
            final NBTTagCompound entry = list.getCompoundTagAt(i);
            final int slot = entry.getByte(TAG_SLOT) & 0xFF;
            if (slot < SIZE) {
                contents[slot] = ItemStack.loadItemStackFromNBT(entry);
            }
        }
    }

    private void writeToContainer() {
        final NBTTagList list = new NBTTagList();
        for (int i = 0; i < SIZE; i++) {
            if (contents[i] != null) {
                final NBTTagCompound entry = new NBTTagCompound();
                entry.setByte(TAG_SLOT, (byte) i);
                contents[i].writeToNBT(entry);
                list.appendTag(entry);
            }
        }
        NBTTagCompound nbt = container.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            container.setTagCompound(nbt);
        }
        nbt.setTag(TAG_ITEMS, list);
    }

    @Override
    public int getSizeInventory() {
        return SIZE;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot >= 0 && slot < SIZE ? contents[slot] : null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        final ItemStack stack = getStackInSlot(slot);
        if (stack == null) {
            return null;
        }
        if (stack.stackSize <= count) {
            contents[slot] = null;
            markDirty();
            return stack;
        }
        final ItemStack split = stack.splitStack(count);
        if (stack.stackSize == 0) {
            contents[slot] = null;
        }
        markDirty();
        return split;
    }

    /** Nothing is handed back on close; the satchel keeps its contents. */
    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (slot < 0 || slot >= SIZE) {
            return;
        }
        contents[slot] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
        }
        markDirty();
    }

    @Override
    public String getInventoryName() {
        return NAME_KEY;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        writeToContainer();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {
        markDirty();
    }

    /** A satchel inside a satchel would be a duplication bug waiting to happen. */
    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return stack == null || !(stack.getItem() instanceof ItemVoidSatchel);
    }
}
