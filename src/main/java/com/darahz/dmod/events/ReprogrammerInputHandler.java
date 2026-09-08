package com.darahz.dmod.events;

import com.darahz.dmod.client.ReprogrammerHud;
import com.darahz.dmod.helpers.KeyboardHelper;
import com.darahz.dmod.items.ItemSpawnerReprogrammer;
import com.darahz.dmod.network.DModNetwork;
import com.darahz.dmod.network.MessageReprogrammerAdjust;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.MouseEvent;

/**
 * Turns shift + scroll / shift + click into re-programmer adjustments.
 *
 * <p>Only sends a request; the server owns the values (see
 * {@link MessageReprogrammerAdjust}).
 */
@SideOnly(Side.CLIENT)
public class ReprogrammerInputHandler {

    private final ReprogrammerHud hud;

    public ReprogrammerInputHandler(ReprogrammerHud hud) {
        this.hud = hud;
    }

    @SubscribeEvent
    public void onMouse(MouseEvent event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.currentScreen != null) {
            return;
        }

        final ItemStack stack = mc.thePlayer.getHeldItem();
        if (stack == null || !(stack.getItem() instanceof ItemSpawnerReprogrammer)) {
            return;
        }
        if (!KeyboardHelper.isHoldingShift()) {
            return;
        }

        final boolean coarse = KeyboardHelper.isHoldingControl();

        if (event.dwheel != 0) {
            send(event.dwheel > 0
                    ? MessageReprogrammerAdjust.ACTION_NEXT
                    : MessageReprogrammerAdjust.ACTION_PREV, coarse);
            mc.thePlayer.playSound("mob.chicken.step", 0.15F, 1.0F);
            event.setCanceled(true);
            return;
        }

        // Only react to the press, not the release.
        if (!event.buttonstate) {
            return;
        }
        if (event.button == 0) {
            send(MessageReprogrammerAdjust.ACTION_INCREASE, coarse);
            event.setCanceled(true);
        } else if (event.button == 1) {
            send(MessageReprogrammerAdjust.ACTION_DECREASE, coarse);
            event.setCanceled(true);
        }
    }

    private void send(byte action, boolean coarse) {
        DModNetwork.channel.sendToServer(new MessageReprogrammerAdjust(action, coarse));
        hud.show();
    }
}
