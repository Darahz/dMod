package com.darahz.dmod.items;

import java.util.List;

import com.darahz.dmod.DMod;
import com.darahz.dmod.DModTab;
import com.darahz.dmod.Reference;
import com.darahz.dmod.helpers.KeyboardHelper;
import com.darahz.dmod.network.DModGuiHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

/**
 * Binds to a crafting table, then opens that table from anywhere for the cost
 * of one experience point.
 */
public class ItemCraftingWidget extends Item {

    private static final String TAG_X = "tableX";
    private static final String TAG_Y = "tableY";
    private static final String TAG_Z = "tableZ";
    private static final String TAG_BOUND = "bound";

    public ItemCraftingWidget() {
        setUnlocalizedName("dmod.craftingwidget");
        setTextureName(Reference.RES + "craftingwidget");
        setCreativeTab(DModTab.INSTANCE);
        setMaxStackSize(1);
    }

    private static boolean isBound(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().getBoolean(TAG_BOUND);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return isBound(stack);
    }

    @Override
    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        if (isBound(stack)) {
            final NBTTagCompound nbt = stack.getTagCompound();
            tooltip.add("Bound to crafting table at : x." + nbt.getInteger(TAG_X)
                    + " y." + nbt.getInteger(TAG_Y)
                    + " z." + nbt.getInteger(TAG_Z));
            tooltip.add(EnumChatFormatting.GRAY + "Costs 1 XP per use.");
        } else if (KeyboardHelper.isHoldingShift()) {
            tooltip.add("Link to a crafting table anywhere in the world");
            tooltip.add("Gets reset if the crafting table is removed");
        } else {
            tooltip.add(EnumChatFormatting.GREEN + "Hold shift for more info.");
        }
    }

    /** Right clicking a crafting table binds the widget to it. */
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
            int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

        if (world.getBlock(x, y, z) != Blocks.crafting_table) {
            return false;
        }
        if (world.isRemote) {
            return true;
        }

        final NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean(TAG_BOUND, true);
        nbt.setInteger(TAG_X, x);
        nbt.setInteger(TAG_Y, y);
        nbt.setInteger(TAG_Z, z);
        stack.setTagCompound(nbt);

        player.addChatComponentMessage(new ChatComponentText(
                EnumChatFormatting.GREEN + "Linked to crafting table."));
        return true;
    }

    /** Right clicking anything else opens the bound table. */
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote || !isBound(stack)) {
            return stack;
        }

        final NBTTagCompound nbt = stack.getTagCompound();
        final int x = nbt.getInteger(TAG_X);
        final int y = nbt.getInteger(TAG_Y);
        final int z = nbt.getInteger(TAG_Z);

        // Only trust the binding if the table is in a loaded chunk. Checking an
        // unloaded chunk would report "missing" and wipe a perfectly good link.
        if (!world.blockExists(x, y, z)) {
            player.addChatComponentMessage(new ChatComponentText(
                    EnumChatFormatting.YELLOW + "That area isn't loaded right now."));
            return stack;
        }

        if (world.getBlock(x, y, z) != Blocks.crafting_table) {
            player.addChatComponentMessage(new ChatComponentText(
                    EnumChatFormatting.RED + "Target crafting table not found!"));
            stack.setTagCompound(new NBTTagCompound());
            return stack;
        }

        if (!player.capabilities.isCreativeMode) {
            if (player.experienceTotal <= 0) {
                player.addChatComponentMessage(new ChatComponentText(
                        "Cannot use this tool without any XP."));
                return stack;
            }
            player.addExperience(-1);
        }

        player.openGui(DMod.instance, DModGuiHandler.REMOTE_WORKBENCH, world, x, y, z);
        return stack;
    }
}
