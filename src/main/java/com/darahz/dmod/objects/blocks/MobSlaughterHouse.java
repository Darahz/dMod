package com.darahz.dmod.objects.blocks;

import com.darahz.dmod.tileentities.dmodTileEntityType;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class MobSlaughterHouse extends Block{

	public MobSlaughterHouse(Properties properties) {
		super(properties.func_226896_b_());
	}
	
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return dmodTileEntityType.MOBSLAUGHTERHOUSE.get().create();
	}
}
