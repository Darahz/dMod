package com.darahz.dmod.objects.items;

import java.util.List;

import com.darahz.dmod.dMod;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class SpawnerRelocator extends Item  {

	public SpawnerRelocator(Properties properties) {
		super(properties);
		setRegistryName("spawnerrelocating_tool");
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		return stack.hasTag();
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if(stack.hasTag()) {
			CompoundNBT nbt = stack.getOrCreateTag();
			CompoundNBT blockData = nbt.getCompound("block");
			ListNBT listnbt = blockData.getList("SpawnPotentials", 10);
			for(int i = 0; i < listnbt.size(); ++i) {
				CompoundNBT entityInfo = listnbt.getCompound(i).getCompound("Entity");
				tooltip.add(new StringTextComponent(TextFormatting.YELLOW + "Spawner Entity" + TextFormatting.RESET + " : " + entityInfo.getString("id")));
			}
		}else {
			tooltip.add(new StringTextComponent(TextFormatting.GOLD + "Right click a spawner to move it"));
		}
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
	
	@Override
	public ActionResultType onItemUse(ItemUseContext context) {

		ItemStack stack = context.getItem();
		PlayerEntity player = context.getPlayer();
		World world = context.getWorld();
		BlockPos pos = context.getPos();
		Block block = context.getWorld().getBlockState(pos).getBlock();
		Hand hand = context.getHand();
		TileEntity isTile = world.getTileEntity(pos);

		if(stack.hasTag()) {
			CompoundNBT stackNbt = stack.getTag().getCompound("block");
			stackNbt.putShort("MinSpawnDelay", (short)-1);
			stackNbt.putBoolean("customSpawner", true);
			dMod.LOGGER.info(stackNbt);
			pos = pos.offset(context.getFace());
			if(!world.isRemote) {
				world.setBlockState(pos, Blocks.SPAWNER.getDefaultState(),11);
				MobSpawnerTileEntity s = (MobSpawnerTileEntity)world.getTileEntity(pos);
				
				s.read(stackNbt);
				s.setPos(pos);
				
				context.getItem().setTag(new CompoundNBT());
			}			
		}else {
			if(!(block instanceof SpawnerBlock)) return ActionResultType.FAIL;
			if(isTile == null) return ActionResultType.FAIL;
			CompoundNBT nbt = new CompoundNBT();
	        nbt.put("block", isTile.serializeNBT());
	        nbt.putString("blockName", isTile.getBlockState().getBlock().toString());
	        stack.setTag(nbt);
	        player.setHeldItem(hand, stack);
	        world.setBlockState(pos, Blocks.AIR.getDefaultState());
		}
		
		 
		return super.onItemUse(context);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity entity, Hand hand) {
		return super.itemInteractionForEntity(stack, playerIn, entity, hand);
	}

}
