package com.darahz.dmod.items;

import java.util.List;

import com.darahz.dmod.DModTab;
import com.darahz.dmod.Reference;
import com.darahz.dmod.helpers.KeyboardHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

/** Pokes a mob into permanently fleeing from players. */
public class ItemMobRepellant extends Item {

    private static final int MAX_USES = 16;
    /** Priority the avoid task is inserted at. */
    private static final int TASK_PRIORITY = 0;

    public ItemMobRepellant() {
        setUnlocalizedName("dmod.mobrepellant");
        setTextureName(Reference.RES + "mobrepellant");
        setCreativeTab(DModTab.INSTANCE);
        setMaxStackSize(1);
        setMaxDamage(MAX_USES);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target) {
        if (!(target instanceof EntityCreature)) {
            return false;
        }
        if (player.worldObj.isRemote) {
            return true;
        }

        final EntityCreature creature = (EntityCreature) target;

        // The original built a fresh AvoidEntity goal, removed *that* new
        // object (a no-op, since it was never in the list) and then added it.
        // Check the existing task list instead so repeated pokes don't stack
        // up dozens of identical tasks.
        if (hasAvoidTask(creature)) {
            return true;
        }

        creature.tasks.addTask(TASK_PRIORITY,
                new EntityAIAvoidEntity(creature, EntityPlayer.class, 16.0F, 0.8D, 1.33D));
        // Vanilla entity event 20 is the "poof" smoke puff.
        creature.worldObj.setEntityState(creature, (byte) 20);

        stack.damageItem(1, player);
        return true;
    }

    /** taskEntries is a raw List in 1.7.10, so walk it as Objects. */
    private static boolean hasAvoidTask(EntityCreature creature) {
        for (final Object entry : creature.tasks.taskEntries) {
            if (((EntityAITasks.EntityAITaskEntry) entry).action instanceof EntityAIAvoidEntity) {
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        if (KeyboardHelper.isHoldingShift()) {
            tooltip.add("Right click a mob to make it flee from players.");
            tooltip.add(EnumChatFormatting.GRAY + "Wears out after " + MAX_USES + " uses.");
        } else {
            tooltip.add(EnumChatFormatting.GREEN + "Hold shift for more info.");
        }
    }
}
