package com.darahz.dmod.blocks.tile;

import com.darahz.dmod.helpers.NumberHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * Spawns its stored creature on a timer.
 *
 * <p>The 1.15 original kept the entity type in a {@code static} field, so
 * every spawner in the world shared one creature and the last block placed
 * won. It is stored per-instance and saved to NBT here.
 */
public class TileEntityReprogrammedSpawner extends TileEntity {

    private static final int CYCLE_TICKS = 120;
    private static final String DEFAULT_ENTITY = "Pig";
    /** Horizontal scatter applied to each spawn. */
    private static final int SCATTER = 4;

    private String entityName = DEFAULT_ENTITY;
    private int tickDown = CYCLE_TICKS;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String name) {
        if (name != null && !name.isEmpty()) {
            entityName = name;
            markDirty();
        }
    }

    @Override
    public void updateEntity() {
        if (worldObj == null) {
            return;
        }

        if (worldObj.isRemote) {
            worldObj.spawnParticle("portal",
                    xCoord + worldObj.rand.nextFloat(),
                    yCoord + worldObj.rand.nextFloat(),
                    zCoord + worldObj.rand.nextFloat(),
                    0.0D, 0.0D, 0.0D);
            return;
        }

        if (--tickDown > 0) {
            return;
        }
        tickDown = CYCLE_TICKS;

        final Entity entity = EntityList.createEntityByName(entityName, worldObj);
        if (entity == null) {
            return;
        }

        final int x = xCoord + NumberHelper.getRandomNumberInRange(-SCATTER, SCATTER);
        final int z = zCoord + NumberHelper.getRandomNumberInRange(-SCATTER, SCATTER);
        // Only spawn where there is actually room, so mobs don't suffocate
        // inside the floor.
        if (!worldObj.isAirBlock(x, yCoord + 1, z) || !worldObj.isAirBlock(x, yCoord + 2, z)) {
            return;
        }

        entity.setLocationAndAngles(x + 0.5D, yCoord + 1, z + 0.5D,
                worldObj.rand.nextFloat() * 360.0F, 0.0F);
        worldObj.spawnEntityInWorld(entity);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey("EntityName")) {
            entityName = nbt.getString("EntityName");
        }
        tickDown = nbt.hasKey("TickDown") ? nbt.getInteger("TickDown") : CYCLE_TICKS;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setString("EntityName", entityName);
        nbt.setInteger("TickDown", tickDown);
    }
}
