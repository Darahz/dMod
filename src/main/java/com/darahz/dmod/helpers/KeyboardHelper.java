package com.darahz.dmod.helpers;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Client-only keyboard polling.
 *
 * <p>LWJGL is not present on a dedicated server, so every method here is
 * marked {@link SideOnly} and must only ever be reached from client code
 * (tooltips, HUD rendering, input events).
 */
@SideOnly(Side.CLIENT)
public final class KeyboardHelper {

    public static boolean isHoldingShift() {
        return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)
                || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
    }

    public static boolean isHoldingControl() {
        return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)
                || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
    }

    private KeyboardHelper() {}
}
