package com.darahz.dmod;

import com.darahz.dmod.blocks.BlockExample;
import com.darahz.dmod.blocks.tile.TileEntityExample;
import com.darahz.dmod.items.ItemExample;
import com.darahz.dmod.items.ItemVoidKey;
import com.darahz.dmod.blocks.BlockZeniteOre;
import com.darahz.dmod.blocks.BlockVoidCondenser;
import com.darahz.dmod.blocks.tile.TileEntityVoidCondenser;
import com.darahz.dmod.init.ModRecipes;
import com.darahz.dmod.world.ZeniteOreGenerator;
import com.darahz.dmod.dimension.VoidDimension;
import com.darahz.dmod.dimension.CommandVoid;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

@Mod(modid = DMod.MODID, name = DMod.NAME, version = DMod.VERSION,
        acceptedMinecraftVersions = "[1.7.10]")
public class DMod {
    public static final String MODID = "dmod";
    public static final String NAME = "dMod";
    public static final String VERSION = "1.0";

    public static Block exampleBlock;
    public static Item exampleItem;
    public static Item voidKey;
    public static Block zeniteOre;
    public static Block voidCondenser;
    public static Item zeniteIngot;
    public static Item voidFragment;

    public static final CreativeTabs TAB = new CreativeTabs(MODID) {
        @Override
        public Item getTabIconItem() {
            return exampleItem;
        }
    };

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        VoidDimension.register(event.getSuggestedConfigurationFile());
        exampleBlock = new BlockExample();
        exampleItem = new ItemExample();
        voidKey = new ItemVoidKey();
        zeniteOre = new BlockZeniteOre();
        voidCondenser = new BlockVoidCondenser();
        zeniteIngot = new Item().setUnlocalizedName(MODID + ".zenite_ingot")
                .setTextureName(MODID + ":zenite_ingot").setCreativeTab(TAB);
        voidFragment = new Item().setUnlocalizedName(MODID + ".void_fragment")
                .setTextureName(MODID + ":void_fragment").setCreativeTab(TAB);

        GameRegistry.registerBlock(exampleBlock, "example_block");
        GameRegistry.registerItem(exampleItem, "example_item");
        GameRegistry.registerItem(voidKey, "void_key");
        GameRegistry.registerBlock(zeniteOre, "zenite_ore");
        GameRegistry.registerBlock(voidCondenser, "void_condenser");
        GameRegistry.registerItem(zeniteIngot, "zenite_ingot");
        GameRegistry.registerItem(voidFragment, "void_fragment");
        GameRegistry.registerTileEntity(TileEntityVoidCondenser.class, MODID + ":void_condenser");
        GameRegistry.registerWorldGenerator(new ZeniteOreGenerator(), 0);
        ModRecipes.register();
        // Keep this ID stable: saved worlds use it to identify the tile entity.
        GameRegistry.registerTileEntity(TileEntityExample.class, MODID + ":example_tile_entity");
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandVoid());
    }
}
