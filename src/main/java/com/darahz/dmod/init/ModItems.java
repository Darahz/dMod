package com.darahz.dmod.init;

import com.darahz.dmod.items.ItemAttractionDevice;
import com.darahz.dmod.items.ItemCraftingWidget;
import com.darahz.dmod.items.ItemMobRelocator;
import com.darahz.dmod.items.ItemMobRepellant;
import com.darahz.dmod.items.ItemNecklaceOfRepair;
import com.darahz.dmod.items.ItemSpawnerRelocator;
import com.darahz.dmod.items.ItemSpawnerReprogrammer;
import com.darahz.dmod.items.ItemTearOfDisenchantment;

import cpw.mods.fml.common.registry.GameRegistry;

public final class ModItems {

    public static ItemMobRelocator mobRelocator;
    public static ItemSpawnerRelocator spawnerRelocator;
    public static ItemSpawnerReprogrammer spawnerReprogrammer;
    public static ItemTearOfDisenchantment tearOfDisenchantment;
    public static ItemNecklaceOfRepair necklaceOfRepair;
    public static ItemCraftingWidget craftingWidget;
    public static ItemAttractionDevice attractionDevice;
    public static ItemMobRepellant mobRepellant;

    public static void register() {
        mobRelocator = new ItemMobRelocator();
        spawnerRelocator = new ItemSpawnerRelocator();
        spawnerReprogrammer = new ItemSpawnerReprogrammer();
        tearOfDisenchantment = new ItemTearOfDisenchantment();
        necklaceOfRepair = new ItemNecklaceOfRepair();
        craftingWidget = new ItemCraftingWidget();
        attractionDevice = new ItemAttractionDevice();
        mobRepellant = new ItemMobRepellant();

        GameRegistry.registerItem(mobRelocator, "mobrelocating_tool");
        GameRegistry.registerItem(spawnerRelocator, "spawnerrelocating_tool");
        GameRegistry.registerItem(spawnerReprogrammer, "spawnerreprogramming_tool");
        GameRegistry.registerItem(tearOfDisenchantment, "tearofdisenchantment");
        GameRegistry.registerItem(necklaceOfRepair, "necklaceofrepair");
        GameRegistry.registerItem(craftingWidget, "craftingwidget");
        GameRegistry.registerItem(attractionDevice, "itemattractiondevice");
        GameRegistry.registerItem(mobRepellant, "mobrepellant");
    }

    private ModItems() {}
}
