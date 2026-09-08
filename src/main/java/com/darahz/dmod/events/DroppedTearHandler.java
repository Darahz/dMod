package com.darahz.dmod.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.darahz.dmod.items.ItemTearOfDisenchantment;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

/**
 * Watches for a dropped Tear of Disenchantment lying on the ground next to
 * another dropped item, and moves that item's enchantments into books.
 */
public class DroppedTearHandler {

    /** How often the tears are checked, in ticks. */
    private static final int CHECK_INTERVAL = 20;
    /** How far from the tear an item may lie and still be stripped. */
    private static final double REACH = 1.5D;

    private final List<EntityItem> tears = new ArrayList<EntityItem>();
    private int ticks;

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.world.isRemote || !(event.entity instanceof EntityItem)) {
            return;
        }
        final EntityItem item = (EntityItem) event.entity;
        final ItemStack stack = item.getEntityItem();
        if (stack == null || !(stack.getItem() instanceof ItemTearOfDisenchantment)) {
            return;
        }
        if (!tears.contains(item)) {
            tears.add(item);
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote) {
            return;
        }
        // The counter is shared, so only advance it for one world -- otherwise
        // a server with several dimensions ticks it down N times as fast.
        if (event.world.provider.dimensionId != 0) {
            return;
        }
        if (++ticks < CHECK_INTERVAL) {
            return;
        }
        ticks = 0;
        processTears();
    }

    private void processTears() {
        final Iterator<EntityItem> it = tears.iterator();
        while (it.hasNext()) {
            final EntityItem tear = it.next();
            if (tear.isDead || tear.worldObj == null) {
                it.remove();
                continue;
            }
            // An entity in an unloaded chunk is gone but never flagged dead,
            // so drop it here or the list grows without bound.
            if (!tear.worldObj.blockExists((int) tear.posX, (int) tear.posY, (int) tear.posZ)) {
                it.remove();
                continue;
            }
            if (!tear.onGround) {
                continue;
            }
            if (disenchantNeighbour(tear)) {
                it.remove();
            }
        }
    }

    /** @return true when the tear was spent. */
    private boolean disenchantNeighbour(EntityItem tear) {
        final World world = tear.worldObj;
        final AxisAlignedBB box = tear.boundingBox.expand(REACH, REACH, REACH);
        final List<?> nearby = world.getEntitiesWithinAABBExcludingEntity(tear, box);

        for (final Object raw : nearby) {
            if (!(raw instanceof EntityItem)) {
                continue;
            }
            final EntityItem neighbour = (EntityItem) raw;
            if (neighbour.isDead) {
                continue;
            }

            final ItemStack target = neighbour.getEntityItem();
            if (target == null || target.getItem() instanceof ItemTearOfDisenchantment) {
                continue;
            }

            final Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(target);
            if (enchantments.isEmpty()) {
                continue;
            }

            // One book per enchantment.
            for (final Map.Entry<Integer, Integer> entry : enchantments.entrySet()) {
                final Enchantment enchantment = Enchantment.enchantmentsList[entry.getKey().intValue()];
                if (enchantment == null) {
                    continue;
                }
                final ItemStack book = new ItemStack(Items.enchanted_book);
                Items.enchanted_book.addEnchantment(book,
                        new EnchantmentData(enchantment, entry.getValue().intValue()));
                spawn(world, tear, book);
            }

            // Give the item back without its enchantments. The original set the
            // returned item to half durability, which destroyed gear for no
            // stated reason; the spent tear is the cost.
            final ItemStack stripped = target.copy();
            EnchantmentHelper.setEnchantments(new HashMap<Integer, Integer>(), stripped);
            spawn(world, tear, stripped);

            neighbour.setDead();
            tear.setDead();
            return true;
        }
        return false;
    }

    private static void spawn(World world, EntityItem origin, ItemStack stack) {
        final EntityItem drop = new EntityItem(world,
                origin.posX, origin.posY + 0.25D, origin.posZ, stack);
        drop.delayBeforeCanPickup = 20;
        world.spawnEntityInWorld(drop);
    }
}
