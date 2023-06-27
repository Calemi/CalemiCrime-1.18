package com.tm.calemicrime.packet;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicrime.blockentity.BlockEntityRentAcceptor;
import com.tm.calemicrime.main.CCReference;
import com.tm.calemicrime.util.RegionTeamHelper;
import com.tm.calemicrime.util.RentAcceptorTypeHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketCalculateRentAcceptorCount {

    private String type;

    public PacketCalculateRentAcceptorCount() {}

    public PacketCalculateRentAcceptorCount(String type) {
        this.type = type;
    }

    public PacketCalculateRentAcceptorCount(FriendlyByteBuf buf) {
        type = buf.readUtf(50).trim();
    }

    public void toBytes (FriendlyByteBuf buf) {
        buf.writeUtf(type, 50);
    }

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();

            if (player != null) {

                CCPacketHandler.INSTANCE.sendTo(new PacketSendClientCount(RentAcceptorTypeHelper.calculateRentTypeCount(player, type)), ctx.get().getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
