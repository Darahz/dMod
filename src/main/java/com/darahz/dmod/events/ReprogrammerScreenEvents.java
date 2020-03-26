package com.darahz.dmod.events;

import org.lwjgl.opengl.GL11;

import com.darahz.dmod.dMod;
import com.darahz.dmod.objects.items.SpawnerReprogrammer;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = dMod.MOD_ID, bus = Bus.FORGE)
public class ReprogrammerScreenEvents {

	protected static boolean shouldDraw = false;
	protected static int tickDown = 1000;
	protected static int rOut = 0;
	protected static int fadein = 0;
	
	@SubscribeEvent
	public static void worldTickEvent(final TickEvent event) {
		if (tickDown != 0) {
			tickDown--;
			shouldDraw = true;
			return;
		}
		shouldDraw = false;
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void renderSelectedOptons( final RenderGameOverlayEvent.Text event) {
		if (dMod.mc == null || dMod.mc.currentScreen != null)
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
		
		final int mult = 12;

		if (shouldDraw) {
			showScreenText();
			
			drawScreenElements(render, spawnerData, selectedIndex, mult);
		} else {
			hideScreenText();
			drawScreenElements(render, spawnerData, selectedIndex, mult);
		}
	}

	private static void hideScreenText() {
		if(rOut != 0) {
			rOut--;
		}
		
		if(fadein > 0) {
			if(fadein - 31 < 0) {
				fadein = 0;
			}else {
				fadein -= 31;
			}
		}
	}

	private static void showScreenText() {
		if (rOut != 8) {
			rOut++;
		}
		
		if(fadein < 255) {
			if(fadein + 31 > 255) {
				fadein = 255;
			}else {
				fadein += 31;
			}
		}
	}

	private static void drawScreenElements(final FontRenderer render, final CompoundNBT spawnerData,
			final int selectedIndex, final int mult) {
		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
		
		if(shouldDraw) {
			AbstractGui.fill(2, spawnerData.keySet().size() * mult, 100, 30, 0x000000  + ((fadein / 4) << 24));
			GL11.glScalef(0.6f, 0.6f, 0f);
			for (int i = 0; i < spawnerData.keySet().size(); i++) {
				final String keyname = (String) spawnerData.keySet().toArray()[i];
				final short keyvalue = spawnerData.getShort(keyname);
				final String outStr = keyname + " : " + keyvalue;

				if (i == selectedIndex) {
					render.drawStringWithShadow(outStr, rOut, i * mult + 60, 0x3D85C6 + (fadein << 24));
				} else {
					render.drawStringWithShadow(outStr, 8, i * mult + 60, 0xffffff);
				}
				
				
			}
		}else {
			if(rOut != 0) {
				GL11.glScalef(0.6f, 0.6f, 0f);
				for (int i = 0; i < spawnerData.keySet().size(); i++) {
					final String keyname = (String) spawnerData.keySet().toArray()[i];
					final short keyvalue = spawnerData.getShort(keyname);
					final String outStr = keyname + " : " + keyvalue;
					
					if (i == selectedIndex) {
						render.drawStringWithShadow(outStr, rOut, i * mult + 60, 0x3D85C6 + (fadein << 24));
					} else {
						render.drawStringWithShadow(outStr, rOut, i * mult + 60, 0xffffff + (fadein << 24));
					}
				}
			}
		}
		
		
		RenderSystem.disableBlend();
		RenderSystem.popMatrix();
	}
	/**
	 * Forces redraw of GUI text.
	 */
	public static void drawText() {
		shouldDraw = true;
		tickDown = 1000;
		rOut = 0;
	}
}
