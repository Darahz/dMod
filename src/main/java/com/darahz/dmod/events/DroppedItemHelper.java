package com.darahz.dmod.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.darahz.dmod.dMod;
import com.darahz.dmod.objects.items.TearOfDisenchantment;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = dMod.MOD_ID, bus = Bus.FORGE)
public class DroppedItemHelper {

	private static int tickDown = 200;
	private static final int tickDownInit = 200;
	private static List<ItemEntity> droppedTears = null;

	@SubscribeEvent
	public static void EntityJoinedWorld(EntityJoinWorldEvent event) {
		if (!(event.getEntity() instanceof ItemEntity))
			return;
		if (droppedTears == null)
			droppedTears = new ArrayList<ItemEntity>();
		final Item item = ((ItemEntity) event.getEntity()).getItem().getItem();
		if (item instanceof TearOfDisenchantment)
			if (!droppedTears.contains((event.getEntity())))
				droppedTears.add(((ItemEntity) event.getEntity()));
	}

	@SubscribeEvent
	public static void tickEvent(TickEvent.WorldTickEvent tick) {
		waitTick(); // Wait for 10 second -> Tick
		if (droppedTears == null)
			return;

		if (!droppedTears.isEmpty())
			for (final ItemEntity tear : droppedTears) {
				if (!tear.isAlive()) {
					droppedTears.remove(tear);
					break;
				}
				final ItemEntity ent = tear;
				final World world = ent.getEntity().world;
				if (ent.onGround) {
					final List<Entity> list = world
							.getEntitiesWithinAABBExcludingEntity(ent,
									ent.getBoundingBox().expand(1, 1, 1));
					final BlockPos pos = ent.getPosition();
					final Block block = world.getBlockState(pos).getBlock();
					final TileEntity tileEnt = world
							.getTileEntity(pos.add(0, -1, 0));

					if (block != Blocks.AIR && block != Blocks.SOUL_SAND
							|| !(block instanceof LeavesBlock)
							|| tileEnt != null)
						world.setBlockState(pos.add(0, -1, 0),
								Blocks.SOUL_SAND.getDefaultState());
					else
						return;

					for (final Entity itemInSpace : list)
						if (itemInSpace.getEntity() instanceof ItemEntity) {
							final Map<Enchantment, Integer> enchantments = EnchantmentHelper
									.getEnchantments(((ItemEntity) itemInSpace
											.getEntity()).getItem());
							if (enchantments.size() == 0)
								break;
							final Boolean isItemDamaged = ((ItemEntity) itemInSpace
									.getEntity()).getItem().isDamaged();
							if (isItemDamaged) {
								final List<Enchantment> remRandom = new ArrayList<Enchantment>(
										enchantments.keySet());
								enchantments.get(remRandom.get(world.getRandom()
										.nextInt(remRandom.size())));
							}

							final ItemStack oldDroppedItem = new ItemStack(
									((ItemEntity) itemInSpace.getEntity())
											.getItem().getItem());
							for (final Entry<Enchantment, Integer> entry : enchantments
									.entrySet()) {

								final ItemStack stack = new ItemStack(
										Items.ENCHANTED_BOOK);
								stack.addEnchantment(entry.getKey(),
										entry.getValue());
								if (!world.isRemote) {
									final ItemEntity enchantedStack = new ItemEntity(
											world, ent.lastTickPosX,
											ent.lastTickPosY, ent.lastTickPosZ,
											stack);

									enchantedStack.setPickupDelay(60);

									if (!world.isRemote)
										world.addEntity(enchantedStack);

									itemInSpace.remove();
									tear.remove();
								}
							}
							if (!world.isRemote) {
								final ItemStack unchantedItem = new ItemStack(
										oldDroppedItem.getItem().getItem(), 1);
								unchantedItem.setDamage(
										oldDroppedItem.getMaxDamage() / 2);
								world.addEntity(new ItemEntity(world,
										ent.lastTickPosX, ent.lastTickPosY,
										ent.lastTickPosZ, unchantedItem));

							}
						}
				}
			}
	}

	private static void waitTick() {
		if (tickDown != 0) {
			tickDown--;
			return;
		}
		tickDown = tickDownInit;
	}

}
