package com.tm.calemicrime.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tm.calemicrime.file.RegionTeamsFile;
import com.tm.calemicrime.team.RegionTeam;
import com.tm.calemicrime.team.RegionTeamMember;
import com.tm.calemicrime.team.RegionTeamPlayer;
import com.tm.calemicrime.util.RegionTeamHelper;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class TeamCommands {

    public static ArgumentBuilder<CommandSourceStack, ?> team() {

        return Commands.literal("team")
                .then(teamCreate())
                .then(teamDelete())
                .then(teamList())
                .then(teamInfo())
                .then(teamRename())
                .then(teamInvite())
                .then(teamUninvite())
                .then(teamKick())
                .then(teamJoin())
                .then(teamLeave())
                .then(teamPromote())
                .then(teamDemote())
                .then(teamAlly())
                .then(teamDisally());
    }

    private static ArgumentBuilder<CommandSourceStack, ?> teamCreate() {

        return Commands.literal("create").then(Commands.argument("teamName", StringArgumentType.word()).executes(ctx -> {

            Player player = ctx.getSource().getPlayerOrException();

            String teamName = StringArgumentType.getString(ctx, "teamName");

            if (checkTeam(player, true, "You are already in a team!")) return Command.SINGLE_SUCCESS;

            if (teamName.length() > 16) {
                CrimeCommandsBase.sendError(player, "That team name is too long! Must be below 16 characters!");
                return Command.SINGLE_SUCCESS;
            }

            if (RegionTeamHelper.getTeam(teamName) != null) {
                CrimeCommandsBase.sendError(player, "A team with that name already exists! Team names must be unique!");
                return Command.SINGLE_SUCCESS;
            }

            RegionTeam team = RegionTeam.create(teamName, player);

            CrimeCommandsBase.sendSuccess(player, "Your team has been created.");
            return Command.SINGLE_SUCCESS;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> teamDelete() {

        return Commands.literal("delete").executes(ctx -> {

            Player player = ctx.getSource().getPlayerOrException();

            if (checkTeam(player, false, "You are not in a team!")) return Command.SINGLE_SUCCESS;
            if (checkOwner(player, false, "Only team owners can delete their team!")) return Command.SINGLE_SUCCESS;

            RegionTeam team = RegionTeamHelper.getTeam(player);
            team.delete();

            CrimeCommandsBase.sendSuccess(player, "Your team has been deleted.");
            return Command.SINGLE_SUCCESS;
        });
    }

    private static ArgumentBuilder<CommandSourceStack, ?> teamList() {

        return Commands.literal("list").executes(ctx -> {

            Player player = ctx.getSource().getPlayerOrException();

            String msg = "";

            for (RegionTeam team : RegionTeamsFile.teams) {
                msg += "[" + team.getName() + "] ";
            }

            CrimeCommandsBase.sendSuccess(player, "All teams:");
            CrimeCommandsBase.sendWarning(player, msg);
            return Command.SINGLE_SUCCESS;
        });
    }

    private static ArgumentBuilder<CommandSourceStack, ?> teamInfo() {

        return Commands.literal("info").executes(ctx -> {

            Player player = ctx.getSource().getPlayerOrException();

            if (checkTeam(player, false, "You are not in a team!")) return Command.SINGLE_SUCCESS;

            RegionTeam team = RegionTeamHelper.getTeam(player);

            return teamInfo(player, team);

        }).then(Commands.argument("team", RegionTeamArgument.team()).executes(ctx -> {

            Player player = ctx.getSource().getPlayerOrException();
            RegionTeam team = null;

            try {
                team = RegionTeamArgument.getTeam(ctx, "team");
            }

            catch (CommandSyntaxException ignored) {}

            if (team == null) {
                CrimeCommandsBase.sendError(player, "That team does not exist!");
                return Command.SINGLE_SUCCESS;
            }

            return teamInfo(player, team);
        }));
    }

    private static int teamInfo(Player player, RegionTeam team) {

        StringBuilder owners = new StringBuilder();
        StringBuilder members = new StringBuilder();
        StringBuilder invitees = new StringBuilder();
        StringBuilder allies = new StringBuilder();

        for (RegionTeamMember member : team.getMembers()) {

            if (member.isOwner()) {
                owners.append("[").append(member.getName()).append("] ");
            }

            members.append("[").append(member.getName()).append("] ");
        }

        for (RegionTeamPlayer invitee : team.getInvited()) {
            invitees.append("[").append(invitee.getName()).append("] ");
        }

        for (RegionTeamPlayer ally : team.getAllies()) {
            allies.append("[").append(ally.getName()).append("] ");
        }

        CrimeCommandsBase.sendSuccess(player, "Team Info: " + team.getName());
        CrimeCommandsBase.sendWarning(player, "Owners: " + owners);
        CrimeCommandsBase.sendWarning(player, "Members: " + members);
        CrimeCommandsBase.sendWarning(player, "Invited: " + invitees);
        CrimeCommandsBase.sendWarning(player, "Allies: " + allies);
        return Command.SINGLE_SUCCESS;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> teamRename() {

        return Commands.literal("rename").then(Commands.argument("teamName", StringArgumentType.word()).executes(ctx -> {

            Player player = ctx.getSource().getPlayerOrException();

            String teamName = StringArgumentType.getString(ctx, "teamName");

            if (checkTeam(player, false,"You are not in a team!")) return Command.SINGLE_SUCCESS;
            if (checkOwner(player, false, "Only team owners can rename their team!")) return Command.SINGLE_SUCCESS;

            if (teamName.length() > 16) {
                CrimeCommandsBase.sendError(player, "That team name is too long! Must be below 16 characters!");
                return Command.SINGLE_SUCCESS;
            }

            RegionTeam team = RegionTeamHelper.getTeam(player);
            team.setName(teamName);

            CrimeCommandsBase.sendSuccess(player, "Your team's name is now: " + teamName);
            return Command.SINGLE_SUCCESS;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> teamInvite() {

        return Commands.literal("invite").then(Commands.argument("invitee", EntityArgument.player()).executes(ctx -> {

            Player player = ctx.getSource().getPlayerOrException();
            Player invitee = EntityArgument.getPlayer(ctx, "invitee");

            if (checkTeam(player, false, "You are not in a team!")) return Command.SINGLE_SUCCESS;
            if (checkOwner(player, false,"Only team owners can invite players to their team!")) return Command.SINGLE_SUCCESS;

            if (RegionTeamHelper.hasTeam(invitee)) {
                CrimeCommandsBase.sendError(player, "That player is already on a team!");
                return Command.SINGLE_SUCCESS;
            }

            RegionTeam team = RegionTeamHelper.getTeam(player);

            if (team.isInvited(invitee)) {
                CrimeCommandsBase.sendError(player, "That player has already been invited!");
                return Command.SINGLE_SUCCESS;
            }

            team.invite(invitee);

            CrimeCommandsBase.sendSuccess(invitee,player.getName().getString() + " has invited you to their team: " + team.getName());

            Component component = Component.Serializer.fromJson("{\"selector\":\"[Click here to join their team]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/crime team join " + team.getName() + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[]}}");
            invitee.sendMessage(component, Util.NIL_UUID);

            CrimeCommandsBase.sendSuccess(player, invitee.getName().getString() + " has been invited.");
            return Command.SINGLE_SUCCESS;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> teamUninvite() {

        return Commands.literal("uninvite").then(Commands.argument("uninviteeName", StringArgumentType.word()).executes(ctx -> {

            Player player = ctx.getSource().getPlayerOrException();
            String uninviteeName = StringArgumentType.getString(ctx, "uninviteeName");

            if (checkTeam(player, false,"You are not in a team!")) return Command.SINGLE_SUCCESS;
            if (checkOwner(player, false, "Only team owners can uninvite players from their team!")) return Command.SINGLE_SUCCESS;

            RegionTeam team = RegionTeamHelper.getTeam(player);

            boolean foundUninvitee = false;

            for (RegionTeamPlayer invitee : team.getInvited()) {

                if (invitee.getName().equals(uninviteeName)) {
                    foundUninvitee = true;
                    break;
                }
            }

            if (!foundUninvitee) {
                CrimeCommandsBase.sendError(player, "Could not find that player on your invited list.");
                return Command.SINGLE_SUCCESS;
            }

            team.uninvite(uninviteeName);

            CrimeCommandsBase.sendSuccess(player, uninviteeName + " has been removed from your invited list.");
            return Command.SINGLE_SUCCESS;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> teamKick() {

        return Commands.literal("kick").then(Commands.argument("kickedPlayerName", StringArgumentType.word()).executes(ctx -> {

            Player player = ctx.getSource().getPlayerOrException();
            String kickedPlayerName = StringArgumentType.getString(ctx, "kickedPlayerName");

            if (checkTeam(player, false,"You are not in a team!")) return Command.SINGLE_SUCCESS;
            if (checkOwner(player, false, "Only team owners can kick players from their team!")) return Command.SINGLE_SUCCESS;

            RegionTeam team = RegionTeamHelper.getTeam(player);

            UUID kickedPlayerID = null;

            for (RegionTeamPlayer member : team.getMembers()) {

                if (member.getName().equals(kickedPlayerName)) {
                    kickedPlayerID = member.getID();
                    break;
                }
            }

            if (kickedPlayerID == null) {
                CrimeCommandsBase.sendError(player, "That player is not a member!");
                return Command.SINGLE_SUCCESS;
            }

            team.removeMember(kickedPlayerID);

            CrimeCommandsBase.sendSuccess(player, kickedPlayerName + " has been kicked from the team.");
            return Command.SINGLE_SUCCESS;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> teamJoin() {

        return Commands.literal("join").then(Commands.argument("team", RegionTeamArgument.team()).executes(ctx -> {

            Player player = ctx.getSource().getPlayerOrException();
            RegionTeam team = null;

            try {
                team = RegionTeamArgument.getTeam(ctx, "team");
            }

            catch (CommandSyntaxException ignored) {}

            if (team == null) {
                CrimeCommandsBase.sendError(player, "That team does not exist!");
                return Command.SINGLE_SUCCESS;
            }

            if (checkTeam(player, true, "You are already in a team!")) return Command.SINGLE_SUCCESS;

            if (!team.isInvited(player)) {
                CrimeCommandsBase.sendError(player, "You are not invited to that team!");
                return Command.SINGLE_SUCCESS;
            }

            for (RegionTeamMember member : team.getMembers()) {

                Player memberPlayer = player.getLevel().getPlayerByUUID(member.getID());

                if (memberPlayer != null) {
                    CrimeCommandsBase.sendSuccess(memberPlayer, player.getName().getString() + " has joined your team!");
                }
            }

            team.addMember(player);

            CrimeCommandsBase.sendSuccess(player, "You have joined the team.");
            return Command.SINGLE_SUCCESS;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> teamLeave() {

        return Commands.literal("leave").executes(ctx -> {

            Player player = ctx.getSource().getPlayerOrException();

            if (checkTeam(player, false,"You are not in a team!")) return Command.SINGLE_SUCCESS;

            RegionTeam team = RegionTeamHelper.getTeam(player);
            team.removeMember(player);

            CrimeCommandsBase.sendSuccess(player, "You have left your team.");
            return Command.SINGLE_SUCCESS;
        });
    }

    private static ArgumentBuilder<CommandSourceStack, ?> teamPromote() {

        return Commands.literal("promote").then(Commands.argument("promotee", EntityArgument.player()).executes(ctx -> {

            Player player = ctx.getSource().getPlayerOrException();
            Player promotee = EntityArgument.getPlayer(ctx, "promotee");

            if (checkTeam(player, false, "You are not in a team!")) return Command.SINGLE_SUCCESS;
            if (checkOwner(player, false,"Only team owners can promote players in their team!")) return Command.SINGLE_SUCCESS;

            RegionTeam team = RegionTeamHelper.getTeam(player);

            for (RegionTeamMember member : team.getMembers()) {

                if (member.getID().equals(promotee.getUUID())) {

                    if (member.isOwner()) {
                        CrimeCommandsBase.sendError(player, promotee.getName().getString() + " is already an owner.");
                        return Command.SINGLE_SUCCESS;
                    }

                    member.setOwnerStatus(true);
                    RegionTeamsFile.save();
                    CrimeCommandsBase.sendSuccess(player, promotee.getName().getString() + " has been promoted to owner status.");
                    return Command.SINGLE_SUCCESS;
                }
            }

            CrimeCommandsBase.sendError(player, promotee.getName().getString() + " could not be found in your team!");
            return Command.SINGLE_SUCCESS;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> teamDemote() {

        return Commands.literal("demote").then(Commands.argument("demotee", StringArgumentType.word()).executes(ctx -> {

            Player player = ctx.getSource().getPlayerOrException();
            String demotee = StringArgumentType.getString(ctx, "demotee");

            if (checkTeam(player, false,"You are not in a team!")) return Command.SINGLE_SUCCESS;
            if (checkOwner(player, false, "Only team owners can demote players in their team!")) return Command.SINGLE_SUCCESS;

            RegionTeam team = RegionTeamHelper.getTeam(player);

            for (RegionTeamMember member : team.getMembers()) {

                if (member.getName().equals(demotee)) {

                    if (player.getUUID().equals(member.getID())) {
                        CrimeCommandsBase.sendError(player, "You cannot demote yourself.");
                        return Command.SINGLE_SUCCESS;
                    }

                    member.setOwnerStatus(false);
                    RegionTeamsFile.save();
                    CrimeCommandsBase.sendSuccess(player, demotee + " has been demoted to member status.");
                    return Command.SINGLE_SUCCESS;
                }
            }

            CrimeCommandsBase.sendError(player, demotee + " could not be found in your team!");
            return Command.SINGLE_SUCCESS;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> teamAlly() {

        return Commands.literal("ally").then(Commands.argument("allyPlayer", EntityArgument.player()).executes(ctx -> {

            Player player = ctx.getSource().getPlayerOrException();
            Player ally = EntityArgument.getPlayer(ctx, "allyPlayer");

            if (checkTeam(player, false,"You are not in a team!")) return Command.SINGLE_SUCCESS;
            if (checkOwner(player, false,"Only team owners can ally players!")) return Command.SINGLE_SUCCESS;

            RegionTeam team = RegionTeamHelper.getTeam(player);

            if (team.isMember(ally)) {
                CrimeCommandsBase.sendError(player, "You cannot ally a player that is already in your team!");
                return Command.SINGLE_SUCCESS;
            }

            if (team.isAlly(ally)) {
                CrimeCommandsBase.sendError(player, "That player is already an ally!");
                return Command.SINGLE_SUCCESS;
            }

            team.ally(ally);

            CrimeCommandsBase.sendSuccess(player, ally.getName().getString() + " is now an ally.");
            return Command.SINGLE_SUCCESS;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> teamDisally() {

        return Commands.literal("disally").then(Commands.argument("disallyName", StringArgumentType.word()).executes(ctx -> {

            Player player = ctx.getSource().getPlayerOrException();
            String disally = StringArgumentType.getString(ctx, "disallyName");

            if (checkTeam(player, false,"You are not in a team!")) return Command.SINGLE_SUCCESS;
            if (checkOwner(player, false, "Only team owners can disally players!")) return Command.SINGLE_SUCCESS;

            RegionTeam team = RegionTeamHelper.getTeam(player);

            if (!team.isAlly(disally)) {
                CrimeCommandsBase.sendError(player, "That player is not an ally!");
                return Command.SINGLE_SUCCESS;
            }

            team.disally(disally);

            CrimeCommandsBase.sendSuccess(player, disally + " is no longer an ally.");
            return Command.SINGLE_SUCCESS;
        }));
    }

    private static boolean checkTeam(Player player, boolean errorCondition, String errorMsg) {

        if (RegionTeamHelper.hasTeam(player) == errorCondition) {
            CrimeCommandsBase.sendError(player, errorMsg);
            return true;
        }

        return false;
    }

    private static boolean checkOwner(Player player, boolean errorCondition, String errorMsg) {

        if (RegionTeamHelper.getTeamMembership(player).isOwner() == errorCondition) {
            CrimeCommandsBase.sendError(player, errorMsg);
            return true;
        }

        return false;
    }
}
