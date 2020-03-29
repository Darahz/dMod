package com.darahz.dmod.helpers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class TooltipHelper {

	
	public static List<ITextComponent> holdingShiftTooltip(String[] out,List<ITextComponent> tooltip,String defaultTooltip,World world) {
		if(world.isRemote) {
			List<ITextComponent> outList = new ArrayList<ITextComponent>();
			if(KeyboardHelper.isHoldingShift()) {
				for (String tooltipText : out) {
					outList.add(new StringTextComponent(tooltipText));
				}
			}else {
				if(defaultTooltip != null) {
					outList.add(new StringTextComponent(defaultTooltip));
				}
					
				outList.add(new StringTextComponent(TextFormatting.GREEN + "Hold shift for more info."));
			}
			return outList;
		} else
			return tooltip;
	}
	
	public static List<ITextComponent> holdingShiftTooltip(String[] out,List<ITextComponent> tooltip,World world) {
		if(world.isRemote) {
			List<ITextComponent> outList = new ArrayList<ITextComponent>();
			if(KeyboardHelper.isHoldingShift()) {
				for (String tooltipText : out) {
					outList.add(new StringTextComponent(tooltipText));
				}
			}else {
				outList.add(new StringTextComponent(TextFormatting.GREEN + "Hold shift for more info."));
			}
			return outList;
		} else
			return tooltip;
	}
	
}
