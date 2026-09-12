package com.darahz.dmod.blocks.tile;

import com.darahz.dmod.dimension.VoidDimension;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/** A small output buffer; right-click the block to collect its fragments. */
public class TileEntityVoidCondenser extends TileEntity {
    public static final int CAPACITY = 64;
    public static final int TICKS_PER_FRAGMENT = 200;
    private int stored;
    private int progress;

    @Override
    public void updateEntity() {
        if (worldObj == null || worldObj.isRemote
                || worldObj.provider.dimensionId != VoidDimension.dimensionId || stored >= CAPACITY) {
            return;
        }
        if (++progress >= TICKS_PER_FRAGMENT) {
            progress = 0;
            stored++;
        }
        // Persist progress as well as output when the chunk is saved/unloaded.
        markDirty();
    }

    public int getStored() { return stored; }
    public int getProgress() { return progress; }

    public void removeFragments(int amount) {
        if (amount > 0) {
            stored = Math.max(0, stored - amount);
            markDirty();
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("Stored", stored);
        tag.setInteger("Progress", progress);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        stored = Math.max(0, Math.min(CAPACITY, tag.getInteger("Stored")));
        progress = Math.max(0, Math.min(TICKS_PER_FRAGMENT - 1, tag.getInteger("Progress")));
    }
}
