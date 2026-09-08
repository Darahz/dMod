package com.darahz.dmod.items;

import java.util.List;

import com.darahz.dmod.DModTab;
import com.darahz.dmod.Reference;
import com.darahz.dmod.helpers.KeyboardHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

/**
 * Drop this next to an enchanted item on the ground and the enchantments are
 * pulled off into books. The actual work happens in
 * {@link com.darahz.dmod.events.DroppedTearHandler}.
 */
public class ItemTearOfDisenchantment extends Item {

    public ItemTearOfDisenchantment() {
        setUnlocalizedName("dmod.tearofdisenchantment");
        setTextureName(Reference.RES + "tearofdisenchantment");
        setCreativeTab(DModTab.INSTANCE);
        setMaxStackSize(1);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.uncommon;
    }

    @Override
    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        if (KeyboardHelper.isHoldingShift()) {
            tooltip.add(EnumChatFormatting.DARK_PURPLE + "Be careful where you drop me.");
            tooltip.add("Drop me beside an enchanted item on the ground");
            tooltip.add("to pull the enchantments off into books.");
        } else {
            tooltip.add("Remove enchants from items.");
        }
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
        // The original played the same sound ten times at the same spot in a
        // loop, which is audibly identical to playing it once.
        if (!player.worldObj.isRemote) {
            player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ,
                    "mob.ghast.moan", 0.5F, 1.0F);
        }
        return super.onDroppedByPlayer(item, player);
    }
}
