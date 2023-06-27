package com.tm.calemicrime.packet;

import com.tm.calemicrime.file.RentAcceptorTypesFile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketCalculateRentAcceptorLimit {

    private String type;

    public PacketCalculateRentAcceptorLimit() {}

    public PacketCalculateRentAcceptorLimit(String type) {
        this.type = type;
    }

    public PacketCalculateRentAcceptorLimit(FriendlyByteBuf buf) {
        type = buf.readUtf(50).trim();
    }

    public void toBytes (FriendlyByteBuf buf) {
        buf.writeUtf(type, 50);
    }

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();

            if (player != null) {
                CCPacketHandler.INSTANCE.sendTo(new PacketSendClientLimit(RentAcceptorTypesFile.getLimit(type)), ctx.get().getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
