package com.darahz.dmod.events;

import org.lwjgl.opengl.GL11;

import com.darahz.dmod.dMod;
import com.darahz.dmod.objects.items.SpawnerReprogrammer;

import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = dMod.MOD_ID, bus = Bus.FORGE)
public class ReprogrammerScreenEvents {
	
	private static boolean shouldDraw = false;
	private static int tickDown = 1000;
	private static int rOut = 0;
	
	@SubscribeEvent
	public static void worldTickEvent(final TickEvent event) {
		if (tickDown != 0) {
			tickDown--;
			shouldDraw = true;
			return;
		}
		shouldDraw = false;

	}

	@SubscribeEvent
	public static void renderSelectedOptons(final RenderGameOverlayEvent.Text event) {
		if (Minecraft.getInstance().currentScreen != null)
			return;
		if (event.getType() != ElementType.TEXT)
			return;

		final Minecraft mc = Minecraft.getInstance();
		final PlayerEntity player = mc.player;

		final ItemStack stack = player.getHeldItemMainhand();
		final FontRenderer render = mc.fontRenderer;

		if (!(stack.getItem() instanceof SpawnerReprogrammer))
			return;
		if (!stack.hasTag())
			return;

		final CompoundNBT itemNBT = stack.getOrCreateTag();
		final CompoundNBT spawnerData = itemNBT.getCompound("spawnerData");
		final int selectedIndex = itemNBT.getInt("selectedValue");

		String lastKey = "";
		short lastData = 0;
		final String selectedKey = (String) spawnerData.keySet().toArray()[selectedIndex];

		if ((selectedIndex - 1) < 0) {
			lastKey = (String) spawnerData.keySet().toArray()[spawnerData.keySet().toArray().length - 1];
			lastData = spawnerData.getShort(lastKey);
		} else {
			lastKey = (String) spawnerData.keySet().toArray()[selectedIndex - 1];
			lastData = spawnerData.getShort(lastKey);
		}

		final short selectedData = spawnerData.getShort(selectedKey);
		if(shouldDraw) {
			GL11.glPushMatrix();
			GL11.glScalef(0.6f,0.6f,0f);
			render.drawStringWithShadow("Last : " + lastKey + " : " + lastData, rOut,8, 0x909090);
			GL11.glPopMatrix();
	        GL11.glScalef(1,1,0);
	        if(tickDown > 500) {
				render.drawStringWithShadow(selectedKey + " : " + selectedData, 10,10, 0xFFFFFF);	        	
	        }else {
				render.drawStringWithShadow(selectedKey + " : " + selectedData, rOut,10, 0xFFFFFF);
	        }

			if(rOut != 10) {
				rOut++;
			}
		}else {
			rOut = 0;
		}
	}
	/**
	    Forces redraw of GUI text.
	    */
	public static void drawText() {
		shouldDraw = true;
		tickDown = 1000;
		rOut = 0;
	}
}
