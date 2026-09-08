package com.darahz.dmod.items;

import java.util.List;

import com.darahz.dmod.DModTab;
import com.darahz.dmod.Reference;
import com.darahz.dmod.helpers.KeyboardHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

/**
 * Holds a set of spawner settings in its NBT and stamps them onto any vanilla
 * spawner you right click.
 *
 * <p>The setting currently being edited is addressed by an index into
 * {@link #KEYS}. The 1.15 original indexed into
 * {@code NBTTagCompound.keySet().toArray()}, which is backed by a HashMap and
 * therefore has no stable order -- the selected setting could change between
 * sessions or even between reads. A fixed array pins it.
 */
public class ItemSpawnerReprogrammer extends Item {

    public static final String TAG_DATA = "spawnerData";
    public static final String TAG_SELECTED = "selectedValue";

    /** Editable settings, in display order. The last entry is the reset action. */
    public static final String[] KEYS = {
            "Delay",
            "MinSpawnDelay",
            "MaxSpawnDelay",
            "SpawnCount",
            "MaxNearbyEntities",
            "RequiredPlayerRange",
            "SpawnRange",
            "RESETDATA",
    };

    private static final short[] DEFAULTS = { 200, 200, 400, 1, 6, 16, 4, 404 };

    /** Index of the pseudo-setting that resets the tool. */
    public static final int RESET_INDEX = KEYS.length - 1;

    public ItemSpawnerReprogrammer() {
        setUnlocalizedName("dmod.spawnerreprogramming_tool");
        setTextureName(Reference.RES + "spawnerreprogramming_tool");
        setCreativeTab(DModTab.INSTANCE);
        setMaxStackSize(1);
    }

    /** Fills in the default settings if this stack has never been configured. */
    public static NBTTagCompound ensureData(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        if (!nbt.hasKey(TAG_DATA, 10)) {
            final NBTTagCompound data = new NBTTagCompound();
            for (int i = 0; i < KEYS.length; i++) {
                data.setShort(KEYS[i], DEFAULTS[i]);
            }
            nbt.setTag(TAG_DATA, data);
            nbt.setInteger(TAG_SELECTED, 0);
        }
        return nbt;
    }

    public static void resetData(ItemStack stack) {
        stack.setTagCompound(new NBTTagCompound());
        ensureData(stack);
    }

    /** Clamps the delay range so min never exceeds max. */
    public static void clampDelays(NBTTagCompound data) {
        final short min = data.getShort("MinSpawnDelay");
        final short max = data.getShort("MaxSpawnDelay");
        if (min > max) {
            data.setShort("MaxSpawnDelay", min);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        if (KeyboardHelper.isHoldingShift()) {
            tooltip.add("Shift + Scroll to change the selected setting");
            tooltip.add("Shift + L-Mouse to increase the value by 1");
            tooltip.add("Shift + R-Mouse to decrease the value by 1");
            tooltip.add("Shift + Ctrl + L/R-Mouse to change it by 10");
            tooltip.add(EnumChatFormatting.DARK_RED + "Reset all values by selecting 'RESETDATA'");
        } else {
            tooltip.add(EnumChatFormatting.GREEN + "Hold shift for more info.");
        }
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
            int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

        if (world.getBlock(x, y, z) != Blocks.mob_spawner) {
            return false;
        }
        // The original returned early when !isRemote, so the settings were only
        // ever applied to the client's throwaway copy of the world and never
        // persisted. Apply them on the server instead.
        if (world.isRemote) {
            return true;
        }

        final TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityMobSpawner)) {
            return false;
        }

        final NBTTagCompound nbt = ensureData(stack);
        final NBTTagCompound data = nbt.getCompoundTag(TAG_DATA);
        final int selected = clampIndex(nbt.getInteger(TAG_SELECTED));

        if (selected == RESET_INDEX) {
            resetData(stack);
            player.addChatComponentMessage(
                    new ChatComponentText(EnumChatFormatting.RED + " > Data reset"));
            return true;
        }

        clampDelays(data);

        final NBTTagCompound spawner = new NBTTagCompound();
        tile.writeToNBT(spawner);
        for (int i = 0; i < RESET_INDEX; i++) {
            spawner.setShort(KEYS[i], data.getShort(KEYS[i]));
        }
        tile.readFromNBT(spawner);
        tile.markDirty();
        world.markBlockForUpdate(x, y, z);

        player.swingItem();
        return true;
    }

    public static int clampIndex(int index) {
        if (index < 0 || index >= KEYS.length) {
            return 0;
        }
        return index;
    }
}
