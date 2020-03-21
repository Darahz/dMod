package com.darahz.dmod.objects.blocks;

import com.darahz.dmod.tileentities.dmodTileEntityType;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class FreezingElement extends Block{
	public FreezingElement(Properties properties) {
		super(properties);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return dmodTileEntityType.FREEZING_ELEMENT.get().create();
	}
	
	@Override
	public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
		BlockState aboveState = worldIn.getBlockState(pos.add(0, 1, 0));
		if(!worldIn.isAirBlock(pos.add(0, 1, 0)) && aboveState.getBlock() instanceof SnowBlock) {
			worldIn.setBlockState(pos.add(0, 1, 0), Blocks.AIR.getDefaultState(), 11);
		}
		super.onPlayerDestroy(worldIn, pos, state);
	}
	
	
}
