package com.darahz.dmod.blocks.tile;

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;

/**
 * Kills hostile mobs in range on a timer.
 *
 * <p>The 1.15 original held its three upgrade counters in {@code static}
 * fields, so upgrading one block upgraded every one in the world. They are
 * per-instance and saved to NBT here.
 *
 * <p>Upgrade materials are an inference: {@code addUpgrade(ItemStack)} existed
 * in the original but had an empty body, and the mod's notes file only says
 * "Add items from Iron, redstone, lapis, gold and diamond to upgrade speed
 * etc." The mapping below is therefore new, not ported.
 */
public class TileEntityMobSlaughterHouse extends TileEntity {

    private static final int BASE_CYCLE_TICKS = 120;
    private static final int MAX_LEVEL = 4;
    /** Vertical reach, which upgrades do not change. */
    private static final int HEIGHT = 5;

    private int tickDown = BASE_CYCLE_TICKS;
    private int speedLevel = 1;
    private int amountLevel = 1;
    private int rangeLevel = 1;

    public static boolean isUpgrade(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        return stack.getItem() == Items.redstone
                || stack.getItem() == Items.gold_ingot
                || stack.getItem() == Items.iron_ingot
                || stack.getItem() == Items.diamond;
    }

    /** @return true when the material was accepted and should be consumed. */
    public boolean addUpgrade(ItemStack stack, EntityPlayer player) {
        if (stack == null) {
            return false;
        }

        final String upgraded;
        if (stack.getItem() == Items.redstone && speedLevel < MAX_LEVEL) {
            speedLevel++;
            upgraded = "Speed " + speedLevel;
        } else if (stack.getItem() == Items.gold_ingot && amountLevel < MAX_LEVEL) {
            amountLevel++;
            upgraded = "Targets " + amountLevel;
        } else if (stack.getItem() == Items.iron_ingot && rangeLevel < MAX_LEVEL) {
            rangeLevel++;
            upgraded = "Range " + rangeLevel;
        } else if (stack.getItem() == Items.diamond
                && (speedLevel < MAX_LEVEL || amountLevel < MAX_LEVEL || rangeLevel < MAX_LEVEL)) {
            speedLevel = Math.min(MAX_LEVEL, speedLevel + 1);
            amountLevel = Math.min(MAX_LEVEL, amountLevel + 1);
            rangeLevel = Math.min(MAX_LEVEL, rangeLevel + 1);
            upgraded = "Everything";
        } else {
            return false;
        }

        markDirty();
        player.addChatComponentMessage(new ChatComponentText(
                EnumChatFormatting.AQUA + "Slaughter house upgraded: " + upgraded));
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void updateEntity() {
        if (worldObj == null || worldObj.isRemote) {
            return;
        }

        if (--tickDown > 0) {
            return;
        }
        tickDown = Math.max(1, BASE_CYCLE_TICKS / speedLevel);

        final AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
                xCoord - rangeLevel, yCoord - HEIGHT, zCoord - rangeLevel,
                xCoord + rangeLevel + 1, yCoord + HEIGHT + 1, zCoord + rangeLevel + 1);

        final List<EntityLiving> mobs = worldObj.getEntitiesWithinAABB(EntityLiving.class, box);
        if (mobs.isEmpty()) {
            return;
        }

        int killed = 0;
        for (final EntityLiving mob : mobs) {
            if (killed >= amountLevel) {
                break;
            }
            if (mob.isDead) {
                continue;
            }
            mob.attackEntityFrom(DamageSource.generic, mob.getHealth());
            killed++;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        tickDown = nbt.hasKey("TickDown") ? nbt.getInteger("TickDown") : BASE_CYCLE_TICKS;
        speedLevel = clampLevel(nbt.getInteger("SpeedLevel"));
        amountLevel = clampLevel(nbt.getInteger("AmountLevel"));
        rangeLevel = clampLevel(nbt.getInteger("RangeLevel"));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("TickDown", tickDown);
        nbt.setInteger("SpeedLevel", speedLevel);
        nbt.setInteger("AmountLevel", amountLevel);
        nbt.setInteger("RangeLevel", rangeLevel);
    }

    /** Levels start at 1; a missing tag reads as 0 and must not disable the block. */
    private static int clampLevel(int value) {
        if (value < 1) {
            return 1;
        }
        return Math.min(MAX_LEVEL, value);
    }
}
