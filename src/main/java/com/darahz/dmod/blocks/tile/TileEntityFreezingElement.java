package com.darahz.dmod.blocks.tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Once a cycle: tops itself with snow, drips a snowball into a container above,
 * and turns a nearby liquid source into ice or obsidian.
 *
 * <p>The 1.15 original kept its countdown and range in {@code static} fields,
 * so every freezing element in the world shared one timer and fought over it.
 * Both are per-instance here.
 */
public class TileEntityFreezingElement extends TileEntity {

    private static final int CYCLE_TICKS = 120;
    /** Radius searched for liquids to freeze. */
    private static final int FREEZE_RANGE = 5;
    /** Chance per cycle that a freeze attempt happens at all. */
    private static final float FREEZE_CHANCE = 0.05F;

    private int tickDown = CYCLE_TICKS;

    @Override
    public void updateEntity() {
        if (worldObj == null) {
            return;
        }

        if (worldObj.isRemote) {
            spawnParticles();
            return;
        }

        if (--tickDown > 0) {
            return;
        }
        tickDown = CYCLE_TICKS;

        placeSnowOnTop();
        dripSnowballIntoContainer();

        if (worldObj.rand.nextFloat() < FREEZE_CHANCE) {
            freezeOneLiquid();
        }
    }

    private void spawnParticles() {
        final double px = xCoord + worldObj.rand.nextFloat();
        final double py = yCoord + 1.2D;
        final double pz = zCoord + worldObj.rand.nextFloat();

        if (worldObj.isAirBlock(xCoord, yCoord + 1, zCoord)) {
            worldObj.spawnParticle("snowshovel", px, py, pz, 0.0D, 0.0D, 0.0D);
        } else if (worldObj.getBlock(xCoord, yCoord + 1, zCoord) == Blocks.water) {
            worldObj.spawnParticle("bubble", px, py, pz, 0.0D, 0.0D, 0.0D);
        }
    }

    /** Lays a snow layer on top when there is nothing else there. */
    private void placeSnowOnTop() {
        if (worldObj.isAirBlock(xCoord, yCoord + 1, zCoord)) {
            worldObj.setBlock(xCoord, yCoord + 1, zCoord, Blocks.snow_layer);
            worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 1.0D, zCoord + 0.5D,
                    "step.snow", 1.0F, 1.0F);
        }
    }

    /** Adds one snowball to an inventory sitting directly on top. */
    private void dripSnowballIntoContainer() {
        final TileEntity above = worldObj.getTileEntity(xCoord, yCoord + 1, zCoord);
        if (!(above instanceof IInventory)) {
            return;
        }
        final IInventory inventory = (IInventory) above;

        // Top up an existing partial stack first, then fall back to an empty slot.
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            final ItemStack stack = inventory.getStackInSlot(slot);
            if (stack != null
                    && stack.getItem() == Items.snowball
                    && stack.stackSize < stack.getMaxStackSize()
                    && stack.stackSize < inventory.getInventoryStackLimit()) {
                stack.stackSize++;
                inventory.markDirty();
                return;
            }
        }
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            if (inventory.getStackInSlot(slot) == null) {
                final ItemStack snowball = new ItemStack(Items.snowball, 1);
                if (inventory.isItemValidForSlot(slot, snowball)) {
                    inventory.setInventorySlotContents(slot, snowball);
                    inventory.markDirty();
                }
                return;
            }
        }
    }

    /** Converts one random water/lava source block in range. */
    private void freezeOneLiquid() {
        final List<int[]> candidates = new ArrayList<int[]>();

        for (int dx = -FREEZE_RANGE; dx <= FREEZE_RANGE; dx++) {
            for (int dy = -FREEZE_RANGE; dy <= 1; dy++) {
                for (int dz = -FREEZE_RANGE; dz <= FREEZE_RANGE; dz++) {
                    final int x = xCoord + dx;
                    final int y = yCoord + dy;
                    final int z = zCoord + dz;
                    if (!worldObj.blockExists(x, y, z)) {
                        continue;
                    }
                    // Metadata 0 means a full source block rather than flow.
                    if (worldObj.getBlockMetadata(x, y, z) != 0) {
                        continue;
                    }
                    final Block block = worldObj.getBlock(x, y, z);
                    if (block == Blocks.water || block == Blocks.lava) {
                        candidates.add(new int[] { x, y, z });
                    }
                }
            }
        }

        if (candidates.isEmpty()) {
            return;
        }

        Collections.shuffle(candidates, worldObj.rand);
        final int[] pos = candidates.get(0);
        final Block block = worldObj.getBlock(pos[0], pos[1], pos[2]);
        worldObj.setBlock(pos[0], pos[1], pos[2],
                block == Blocks.water ? Blocks.ice : Blocks.obsidian);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        tickDown = nbt.hasKey("TickDown") ? nbt.getInteger("TickDown") : CYCLE_TICKS;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("TickDown", tickDown);
    }

    /** Particles need to keep rendering while the chunk is loaded. */
    @Override
    public boolean canUpdate() {
        return true;
    }
}
