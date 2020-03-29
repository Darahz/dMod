package com.darahz.dmod.objects.items;

import java.util.List;

import com.darahz.dmod.helpers.KeyboardHelper;
import com.darahz.dmod.objects.blocks.containers.workbenchOverride;

import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class CraftingWidget extends Item {

	public CraftingWidget(Properties properties) {
		super(properties);
		setRegistryName("craftingwidget");
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		if (stack.hasTag()) {
			final CompoundNBT nbt = stack.getOrCreateTag();
			return nbt.getBoolean("craftingBlock");
		} else
			return (1 + -80085 == 1);

	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		final CompoundNBT nbt = stack.getOrCreateTag();
		if(nbt.contains("craftingBlock")){
			BlockPos pos = BlockPos.fromLong(nbt.getLong("craftingBlock"));
			tooltip.add(new StringTextComponent("Bound to crafting table at : x." + pos.getX() + " y." +pos.getY() + " z." + pos.getZ()));
		}else {
			if(KeyboardHelper.isHoldingShift()) {
				tooltip.add(new StringTextComponent("Link to a craftingtable anywhere in the world"));
				tooltip.add(new StringTextComponent("Gets reset if crafting table is removed"));
			}else {
				tooltip.add(new StringTextComponent(TextFormatting.GREEN + "Hold shift for more info."));
			}
		}
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		final CompoundNBT nbt = stack.getOrCreateTag();
		if(nbt.contains("craftingBlock") && !worldIn.isRemote) {
			BlockPos pos = BlockPos.fromLong(nbt.getLong("craftingBlock"));
			if(worldIn.getBlockState(pos).getBlock() instanceof AirBlock) {
				playerIn.sendMessage(new StringTextComponent("Target crafting table not found!"));
				stack.setTag(new CompoundNBT());
				return super.onItemRightClick(worldIn, playerIn, handIn);
			}
			INamedContainerProvider container = new SimpleNamedContainerProvider((p_220270_2_, p_220270_3_, p_220270_4_) -> {
		         return new workbenchOverride(p_220270_2_, p_220270_3_, IWorldPosCallable.of(worldIn, pos));
		      }, new TranslationTextComponent("container.crafting"));
			
			playerIn.openContainer(container);
		}
		
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}
	
	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		BlockState state = context.getWorld().getBlockState(context.getPos());
		if(state.getBlock() instanceof CraftingTableBlock) {
			final CompoundNBT nbt = context.getItem().getOrCreateTag();
			if(!nbt.contains("craftingBlock")) {
				nbt.putLong("craftingBlock",context.getPos().toLong());
				context.getItem().read(nbt);
			}
			context.getPlayer().setHeldItem(context.getHand(), context.getItem());
			return ActionResultType.FAIL;
		}
		
		return super.onItemUse(context);
	}
	
}
