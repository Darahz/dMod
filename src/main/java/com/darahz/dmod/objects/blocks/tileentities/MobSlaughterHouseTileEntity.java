package com.darahz.dmod.objects.blocks.tileentities;

import java.util.List;

import com.darahz.dmod.tileentities.dmodTileEntityType;

import net.minecraft.entity.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class MobSlaughterHouseTileEntity extends TileEntity
		implements
			ITickableTileEntity {

	private int tickDown = 120;
	private final int tickDownInit = 120;
	private static int speedUpgrades = 1;
	private static int amountUpgrades = 1;
	private static int rangeUpgrades = 1;

	public MobSlaughterHouseTileEntity() {
		this(dmodTileEntityType.MOBSLAUGHTERHOUSE.get());
	}

	public MobSlaughterHouseTileEntity(TileEntityType<MobSlaughterHouseTileEntity> tileEntityType) {
		super(tileEntityType);
	}

	@Override
	public void tick() {
		int speed = tickDownInit / (speedUpgrades);
		int entAmount = amountUpgrades;
		int range = rangeUpgrades;

		if(!doTick()) return;
		if(this.world.isRemote) return;
		World world = this.world;
		List<MobEntity> list = world.getEntitiesWithinAABB(MobEntity.class,this.getRenderBoundingBox().grow(range, 1, range));
		
		if(list.size() == 0) return;
		if(!(entAmount > list.size())) {
			list = list.subList(0, entAmount);
		}
		
		for (MobEntity mobEntity : list) {
			mobEntity.attackEntityFrom(DamageSource.GENERIC, mobEntity.getHealth());
		}
				
	}
	
	
	public static void addUpgrade(ItemStack stack) {
		Item item = stack.getItem();
		
	}

	private boolean doTick() {
		if (this.tickDown != 0) {
			this.tickDown--;
			return false;
		} else {
			this.tickDown = Math.round(this.tickDownInit / speedUpgrades);
			return true;
		}
	}
}
