package com.darahz.dmod.init;

import com.darahz.dmod.blocks.BlockFreezingElement;
import com.darahz.dmod.blocks.BlockMobSlaughterHouse;
import com.darahz.dmod.blocks.BlockReprogrammedSpawner;
import com.darahz.dmod.blocks.tile.TileEntityFreezingElement;
import com.darahz.dmod.blocks.tile.TileEntityMobSlaughterHouse;
import com.darahz.dmod.blocks.tile.TileEntityReprogrammedSpawner;

import cpw.mods.fml.common.registry.GameRegistry;

public final class ModBlocks {

    public static BlockFreezingElement freezingElement;
    public static BlockMobSlaughterHouse mobSlaughterHouse;
    public static BlockReprogrammedSpawner reprogrammedSpawner;

    public static void register() {
        freezingElement = new BlockFreezingElement();
        mobSlaughterHouse = new BlockMobSlaughterHouse();
        reprogrammedSpawner = new BlockReprogrammedSpawner();

        GameRegistry.registerBlock(freezingElement, "freezingelement_block");
        GameRegistry.registerBlock(mobSlaughterHouse, "mobslaughterhouse_block");
        GameRegistry.registerBlock(reprogrammedSpawner, "reprogrammedspawner_block");

        GameRegistry.registerTileEntity(TileEntityFreezingElement.class,
                "dmod_freezingelement");
        GameRegistry.registerTileEntity(TileEntityMobSlaughterHouse.class,
                "dmod_mobslaughterhouse");
        GameRegistry.registerTileEntity(TileEntityReprogrammedSpawner.class,
                "dmod_reprogrammedspawner");
    }

    private ModBlocks() {}
}
