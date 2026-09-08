package com.darahz.dmod.blocks;

import com.darahz.dmod.DModTab;
import com.darahz.dmod.Reference;
import com.darahz.dmod.blocks.tile.TileEntityMobSlaughterHouse;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/** Quietly kills mobs standing near it. */
public class BlockMobSlaughterHouse extends Block {

    public BlockMobSlaughterHouse() {
        super(Material.iron);
        setBlockName("dmod.mobslaughterhouse_block");
        setBlockTextureName(Reference.RES + "mobslaughterhouse_block");
        setCreativeTab(DModTab.INSTANCE);
        setHardness(3.0F);
        setHarvestLevel("pickaxe", 1);
    }

    // The 1.15 version asked for the translucent render layer; the 1.7.10
    // equivalent is a non-opaque block drawn in the alpha pass.
    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileEntityMobSlaughterHouse();
    }

    /**
     * Right clicking with an upgrade material consumes it. The 1.15 original
     * declared {@code addUpgrade(ItemStack)} but left the body empty, with the
     * intended materials listed in its notes file.
     */
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
            int side, float hitX, float hitY, float hitZ) {

        final ItemStack held = player.getHeldItem();
        if (held == null) {
            return false;
        }
        final TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityMobSlaughterHouse)) {
            return false;
        }
        if (world.isRemote) {
            return TileEntityMobSlaughterHouse.isUpgrade(held);
        }

        final TileEntityMobSlaughterHouse slaughter = (TileEntityMobSlaughterHouse) tile;
        if (!slaughter.addUpgrade(held, player)) {
            return false;
        }
        if (!player.capabilities.isCreativeMode) {
            held.stackSize--;
            if (held.stackSize <= 0) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            }
        }
        return true;
    }
}
