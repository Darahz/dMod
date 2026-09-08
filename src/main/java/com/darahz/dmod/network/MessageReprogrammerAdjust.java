package com.darahz.dmod.network;

import com.darahz.dmod.items.ItemSpawnerReprogrammer;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Sent from the client when the player scrolls or clicks while holding the
 * spawner re-programmer.
 *
 * <p>The 1.15 original edited the held stack's NBT on the client only, so every
 * adjustment was thrown away the next time the server re-sent the slot. The
 * client now only asks; the server owns the values.
 */
public class MessageReprogrammerAdjust implements IMessage {

    public static final byte ACTION_NEXT = 0;
    public static final byte ACTION_PREV = 1;
    public static final byte ACTION_INCREASE = 2;
    public static final byte ACTION_DECREASE = 3;

    /** Smallest value any real setting may hold. */
    private static final short MIN_VALUE = 1;

    private byte action;
    private boolean coarse;

    public MessageReprogrammerAdjust() {}

    public MessageReprogrammerAdjust(byte action, boolean coarse) {
        this.action = action;
        this.coarse = coarse;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        action = buf.readByte();
        coarse = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(action);
        buf.writeBoolean(coarse);
    }

    public static class Handler implements IMessageHandler<MessageReprogrammerAdjust, IMessage> {

        @Override
        public IMessage onMessage(MessageReprogrammerAdjust msg, MessageContext ctx) {
            // 1.7.10 has no IThreadListener/addScheduledTask, so this runs on
            // the netty thread. That is the normal pattern for this Forge
            // version and is safe here: the work only touches one player's
            // held stack, never shared world state.
            final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            apply(player, msg);
            return null;
        }

        private static void apply(EntityPlayerMP player, MessageReprogrammerAdjust msg) {
            final ItemStack stack = player.getHeldItem();
            if (stack == null || !(stack.getItem() instanceof ItemSpawnerReprogrammer)) {
                return;
            }

            final NBTTagCompound nbt = ItemSpawnerReprogrammer.ensureData(stack);
            final NBTTagCompound data = nbt.getCompoundTag(ItemSpawnerReprogrammer.TAG_DATA);
            final String[] keys = ItemSpawnerReprogrammer.KEYS;
            int selected = ItemSpawnerReprogrammer.clampIndex(
                    nbt.getInteger(ItemSpawnerReprogrammer.TAG_SELECTED));

            switch (msg.action) {
                case ACTION_NEXT:
                    selected = (selected + 1) % keys.length;
                    nbt.setInteger(ItemSpawnerReprogrammer.TAG_SELECTED, selected);
                    break;

                case ACTION_PREV:
                    selected = (selected + keys.length - 1) % keys.length;
                    nbt.setInteger(ItemSpawnerReprogrammer.TAG_SELECTED, selected);
                    break;

                case ACTION_INCREASE:
                case ACTION_DECREASE:
                    if (selected == ItemSpawnerReprogrammer.RESET_INDEX) {
                        ItemSpawnerReprogrammer.resetData(stack);
                        break;
                    }
                    final int step = (msg.coarse ? 10 : 1)
                            * (msg.action == ACTION_INCREASE ? 1 : -1);
                    final String key = keys[selected];
                    int value = data.getShort(key) + step;
                    if (value < MIN_VALUE) {
                        value = MIN_VALUE;
                    }
                    if (value > Short.MAX_VALUE) {
                        value = Short.MAX_VALUE;
                    }
                    data.setShort(key, (short) value);
                    ItemSpawnerReprogrammer.clampDelays(data);
                    break;

                default:
                    return;
            }

            // Resend the slot so the client HUD reflects the new values.
            player.inventoryContainer.detectAndSendChanges();
        }
    }
}
