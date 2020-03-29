package com.darahz.dmod.objects.blocks.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.util.IWorldPosCallable;

public class workbenchOverride extends WorkbenchContainer {

	public workbenchOverride(int p_i50089_1_, PlayerInventory p_i50089_2_) {
		super(p_i50089_1_, p_i50089_2_);
		// TODO Auto-generated constructor stub
	}

	public workbenchOverride(int p_i50090_1_, PlayerInventory p_i50090_2_, IWorldPosCallable p_i50090_3_) {
		super(p_i50090_1_, p_i50090_2_, p_i50090_3_);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}
}
