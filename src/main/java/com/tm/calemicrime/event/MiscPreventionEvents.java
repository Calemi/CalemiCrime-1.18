package com.tm.calemicrime.event;

import com.tm.calemicore.util.Location;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.PistonEvent;
import net.minecraftforge.event.world.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MiscPreventionEvents {

    @SubscribeEvent
    public void onPistonEvent(PistonEvent.Pre event) {

        Location location = new Location((Level) event.getWorld(), event.getPos());

        if (location.getBlock() == Blocks.STICKY_PISTON) {
            event.setCanceled(true);
            return;
        }

        Location moveLocation = new Location((Level) event.getWorld(), event.getFaceOffsetPos());

        if (event.getPistonMoveType().isExtend) {
            if (!moveLocation.isAirBlock() && moveLocation.getBlock().getPistonPushReaction(moveLocation.getBlockState()) != PushReaction.DESTROY) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPistonEvent(BlockEvent.PortalSpawnEvent event) {
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onSaplingGrow(SaplingGrowTreeEvent event) {

        if (event.getFeature() == TreeFeatures.HUGE_BROWN_MUSHROOM || event.getFeature() == TreeFeatures.HUGE_RED_MUSHROOM) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onSaplingGrow(ExplosionEvent event) {
        event.setCanceled(true);
        event.setResult(Event.Result.DENY);
    }
}
