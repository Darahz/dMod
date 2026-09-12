package com.darahz.dmod.dimension;

import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

/** Shared server-side travel for the Void Key and testing command. */
public final class VoidTravel {
    private static final String RETURN_KEY = "dmodVoidReturn";

    private VoidTravel() {}

    public static void travel(EntityPlayerMP player) {
        if (player.ridingEntity != null || player.riddenByEntity != null) {
            throw new CommandException("Dismount before travelling to the void.");
        }
        boolean leaving = player.dimension == VoidDimension.dimensionId;
        if (!leaving && player.dimension != 0) {
            throw new CommandException("Enter the void from the Overworld.");
        }
        if (!leaving && !player.onGround && !player.capabilities.isCreativeMode) {
            throw new CommandException("Stand on solid ground before entering the void.");
        }
        NBTTagCompound data = player.getEntityData();
        if (leaving && !data.hasKey(RETURN_KEY)) {
            throw new CommandException("No void return position is saved for this player.");
        }
        NBTTagCompound position;
        if (leaving) {
            position = data.getCompoundTag(RETURN_KEY);
        } else {
            position = new NBTTagCompound();
            position.setDouble("X", player.posX);
            position.setDouble("Y", player.posY);
            position.setDouble("Z", player.posZ);
            position.setFloat("Yaw", player.rotationYaw);
            position.setFloat("Pitch", player.rotationPitch);
            position.setBoolean("Flying", player.capabilities.isFlying);
        }
        final double x = leaving ? position.getDouble("X") : 0.5D;
        final double y = leaving ? position.getDouble("Y") : 65.0D;
        final double z = leaving ? position.getDouble("Z") : 0.5D;
        final float yaw = leaving ? position.getFloat("Yaw") : player.rotationYaw;
        final float pitch = leaving ? position.getFloat("Pitch") : 0.0F;
        int targetId = leaving ? 0 : VoidDimension.dimensionId;
        MinecraftServer server = MinecraftServer.getServer();
        WorldServer target = server.worldServerForDimension(targetId);
        target.getChunkFromBlockCoords((int) Math.floor(x), (int) Math.floor(z));
        if (leaving) {
            net.minecraft.util.AxisAlignedBB landing = net.minecraft.util.AxisAlignedBB.getBoundingBox(
                    x - 0.3D, y, z - 0.3D, x + 0.3D, y + 1.8D, z + 0.3D);
            if (!target.getCollidingBoundingBoxes(player, landing).isEmpty() || target.isAnyLiquid(landing)) {
                throw new CommandException("Your return spot is obstructed. Clear it before returning.");
            }
            if (!player.capabilities.allowFlying
                    && target.getCollidingBoundingBoxes(player, landing.offset(0.0D, -0.1D, 0.0D)).isEmpty()) {
                throw new CommandException("The floor at your return spot is missing. Restore it before returning.");
            }
        }
        if (!leaving) {
            if (!target.isAirBlock(0, 65, 0) || !target.isAirBlock(0, 66, 0)) {
                throw new CommandException("The void landing spot is blocked. Clear the blocks at 0,65,0 and 0,66,0 first.");
            }
            // Restore the landing block on every visit, including existing worlds.
            target.setBlock(0, 64, 0, Blocks.bedrock, 0, 3);
        }
        player.worldObj.playSoundAtEntity(player, "mob.endermen.portal", 1.0F, 0.8F);
        server.getConfigurationManager().transferPlayerToDimension(player, targetId, new Teleporter(target) {
            @Override
            public void placeInPortal(Entity entity, double oldX, double oldY, double oldZ, float oldYaw) {
                entity.setLocationAndAngles(x, y, z, yaw, pitch);
                entity.motionX = entity.motionY = entity.motionZ = 0.0D;
                entity.fallDistance = 0.0F;
            }
        });
        player.playerNetServerHandler.setPlayerLocation(x, y, z, yaw, pitch);
        player.capabilities.isFlying = leaving && player.capabilities.allowFlying && position.getBoolean("Flying");
        player.sendPlayerAbilities();
        if (leaving) {
            data.removeTag(RETURN_KEY);
        } else {
            data.setTag(RETURN_KEY, position);
        }
        target.playSoundAtEntity(player, "mob.endermen.portal", 1.0F, 1.2F);
        player.addChatMessage(new ChatComponentText(leaving ? "Returned from dMod Void."
                : "Entered dMod Void. Charge your Void Key again to return."));
    }
}
