package com.darahz.dmod.items;

import java.util.List;

import com.darahz.dmod.DModTab;
import com.darahz.dmod.Reference;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.world.World;

/** Picks up a vanilla mob spawner and puts it back down somewhere else. */
public class ItemSpawnerRelocator extends Item {

    private static final String TAG_BLOCK = "block";

    public ItemSpawnerRelocator() {
        setUnlocalizedName("dmod.spawnerrelocating_tool");
        setTextureName(Reference.RES + "spawnerrelocating_tool");
        setCreativeTab(DModTab.INSTANCE);
        setMaxStackSize(1);
    }

    private static boolean isLoaded(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(TAG_BLOCK, 10);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return isLoaded(stack);
    }

    @Override
    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        if (isLoaded(stack)) {
            final NBTTagCompound block = stack.getTagCompound().getCompoundTag(TAG_BLOCK);
            final String id = block.getString("EntityId");
            tooltip.add(EnumChatFormatting.YELLOW + "Spawner Entity" + EnumChatFormatting.RESET
                    + " : " + (id.isEmpty() ? "<empty>" : id));
        } else {
            tooltip.add(EnumChatFormatting.GOLD + "Right click a spawner to move it");
        }
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
            int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

        if (world.isRemote) {
            // Let the client predict a swing only when the action can succeed.
            return isLoaded(stack) || world.getBlock(x, y, z) == Blocks.mob_spawner;
        }

        if (isLoaded(stack)) {
            return placeSpawner(stack, world, x, y, z, side);
        }
        return pickUpSpawner(stack, world, x, y, z);
    }

    private boolean pickUpSpawner(ItemStack stack, World world, int x, int y, int z) {
        if (world.getBlock(x, y, z) != Blocks.mob_spawner) {
            return false;
        }
        final TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityMobSpawner)) {
            return false;
        }

        final NBTTagCompound blockTag = new NBTTagCompound();
        tile.writeToNBT(blockTag);
        // Position is re-assigned on placement, so don't carry the old one.
        blockTag.removeTag("x");
        blockTag.removeTag("y");
        blockTag.removeTag("z");

        final NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag(TAG_BLOCK, blockTag);
        stack.setTagCompound(nbt);

        world.setBlockToAir(x, y, z);
        return true;
    }

    private boolean placeSpawner(ItemStack stack, World world, int x, int y, int z, int side) {
        final ForgeDirection face = ForgeDirection.getOrientation(side);
        final int px = x + face.offsetX;
        final int py = y + face.offsetY;
        final int pz = z + face.offsetZ;

        if (!world.isAirBlock(px, py, pz)) {
            return false;
        }

        world.setBlock(px, py, pz, Blocks.mob_spawner, 0, 3);

        final TileEntity tile = world.getTileEntity(px, py, pz);
        if (!(tile instanceof TileEntityMobSpawner)) {
            return false;
        }

        final NBTTagCompound blockTag = (NBTTagCompound) stack.getTagCompound()
                .getCompoundTag(TAG_BLOCK).copy();
        blockTag.setInteger("x", px);
        blockTag.setInteger("y", py);
        blockTag.setInteger("z", pz);
        tile.readFromNBT(blockTag);
        tile.markDirty();
        world.markBlockForUpdate(px, py, pz);

        stack.setTagCompound(new NBTTagCompound());
        return true;
    }
}
