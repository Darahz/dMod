package com.darahz.dmod.items;

import java.util.List;

import com.darahz.dmod.DModTab;
import com.darahz.dmod.Reference;
import com.darahz.dmod.helpers.KeyboardHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

/** Drags loose items towards the holder while switched on. */
public class ItemAttractionDevice extends Item {

    private static final String TAG_ENABLED = "enabled";
    /** Squared distance within which items get pulled. */
    private static final double PULL_RANGE_SQ = 64.0D;
    /** Squared distance within which items get picked up. */
    private static final double GRAB_RANGE_SQ = 3.0D;

    public ItemAttractionDevice() {
        setUnlocalizedName("dmod.itemattractiondevice");
        setTextureName(Reference.RES + "itemattractiondevice");
        setCreativeTab(DModTab.INSTANCE);
        setMaxStackSize(1);
    }

    private static boolean isEnabled(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().getBoolean(TAG_ENABLED);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return isEnabled(stack);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        // Sneak-use toggles. Checked via the sneak flag rather than the raw
        // keyboard so it works on a dedicated server too.
        if (player.isSneaking()) {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt == null) {
                nbt = new NBTTagCompound();
                stack.setTagCompound(nbt);
            }
            nbt.setBoolean(TAG_ENABLED, !nbt.getBoolean(TAG_ENABLED));
        }
        return stack;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onUpdate(ItemStack stack, World world, Entity holder, int slot, boolean isSelected) {
        if (!(holder instanceof EntityPlayer) || !isEnabled(stack)) {
            return;
        }
        final EntityPlayer player = (EntityPlayer) holder;

        final AxisAlignedBB box = player.boundingBox.expand(10.0D, 10.0D, 10.0D);
        final List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, box);

        for (final EntityItem item : items) {
            if (item.isDead) {
                continue;
            }
            final double dx = player.posX - item.posX;
            final double dy = player.posY + player.getEyeHeight() / 2.0D - item.posY;
            final double dz = player.posZ - item.posZ;
            final double distSq = dx * dx + dy * dy + dz * dz;

            if (distSq < PULL_RANGE_SQ) {
                final double len = Math.sqrt(distSq);
                if (len > 1.0E-4D) {
                    item.motionX += (dx / len) * 0.2D;
                    item.motionY += (dy / len) * 0.2D;
                    item.motionZ += (dz / len) * 0.2D;
                }
                item.delayBeforeCanPickup = 0;

                if (world.isRemote) {
                    world.spawnParticle("portal", item.posX, item.posY, item.posZ,
                            world.rand.nextDouble() - 0.5D, 0.0D, world.rand.nextDouble() - 0.5D);
                }
            }

            if (!world.isRemote && distSq < GRAB_RANGE_SQ) {
                if (player.inventory.addItemStackToInventory(item.getEntityItem())) {
                    item.setDead();
                    world.playSoundAtEntity(player, "random.pop", 0.2F, 1.8F);
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add(isEnabled(stack)
                ? EnumChatFormatting.GREEN + "Active"
                : EnumChatFormatting.RED + "Inactive");
        if (KeyboardHelper.isHoldingShift()) {
            tooltip.add("Sneak + right click to toggle.");
            tooltip.add("Steve: " + EnumChatFormatting.LIGHT_PURPLE
                    + "\"I don't see how an Apple with a String can attract stuff?\"");
        } else {
            tooltip.add(EnumChatFormatting.GREEN + "Hold shift for more info.");
        }
    }
}
