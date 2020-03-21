package com.darahz.dmod.objects.items;

import java.util.List;

import com.darahz.dmod.helpers.KeyboardHelper;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AirItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class NecklaceOfRepair extends Item {

	public NecklaceOfRepair(Properties properties) {
		super(properties);
		setRegistryName("necklaceofrepair");
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.RARE;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn,
			PlayerEntity player, Hand handIn) {
		final ItemStack stack = player.getHeldItem(handIn);
		if (stack.hasTag() && !worldIn.isRemote
				&& KeyboardHelper.isHoldingShift())
			toggleEnabled(stack, false);

		player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f,
				player.getRNG().nextFloat());

		return super.onItemRightClick(worldIn, player, handIn);
	}

	private void toggleEnabled(final ItemStack stack, boolean forceOff) {
		final CompoundNBT nbt = stack.getOrCreateTag();
		final boolean enabled = nbt.getBoolean("enabled");
		if (!forceOff) {
			nbt.putBoolean("enabled", !enabled);
			stack.read(nbt);
		} else {
			nbt.putBoolean("enabled", false);
			stack.read(nbt);
		}
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
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn,
			int itemSlot, boolean isSelected) {

		boolean isEnabled = false;
		if (stack.hasTag())
			isEnabled = stack.getOrCreateTag().getBoolean("enabled");

		if (!isEnabled) {
			final PlayerEntity player = (entityIn instanceof PlayerEntity)
					? (PlayerEntity) entityIn
					: null;
			if (player == null)
				return;

			updateSecondsLiving(stack, player, itemSlot);
		}

		// if (!isSelected)
		// toggleEnabled(stack, true);

		if (isEnabled && !worldIn.isRemote) {
			if (!(entityIn instanceof PlayerEntity))
				return;
			if (!worldIn.dimension.hasSkyLight())
				return;

			final PlayerEntity player = (PlayerEntity) entityIn;
			final int ticktosec = (KeyboardHelper.isHoldingShift()
					&& isSelected) ? 2 : 20;

			if ((player.ticksExisted % ticktosec) != 0)
				return;

			final NonNullList<ItemStack> inv = player.inventory.mainInventory;
			final int i = worldIn.func_226658_a_(LightType.SKY,
					player.getPosition()) - worldIn.getSkylightSubtracted();

			if (i < 10)
				return;

			for (final ItemStack itemStack : inv) {
				if (itemStack.getItem() instanceof AirItem && !isEnabled)
					return;
				if (itemStack.getDamage() != 0) {
					final CompoundNBT nbt = stack.getOrCreateTag();
					final long ininventory = nbt.getLong("inInventory");
					if (ininventory != 0) {
						itemStack.setDamage(itemStack.getDamage() - 1);
						nbt.putLong("inInventory", ininventory - 1);
						if (isEnabled)
							player.replaceItemInInventory(itemSlot, stack);

					}

				}

			}

		}
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
	}

	private void updateSecondsLiving(ItemStack stack, final PlayerEntity player,
			int slot) {
		if ((player.ticksExisted % 20) == 0)
			if (!stack.hasTag()) {
				final CompoundNBT nbt = stack.getOrCreateTag();
				nbt.putLong("inInventory", 0);
				nbt.putBoolean("enabled", false);
				stack.read(nbt);
				player.replaceItemInInventory(slot, stack);
			} else {
				final CompoundNBT nbt = stack.getOrCreateTag();
				final long ininventory = nbt.getLong("inInventory");
				if (ininventory <= 299) {
					nbt.putLong("inInventory", ininventory + 1);
					player.replaceItemInInventory(slot, stack);
				}

			}
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn,
			List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if (stack.hasTag()) {
			if (!stack.getTag().contains("inInventory"))
				return;

			final CompoundNBT nbt = stack.getOrCreateTag();
			final long totalSecs = (nbt.getLong("inInventory"));
			final long minutes = (totalSecs % 3600) / 60;
			final long seconds = totalSecs % 60;
			if (KeyboardHelper.isHoldingShift()) {
				tooltip.add(new StringTextComponent(TextFormatting.WHITE
						+ ">Time kept in inventory: " + TextFormatting.GOLD
						+ minutes + ":" + seconds));
				tooltip.add(new StringTextComponent(
						"Will repair all items in players inventory."));
				tooltip.add(new StringTextComponent(
						TextFormatting.GRAY + "Recharges over time."));
				tooltip.add(new StringTextComponent(
						TextFormatting.GRAY + "Only works in light areas."));
			} else {
				tooltip.add(new StringTextComponent(TextFormatting.WHITE
						+ "Time kept in inventory: " + TextFormatting.GOLD
						+ minutes + ":" + seconds));
				tooltip.add(new StringTextComponent(
						TextFormatting.GREEN + "Hold shift for more info."));
			}

		} else
			tooltip.add(new StringTextComponent(
					"Seconds kept: " + TextFormatting.WHITE + 0));

		super.addInformation(stack, worldIn, tooltip, flagIn);
	}

}
