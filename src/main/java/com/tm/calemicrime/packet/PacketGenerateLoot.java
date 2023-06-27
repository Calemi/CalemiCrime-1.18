package com.tm.calemicrime.packet;

import com.tm.calemicrime.file.LootBoxFile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketGenerateLoot {

    private String pool;

    public PacketGenerateLoot() {}

    public PacketGenerateLoot(String type) {
        this.pool = type;
    }

    public PacketGenerateLoot(FriendlyByteBuf buf) {
        pool = buf.readUtf(100).trim();
    }

    public void toBytes (FriendlyByteBuf buf) {
        buf.writeUtf(pool, 100);
    }

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();

            if (player != null) {

                ItemStack[] stacks = new ItemStack[16];
                int[] rarities = new int[16];

                for (int i = 0; i < stacks.length; i++) {
                    LootBoxFile.LootEntry lootBoxEntry = LootBoxFile.getLootBoxReward(player, pool);
                    stacks[i] = lootBoxEntry.getStack();
                    rarities[i] = lootBoxEntry.rarityIndex();
                }

                CCPacketHandler.INSTANCE.sendTo(new PacketSendClientLoot(stacks, rarities), ctx.get().getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
