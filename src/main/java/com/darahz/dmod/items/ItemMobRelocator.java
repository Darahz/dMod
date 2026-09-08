package com.darahz.dmod.items;

import java.util.List;

import com.darahz.dmod.DModTab;
import com.darahz.dmod.Reference;
import com.darahz.dmod.blocks.tile.TileEntityReprogrammedSpawner;
import com.darahz.dmod.helpers.KeyboardHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.world.World;

/**
 * Captures a living entity into the item's NBT, then releases it -- or feeds
 * it to a spawner as the spawned type.
 */
public class ItemMobRelocator extends Item {

    /** Root tag holding the serialised entity. */
    private static final String TAG_ENTITY = "entity";

    public ItemMobRelocator() {
        setUnlocalizedName("dmod.mobrelocating_tool");
        setTextureName(Reference.RES + "mobrelocating_tool");
        setCreativeTab(DModTab.INSTANCE);
        setMaxStackSize(1);
    }

    private static boolean hasCapture(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(TAG_ENTITY, 10);
    }

    private static NBTTagCompound capture(ItemStack stack) {
        return stack.getTagCompound().getCompoundTag(TAG_ENTITY);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return hasCapture(stack);
    }

    @Override
    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        if (hasCapture(stack)) {
            final NBTTagCompound nbt = capture(stack);
            tooltip.add(EnumChatFormatting.GRAY + "   Creature captured: "
                    + EnumChatFormatting.WHITE + nbt.getString("id"));
            tooltip.add(EnumChatFormatting.GRAY + "   Creature health: "
                    + EnumChatFormatting.WHITE + nbt.getShort("Health"));
        } else if (KeyboardHelper.isHoldingShift()) {
            tooltip.add("Right click a creature to capture it.");
            tooltip.add("Right click a spawner to set creature as spawned entity.");
            tooltip.add(" > Use the " + EnumChatFormatting.GOLD
                    + "Spawner re-programmer 2k^2" + EnumChatFormatting.RESET);
            tooltip.add("   to change more settings of the spawner");
        } else {
            tooltip.add(EnumChatFormatting.GREEN + "Hold shift for more info.");
        }
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
            int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

        if (!hasCapture(stack)) {
            return false;
        }
        // All world mutation happens server side; the client just plays the swing.
        if (world.isRemote) {
            return true;
        }

        final NBTTagCompound nbt = capture(stack);
        final Block block = world.getBlock(x, y, z);

        final TileEntity here = world.getTileEntity(x, y, z);

        // Our own spawner block reads the same "right click a spawner to set
        // the spawned creature" behaviour the tooltip already advertises.
        if (here instanceof TileEntityReprogrammedSpawner) {
            ((TileEntityReprogrammedSpawner) here).setEntityName(nbt.getString("id"));
            world.markBlockForUpdate(x, y, z);
            stack.setTagCompound(new NBTTagCompound());
            player.swingItem();
            return true;
        }

        if (block == Blocks.mob_spawner) {
            if (!(here instanceof TileEntityMobSpawner)) {
                return false;
            }
            final MobSpawnerBaseLogic logic = ((TileEntityMobSpawner) here).func_145881_a();
            logic.setEntityName(nbt.getString("id"));
            // Force the next spawn to happen immediately.
            logic.spawnDelay = 0;
            here.markDirty();
            world.markBlockForUpdate(x, y, z);
        } else {
            final Entity entity = EntityList.createEntityFromNBT(nbt, world);
            if (entity == null) {
                return false;
            }
            final ForgeDirection face = ForgeDirection.getOrientation(side);
            entity.setLocationAndAngles(
                    x + face.offsetX + 0.5D,
                    y + face.offsetY + 0.5D,
                    z + face.offsetZ + 0.5D, 0.0F, 0.0F);
            world.spawnEntityInWorld(entity);
        }

        stack.setTagCompound(new NBTTagCompound());
        player.swingItem();
        return true;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target) {
        // Never let the tool swallow another player.
        if (target instanceof EntityPlayer) {
            return false;
        }
        if (hasCapture(stack)) {
            return false;
        }
        if (player.worldObj.isRemote) {
            return true;
        }

        final NBTTagCompound entityTag = new NBTTagCompound();
        if (!target.writeToNBTOptional(entityTag)) {
            return false;
        }

        final NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag(TAG_ENTITY, entityTag);
        stack.setTagCompound(nbt);

        target.setDead();
        return true;
    }
}
