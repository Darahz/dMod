package com.darahz.dmod.objects.items;

import java.util.List;

import com.darahz.dmod.helpers.KeyboardHelper;

import net.minecraft.block.Block;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class SpawnerReprogrammer extends Item {

	public SpawnerReprogrammer(Properties properties) {
		super(properties);
		setRegistryName("spawnerreprogramming_tool");
	}
		
	@Override
	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if(KeyboardHelper.isHoldingShift()) {
			tooltip.add(new StringTextComponent("Shift + Scroll to change selected setting"));
			tooltip.add(new StringTextComponent("Shift + L-Mouse button to increase the value"));
			tooltip.add(new StringTextComponent("Shift + R-Mouse button to decrease the value"));
			tooltip.add(new StringTextComponent("Shift + L-Mouse button to increase the value by 10"));
			tooltip.add(new StringTextComponent("Shift + R-Mouse button to decrease the value by 10"));
			tooltip.add(new StringTextComponent(TextFormatting.DARK_RED + "Reset all values by selecing 'reset'"));
		}else {
			tooltip.add(new StringTextComponent(TextFormatting.GREEN +"Hold shift for more info."));
		}
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
	
		
	@Override
	public ActionResultType onItemUse(ItemUseContext context) {

		PlayerEntity player = context.getPlayer();
		World world = context.getWorld();
		BlockPos pos = context.getPos();
		Block block = context.getWorld().getBlockState(pos).getBlock();
		TileEntity isTile = world.getTileEntity(pos);
		ItemStack stack = player.getHeldItemMainhand();
    	CompoundNBT itemNBT = stack.getOrCreateTag();
    	
    	if(!(block instanceof SpawnerBlock)) return ActionResultType.FAIL;
		if(isTile == null) return ActionResultType.FAIL;
		if(!world.isRemote) return ActionResultType.FAIL;
		
    	CompoundNBT spawnerData = itemNBT.getCompound("spawnerData");
    	int selectedIndex = itemNBT.getInt("selectedValue");
		String selectedKey = (String) spawnerData.keySet().toArray()[selectedIndex];

		if(selectedKey.contains("RESETDATA")) {
			player.sendMessage(new StringTextComponent(TextFormatting.RED + " > Data reset"));
			stack.setTag(new CompoundNBT());
			return ActionResultType.FAIL;
		}
		
		MobSpawnerTileEntity s = (MobSpawnerTileEntity)context.getWorld().getTileEntity(context.getPos());
    	CompoundNBT spawnerInfo = s.serializeNBT();
    	
		spawnerInfo.putShort("Delay", spawnerData.getShort("Delay"));
		spawnerInfo.putShort("MinSpawnDelay", spawnerData.getShort("MinSpawnDelay"));
		spawnerInfo.putShort("MaxSpawnDelay", spawnerData.getShort("MaxSpawnDelay"));
		spawnerInfo.putShort("SpawnCount", spawnerData.getShort("SpawnCount"));
		spawnerInfo.putShort("MaxNearbyEntities",spawnerData.getShort("MaxNearbyEntities"));
		spawnerInfo.putShort("RequiredPlayerRange", spawnerData.getShort("RequiredPlayerRange"));
		spawnerInfo.putShort("SpawnRange", spawnerData.getShort("SpawnRange"));
		 
    	s.read(spawnerInfo);
		
		player.swingArm(Hand.MAIN_HAND);
		
		return super.onItemUse(context);
	}
	
	
	public static void checkItemNBT(ItemStack stack) {
		CompoundNBT itemNBT = stack.getOrCreateTag();
		if(!itemNBT.contains("spawnerData")) {
			
			CompoundNBT spawnerDefSettings = new CompoundNBT();
			
			spawnerDefSettings.putShort("Delay", (short) 200);
			spawnerDefSettings.putShort("MinSpawnDelay", (short)200);
			spawnerDefSettings.putShort("MaxSpawnDelay", (short)400);
			spawnerDefSettings.putShort("SpawnCount", (short)1);
			spawnerDefSettings.putShort("MaxNearbyEntities",(short)6);
			spawnerDefSettings.putShort("RequiredPlayerRange", (short)16);
			spawnerDefSettings.putShort("SpawnRange", (short)4);
			spawnerDefSettings.putShort("RESETDATA", (short) 404);
			itemNBT.put("spawnerData", spawnerDefSettings);
			
			itemNBT.putInt("selectedValue", 0);
			
			//player.setHeldItem(player.getActiveHand(), stack);
		}
	}
	

}
