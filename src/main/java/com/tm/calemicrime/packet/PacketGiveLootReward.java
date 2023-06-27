package com.tm.calemicrime.packet;

import com.tm.calemicore.util.helper.ItemHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketGiveLootReward {

    private ItemStack stack;

    public PacketGiveLootReward() {}

    public PacketGiveLootReward(ItemStack stack) {
        this.stack = stack;
    }

    public PacketGiveLootReward(FriendlyByteBuf buf) {
        stack = buf.readItem();
    }

    public void toBytes (FriendlyByteBuf buf) {
        buf.writeItem(stack);
    }

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();

            if (player != null) {
                ItemHelper.spawnStackAtEntity(player.level, player, stack);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
