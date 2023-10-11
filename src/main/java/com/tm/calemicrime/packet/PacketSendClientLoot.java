package com.tm.calemicrime.packet;

import com.tm.calemicrime.client.screen.ScreenLootBox;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSendClientLoot {

    private ItemStack[] stacks = new ItemStack[32];
    private int[] rarities = new int[32];

    public PacketSendClientLoot() {}

    public PacketSendClientLoot(ItemStack[] stacks, int[] rarities) {
        this.stacks = stacks;
        this.rarities = rarities;
    }

    public PacketSendClientLoot(FriendlyByteBuf buf) {

        for (int i = 0; i < stacks.length; i++) {
            stacks[i] = buf.readItem();
            rarities[i] = buf.readInt();
        }
    }

    public void toBytes (FriendlyByteBuf buf) {

        for (int i = 0; i < stacks.length; i++) {
            buf.writeItem(stacks[i]);
            buf.writeInt(rarities[i]);
        }
    }

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            if (Minecraft.getInstance().screen instanceof ScreenLootBox screenLootBox) {
                screenLootBox.stacks = stacks;
                screenLootBox.rarities = rarities;
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
