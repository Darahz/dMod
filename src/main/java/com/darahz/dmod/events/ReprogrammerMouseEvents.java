package com.darahz.dmod.events;

import com.darahz.dmod.dMod;
import com.darahz.dmod.helpers.KeyboardHelper;
import com.darahz.dmod.objects.ItemInit;
import com.darahz.dmod.objects.items.SpawnerReprogrammer;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.MouseInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = dMod.MOD_ID, bus = Bus.FORGE)
public class ReprogrammerMouseEvents {

	@SubscribeEvent()
	@OnlyIn(Dist.CLIENT)
	public static void mouseClickInput(MouseInputEvent event) {
		final Minecraft mc = Minecraft.getInstance();
		if (mc == null)
			return;
		if (mc.currentScreen != null || mc.player == null)
			return;

		final PlayerEntity player = mc.player;
		if (!player.inventory.hasItemStack(
				new ItemStack(ItemInit.spawnerreprogramming_tool)))
			return;

		final ItemStack stack = player.getHeldItemMainhand();
		// Button 0 = Left
		// Button 1 = right
		// Action 1 = Down
		// Action 0 = Up
		if (event.getAction() != 1)
			return;
		if (!(stack.getItem() instanceof SpawnerReprogrammer))
			return;

		checkItemNBT(stack);

		final CompoundNBT itemNBT = stack.getOrCreateTag();
		final CompoundNBT spawnerData = itemNBT.getCompound("spawnerData");
		final int curselected = itemNBT.getInt("selectedValue");
		final String selectedKey = (String) spawnerData.keySet()
				.toArray()[curselected];

		if (resetToolSettings(player, stack, selectedKey))
			return;

		if (!KeyboardHelper.isHoldingShift())
			return;

		short selectedData = spawnerData.getShort(selectedKey);
		if (event.getButton() == 0) {

			if (KeyboardHelper.isHoldingControl()) {
				selectedData += 10;
			} else {
				selectedData++;
			}
		} else {
			if (KeyboardHelper.isHoldingControl()) {
				selectedData -= 10;
			} else {
				selectedData--;
			}
			if (selectedData < 1) {
				selectedData = 1;
			}
		}
		if (spawnerData.getShort("MinSpawnDelay") > spawnerData
				.getShort("MaxSpawnDelay")) {
			spawnerData.putShort("MinSpawnDelay",
					(short) (spawnerData.getShort("MaxSpawnDelay") - 1));
			player.sendMessage(new StringTextComponent(
					"MinSpawnDelay cannot be less than MaxSpawnDelay (setting minSpawnDelay to MaxSpawnDelay-1)"));
			return;
		}
		spawnerData.putShort(selectedKey, selectedData);

		ReprogrammerScreenEvents.drawText();
	}

	private static boolean resetToolSettings(final PlayerEntity player,
			final ItemStack stack, final String selectedKey) {
		if (selectedKey.contains("RESETDATA")) {
			dMod.LOGGER.info(selectedKey);
			player.sendMessage(new StringTextComponent(
					TextFormatting.RED + " > Tools settings set to default."));
			stack.setTag(new CompoundNBT());
			return true;
		} else
			return false;
	}

	private static void checkItemNBT(ItemStack stack) {
		final CompoundNBT itemNBT = stack.getOrCreateTag();
		if (!itemNBT.contains("spawnerData")) {

			final CompoundNBT spawnerDefSettings = new CompoundNBT();

			spawnerDefSettings.putShort("Delay", (short) 200);
			spawnerDefSettings.putShort("MinSpawnDelay", (short) 200);
			spawnerDefSettings.putShort("MaxSpawnDelay", (short) 400);
			spawnerDefSettings.putShort("SpawnCount", (short) 1);
			spawnerDefSettings.putShort("MaxNearbyEntities", (short) 6);
			spawnerDefSettings.putShort("RequiredPlayerRange", (short) 16);
			spawnerDefSettings.putShort("SpawnRange", (short) 4);
			spawnerDefSettings.putShort("RESETDATA", (short) 404);
			itemNBT.put("spawnerData", spawnerDefSettings);

			itemNBT.putInt("selectedValue", 0);

			// player.setHeldItem(player.getActiveHand(), stack);
		}
	}

	@SubscribeEvent
	public static void mouseWheelInput(MouseScrollEvent event) {
		final Boolean scrollUp = event.getScrollDelta() == 1.0;
		final Boolean scrollDown = event.getScrollDelta() == -1.0;
		final Minecraft mc = Minecraft.getInstance();
		final PlayerEntity player = mc.player;
		final ItemStack stack = player.getHeldItemMainhand();
		if (!(stack.getItem() instanceof SpawnerReprogrammer))
			return;
		if (!KeyboardHelper.isHoldingShift())
			return;

		checkItemNBT(stack);

		final CompoundNBT itemNBT = stack.getOrCreateTag();
		final CompoundNBT spawnerData = itemNBT.getCompound("spawnerData");
		int curselected = itemNBT.getInt("selectedValue");

		if (scrollDown) {
			if (curselected == 0) {
				curselected = spawnerData.keySet().toArray().length - 1;
			} else {
				curselected--;
			}
		} else if (scrollUp)
			if (curselected == spawnerData.keySet().toArray().length - 1) {
				curselected = 0;
			} else {
				curselected++;
			}

		ReprogrammerScreenEvents.drawText();
		itemNBT.putInt("selectedValue", curselected);
		player.playSound(SoundEvents.ENTITY_CHICKEN_STEP, 0.15F,
				player.getRNG().nextFloat());
		event.setCanceled(true);

	}
}