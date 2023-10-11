package com.tm.calemicrime.event;

import com.tm.calemicore.util.Location;
import com.tm.calemicrime.blockentity.BlockEntityRentAcceptor;
import com.tm.calemicrime.file.RegionTeamsFile;
import com.tm.calemicrime.team.RegionTeam;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RegionTeamRentAcceptorsCleanEvents {

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {

        if (event.world.isClientSide()) {
            return;
        }

        if (event.world.getGameTime() % 20 == 0) {

            for (RegionTeam team : RegionTeamsFile.teams) {

                if (team.getOwnedRentAcceptorPositions() == null) {
                    return;
                }

                for (RegionTeam.RentAcceptorPos pos : team.getOwnedRentAcceptorPositions()) {

                    Location location = pos.getLocation(event.world);

                    if (location.level.isLoaded(location.getBlockPos())) {

                        if (location.getBlockEntity() == null) {
                            team.removeRentAcceptorPosition(location);
                            return;
                        }

                        if (!(location.getBlockEntity() instanceof BlockEntityRentAcceptor rentAcceptor)) {
                            team.removeRentAcceptorPosition(location);
                            return;
                        }

                        if (rentAcceptor.residentTeamID == null || !rentAcceptor.residentTeamID.equals(team.getId())) {
                            team.removeRentAcceptorPosition(location);
                            return;
                        }
                    }
                }
            }
        }
    }
}
