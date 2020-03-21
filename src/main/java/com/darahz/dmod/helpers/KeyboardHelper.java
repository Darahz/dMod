package com.darahz.dmod.helpers;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class KeyboardHelper {

	@OnlyIn(Dist.CLIENT)
	public static boolean isHoldingShift() {
		return InputMappings.isKeyDown(Minecraft.getInstance().func_228018_at_().getHandle(),GLFW.GLFW_KEY_LEFT_SHIFT) || InputMappings.isKeyDown(Minecraft.getInstance().func_228018_at_().getHandle(),GLFW.GLFW_KEY_RIGHT_SHIFT);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static boolean isHoldingControl() {
		return InputMappings.isKeyDown(Minecraft.getInstance().func_228018_at_().getHandle(),GLFW.GLFW_KEY_LEFT_CONTROL) || InputMappings.isKeyDown(Minecraft.getInstance().func_228018_at_().getHandle(),GLFW.GLFW_KEY_RIGHT_CONTROL);
	}
	
}
