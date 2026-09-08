package com.darahz.dmod.client;

import com.darahz.dmod.items.ItemSpawnerReprogrammer;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/**
 * Lists the re-programmer's settings in the top-left corner and highlights the
 * one currently selected.
 */
@SideOnly(Side.CLIENT)
public class ReprogrammerHud {

    /** How long the panel stays up after the last adjustment, in ticks. */
    private static final int LINGER = 120;
    /** Ticks spent fading out at the end of the linger. */
    private static final int FADE = 20;

    private static final int LINE_HEIGHT = 10;
    private static final int PANEL_X = 4;
    private static final int PANEL_Y = 4;
    private static final int SELECTED_COLOUR = 0x3D85C6;
    private static final int NORMAL_COLOUR = 0xFFFFFF;

    private int visibleTicks;

    /** Called by the input handler whenever the player changes something. */
    public void show() {
        visibleTicks = LINGER;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && visibleTicks > 0) {
            visibleTicks--;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRenderText(RenderGameOverlayEvent.Text event) {
        if (visibleTicks <= 0) {
            return;
        }

        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.currentScreen != null) {
            return;
        }

        final ItemStack stack = mc.thePlayer.getHeldItem();
        if (stack == null || !(stack.getItem() instanceof ItemSpawnerReprogrammer)) {
            return;
        }

        final NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null || !nbt.hasKey(ItemSpawnerReprogrammer.TAG_DATA, 10)) {
            return;
        }

        final NBTTagCompound data = nbt.getCompoundTag(ItemSpawnerReprogrammer.TAG_DATA);
        final int selected = ItemSpawnerReprogrammer.clampIndex(
                nbt.getInteger(ItemSpawnerReprogrammer.TAG_SELECTED));
        final String[] keys = ItemSpawnerReprogrammer.KEYS;

        final int alpha = alpha255();
        if (alpha <= 0) {
            return;
        }

        final FontRenderer font = mc.fontRenderer;
        int widest = 0;
        for (final String key : keys) {
            widest = Math.max(widest, font.getStringWidth(label(key, data)));
        }

        Gui.drawRect(PANEL_X - 2, PANEL_Y - 2,
                PANEL_X + widest + 2, PANEL_Y + keys.length * LINE_HEIGHT,
                ((alpha / 3) << 24));

        for (int i = 0; i < keys.length; i++) {
            final int colour = (i == selected ? SELECTED_COLOUR : NORMAL_COLOUR) | (alpha << 24);
            font.drawStringWithShadow(label(keys[i], data),
                    PANEL_X, PANEL_Y + i * LINE_HEIGHT, colour);
        }
    }

    private static String label(String key, NBTTagCompound data) {
        if (key.equals("RESETDATA")) {
            return "RESETDATA";
        }
        return key + " : " + data.getShort(key);
    }

    /** Full opacity until the last {@link #FADE} ticks, then ramps down. */
    private int alpha255() {
        if (visibleTicks >= FADE) {
            return 255;
        }
        return Math.max(0, (visibleTicks * 255) / FADE);
    }
}
