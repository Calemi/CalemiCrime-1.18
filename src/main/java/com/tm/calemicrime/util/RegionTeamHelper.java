package com.tm.calemicrime.util;

import com.tm.calemicrime.file.RegionTeamsFile;
import com.tm.calemicrime.team.RegionTeam;
import com.tm.calemicrime.team.RegionTeamMember;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class RegionTeamHelper {

    public static RegionTeam getTeam(UUID teamID) {

        for (RegionTeam team : RegionTeamsFile.teams) {

            if (team.getId().equals(teamID)) {
                return team;
            }
        }

        return null;
    }

    public static RegionTeam getTeam(String teamName) {

        for (RegionTeam team : RegionTeamsFile.teams) {

            if (team.getName().equals(teamName)) {
                return team;
            }
        }

        return null;
    }

    public static RegionTeam getTeam(Player player) {

        for (RegionTeam team : RegionTeamsFile.teams) {

            for (RegionTeamMember member : team.getMembers()) {

                if (member.getID().equals(player.getUUID())) {
                    return team;
                }
            }
        }

        return null;
    }

    public static RegionTeam getTeamByPlayerID(UUID playerID) {

        for (RegionTeam team : RegionTeamsFile.teams) {

            for (RegionTeamMember teamMember : team.getMembers()) {

                if (teamMember.getID().equals(playerID)) {
                    return team;
                }
            }
        }

        return null;
    }

    public static boolean hasTeam(Player player) {
        return getTeam(player) != null;
    }

    public static RegionTeamMember getTeamMembership(Player player) {

        RegionTeam team = getTeam(player);

        if (team == null) {
            return null;
        }

        for (RegionTeamMember member : team.getMembers()) {

            if (member.getID().equals(player.getUUID())) {
                return member;
            }
        }

        return null;
    }
}
