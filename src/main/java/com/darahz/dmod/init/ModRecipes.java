package com.darahz.dmod.init;

import com.darahz.dmod.DMod;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/** Crafting and furnace recipes live here. */
public final class ModRecipes {
    private ModRecipes() {}

    public static void register() {
        GameRegistry.addRecipe(new ItemStack(DMod.voidKey), " O ", "OEO", " D ",
                'O', Blocks.obsidian, 'E', Items.ender_eye, 'D', Items.diamond);
        GameRegistry.addSmelting(DMod.zeniteOre, new ItemStack(DMod.zeniteIngot), 0.7F);
        GameRegistry.addRecipe(new ItemStack(DMod.voidCondenser), "ZGZ", "ZEZ", "ZOZ",
                'Z', DMod.zeniteIngot, 'G', Blocks.glass, 'E', Items.ender_eye, 'O', Blocks.obsidian);
    }
}
