package com.darahz.dmod.objects.blocks;

import com.darahz.dmod.tileentities.dmodTileEntityType;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class FreezingElement extends Block {
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	public FreezingElement(Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING,
				Direction.NORTH));
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}

	@Override
	protected void fillStateContainer(
			net.minecraft.state.StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		// TODO Auto-generated method stub
		return this.getDefaultState().with(FACING,
				context.getPlacementHorizontalFacing());
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
	public void onPlayerDestroy(IWorld worldIn, BlockPos pos,
			BlockState state) {
		final BlockState aboveState = worldIn.getBlockState(pos.add(0, 1, 0));
		if (!worldIn.isAirBlock(pos.add(0, 1, 0))
				&& aboveState.getBlock() instanceof SnowBlock) {
			worldIn.setBlockState(pos.add(0, 1, 0),
					Blocks.AIR.getDefaultState(), 11);
		}
		super.onPlayerDestroy(worldIn, pos, state);
	}

}
