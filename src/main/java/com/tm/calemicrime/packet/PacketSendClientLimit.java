package com.tm.calemicrime.packet;

import com.tm.calemicrime.client.screen.ScreenRentAcceptor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSendClientLimit {

    private int count;

    public PacketSendClientLimit() {}

    public PacketSendClientLimit(int count) {
        this.count = count;
    }

    public PacketSendClientLimit(FriendlyByteBuf buf) {
        count = buf.readInt();
    }

    public void toBytes (FriendlyByteBuf buf) {
        buf.writeInt(count);
    }

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            if (Minecraft.getInstance().screen instanceof ScreenRentAcceptor screenRentAcceptor) {
                screenRentAcceptor.typeLimit = count;
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
