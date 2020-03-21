package com.darahz.dmod.objects.blocks.tileentities;

import com.darahz.dmod.helpers.NumberHelper;
import com.darahz.dmod.tileentities.dmodTileEntityType;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ReprogrammedSpawnerTileEntity extends TileEntity
		implements
			ITickableTileEntity {

	private int tickDown = 120;
	private final int tickDownInit = 120;
	private static EntityType<?> entitytype = null;

	public ReprogrammedSpawnerTileEntity() {
		this(dmodTileEntityType.REPROGRAMMED_SPAWNER.get());
		setEntitytype(EntityType.PIG);
	}

	public ReprogrammedSpawnerTileEntity(
			TileEntityType<ReprogrammedSpawnerTileEntity> tileEntityType) {
		super(tileEntityType);
	}

	@Override
	public void tick() {
		final BlockPos pos = this.getPos();
		final World world = this.getWorld();

		if (world.isRemote) {
			final double d3 = (double) pos.getX()
					+ (double) world.rand.nextFloat();
			final double d4 = (double) pos.getY()
					+ (double) world.rand.nextFloat();
			final double d5 = (double) pos.getZ()
					+ (double) world.rand.nextFloat();
			world.addParticle(ParticleTypes.PORTAL, d3, d4, d5, 0.0D, 0.0D,
					0.0D);
		}

		if (doTick())
			if (!world.isRemote) {
				final EntityType<?> type = getEntitytype();
				final Entity entity = type.create(world);

				entity.setPosition(
						(pos.getX() + 1)
								+ NumberHelper.getRandomNumberInRange(-4, 4),
						pos.getY(), (pos.getZ() + 1)
								+ NumberHelper.getRandomNumberInRange(-4, 4));
				world.addEntity(entity);

			} else
				for (int i = 0; i < 20; ++i) {
					final double d0 = world.rand.nextGaussian() * 0.02D;
					final double d1 = world.rand.nextGaussian() * 0.02D;
					final double d2 = world.rand.nextGaussian() * 0.02D;
					this.world.addParticle(ParticleTypes.POOF, pos.getX() + 0.5,
							pos.getY(), pos.getZ() + 0.5, d0, d1, d2);
				}

	}

	private boolean doTick() {
		if (this.tickDown != 0) {
			this.tickDown--;
			return false;
		} else {
			this.tickDown = this.tickDownInit;
			return true;
		}
	}

	public static EntityType<?> getEntitytype() {
		return entitytype;
	}

	public static void setEntitytype(EntityType<?> entitytype) {
		ReprogrammedSpawnerTileEntity.entitytype = entitytype;
	}

}
