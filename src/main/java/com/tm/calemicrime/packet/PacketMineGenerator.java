package com.tm.calemicrime.packet;

import com.tm.calemicore.util.Location;
import com.tm.calemicrime.blockentity.BlockEntityMineGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketMineGenerator {

    private String command;
    private BlockPos pos;
    private BlockPos regionOffset;
    private BlockPos regionEdge;
    private boolean outerLayer;

    public PacketMineGenerator() {}

    public PacketMineGenerator(String command, BlockPos pos, BlockPos regionOffset, BlockPos regionEdge, boolean outerLayer) {
        this.command = command;
        this.pos = pos;
        this.regionOffset = regionOffset;
        this.regionEdge = regionEdge;
        this.outerLayer = outerLayer;
    }

    public PacketMineGenerator(FriendlyByteBuf buf) {
        command = buf.readUtf(20).trim();
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        regionOffset = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        regionEdge = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        outerLayer = buf.readBoolean();
    }

    public void toBytes (FriendlyByteBuf buf) {
        buf.writeUtf(command, 20);
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeInt(regionOffset.getX());
        buf.writeInt(regionOffset.getY());
        buf.writeInt(regionOffset.getZ());
        buf.writeInt(regionEdge.getX());
        buf.writeInt(regionEdge.getY());
        buf.writeInt(regionEdge.getZ());
        buf.writeBoolean(outerLayer);
    }

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();

            if (player != null) {

                Location location = new Location(player.getLevel(), pos);

                if (location.getBlockEntity() instanceof BlockEntityMineGenerator mineGenerator) {

                    if (command.equalsIgnoreCase("syncregion")) {
                        mineGenerator.setRegionOffset(new Location(player.getLevel(), regionOffset));
                        mineGenerator.setRegionSize(new Location(player.getLevel(), regionEdge));
                    }

                    if (command.equalsIgnoreCase("syncouterlayer")) {
                        mineGenerator.setHasOuterLayer(outerLayer);
                    }

                    mineGenerator.markUpdated();
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
