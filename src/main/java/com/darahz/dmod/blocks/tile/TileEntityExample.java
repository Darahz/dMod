package com.darahz.dmod.blocks.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/** Each placed example block owns its own counter, saved with the chunk. */
public class TileEntityExample extends TileEntity {
    private int clicks;

    public int getClicks() {
        return clicks;
    }

    public void incrementClicks() {
        if (clicks < Integer.MAX_VALUE) {
            clicks++;
            markDirty();
        }
    }

    @Override
    public boolean canUpdate() {
        // This tile entity is interaction-driven and needs no per-tick work.
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        clicks = Math.max(0, tag.getInteger("Clicks"));
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("Clicks", clicks);
    }
}
