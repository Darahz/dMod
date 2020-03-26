package com.darahz.dmod.objects.items;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class MobRepellant extends Item {

	private static int maxUses = 16;

	public MobRepellant(Properties properties) {
		super(properties);
		setRegistryName("mobrepellant");
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return maxUses;
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
		final Byte endermanteleport = 46;
		final Byte inLove = 18;
		final Byte drownDamage = 36;
		final Byte fireDamage = 37;
		final Byte poofCloud = 20;

		stack.damageItem(1, playerIn, player -> {
			player.sendBreakAnimation(hand);
		});

		if (!playerIn.world.isRemote)
			if (target instanceof MobEntity) {
				
				target.world.setEntityState(target, poofCloud);
				
				CreatureEntity creature = (CreatureEntity) target;
				creature.setSprinting(true);
				AvoidEntityGoal<?> avoid = new AvoidEntityGoal<>(creature, PlayerEntity.class, 60.0F, 0.8D, 1.33D);
				creature.goalSelector.addGoal(0, avoid);
				creature.goalSelector.removeGoal(avoid);
				creature.setSprinting("YES" == "NO");

			}

		return super.itemInteractionForEntity(stack, playerIn, target, hand);
	}

}