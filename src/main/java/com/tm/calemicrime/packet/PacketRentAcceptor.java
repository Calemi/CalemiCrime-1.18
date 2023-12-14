package com.tm.calemicrime.packet;

import com.tm.calemicore.util.Location;
import com.tm.calemicrime.blockentity.BlockEntityRentAcceptor;
import com.tm.calemicrime.file.RentAcceptorTypesFile;
import com.tm.calemicrime.team.RegionTeam;
import com.tm.calemicrime.util.RegionRuleSet;
import com.tm.calemicrime.util.RegionTeamHelper;
import com.tm.calemicrime.util.RentAcceptorTypeHelper;
import com.tm.calemieconomy.item.ItemWallet;
import com.tm.calemieconomy.util.helper.CurrencyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketRentAcceptor {

    private String command;
    private BlockPos pos;
    private int maxRentTicks;
    private long costToFill;
    private String type;
    private String fileKey;
    private int ruleSetIndex;
    private byte ruleOverrideIndex;
    private boolean autoResetPlot;
    private int plotResetTimeSeconds;

    public PacketRentAcceptor() {}

    public PacketRentAcceptor(String command, BlockPos pos, int maxRentTicks, long costToFill, String type, String fileKey, int ruleSetIndex, byte ruleOverrideIndex, boolean shouldResetPlot, int plotResetTimeSeconds) {
        this.command = command;
        this.pos = pos;
        this.maxRentTicks = maxRentTicks;
        this.costToFill = costToFill;
        this.type = type;
        this.fileKey = fileKey;
        this.ruleSetIndex = ruleSetIndex;
        this.ruleOverrideIndex = ruleOverrideIndex;
        this.autoResetPlot = shouldResetPlot;
        this.plotResetTimeSeconds = plotResetTimeSeconds;
    }

    public PacketRentAcceptor(String command, BlockPos pos, int ruleSetIndex, byte ruleOverrideIndex) {
        this(command, pos, 0, 0, "", "", ruleSetIndex, ruleOverrideIndex, false, 0);
    }

    public PacketRentAcceptor(String command, BlockPos pos, int maxRentTicks, long costToFill, String type, String fileKey, int plotResetTimeSeconds) {
        this(command, pos, maxRentTicks, costToFill, type, fileKey, 0, (byte)0, false, plotResetTimeSeconds);
    }

    public PacketRentAcceptor(String command, BlockPos pos, boolean shouldResetPlot) {
        this(command, pos, 0, 0, "", "", 0, (byte)0, shouldResetPlot, 0);
    }

    public PacketRentAcceptor(String command, BlockPos pos) {
        this(command, pos, 0, 0, "", "", 0, (byte)0, false, 0);
    }

    public PacketRentAcceptor(FriendlyByteBuf buf) {
        command = buf.readUtf(100).trim();
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        maxRentTicks = buf.readInt();
        costToFill = buf.readLong();
        type = buf.readUtf(100).trim();
        fileKey = buf.readUtf(100).trim();
        ruleSetIndex = buf.readInt();
        ruleOverrideIndex = buf.readByte();
        autoResetPlot = buf.readBoolean();
        plotResetTimeSeconds = buf.readInt();
    }

    public void toBytes (FriendlyByteBuf buf) {
        buf.writeUtf(command, 100);
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeInt(maxRentTicks);
        buf.writeLong(costToFill);
        buf.writeUtf(type, 100);
        buf.writeUtf(fileKey, 100);
        buf.writeInt(ruleSetIndex);
        buf.writeByte(ruleOverrideIndex);
        buf.writeBoolean(autoResetPlot);
        buf.writeInt(plotResetTimeSeconds);
    }

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();

            if (player != null) {

                Location location = new Location(player.getLevel(), pos);

                //Checks if the Tile Entity is a Region Protector.
                if (location.getBlockEntity() instanceof BlockEntityRentAcceptor rentAcceptor) {

                    RegionTeam residentTeam = rentAcceptor.getResidentTeam();

                    if (command.equalsIgnoreCase("syncoptions")) {
                        rentAcceptor.maxRentHours = maxRentTicks;
                        rentAcceptor.costToFillRentTime = costToFill;
                        rentAcceptor.rentType = type;
                        rentAcceptor.fileKey = fileKey;
                        rentAcceptor.plotResetTimeSeconds = plotResetTimeSeconds;
                    }

                    //Handles syncing rule.
                    else if (command.equalsIgnoreCase("syncrule")) {
                        rentAcceptor.regionRuleSetOverride.ruleSets[ruleSetIndex] = RegionRuleSet.RuleOverrideType.fromIndex(ruleOverrideIndex);
                    }

                    else if (command.equalsIgnoreCase("payrent")) {

                        //TEAM CHECK

                        if (RegionTeamHelper.getTeam(player) == null) {
                            player.sendMessage(new TextComponent(ChatFormatting.RED + "You need to create a team first!"), Util.NIL_UUID);
                            player.sendMessage(new TextComponent(ChatFormatting.RED + "Use: /crime team create <teamname>"),  Util.NIL_UUID);
                            return;
                        }

                        if (residentTeam != null) {

                            if (!RegionTeamHelper.getTeam(player).equals(residentTeam) && !player.isCreative()) {
                                player.sendMessage(new TextComponent(ChatFormatting.RED + "You are not a member of the team!"), Util.NIL_UUID);
                                return;
                            }
                        }

                        //TYPE LIMIT CHECK

                        int rentTypeCount = RentAcceptorTypeHelper.calculateRentTypeCount(player, rentAcceptor.rentType);
                        int rentTypeLimit = RentAcceptorTypesFile.getLimit(rentAcceptor.rentType);

                        if (rentAcceptor.getResidentTeam() == null) {

                            if (rentTypeCount >= rentTypeLimit) {
                                player.sendMessage(new TextComponent(ChatFormatting.RED + "You have reached your limit for this rent type!"), Util.NIL_UUID);
                                player.sendMessage(new TextComponent(ChatFormatting.RED + "Limit for [" + rentAcceptor.rentType + "] is " + rentTypeLimit), Util.NIL_UUID);
                                return;
                            }
                        }

                        //MONEY CHECK

                        ItemStack walletStack = CurrencyHelper.getCurrentWallet(player);

                        boolean usingWallet = false;
                        long currencyAmount = 0;

                        if (rentAcceptor.getBank() != null) {
                            currencyAmount = rentAcceptor.getBank().getCurrency();
                        }

                        else if (!walletStack.isEmpty() && walletStack.getItem() instanceof ItemWallet wallet) {
                            currencyAmount = wallet.getCurrency(walletStack);
                            usingWallet = true;
                        }

                        else {
                            player.sendMessage(new TextComponent(ChatFormatting.RED + "Could not find a Wallet or a connected Bank!"), Util.NIL_UUID);
                            return;
                        }

                        long refillCost = rentAcceptor.getCostToRefillRentTime();

                        if (currencyAmount < refillCost) {
                            player.sendMessage(new TextComponent(ChatFormatting.RED + "You do not have enough money!"), Util.NIL_UUID);
                            return;
                        }

                        if (usingWallet) {
                            ((ItemWallet)walletStack.getItem()).withdrawCurrency(walletStack, refillCost);
                        }

                        else {
                            rentAcceptor.getBank().withdrawCurrency(refillCost);
                        }

                        rentAcceptor.refillRentTime();
                        rentAcceptor.setResidentTeam(RegionTeamHelper.getTeam(player));
                    }

                    else if (command.equalsIgnoreCase("stoprent")) {

                        if (residentTeam != null && !player.isCreative()) {

                            if (!RegionTeamHelper.getTeam(player).equals(residentTeam)) {

                                player.sendMessage(new TextComponent(ChatFormatting.RED + "You are not a member of the team!"), Util.NIL_UUID);

                                return;
                            }

                            if (!residentTeam.isOwner(player)) {

                                player.sendMessage(new TextComponent(ChatFormatting.RED + "Only officers can stop rent!"), Util.NIL_UUID);

                                return;
                            }
                        }

                        rentAcceptor.emptyRentTime();
                    }

                    else if (command.equalsIgnoreCase("syncautoplotreset")) {
                        rentAcceptor.autoPlotReset = autoResetPlot;
                    }

                    else if (command.equalsIgnoreCase("loadplot")) {

                        if (rentAcceptor.regionProtector != null) {
                            rentAcceptor.isPlotReset = true;
                            rentAcceptor.lastRentDepleteTimeSeconds = Integer.MAX_VALUE;
                            rentAcceptor.regionProtector.loadPlot(player.getLevel());
                        }
                    }

                    rentAcceptor.markUpdated();
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
