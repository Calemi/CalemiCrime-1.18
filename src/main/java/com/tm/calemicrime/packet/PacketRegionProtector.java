package com.tm.calemicrime.packet;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicrime.blockentity.BlockEntityRegionProtector;
import com.tm.calemicrime.main.CCReference;
import com.tm.calemicrime.util.RegionRuleSet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketRegionProtector {

    private String command;
    private BlockPos pos;
    private BlockPos regionOffset;
    private BlockPos regionEdge;
    private int priority;
    private int ruleSetIndex;
    private byte ruleOverrideIndex;

    public PacketRegionProtector() {}

    /**
     * Used to sync the data of the Trading Post.
     * @param command Used to determine the type of packet to send.
     * @param pos The Block position of the Tile Entity.
     * @param regionOffset The Location of the region's offset.
     * @param regionEdge The Location of the region's edge.
     * @param priority The number of priority.
     * @param ruleSetIndex The rule set index.
     * @param ruleOverrideIndex The index of type of override for the rule.
     */
    public PacketRegionProtector(String command, BlockPos pos, BlockPos regionOffset, BlockPos regionEdge, int priority, int ruleSetIndex, byte ruleOverrideIndex) {
        this.command = command;
        this.pos = pos;
        this.regionOffset = regionOffset;
        this.regionEdge = regionEdge;
        this.priority = priority;
        this.ruleSetIndex = ruleSetIndex;
        this.ruleOverrideIndex = ruleOverrideIndex;
    }

    public PacketRegionProtector(String command, BlockPos pos, BlockPos regionOffset, BlockPos regionEdge) {
        this(command, pos, regionOffset, regionEdge, 0, 0, (byte)0);
    }

    public PacketRegionProtector(String command, BlockPos pos, int priority) {
        this(command, pos, BlockPos.ZERO, BlockPos.ZERO, priority, 0, (byte)0);
    }

    public PacketRegionProtector(String command, BlockPos pos, int ruleSetIndex, byte ruleOverrideIndex) {
        this(command, pos, BlockPos.ZERO, BlockPos.ZERO, 0, ruleSetIndex, ruleOverrideIndex);
    }

    public PacketRegionProtector(FriendlyByteBuf buf) {
        command = buf.readUtf(13).trim();
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        regionOffset = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        regionEdge = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        priority = buf.readInt();
        ruleSetIndex = buf.readInt();
        ruleOverrideIndex = buf.readByte();
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
        buf.writeInt(priority);
        buf.writeInt(ruleSetIndex);
        buf.writeByte(ruleOverrideIndex);
    }

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();

            if (player != null) {

                Location location = new Location(player.getLevel(), pos);

                //Checks if the Tile Entity is a Region Protector.
                if (location.getBlockEntity() instanceof BlockEntityRegionProtector regionProtector) {

                    //Handles syncing locations.
                    if (command.equalsIgnoreCase("synclocations")) {
                        regionProtector.setRegionOffset(new Location(player.getLevel(), regionOffset));
                        regionProtector.setRegionSize(new Location(player.getLevel(), regionEdge));
                    }

                    //Handles syncing priority.
                    else if (command.equalsIgnoreCase("syncpriority")) {
                        regionProtector.setPriority(priority);
                    }

                    //Handles syncing rule.
                    else if (command.equalsIgnoreCase("syncrule")) {
                        regionProtector.getRegionRuleSet().ruleSets[ruleSetIndex] = RegionRuleSet.RuleOverrideType.fromIndex(ruleOverrideIndex);
                    }

                    regionProtector.markUpdated();
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
