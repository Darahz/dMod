package com.darahz.dmod.init;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * The 1.15 version shipped two data-driven recipe JSONs; 1.7.10 has no recipe
 * data pack, so they are declared in code with the same patterns.
 */
public final class ModRecipes {

    public static void register() {
        // ixi / SxS / SSS  -- iron bars, snow blocks, stone
        GameRegistry.addRecipe(new ItemStack(ModBlocks.freezingElement),
                "ixi",
                "SxS",
                "SSS",
                'i', Blocks.iron_bars,
                'x', Blocks.snow,
                'S', Blocks.stone);

        // eii / gid / iii  -- ender pearl, iron, glowstone dust, diamond
        GameRegistry.addRecipe(new ItemStack(ModItems.spawnerRelocator),
                "eii",
                "gid",
                "iii",
                'e', Items.ender_pearl,
                'i', Items.iron_ingot,
                'g', Items.glowstone_dust,
                'd', Items.diamond);
    }

    private ModRecipes() {}
}
