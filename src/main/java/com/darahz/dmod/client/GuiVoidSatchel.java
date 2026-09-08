package com.darahz.dmod.client;

import org.lwjgl.opengl.GL11;

import com.darahz.dmod.inventory.ContainerVoidSatchel;
import com.darahz.dmod.inventory.InventoryVoidSatchel;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

/** Reuses vanilla's hopper background, which is already exactly five slots. */
@SideOnly(Side.CLIENT)
public class GuiVoidSatchel extends GuiContainer {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("textures/gui/container/hopper.png");

    private final InventoryPlayer playerInventory;

    public GuiVoidSatchel(InventoryPlayer playerInventory, InventoryVoidSatchel satchel, int lockedSlot) {
        super(new ContainerVoidSatchel(playerInventory, satchel, lockedSlot));
        this.playerInventory = playerInventory;
        this.allowUserInput = false;
        this.ySize = 133;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRendererObj.drawString(I18n.format(InventoryVoidSatchel.NAME_KEY), 8, 6, 4210752);
        fontRendererObj.drawString(
                I18n.format(playerInventory.getInventoryName()), 8, ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(TEXTURE);
        final int x = (width - xSize) / 2;
        final int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
}
