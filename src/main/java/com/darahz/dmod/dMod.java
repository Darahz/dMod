package com.darahz.dmod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.darahz.dmod.objects.BlockInit;
import com.darahz.dmod.objects.ItemInit;
import com.darahz.dmod.tileentities.dmodTileEntityType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("darahzmod")
public class dMod {

	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "darahzmod";
	public static dMod instance;
	public static Minecraft mc;
	public dMod() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get()
				.getModEventBus();

		FMLJavaModLoadingContext.get().getModEventBus()
				.addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus()
				.addListener(this::doClientStuff);

		dmodTileEntityType.TILE_ENTITY_TYPES.register(modEventBus);

		instance = this;
		mc = Minecraft.getInstance();
		
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static void info(String in) {
		LOGGER.info(in);
	}

	private void setup(final FMLCommonSetupEvent event) {
		final RenderType translucent = RenderType.func_228645_f_();
		RenderTypeLookup.setRenderLayer(BlockInit.reprogrammedspawner_block,
				translucent);

	}

	private void doClientStuff(final FMLClientSetupEvent event) {

	}

	@SubscribeEvent
	public void onServerStarting(FMLServerStartingEvent event) {

	}

	public static class dmodTab extends ItemGroup {
		public static final ItemGroup instance = new dmodTab(
				ItemGroup.GROUPS.length, "dmodTab");

		private dmodTab(int index, String label) {
			super(index, label);
		}

		@Override
		public ItemStack createIcon() {
			return new ItemStack(ItemInit.necklaceofrepair);
		}
	}

}
