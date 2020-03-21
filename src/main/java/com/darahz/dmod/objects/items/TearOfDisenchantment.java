package com.darahz.dmod.objects.items;

import java.util.List;

import com.darahz.dmod.dMod;
import com.darahz.dmod.helpers.KeyboardHelper;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class TearOfDisenchantment extends Item {

	public TearOfDisenchantment(Properties properties) {
		super(properties);
		setRegistryName("tearofdisenchantment");
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}
	
	@Override
	public boolean isComplex() {
		return false;
	}
	
	@Override
	public ActionResultType onItemUse(ItemUseContext context) {

		return super.onItemUse(context);
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		
		if(KeyboardHelper.isHoldingShift()) {tooltip.add(new StringTextComponent(TextFormatting.DARK_PURPLE + "Be carefull where you drop me."));}
		else {tooltip.add(new StringTextComponent("Remove enchants from items."));}
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
	
	@Override
	public boolean onDroppedByPlayer(ItemStack item, PlayerEntity player) {
		dMod.info("asdasd");
		for (int i = 0; i < 10; i++) {
			if(!player.world.isRemote)
				player.world.playSound((PlayerEntity)null, player.getPosition(), SoundEvents.ENTITY_PARROT_IMITATE_GHAST, SoundCategory.BLOCKS, 0.5F, 1F);
		}
		return super.onDroppedByPlayer(item, player);
	}
	
}
