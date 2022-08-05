package com.tm.calemicrime.packet;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicrime.blockentity.BlockEntityRentAcceptor;
import com.tm.calemicrime.main.CCReference;
import com.tm.calemicrime.util.RegionRuleSet;
import com.tm.calemieconomy.item.ItemWallet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketRentAcceptor {

    private String command;
    private BlockPos pos;
    private int ruleSetIndex;
    private byte ruleOverrideIndex;
    private int walletCurrency;
    private int bankCurrency;

    public PacketRentAcceptor() {}

    /**
     * Used to sync the data of the Rent Acceptor.
     * @param command Used to determine the type of packet to send.
     * @param pos The Block position of the Tile Entity.
     * @param ruleSetIndex The rule set index.
     * @param ruleOverrideIndex The index of type of override for the rule.
     * @param walletCurrency The Bank's Wallet's stored currency.
     * @param bankCurrency The Bank's stored currency.
     */
    public PacketRentAcceptor(String command, BlockPos pos, int ruleSetIndex, byte ruleOverrideIndex, int walletCurrency, int bankCurrency) {
        this.command = command;
        this.pos = pos;
        this.ruleSetIndex = ruleSetIndex;
        this.ruleOverrideIndex = ruleOverrideIndex;
        this.bankCurrency = bankCurrency;
        this.walletCurrency = walletCurrency;
    }

    public PacketRentAcceptor(String command, BlockPos pos, int ruleSetIndex, byte ruleOverrideIndex) {
        this(command, pos, ruleSetIndex, ruleOverrideIndex, 0, 0);
    }

    public PacketRentAcceptor(String command, BlockPos pos, int walletCurrency, int bankCurrency) {
        this(command, pos, 0, (byte)0, walletCurrency, bankCurrency);
    }

    public PacketRentAcceptor(FriendlyByteBuf buf) {
        command = buf.readUtf(16).trim();
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
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

                    LogHelper.log(CCReference.MOD_NAME, "PACKET RECEIVED");
                    LogHelper.log(CCReference.MOD_NAME, "SET OVERRIDE TO " + ruleOverrideIndex);

                    //Handles syncing rule.
                    if (command.equalsIgnoreCase("syncrule")) {
                        rentAcceptor.getRegionRuleSetOverride().ruleSets[ruleSetIndex] = RegionRuleSet.RuleOverrideType.fromIndex(ruleOverrideIndex);
                    }

                    else if (command.equalsIgnoreCase("refillrentwallet")) {

                        if (rentAcceptor.getItem(0).getItem() instanceof ItemWallet wallet) {
                            wallet.setCurrency(rentAcceptor.getItem(0), walletCurrency);
                        }

                        rentAcceptor.refillRentTime();
                    }

                    else if (command.equalsIgnoreCase("refillrentbank")) {
                        rentAcceptor.getBank().setCurrency(bankCurrency);
                        rentAcceptor.refillRentTime();
                    }

                    rentAcceptor.markUpdated();
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
