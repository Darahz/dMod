package com.darahz.dmod.blocks;

import com.darahz.dmod.DMod;
import com.darahz.dmod.blocks.tile.TileEntityVoidCondenser;
import com.darahz.dmod.dimension.VoidDimension;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

public class BlockVoidCondenser extends BlockContainer {
    public BlockVoidCondenser() {
        super(Material.iron);
        setBlockName(DMod.MODID + ".void_condenser");
        setBlockTextureName(DMod.MODID + ":void_condenser");
        setCreativeTab(DMod.TAB);
        setHardness(4.0F);
        setResistance(10.0F);
        setStepSound(soundTypeMetal);
        setHarvestLevel("pickaxe", 1);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityVoidCondenser();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
            int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityVoidCondenser)) {
            return true;
        }
        TileEntityVoidCondenser condenser = (TileEntityVoidCondenser) tile;
        if (condenser.getStored() > 0) {
            int available = condenser.getStored();
            ItemStack output = new ItemStack(DMod.voidFragment, available);
            player.inventory.addItemStackToInventory(output);
            int collected = available - output.stackSize;
            condenser.removeFragments(collected);
            player.inventory.markDirty();
            player.addChatMessage(new ChatComponentTranslation(
                    collected > 0 ? "chat.dmod.condenser.collected" : "chat.dmod.condenser.inventory_full", collected));
        } else if (world.provider.dimensionId != VoidDimension.dimensionId) {
            player.addChatMessage(new ChatComponentTranslation("chat.dmod.condenser.wrong_dimension"));
        } else {
            int seconds = (TileEntityVoidCondenser.TICKS_PER_FRAGMENT - condenser.getProgress() + 19) / 20;
            player.addChatMessage(new ChatComponentTranslation("chat.dmod.condenser.working", seconds));
        }
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!world.isRemote && tile instanceof TileEntityVoidCondenser) {
            TileEntityVoidCondenser condenser = (TileEntityVoidCondenser) tile;
            if (condenser.getStored() > 0) {
                world.spawnEntityInWorld(new EntityItem(world, x + 0.5D, y + 0.5D, z + 0.5D,
                        new ItemStack(DMod.voidFragment, condenser.getStored())));
            }
        }
        super.breakBlock(world, x, y, z, block, metadata);
    }

    @Override @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        if (world.provider.dimensionId == VoidDimension.dimensionId) {
            double dx = random.nextDouble() - 0.5D;
            double dz = random.nextDouble() - 0.5D;
            world.spawnParticle("portal", x + 0.5D + dx, y + 1.1D, z + 0.5D + dz,
                    -dx * 0.5D, -0.1D, -dz * 0.5D);
        }
    }
}
