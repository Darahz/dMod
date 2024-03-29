package com.darahz.dmod.tileentities;

import com.darahz.dmod.dMod;
import com.darahz.dmod.objects.BlockInit;
import com.darahz.dmod.objects.blocks.tileentities.FreezingElementTileEntity;
import com.darahz.dmod.objects.blocks.tileentities.MobSlaughterHouseTileEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class dmodTileEntityType {

	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES,dMod.MOD_ID);
	
	public static final RegistryObject<TileEntityType<FreezingElementTileEntity>> FREEZING_ELEMENT = TILE_ENTITY_TYPES.register("freezingelement_block", () -> TileEntityType.Builder.create(FreezingElementTileEntity::new,BlockInit.freezingelement_block).build(null));
	public static final RegistryObject<TileEntityType<MobSlaughterHouseTileEntity>> MOBSLAUGHTERHOUSE = TILE_ENTITY_TYPES.register("mobslaughterhouse_block", () -> TileEntityType.Builder.create(MobSlaughterHouseTileEntity::new,BlockInit.mobslaughterhouse_block).build(null));
}
