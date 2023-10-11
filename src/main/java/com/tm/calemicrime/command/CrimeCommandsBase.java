package com.tm.calemicrime.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tm.calemicrime.file.*;
import com.tm.calemicrime.team.RegionTeam;
import com.tm.calemicrime.team.RegionTeamMember;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;

public class CrimeCommandsBase {

    /**
     * Registers all the commands.
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        LiteralArgumentBuilder<CommandSourceStack> plotCommand = Commands.literal("crime");

        plotCommand.requires(commandSource -> true)
                .then(reload())
                .then(UnstuckCommands.unstuck())
                .then(PlotCommands.plots())
                .then(TeamCommands.team());

        dispatcher.register(plotCommand);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> reload() {

        return Commands.literal("reload").requires((player) -> player.hasPermission(3)).executes(ctx -> {

            Player player = ctx.getSource().getPlayerOrException();

            LootBoxFile.init();
            RentAcceptorTypesFile.init();
            PlotsFile.init();
            DryingRackRecipesFile.init();
            RegionTeamsFile.init();
            UnstuckLocationsFile.init();
            PlotBlockPlaceLimitFile.init();
            DirtyFile.markDirty();

            for (RegionTeam team : RegionTeamsFile.teams) {

                for (RegionTeamMember member : team.getMembers()) {
                    member.refreshName(player.getLevel());
                }
            }

            /*for (BlockEntityRentAcceptor rentAcceptor : BlockEntityRentAcceptor.rentAcceptors) {

                LogHelper.log(CCReference.MOD_NAME, rentAcceptor.fileKey);

                if (!rentAcceptor.injectValuesFromFile()) {
                    if (!ctx.getSource().getPlayerOrException().getLevel().isClientSide()) {
                        sendWarning(player, "Could not find plot entry in file: " + rentAcceptor.fileKey);
                    }
                }
            }*/

            sendSuccess(player, "Reload Complete!");

            return Command.SINGLE_SUCCESS;
        });
    }



    public static void sendSuccess(Player player, String msg) {
        player.sendMessage(new TextComponent(ChatFormatting.GREEN + msg), Util.NIL_UUID);
    }

    public static void sendWarning(Player player, String msg) {
        player.sendMessage(new TextComponent(ChatFormatting.YELLOW + msg), Util.NIL_UUID);
    }

    public static void sendError(Player player, String msg) {
        player.sendMessage(new TextComponent(ChatFormatting.RED + msg), Util.NIL_UUID);
    }
}
