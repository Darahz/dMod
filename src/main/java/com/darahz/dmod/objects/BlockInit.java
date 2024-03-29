package com.darahz.dmod.objects;

import com.darahz.dmod.dMod;
import com.darahz.dmod.objects.blocks.FreezingElement;
import com.darahz.dmod.objects.blocks.MobSlaughterHouse;
import com.darahz.dmod.objects.items.blockItems.FreezingElementItem;
import com.darahz.dmod.objects.items.blockItems.MobSlaughterHouseItem;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(dMod.MOD_ID)
@Mod.EventBusSubscriber(modid = dMod.MOD_ID,bus = Bus.MOD)
public class BlockInit {

	public static final Block freezingelement_block = null;
	public static final Block mobslaughterhouse_block = null;
	
	@SubscribeEvent
	public static void registerBlock(final Register<Block> event) {
		event.getRegistry().register(new FreezingElement(Block.Properties.create(Material.IRON)).setRegistryName("freezingelement_block"));
		event.getRegistry().register(new MobSlaughterHouse(Block.Properties.create(Material.IRON)).setRegistryName("mobslaughterhouse_block"));
	}
	
	@SubscribeEvent
	public static void registerBlockItems(final Register<Item> event) {
		event.getRegistry().register(new FreezingElementItem(freezingelement_block,new Item.Properties().group(dMod.dmodTab.instance)).setRegistryName("freezingelement_block"));
		event.getRegistry().register(new MobSlaughterHouseItem(mobslaughterhouse_block,new Item.Properties().group(dMod.dmodTab.instance)).setRegistryName("mobslaughterhouse_block"));
	}
	
}