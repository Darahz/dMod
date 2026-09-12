package com.darahz.dmod.items;

import com.darahz.dmod.DMod;
import com.darahz.dmod.dimension.VoidTravel;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemVoidKey extends Item {
    private static final int CHARGE_TICKS = 40;
    private static final int COOLDOWN_TICKS = 100;
    private static final String COOLDOWN_KEY = "dmodVoidKeyReadyAt";

    public ItemVoidKey() {
        setUnlocalizedName(DMod.MODID + ".void_key");
        setTextureName(DMod.MODID + ":void_key");
        setCreativeTab(DMod.TAB);
        setMaxStackSize(1);
    }

    private long serverTime() {
        // One clock for both dimensions and all keys held by this player.
        return MinecraftServer.getServer().worldServerForDimension(0).getTotalWorldTime();
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote && player.getEntityData().getLong(COOLDOWN_KEY) > serverTime()) {
            player.addChatMessage(new ChatComponentTranslation("chat.dmod.void_key.cooldown"));
            return stack;
        }
        player.setItemInUse(stack, CHARGE_TICKS);
        return stack;
    }

    @Override public int getMaxItemUseDuration(ItemStack stack) { return CHARGE_TICKS; }
    @Override public EnumAction getItemUseAction(ItemStack stack) { return EnumAction.bow; }

    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int remaining) {
        World world = player.worldObj;
        if (world.isRemote) {
            double progress = (CHARGE_TICKS - remaining) / (double) CHARGE_TICKS;
            double angle = progress * Math.PI * 8.0D;
            for (int i = 0; i < 4; i++) {
                double phase = angle + i * Math.PI / 2.0D;
                double dx = Math.cos(phase) * (1.2D - progress * 0.8D);
                double dz = Math.sin(phase) * (1.2D - progress * 0.8D);
                world.spawnParticle("portal", player.posX + dx, player.boundingBox.minY + progress * 1.8D,
                        player.posZ + dz, -dx * 0.2D, 0.1D, -dz * 0.2D);
            }
        } else if (remaining % 10 == 0) {
            world.playSoundAtEntity(player, "note.harp", 0.6F, 0.6F + (CHARGE_TICKS - remaining) / 30.0F);
        }
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        // Vanilla calls this when held use finishes, even for non-food items.
        if (!world.isRemote && player instanceof EntityPlayerMP) {
            if (player.getEntityData().getLong(COOLDOWN_KEY) > serverTime()) {
                return stack;
            }
            try {
                VoidTravel.travel((EntityPlayerMP) player);
                player.getEntityData().setLong(COOLDOWN_KEY, serverTime() + COOLDOWN_TICKS);
            } catch (CommandException failure) {
                player.addChatMessage(new ChatComponentText(failure.getMessage()));
            }
        }
        return stack;
    }

    @Override @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack, int pass) { return true; }

    @Override @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add(StatCollector.translateToLocal("tooltip.dmod.void_key.charge"));
        tooltip.add(StatCollector.translateToLocal("tooltip.dmod.void_key.return"));
    }
}
