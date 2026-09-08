package com.darahz.dmod;

import com.darahz.dmod.init.ModItems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * The mod's creative tab. Forge adds the single-argument CreativeTabs
 * constructor that assigns the next free index for us.
 */
public class DModTab extends CreativeTabs {

    public static final DModTab INSTANCE = new DModTab();

    private DModTab() {
        super("dmodTab");
    }

    @Override
    public Item getTabIconItem() {
        return ModItems.necklaceOfRepair;
    }
}
