package com.tm.calemicrime.packet;

import com.tm.calemicore.util.Location;
import com.tm.calemicrime.blockentity.BlockEntityRadiationProjector;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketRadiationProjector {

    private String command;
    private BlockPos pos;
    private BlockPos regionOffset;
    private BlockPos regionEdge;
    private float radiationStrength;

    public PacketRadiationProjector() {}

    public PacketRadiationProjector(String command, BlockPos pos, BlockPos regionOffset, BlockPos regionEdge, float radiationStrength) {
        this.command = command;
        this.pos = pos;
        this.regionOffset = regionOffset;
        this.regionEdge = regionEdge;
        this.radiationStrength = radiationStrength;
    }

    public PacketRadiationProjector(FriendlyByteBuf buf) {
        command = buf.readUtf(13).trim();
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        regionOffset = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        regionEdge = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        radiationStrength = buf.readFloat();
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
        buf.writeFloat(radiationStrength);
    }

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();

            if (player != null) {

                Location location = new Location(player.getLevel(), pos);

                //Checks if the Tile Entity is a Region Protector.
                if (location.getBlockEntity() instanceof BlockEntityRadiationProjector radiationProjector) {

                    //Handles syncing locations.
                    if (command.equalsIgnoreCase("syncregion")) {
                        radiationProjector.setRegionOffset(new Location(player.getLevel(), regionOffset));
                        radiationProjector.setRegionSize(new Location(player.getLevel(), regionEdge));
                        radiationProjector.setRadiationStrength(radiationStrength);
                    }

                    radiationProjector.markUpdated();
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
