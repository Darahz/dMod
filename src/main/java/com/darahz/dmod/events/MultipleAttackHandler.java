package com.darahz.dmod.events;

import com.darahz.dmod.dMod;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = dMod.MOD_ID, bus = Bus.FORGE)
public class MultipleAttackHandler {

	private static int ticks = 20 * 1;
	private static boolean running = false;
	private static Hand hand = null;
	private static PlayerEntity player = null;
	private static int repeats = 0;

	public static void queueAttack(int delayTicks, PlayerEntity playerIn,
			Hand handUsing) {

		player = playerIn;
		hand = handUsing;
		repeats = 3;
		running = true;
	}

	@SubscribeEvent
	public static void doAttacks(TickEvent.ClientTickEvent event) {
		if (!running)
			return;
		if (ticks % 20 == 0)
			if (repeats != 0) {

				// Do what ever else you want
				player.func_226292_a_(hand, true);
				repeats--;
			} else
				running = false;
		ticks++;

	}

}
