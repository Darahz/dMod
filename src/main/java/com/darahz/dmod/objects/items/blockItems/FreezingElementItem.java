package com.darahz.dmod.objects.items.blockItems;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.SoundType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class FreezingElementItem extends Item {

	private static Block block;
	
	public FreezingElementItem(Block blockIn, Properties props) {
		super(props);
		FreezingElementItem.block = blockIn;
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(new StringTextComponent(TextFormatting.AQUA + "-10,000 Degrees"));
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
	

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		BlockItemUseContext action = new BlockItemUseContext(context);
		if (!action.canPlace() || action == null)
			return ActionResultType.FAIL;
		World world = context.getWorld();
		BlockPos pos = action.getPos();
		boolean canPlace = false;
		if(world.getBlockState(pos).getBlock() instanceof FlowingFluidBlock) {
			canPlace = true;
		}else if(world.isAirBlock(pos)) {
			canPlace = true;
		}else {
			canPlace = false;
		}
				
		if (canPlace) {
			BlockState state = block.getDefaultState();
			world.setBlockState(pos, state);

			SoundEvent placeSound = state.getSoundType(world, pos, context.getPlayer()).getPlaceSound();
			SoundType soundtype = state.getSoundType(world, context.getPos(), context.getPlayer());
			world.playSound(context.getPlayer(), context.getPos(), placeSound, SoundCategory.BLOCKS,
					(soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
			context.getItem().shrink(1);
			context.getPlayer().swingArm(context.getHand());
		}

		return super.onItemUse(context);
	}

}
