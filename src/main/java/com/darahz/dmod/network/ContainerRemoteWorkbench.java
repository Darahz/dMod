package com.darahz.dmod.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.world.World;

/**
 * A normal crafting bench that does not require the player to stand next to it.
 *
 * <p>{@link ContainerWorkbench#canInteractWith} checks both the block under the
 * stored coordinates and an 8-block radius, which would slam the GUI shut the
 * instant it opened for a remote table.
 */
public class ContainerRemoteWorkbench extends ContainerWorkbench {

    public ContainerRemoteWorkbench(InventoryPlayer inventory, World world, int x, int y, int z) {
        super(inventory, world, x, y, z);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }
}
