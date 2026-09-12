package com.darahz.dmod.blocks;

import com.darahz.dmod.DMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

/** Drops the ore block itself for smelting; requires an iron pickaxe or better. */
public class BlockZeniteOre extends Block {
    public BlockZeniteOre() {
        super(Material.rock);
        setBlockName(DMod.MODID + ".zenite_ore");
        setBlockTextureName(DMod.MODID + ":zenite_ore");
        setCreativeTab(DMod.TAB);
        setHardness(3.0F);
        setResistance(5.0F);
        setStepSound(soundTypeStone);
        setHarvestLevel("pickaxe", 2);
    }
}
