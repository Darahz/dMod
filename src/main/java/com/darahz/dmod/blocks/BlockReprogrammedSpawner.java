package com.darahz.dmod.blocks;

import com.darahz.dmod.DModTab;
import com.darahz.dmod.Reference;
import com.darahz.dmod.blocks.tile.TileEntityReprogrammedSpawner;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * A spawner whose creature is set with the Mob Re-locator.
 *
 * <p>This existed in the 1.15 source but was never registered, and referenced
 * a tile entity type that was never declared -- so it could not compile or
 * appear in game. It is wired up properly here.
 */
public class BlockReprogrammedSpawner extends Block {

    public BlockReprogrammedSpawner() {
        super(Material.rock);
        setBlockName("dmod.reprogrammedspawner_block");
        setBlockTextureName(Reference.RES + "reprogrammedspawner_block");
        setCreativeTab(DModTab.INSTANCE);
        setHardness(5.0F);
        setHarvestLevel("pickaxe", 1);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileEntityReprogrammedSpawner();
    }
}
