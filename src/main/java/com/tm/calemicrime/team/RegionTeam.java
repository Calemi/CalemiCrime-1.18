package com.tm.calemicrime.team;

import com.tm.calemicrime.file.RegionTeamsFile;
import com.tm.calemicrime.util.RegionTeamHelper;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class RegionTeam {

    private final UUID id;
    private String name;
    private final List<RegionTeamMember> members;
    private final List<RegionTeamPlayer> invited;
    private final List<RegionTeamPlayer> allies;

    private RegionTeam(String name, List<RegionTeamMember> members) {

        this.id = UUID.randomUUID();
        this.name = name;
        this.members = members;

        invited = new ArrayList<>();
        allies = new ArrayList<>();
    }

    public static RegionTeam create(String name, Player admin) {

        RegionTeam team = new RegionTeam(name, new ArrayList<>());

        RegionTeamsFile.teams.add(team);

        RegionTeamMember member = team.addMember(admin);

        if (member != null) {
            member.setOwnerStatus(true);
        }

        RegionTeamsFile.save();

        return team;
    }

    public void delete() {
        RegionTeamsFile.teams.remove(this);
        RegionTeamsFile.save();
    }

    public RegionTeamMember addMember(Player player) {

        if (RegionTeamHelper.hasRegionTeam(player)) {
            return null;
        }

        RegionTeamMember member = new RegionTeamMember(player);
        members.add(member);

        uninvite(player.getName().getString());
        disally(player.getName().getString());

        RegionTeamsFile.save();

        return member;
    }

    public void removeMember(UUID id) {

        RegionTeamMember memberToRemove = null;

        for (RegionTeamMember member : getMembers()) {

            if (member.getID().equals(id)) {
                memberToRemove = member;
                break;
            }
        }

        members.remove(memberToRemove);

        boolean foundOwner = false;

        for (RegionTeamMember member : getMembers()) {

            if (member.isOwner()) {
                foundOwner = true;
                break;
            }
        }

        if (!foundOwner && members.size() > 0) {
            members.get(0).setOwnerStatus(true);
        }

        RegionTeamsFile.save();
    }

    public void removeMember(Player player) {
        removeMember(player.getUUID());
    }

    public boolean isMember(Player player) {

        for (RegionTeamMember member : getMembers()) {

            if (member.getID().equals(player.getUUID())) {
                return true;
            }
        }

        return false;
    }

    public boolean isOwner(Player player) {

        for (RegionTeamMember member : getMembers()) {

            if (member.getID().equals(player.getUUID())) {

                if (member.isOwner()) {
                    return true;
                }
            }
        }

        return false;
    }

    public void invite(Player player) {
        getInvited().add(new RegionTeamPlayer(player));
        RegionTeamsFile.save();
    }

    public void uninvite(String playerName) {

        for (RegionTeamPlayer teamPlayer : getInvited()) {

            if (teamPlayer.getName().equals(playerName)) {
                getInvited().remove(teamPlayer);
                break;
            }
        }

        RegionTeamsFile.save();
    }

    public boolean isInvited(Player player) {

        for (RegionTeamPlayer teamPlayer : getInvited()) {

            if (teamPlayer.getID().equals(player.getUUID())) {
                return true;
            }
        }

        return false;
    }

    public void ally(Player player) {
        getAllies().add(new RegionTeamPlayer(player));
        RegionTeamsFile.save();
    }

    public void disally(String playerName) {

        for (RegionTeamPlayer teamPlayer : getAllies()) {

            if (teamPlayer.getName().equals(playerName)) {
                getAllies().remove(teamPlayer);
                break;
            }
        }

        RegionTeamsFile.save();
    }

    public boolean isAlly(Player player) {

        for (RegionTeamPlayer ally : getAllies()) {

            if (ally.getID().equals(player.getUUID())) {
                return true;
            }
        }

        return false;
    }

    public boolean isAlly(String playerName) {

        for (RegionTeamPlayer ally : getAllies()) {

            if (ally.getName().equals(playerName)) {
                return true;
            }
        }

        return false;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        RegionTeamsFile.save();
    }

    public List<RegionTeamMember> getMembers() {
        return members;
    }

    public List<RegionTeamPlayer> getInvited() {
        return invited;
    }

    public List<RegionTeamPlayer> getAllies() {
        return allies;
    }
}
