package com.darahz.dmod.objects.items;

import java.util.List;

import com.darahz.dmod.helpers.KeyboardHelper;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemAttractionDevice extends Item {

	public ItemAttractionDevice(Properties properties) {
		super(properties);
		setRegistryName("itemattractiondevice");
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		if (stack.hasTag()) {
			final CompoundNBT nbt = stack.getOrCreateTag();
			return nbt.getBoolean("enabled");
		} else
			return (1 + -80085 == 1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn,
			PlayerEntity player, Hand handIn) {
		if (KeyboardHelper.isHoldingShift()) {
			final ItemStack stack = player.getHeldItem(handIn);
			toggleEnabled(stack);
		}
		return super.onItemRightClick(worldIn, player, handIn);
	}

	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn,
			int itemSlot, boolean isSelected) {

		if (entityIn instanceof PlayerEntity) {
			final CompoundNBT nbt = stack.getOrCreateTag();
			final boolean enabled = nbt.getBoolean("enabled");

			final PlayerEntity player = (PlayerEntity) entityIn;
			final World world = player.world;
			if (KeyboardHelper.isHoldingShift())
				return;
			if (enabled) {
				final List<ItemEntity> list = world.getEntitiesWithinAABB(
						ItemEntity.class,
						player.getBoundingBox().grow(10, 10, 10));
				for (final ItemEntity item : list) {
					final Vec3d vec3d = new Vec3d(
							player.func_226277_ct_() - item.func_226277_ct_(),
							player.func_226278_cu_()
									+ player.getEyeHeight() / 2.0D
									- item.func_226278_cu_(),
							player.func_226281_cx_() - item.func_226281_cx_());
					final double d1 = vec3d.lengthSquared();
					if (d1 < 64.0D) {
						final double d2 = 1.0D - Math.sqrt(d1) / 8.0D;
						item.setMotion(item.getMotion()
								.add(vec3d.normalize().scale(0.2d)));
						item.setPickupDelay((int) d1);
						world.addParticle(ParticleTypes.PORTAL, true,
								item.getPosition().getX(),
								item.getPosition().getY(),
								item.getPosition().getZ(),
								world.getRandom().nextDouble(), 0,
								world.getRandom().nextDouble());

					}

					if (d1 < 3D)
						if (player.inventory
								.addItemStackToInventory(item.getItem())) {
							item.remove();
							player.playSound(SoundEvents.UI_TOAST_IN, 1, 1);
						}

				}

			}
		}

		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn,
			List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if (KeyboardHelper.isHoldingShift())
			tooltip.add(new StringTextComponent("Steve: "
					+ TextFormatting.LIGHT_PURPLE
					+ "\"I don't see how an Apple with a String can attract stuff?\""));
		else
			tooltip.add(new StringTextComponent(
					TextFormatting.GREEN + "Hold shift for more info."));

		super.addInformation(stack, worldIn, tooltip, flagIn);
	}

	private void toggleEnabled(ItemStack stack) {
		final CompoundNBT nbt = stack.getOrCreateTag();
		final boolean enabled = nbt.getBoolean("enabled");
		nbt.putBoolean("enabled", !enabled);
		stack.read(nbt);
	}

}
