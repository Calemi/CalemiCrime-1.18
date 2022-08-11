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

    public PacketMineGenerator() {}

    public PacketMineGenerator(String command, BlockPos pos, BlockPos regionOffset, BlockPos regionEdge) {
        this.command = command;
        this.pos = pos;
        this.regionOffset = regionOffset;
        this.regionEdge = regionEdge;
    }

    public PacketMineGenerator(FriendlyByteBuf buf) {
        command = buf.readUtf(13).trim();
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        regionOffset = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        regionEdge = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public void toBytes (FriendlyByteBuf buf) {
        buf.writeUtf(command, 13);
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeInt(regionOffset.getX());
        buf.writeInt(regionOffset.getY());
        buf.writeInt(regionOffset.getZ());
        buf.writeInt(regionEdge.getX());
        buf.writeInt(regionEdge.getY());
        buf.writeInt(regionEdge.getZ());
    }

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();

            if (player != null) {

                Location location = new Location(player.getLevel(), pos);

                //Checks if the Tile Entity is a Region Protector.
                if (location.getBlockEntity() instanceof BlockEntityMineGenerator mineGenerator) {

                    //Handles syncing locations.
                    if (command.equalsIgnoreCase("syncregion")) {
                        mineGenerator.setRegionOffset(new Location(player.getLevel(), regionOffset));
                        mineGenerator.setRegionSize(new Location(player.getLevel(), regionEdge));
                    }

                    mineGenerator.markUpdated();
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
