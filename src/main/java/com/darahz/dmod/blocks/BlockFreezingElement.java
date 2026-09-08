package com.darahz.dmod.blocks;

import com.darahz.dmod.DModTab;
import com.darahz.dmod.Reference;
import com.darahz.dmod.blocks.tile.TileEntityFreezingElement;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Freezes nearby liquids and drips snowballs into a container above it.
 *
 * <p>1.7.10 has no block states, so the horizontal facing lives in the block's
 * metadata (0-3, same order as vanilla furnaces).
 */
public class BlockFreezingElement extends Block {

    private IIcon iconTop;
    private IIcon iconFront;

    public BlockFreezingElement() {
        super(Material.iron);
        setBlockName("dmod.freezingelement_block");
        setCreativeTab(DModTab.INSTANCE);
        setHardness(3.0F);
        setHarvestLevel("pickaxe", 1);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileEntityFreezingElement();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        final int facing = MathHelper.floor_double((placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        world.setBlockMetadataWithNotify(x, y, z, facing, 2);
    }

    /**
     * Clears the snow layer this block was maintaining, so breaking it does not
     * leave a floating patch of snow behind.
     */
    @Override
    public void breakBlock(World world, int x, int y, int z, Block broken, int meta) {
        if (world.getBlock(x, y + 1, z) == Blocks.snow_layer) {
            world.setBlockToAir(x, y + 1, z);
        }
        super.breakBlock(world, x, y, z, broken, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        blockIcon = reg.registerIcon(Reference.RES + "freezingelement_block");
        iconTop = reg.registerIcon(Reference.RES + "freezingelement_block_top");
        iconFront = reg.registerIcon(Reference.RES + "freezingelement_block_front");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side == 0 || side == 1) {
            return iconTop;
        }
        // Metadata 0-3 maps to north, east, south, west; sides 2-5 are
        // north, south, west, east.
        final int facingSide;
        switch (meta & 3) {
            case 0:  facingSide = 2; break;
            case 1:  facingSide = 5; break;
            case 2:  facingSide = 3; break;
            default: facingSide = 4; break;
        }
        return side == facingSide ? iconFront : blockIcon;
    }
}
