package com.darahz.dmod.objects.items;

import java.util.List;
import com.darahz.dmod.helpers.KeyboardHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.spawner.AbstractSpawner;

public class Mobrelocator extends Item {

	public Mobrelocator(Properties properties) {
		super(properties);
		setRegistryName("mobrelocating_tool");
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		return stack.hasTag();
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if(stack.hasTag()) {
			CompoundNBT NBT = (CompoundNBT) stack.getTag().get("entity");
			tooltip.add(new StringTextComponent(TextFormatting.GRAY + "   Creature captured: " + TextFormatting.WHITE + NBT.getString("id")));
			tooltip.add(new StringTextComponent(TextFormatting.GRAY + "   Creature health: " + TextFormatting.WHITE + NBT.getDouble("Health")));
		}else {
			if(KeyboardHelper.isHoldingShift()) {
				tooltip.add(new StringTextComponent("Right click a creature to capture it."));
				tooltip.add(new StringTextComponent("Right click a spawner to set creature as spawned entity."));
				tooltip.add(new StringTextComponent(" > Use the " + TextFormatting.GOLD + "Spawner re-programmer 2k^2" + TextFormatting.RESET));
				tooltip.add(new StringTextComponent("   to change more settings of the spawner"));
			}else {
				tooltip.add(new StringTextComponent(TextFormatting.GREEN +"Hold shift for more info."));
			}
			
		}
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {

		if(!context.getItem().hasTag()) return ActionResultType.FAIL;
		
        CompoundNBT NBT = (CompoundNBT) context.getItem().getTag().get("entity");
        EntityType<?> type = EntityType.byKey(NBT.getString("id")).orElse(null);
        
        if(type == null) return ActionResultType.FAIL;
        
        Entity entity = type.create(context.getWorld());
        entity.read(NBT);
        
        BlockState state = context.getWorld().getBlockState(context.getPos());
        if(state.getBlock() instanceof SpawnerBlock) {
        	
        	MobSpawnerTileEntity s = (MobSpawnerTileEntity)context.getWorld().getTileEntity(context.getPos());
        	AbstractSpawner spawns = s.getSpawnerBaseLogic();
        	spawns.setEntityType(entity.getType());
        	spawns.setDelayToMin(0);
        	
        	context.getItem().setTag(new CompoundNBT());
        } else {
        	BlockPos blockPos = context.getPos().offset(context.getFace());
            entity.setPositionAndRotation(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 0, 0);
            context.getWorld().addEntity(entity);
            context.getItem().setTag(new CompoundNBT());
        }
        context.getPlayer().swingArm(context.getHand());
		return super.onItemUse(context);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {

		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity entity, Hand hand) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("entity", entity.serializeNBT());

        stack.setTag(nbt);
        entity.remove(true);
        playerIn.setHeldItem(hand, stack);

		return super.itemInteractionForEntity(stack, playerIn, entity, hand);
	}
	
}
