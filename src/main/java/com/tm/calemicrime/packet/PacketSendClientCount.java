package com.tm.calemicrime.packet;

import com.tm.calemicrime.client.screen.ScreenRentAcceptor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSendClientCount {

    private int count;

    public PacketSendClientCount() {}

    public PacketSendClientCount(int count) {
        this.count = count;
    }

    public PacketSendClientCount(FriendlyByteBuf buf) {
        count = buf.readInt();
    }

    public void toBytes (FriendlyByteBuf buf) {
        buf.writeInt(count);
    }

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            if (Minecraft.getInstance().screen instanceof ScreenRentAcceptor screenRentAcceptor) {
                screenRentAcceptor.typeCount = count;
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
