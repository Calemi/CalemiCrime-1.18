package com.tm.calemicrime.util;

import com.tm.calemicrime.team.RegionTeam;
import net.minecraft.world.entity.player.Player;

public class RentAcceptorTypeHelper {

    public static int calculateRentTypeCount(Player player, String rentType) {

        RegionTeam team = RegionTeamHelper.getTeam(player);

        if (team == null) {
            return 0;
        }

        return team.getRentAcceptorCountOfType(player.getLevel(), rentType);
    }
}
