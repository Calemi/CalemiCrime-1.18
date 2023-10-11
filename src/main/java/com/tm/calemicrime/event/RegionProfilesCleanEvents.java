package com.tm.calemicrime.event;

import com.tm.calemicrime.blockentity.BlockEntityRegionProtector;
import com.tm.calemicrime.blockentity.BlockEntityRentAcceptor;
import com.tm.calemicrime.util.RegionHelper;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RegionProfilesCleanEvents {

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {

        if (event.world.isClientSide()) {
            return;
        }

        if (event.world.getGameTime() % 20 == 0) {

            for (BlockPos pos : RegionHelper.allProfiles.keySet()) {

                if (event.world.isLoaded(pos)) {

                    if (!(event.world.getBlockEntity(pos) instanceof BlockEntityRegionProtector)) {
                        RegionHelper.allProfiles.remove(pos);
                        return;
                    }
                }
            }
        }
    }
}
