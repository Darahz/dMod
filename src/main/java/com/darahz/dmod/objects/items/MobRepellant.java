package com.darahz.dmod.objects.items;

import java.util.List;

import com.darahz.dmod.dMod;
import com.darahz.dmod.events.AvoidPlayer;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class MobRepellant extends Item {

	private static int maxUses = 1058;

	public MobRepellant(Properties properties) {
		super(properties);
		setRegistryName("mobrepellant");
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return maxUses;
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn,
			List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		// TODO Auto-generated method stub
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world,
			PlayerEntity player, Hand hand) {
		final ItemStack stack = player.getHeldItem(hand);

		stack.damageItem(1, player, (playerIn) -> {
			playerIn.sendBreakAnimation(hand);
		});

		return super.onItemRightClick(world, player, hand);
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack,
			PlayerEntity playerIn, LivingEntity target, Hand hand) {
		final Byte endermanteleport = 46;
		final Byte inLove = 18;
		final Byte drownDamage = 36;
		final Byte fireDamage = 37;
		final Byte poofCloud = 20;

		final Brain<?> brain = target.getBrain();
		if (!playerIn.world.isRemote)
			if (target instanceof AnimalEntity) {

				final AnimalEntity animal = (AnimalEntity) target;
				animal.setSprinting(true);
				final AvoidPlayer avoid = new AvoidPlayer<>(animal,
						PlayerEntity.class, 16.0F, 0.8D, 1.33D);
				animal.goalSelector.removeGoal(avoid);
				animal.goalSelector.addGoal(4, avoid);

			}
		if (target instanceof MonsterEntity) {
			final MonsterEntity monster = (MonsterEntity) target;
			monster.setSprinting(true);
			final AvoidPlayer avoid = new AvoidPlayer<>(monster,
					PlayerEntity.class, 16.0F, 0.8D, 1.33D);
			monster.goalSelector.removeGoal(avoid);
			monster.goalSelector.getRunningGoals().forEach((Goal) -> {
				dMod.LOGGER.info(Goal.getGoal());

			});;
			monster.goalSelector.addGoal(4, avoid);
		}
		return super.itemInteractionForEntity(stack, playerIn, target, hand);
	}

}
