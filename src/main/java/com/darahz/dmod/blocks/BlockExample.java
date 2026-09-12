package com.darahz.dmod.blocks;

import com.darahz.dmod.DMod;
import com.darahz.dmod.blocks.tile.TileEntityExample;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

public class BlockExample extends BlockContainer {
    public BlockExample() {
        super(Material.rock);
        setBlockName(DMod.MODID + ".example_block");
        setBlockTextureName(DMod.MODID + ":example_block");
        setCreativeTab(DMod.TAB);
        setHardness(2.0F);
        setResistance(10.0F);
        setStepSound(soundTypeStone);
        setHarvestLevel("pickaxe", 0);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityExample();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z,
            EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        // Only the server changes saved data, avoiding double increments.
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityExample) {
                TileEntityExample example = (TileEntityExample) tile;
                example.incrementClicks();
                player.addChatMessage(new ChatComponentTranslation(
                        "chat.dmod.example_block.clicks", example.getClicks()));
            }
        }
        return true;
    }
}
