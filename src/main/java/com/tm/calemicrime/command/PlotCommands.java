package com.tm.calemicrime.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.tm.calemicrime.blockentity.BlockEntityRentAcceptor;
import com.tm.calemicrime.util.RegionHelper;
import com.tm.calemicrime.util.RegionProfile;
import com.tm.calemieconomy.util.helper.CurrencyHelper;
import dev.ftb.mods.ftbteams.data.TeamManager;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;

import java.text.DecimalFormat;

public class PlotCommands {

    public static ArgumentBuilder<CommandSourceStack, ?> plots() {

        return Commands.literal("plots")
                .then(rent());
    }

    private static ArgumentBuilder<CommandSourceStack, ?> rent() {

        return Commands.literal("rent")
                .then(plotRentGet())
                .then(plotRentAdd());
    }

    private static ArgumentBuilder<CommandSourceStack, ?> plotRentGet() {

        return Commands.literal("get").executes(ctx -> {

            Player player = ctx.getSource().getPlayerOrException();
            TeamManager manager = TeamManager.INSTANCE;

            player.sendMessage(new TextComponent(ChatFormatting.GREEN + "Your plots:"), Util.NIL_UUID);
            player.sendMessage(new TextComponent(""), Util.NIL_UUID);

            for (RegionProfile regionProfile : RegionHelper.allProfiles.values()) {

                if (regionProfile.getRentAcceptor() == null) {
                    continue;
                }

                BlockEntityRentAcceptor rentAcceptor = regionProfile.getRentAcceptor();

                if (rentAcceptor.getResidentTeam() != null && rentAcceptor.getResidentTeam().isMember(player)) {

                    rentAcceptor.refreshSystemTimeSeconds();
                    rentAcceptor.markUpdated();

                    ChatFormatting rentTimeColor = ChatFormatting.GREEN;

                    int remainingRentTime = rentAcceptor.getRemainingRentSeconds();
                    int maxRentTime = rentAcceptor.getMaxRentSeconds();
                    float ratio = (float) remainingRentTime / (float) maxRentTime;

                    if (ratio < 0.75F) {
                        rentTimeColor = ChatFormatting.YELLOW;
                    }

                    if (ratio < 0.5F) {
                        rentTimeColor = ChatFormatting.GOLD;
                    }

                    if (ratio < 0.25F) {
                        rentTimeColor = ChatFormatting.RED;
                    }

                    ChatFormatting rentTypeColor = ChatFormatting.WHITE;

                    if (rentAcceptor.rentType.equalsIgnoreCase("residential")) {
                        rentTypeColor = ChatFormatting.GREEN;
                    }

                    if (rentAcceptor.rentType.equalsIgnoreCase("commercial")) {
                        rentTypeColor = ChatFormatting.BLUE;
                    }

                    DecimalFormat formatter = new DecimalFormat("#0.0");
                    player.sendMessage(new TextComponent(rentTypeColor + rentAcceptor.rentType + " " + rentAcceptor.getLocation() + (!rentAcceptor.fileKey.isEmpty() ? " (" + rentAcceptor.fileKey + ")" : "")), Util.NIL_UUID);
                    player.sendMessage(new TextComponent("  Rent Time: " + rentTimeColor + formatter.format(ratio * 100) + "% - " + rentAcceptor.getFormattedTime(remainingRentTime) + " / " + rentAcceptor.getFormattedTime(maxRentTime)), Util.NIL_UUID);
                    player.sendMessage(new TextComponent("  Cost to Refill Time: ").append(CurrencyHelper.formatCurrency(rentAcceptor.getCostToRefillRentTime(), true)), Util.NIL_UUID);
                    player.sendMessage(new TextComponent(""), Util.NIL_UUID);
                }
            }

            return Command.SINGLE_SUCCESS;
        });
    }

    private static ArgumentBuilder<CommandSourceStack, ?> plotRentAdd() {

        return Commands.literal("add").requires((player) -> player.hasPermission(3)).then(Commands.argument("minutes", IntegerArgumentType.integer(1, Integer.MAX_VALUE)).executes(ctx -> {

            int minutes = IntegerArgumentType.getInteger(ctx, "minutes");

            Player player = ctx.getSource().getPlayerOrException();

            player.sendMessage(new TextComponent(ChatFormatting.GREEN + "Added " + minutes + " minute(s) to all plots."), Util.NIL_UUID);

            for (RegionProfile regionProfile : RegionHelper.allProfiles.values()) {

                if (regionProfile.getRentAcceptor() == null) {
                    continue;
                }

                regionProfile.getRentAcceptor().addRentTime(minutes);
            }

            return Command.SINGLE_SUCCESS;
        }));
    }
}
