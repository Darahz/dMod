package com.darahz.dmod.objects.blocks.tileentities;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.darahz.dmod.tileentities.dmodTileEntityType;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FreezingElementTileEntity extends TileEntity
		implements
			ITickableTileEntity {

	private static int tickDown = 120;
	private static final int tickDownInit = 120;
	private static int freezingRange = 5;

	public FreezingElementTileEntity() {
		this(dmodTileEntityType.FREEZING_ELEMENT.get());
	}

	public FreezingElementTileEntity(final TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);

	}

	@Override
	public void tick() {

		if (tickDown == 120) {
			final BlockPos pos = this.getPos().add(0, 1, 0);

			final TileEntity tileentity = world.getTileEntity(pos);
			if (tileentity == null)
				if (world.isAirBlock(pos)) {
					this.world.setBlockState(pos,
							Blocks.SNOW.getDefaultState());
					final PlayerEntity player = world.getClosestPlayer(
							pos.getX(), pos.getY(), pos.getZ(), 10d, true);
					final SoundEvent placeSound = Blocks.SNOW.getDefaultState()
							.getSoundType(world, pos, player).getPlaceSound();
					this.world.playSound(player, pos, placeSound,
							SoundCategory.BLOCKS, 1f, 1f);
				}

			placeItemInChest(tileentity);
		}

		if (tickDown != 0) {
			tickDown--;

			spawnParticles(world, pos);
			final float willFreeze = (world.getRandom().nextFloat() / 4);

			if (willFreeze < 0.003f) {
				freezeLiquids(world, pos);
			}

			return;
		}

		tickDown = FreezingElementTileEntity.tickDownInit;

	}

	private static void spawnParticles(World world, BlockPos pos) {
		final double d0 = (double) pos.getX() + (double) world.rand.nextFloat();
		final double d1 = pos.getY() + 0.8D;
		final double d2 = (double) pos.getZ() + (double) world.rand.nextFloat();
		if (world.isAirBlock(pos.add(0, 1, 0))) {
			world.addParticle(ParticleTypes.ITEM_SNOWBALL, d0, d1 + 0.4D, d2,
					0.0D, 0.0D, 0.0D);
		}
	}

	private static void freezeLiquids(World world, BlockPos pos) {
		if (freezingRange > 1) {

			final List<BlockPos> list = BlockPos.getAllInBox(
					pos.add(-freezingRange, -freezingRange, -freezingRange),
					pos.add(freezingRange, freezingRange, freezingRange))
					.map(BlockPos::toImmutable).collect(Collectors.toList());

			Collections.shuffle(list);
			for (final BlockPos blockPos : list) {
				final BlockState state = world.getBlockState(blockPos);
				if (state.getBlock() instanceof FlowingFluidBlock) {
					final IFluidState ifluidstate = world
							.getFluidState(blockPos);
					if (ifluidstate.isSource())
						if (state.getBlock() == Blocks.WATER) {
							world.setBlockState(blockPos,
									Blocks.ICE.getDefaultState());
						} else if (state.getBlock() == Blocks.LAVA) {
							world.setBlockState(blockPos,
									Blocks.OBSIDIAN.getDefaultState());
						}
				}
			}
		} else {
			pos = pos.add(0, 1, 0);
			final BlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof FlowingFluidBlock) {
				final IFluidState ifluidstate = world.getFluidState(pos);
				if (ifluidstate.isSource())
					if (state.getBlock() == Blocks.WATER) {
						world.setBlockState(pos, Blocks.ICE.getDefaultState());
					} else if (state.getBlock() == Blocks.LAVA) {
						world.setBlockState(pos,
								Blocks.OBSIDIAN.getDefaultState());
					}
			}
		}

	}

	private static boolean placeItemInChest(TileEntity tileEntity) {
		if (tileEntity instanceof IInventory) {
			boolean doublechest = false;
			if (tileEntity.getBlockState().getBlock() instanceof ChestBlock) {
				final ChestType chesttype = tileEntity.getBlockState()
						.get(ChestBlock.TYPE);
				doublechest = chesttype != ChestType.SINGLE;
			}

			final World world = tileEntity.getWorld();

			final IInventory inventory = (IInventory) tileEntity;

			if (doublechest) {
				final BlockPos opos = tileEntity.getPos().offset(ChestBlock
						.getDirectionToAttached(tileEntity.getBlockState()));
				final IInventory chestsideone = inventory;
				final IInventory chestsidetwo = (IInventory) world
						.getTileEntity(opos);

				for (int i = 0; i < chestsideone.getSizeInventory(); i++) {
					final ItemStack stack = chestsideone.getStackInSlot(i);
					if (stack.getItem() == Items.AIR) {
						chestsideone.setInventorySlotContents(i,
								new ItemStack(Items.SNOWBALL, 1));
						return true;
					} else if (stack.getCount() != stack.getMaxStackSize()
							&& stack.getItem() == Items.SNOWBALL) {
						chestsideone.setInventorySlotContents(i, new ItemStack(
								stack.getItem(), stack.getCount() + 1));
						return true;
					}
				}

				for (int i = 0; i < chestsidetwo.getSizeInventory(); i++) {
					final ItemStack stack = chestsidetwo.getStackInSlot(i);
					if (stack.getItem() == Items.AIR) {
						chestsidetwo.setInventorySlotContents(i,
								new ItemStack(Items.SNOWBALL, 1));
						return true;
					} else if (stack.getCount() != stack.getMaxStackSize()
							&& stack.getItem() == Items.SNOWBALL) {
						chestsidetwo.setInventorySlotContents(i, new ItemStack(
								stack.getItem(), stack.getCount() + 1));
						return true;
					}
				}
				return false;
			} else {
				final int invSlots = inventory.getSizeInventory();
				int curSlot = 0;

				while (curSlot != invSlots) {
					final ItemStack stack = inventory.getStackInSlot(curSlot);
					if (stack.getItem() == Items.AIR) {
						inventory.setInventorySlotContents(curSlot,
								new ItemStack(Items.SNOWBALL, 1));
						return true;
					} else if (stack.getCount() != stack.getMaxStackSize()
							&& stack.getItem() == Items.SNOWBALL) {
						inventory.setInventorySlotContents(curSlot,
								new ItemStack(stack.getItem(),
										stack.getCount() + 1));
						return true;
					}
					curSlot++;
				}
			}

			return false;
		}
		return false;
	}

}
