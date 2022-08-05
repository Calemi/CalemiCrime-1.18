package com.tm.calemicrime.packet;

import com.tm.calemicore.util.Location;
import com.tm.calemicrime.blockentity.BlockEntityRentAcceptor;
import com.tm.calemicrime.util.RegionRuleSet;
import com.tm.calemieconomy.item.ItemWallet;
import dev.ftb.mods.ftbteams.data.TeamManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketRentAcceptor {

    private String command;
    private BlockPos pos;
    private int maxRentTicks;
    private int costPerHour;
    private int ruleSetIndex;
    private byte ruleOverrideIndex;
    private int walletCurrency;
    private int bankCurrency;

    public PacketRentAcceptor() {}

    /**
     * Used to sync the data of the Rent Acceptor.
     * @param command Used to determine the type of packet to send.
     * @param pos The Block position of the Tile Entity.
     * @param maxRentTicks The max time of rent in ticks.
     * @param costPerHour The cost of currency per hour of rent.
     * @param ruleSetIndex The rule set index.
     * @param ruleOverrideIndex The index of type of override for the rule.
     * @param walletCurrency The Bank's Wallet's stored currency.
     * @param bankCurrency The Bank's stored currency.
     */
    public PacketRentAcceptor(String command, BlockPos pos, int maxRentTicks, int costPerHour, int ruleSetIndex, byte ruleOverrideIndex, int walletCurrency, int bankCurrency) {
        this.command = command;
        this.pos = pos;
        this.maxRentTicks = maxRentTicks;
        this.costPerHour = costPerHour;
        this.ruleSetIndex = ruleSetIndex;
        this.ruleOverrideIndex = ruleOverrideIndex;
        this.bankCurrency = bankCurrency;
        this.walletCurrency = walletCurrency;
    }

    public PacketRentAcceptor(String command, BlockPos pos, int ruleSetIndex, byte ruleOverrideIndex) {
        this(command, pos, 0, 0, ruleSetIndex, ruleOverrideIndex, 0, 0);
    }

    public PacketRentAcceptor(String command, BlockPos pos, int maxRentTicks, int costPerHour, int walletCurrency, int bankCurrency) {
        this(command, pos, maxRentTicks, costPerHour, 0, (byte)0, walletCurrency, bankCurrency);
    }

    public PacketRentAcceptor(FriendlyByteBuf buf) {
        command = buf.readUtf(16).trim();
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        maxRentTicks = buf.readInt();
        costPerHour = buf.readInt();
        ruleSetIndex = buf.readInt();
        ruleOverrideIndex = buf.readByte();
        walletCurrency = buf.readInt();
        bankCurrency = buf.readInt();
    }

    public void toBytes (FriendlyByteBuf buf) {
        buf.writeUtf(command, 16);
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeInt(maxRentTicks);
        buf.writeInt(costPerHour);
        buf.writeInt(ruleSetIndex);
        buf.writeByte(ruleOverrideIndex);
        buf.writeInt(walletCurrency);
        buf.writeInt(bankCurrency);
    }

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();

            if (player != null) {

                Location location = new Location(player.getLevel(), pos);

                //Checks if the Tile Entity is a Region Protector.
                if (location.getBlockEntity() instanceof BlockEntityRentAcceptor rentAcceptor) {

                    if (command.equalsIgnoreCase("syncoptions")) {
                        rentAcceptor.setMaxRentTime(maxRentTicks);
                        rentAcceptor.setCostToFillRentTime(costPerHour);
                    }

                    //Handles syncing rule.
                    else if (command.equalsIgnoreCase("syncrule")) {
                        rentAcceptor.getRegionRuleSetOverride().ruleSets[ruleSetIndex] = RegionRuleSet.RuleOverrideType.fromIndex(ruleOverrideIndex);
                    }

                    else if (command.equalsIgnoreCase("refillrentwallet")) {

                        if (rentAcceptor.getItem(0).getItem() instanceof ItemWallet wallet) {
                            wallet.setCurrency(rentAcceptor.getItem(0), walletCurrency);
                        }

                        rentAcceptor.refillRentTime();
                        rentAcceptor.setResidentTeam(TeamManager.INSTANCE.getPlayerTeam(player));
                    }

                    else if (command.equalsIgnoreCase("refillrentbank")) {

                        rentAcceptor.getBank().setCurrency(bankCurrency);

                        rentAcceptor.refillRentTime();
                        rentAcceptor.setResidentTeam(TeamManager.INSTANCE.getPlayerTeam(player));
                    }

                    rentAcceptor.markUpdated();
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
