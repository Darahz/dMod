package com.darahz.dmod.items;

import java.util.List;

import com.darahz.dmod.DModTab;
import com.darahz.dmod.Reference;
import com.darahz.dmod.helpers.KeyboardHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

/**
 * Banks one charge per second spent in the inventory, then spends those charges
 * repairing damaged gear a point at a time while switched on.
 */
public class ItemNecklaceOfRepair extends Item {

    private static final String TAG_ENABLED = "enabled";
    private static final String TAG_CHARGE = "inInventory";

    public ItemNecklaceOfRepair() {
        setUnlocalizedName("dmod.necklaceofrepair");
        setTextureName(Reference.RES + "necklaceofrepair");
        setCreativeTab(DModTab.INSTANCE);
        setMaxStackSize(1);
    }

    private static NBTTagCompound data(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        return nbt;
    }

    private static boolean isEnabled(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().getBoolean(TAG_ENABLED);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.rare;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return isEnabled(stack);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        // Sneak-use toggles, so the behaviour does not depend on polling the
        // keyboard from code that also runs server side.
        if (player.isSneaking()) {
            final NBTTagCompound nbt = data(stack);
            nbt.setBoolean(TAG_ENABLED, !nbt.getBoolean(TAG_ENABLED));
            player.playSound("random.orb", 0.5F, 1.0F);
        }
        return stack;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity holder, int slot, boolean isSelected) {
        if (world.isRemote || !(holder instanceof EntityPlayer)) {
            return;
        }
        final EntityPlayer player = (EntityPlayer) holder;
        // Everything below is once-per-second book-keeping.
        if (player.ticksExisted % 20 != 0) {
            return;
        }

        final NBTTagCompound nbt = data(stack);

        if (!isEnabled(stack)) {
            nbt.setLong(TAG_CHARGE, nbt.getLong(TAG_CHARGE) + 1L);
            return;
        }

        long charge = nbt.getLong(TAG_CHARGE);
        if (charge <= 0L) {
            return;
        }

        // Repair one point on the first damaged item found, so a single
        // second of charge is worth exactly one durability point.
        for (final ItemStack candidate : player.inventory.mainInventory) {
            // Empty slots are null in 1.7.10, not an "air" stack.
            if (candidate == null || candidate == stack) {
                continue;
            }
            if (!candidate.getItem().isDamageable() || candidate.getItemDamage() <= 0) {
                continue;
            }
            candidate.setItemDamage(candidate.getItemDamage() - 1);
            charge--;
            nbt.setLong(TAG_CHARGE, charge);
            break;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        final long charge = stack.hasTagCompound() ? stack.getTagCompound().getLong(TAG_CHARGE) : 0L;
        final long minutes = charge / 60L;
        final long seconds = charge % 60L;

        tooltip.add(isEnabled(stack)
                ? EnumChatFormatting.GREEN + "Active"
                : EnumChatFormatting.RED + "Inactive");
        tooltip.add(EnumChatFormatting.WHITE + "Charge: " + EnumChatFormatting.GOLD
                + minutes + ":" + String.format("%02d", seconds));

        if (KeyboardHelper.isHoldingShift()) {
            tooltip.add("Sneak + right click to toggle.");
            tooltip.add("Repairs damaged items in your inventory.");
            tooltip.add(EnumChatFormatting.GRAY + "Recharges while carried.");
            tooltip.add(EnumChatFormatting.GRAY + "One second charged = 1 durability.");
        } else {
            tooltip.add(EnumChatFormatting.GREEN + "Hold shift for more info.");
        }
    }
}
