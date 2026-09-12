package com.darahz.dmod.items;

import com.darahz.dmod.DMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

public class ItemExample extends Item {
    public ItemExample() {
        setUnlocalizedName(DMod.MODID + ".example_item");
        setTextureName(DMod.MODID + ":example_item");
        setCreativeTab(DMod.TAB);
        setMaxStackSize(64);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            player.addChatMessage(new ChatComponentTranslation("chat.dmod.example_item.used"));
        }
        return stack;
    }
}
