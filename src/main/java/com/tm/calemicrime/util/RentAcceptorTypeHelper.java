package com.tm.calemicrime.util;

import com.tm.calemicore.util.Location;
import com.tm.calemicrime.blockentity.BlockEntityRentAcceptor;
import net.minecraft.world.entity.player.Player;

public class RentAcceptorTypeHelper {

    public static int calculateRentTypeCount(Player player, String rentType) {

        int count = 0;

        for (BlockEntityRentAcceptor rentAcceptor : BlockEntityRentAcceptor.rentAcceptors) {

            if (rentAcceptor.rentType.equals(rentType)) {

                if (rentAcceptor.getResidentTeam() != null && rentAcceptor.getResidentTeam().equals(RegionTeamHelper.getRegionTeam(player))) {
                    count++;
                }
            }
        }

        return count;
    }
}
