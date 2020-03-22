package com.darahz.dmod.events;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;

public class AvoidPlayer<T extends LivingEntity> extends AvoidEntityGoal<T> {

	public AvoidPlayer(CreatureEntity entityIn, Class<T> classToAvoidIn,
			float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
		super(entityIn, classToAvoidIn, avoidDistanceIn, farSpeedIn,
				nearSpeedIn);
		// TODO Auto-generated constructor stub
	}

}
