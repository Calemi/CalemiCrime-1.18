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
    private boolean global;
    private int regionTypeIndex;
    private int ruleSetIndex;
    private byte ruleOverrideIndex;
    private String plotName;

    public PacketRegionProtector() {}

    public PacketRegionProtector(String command, BlockPos pos, BlockPos regionOffset, BlockPos regionEdge, int priority, boolean global, int regionTypeIndex, int ruleSetIndex, byte ruleOverrideIndex, String plotName) {
        this.command = command;
        this.pos = pos;
        this.regionOffset = regionOffset;
        this.regionEdge = regionEdge;
        this.priority = priority;
        this.global = global;
        this.regionTypeIndex = regionTypeIndex;
        this.ruleSetIndex = ruleSetIndex;
        this.ruleOverrideIndex = ruleOverrideIndex;
        this.plotName = plotName;
    }

    public PacketRegionProtector(String command, BlockPos pos, BlockPos regionOffset, BlockPos regionEdge) {
        this(command, pos, regionOffset, regionEdge, 0, false, 0, 0, (byte)0, "");
    }

    public PacketRegionProtector(String command, BlockPos pos, int priority) {
        this(command, pos, BlockPos.ZERO, BlockPos.ZERO, priority,  false, 0, 0, (byte)0, "");
    }

    public PacketRegionProtector(String command, BlockPos pos, boolean global, int regionTypeIndex) {
        this(command, pos, BlockPos.ZERO, BlockPos.ZERO, 0,  global, regionTypeIndex, 0, (byte)0, "");
    }

    public PacketRegionProtector(String command, BlockPos pos, int ruleSetIndex, byte ruleOverrideIndex) {
        this(command, pos, BlockPos.ZERO, BlockPos.ZERO, 0, false, 0, ruleSetIndex, ruleOverrideIndex, "");
    }

    public PacketRegionProtector(String command, BlockPos pos) {
        this(command, pos, BlockPos.ZERO, BlockPos.ZERO, 0, false, 0, 0, (byte)0, "");
    }

    public PacketRegionProtector(String command, BlockPos pos, String plotName) {
        this(command, pos, BlockPos.ZERO, BlockPos.ZERO, 0, false, 0, 0, (byte)0, plotName);
    }

    public PacketRegionProtector(FriendlyByteBuf buf) {
        command = buf.readUtf(14).trim();
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        regionOffset = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        regionEdge = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        priority = buf.readInt();
        global = buf.readBoolean();
        regionTypeIndex = buf.readInt();
        ruleSetIndex = buf.readInt();
        ruleOverrideIndex = buf.readByte();
        plotName = buf.readUtf(100).trim();
    }

    public void toBytes (FriendlyByteBuf buf) {
        buf.writeUtf(command, 14);
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
        buf.writeBoolean(global);
        buf.writeInt(regionTypeIndex);
        buf.writeInt(ruleSetIndex);
        buf.writeByte(ruleOverrideIndex);
        buf.writeUtf(plotName, 100);
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
                        regionProtector.regionOffset = new Location(player.getLevel(), regionOffset);
                        regionProtector.regionSize = new Location(player.getLevel(), regionEdge);
                    }

                    //Handles syncing priority.
                    else if (command.equalsIgnoreCase("syncpriority")) {
                        regionProtector.priority = priority;
                    }

                    else if (command.equalsIgnoreCase("syncglobal")) {
                        regionProtector.global = global;
                        LogHelper.log(CCReference.MOD_NAME, global);
                    }

                    else if (command.equalsIgnoreCase("syncregiontype")) {
                        regionProtector.regionType = BlockEntityRegionProtector.RegionType.fromIndex(regionTypeIndex);
                    }

                    //Handles syncing rule.
                    else if (command.equalsIgnoreCase("syncrule")) {
                        regionProtector.regionRuleSet.ruleSets[ruleSetIndex] = RegionRuleSet.RuleOverrideType.fromIndex(ruleOverrideIndex);
                    }

                    else if (command.equalsIgnoreCase("saveplot")) {
                        regionProtector.savePlot();
                        regionProtector.lastSaveTime = System.currentTimeMillis();
                    }

                    else if (command.equalsIgnoreCase("loadplot")) {
                        regionProtector.loadPlot(player.getLevel());
                    }

                    regionProtector.markUpdated();
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
