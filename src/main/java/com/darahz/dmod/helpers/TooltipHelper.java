package com.darahz.dmod.helpers;

import java.util.List;

import net.minecraft.util.EnumChatFormatting;

/**
 * Shared "hold shift for more" tooltip behaviour.
 *
 * <p>Adds directly to the supplied list rather than returning a new one --
 * the 1.15 original returned a list the callers then appended, which meant
 * the default line and the expanded lines could both end up shown.
 */
public final class TooltipHelper {

    public static void holdingShiftTooltip(List<String> tooltip, String defaultLine, String... expanded) {
        if (KeyboardHelper.isHoldingShift()) {
            for (final String line : expanded) {
                tooltip.add(line);
            }
        } else {
            if (defaultLine != null) {
                tooltip.add(defaultLine);
            }
            tooltip.add(EnumChatFormatting.GREEN + "Hold shift for more info.");
        }
    }

    public static void holdingShiftTooltip(List<String> tooltip, String... expanded) {
        holdingShiftTooltip(tooltip, null, expanded);
    }

    private TooltipHelper() {}
}
