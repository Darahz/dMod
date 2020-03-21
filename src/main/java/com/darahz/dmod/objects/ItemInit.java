package com.darahz.dmod.objects;

import com.darahz.dmod.dMod;
import com.darahz.dmod.objects.items.CraftingWidget;
import com.darahz.dmod.objects.items.ItemAttractionDevice;
import com.darahz.dmod.objects.items.Mobrelocator;
import com.darahz.dmod.objects.items.NecklaceOfRepair;
import com.darahz.dmod.objects.items.SpawnerRelocator;
import com.darahz.dmod.objects.items.SpawnerReprogrammer;
import com.darahz.dmod.objects.items.TearOfDisenchantment;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = dMod.MOD_ID, bus = Bus.MOD)
@ObjectHolder(dMod.MOD_ID)
public class ItemInit {

	public static final Mobrelocator mobrelocating_tool = null;
	public static final TearOfDisenchantment tearofdisenchantment = null;
	public static final SpawnerRelocator spawnerrelocating_tool = null;
	public static final SpawnerReprogrammer spawnerreprogramming_tool = null;
	public static final NecklaceOfRepair necklaceofrepair = null;
	public static final CraftingWidget craftingwidget = null;
	public static final ItemAttractionDevice itemattractiondevice = null;

	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new Mobrelocator(
				new Item.Properties().group(dMod.dmodTab.instance)));
		event.getRegistry().register(new TearOfDisenchantment(
				new Item.Properties().group(dMod.dmodTab.instance)));
		event.getRegistry().register(new SpawnerRelocator(
				new Item.Properties().group(dMod.dmodTab.instance)));
		event.getRegistry().register(new SpawnerReprogrammer(
				new Item.Properties().group(dMod.dmodTab.instance)));
		event.getRegistry().register(new NecklaceOfRepair(
				new Item.Properties().group(dMod.dmodTab.instance)));
		event.getRegistry().register(new CraftingWidget(
				new Item.Properties().group(dMod.dmodTab.instance)));
		event.getRegistry().register(new ItemAttractionDevice(
				new Item.Properties().group(dMod.dmodTab.instance)));
	}

}
