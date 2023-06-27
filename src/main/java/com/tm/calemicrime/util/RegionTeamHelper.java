package com.tm.calemicrime.util;

import com.tm.calemicrime.file.RegionTeamsFile;
import com.tm.calemicrime.team.RegionTeam;
import com.tm.calemicrime.team.RegionTeamMember;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class RegionTeamHelper {

    public static RegionTeam getRegionTeam(UUID id) {

        for (RegionTeam team : RegionTeamsFile.teams) {

            if (team.getId().equals(id)) {
                return team;
            }
        }

        return null;
    }

    public static RegionTeam getRegionTeam(String teamName) {

        for (RegionTeam team : RegionTeamsFile.teams) {

            if (team.getName().equals(teamName)) {
                return team;
            }
        }

        return null;
    }

    public static RegionTeam getRegionTeam(Player player) {

        for (RegionTeam team : RegionTeamsFile.teams) {

            for (RegionTeamMember member : team.getMembers()) {

                if (member.getID().equals(player.getUUID())) {
                    return team;
                }
            }
        }

        return null;
    }

    public static boolean hasRegionTeam(Player player) {
        return getRegionTeam(player) != null;
    }

    public static RegionTeamMember getRegionTeamMembership(Player player) {

        RegionTeam team = getRegionTeam(player);

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
