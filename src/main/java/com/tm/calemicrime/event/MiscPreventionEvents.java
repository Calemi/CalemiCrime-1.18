package com.tm.calemicrime.event;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import com.tm.calemicore.util.Location;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.PistonEvent;
import net.minecraftforge.event.world.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
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
    public void onExplosion(ExplosionEvent event) {

        if (event.isCancelable()) {
            event.setCanceled(true);
        }

        event.setResult(Event.Result.DENY);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockEvent.BreakEvent event) {

        Level level = (Level) event.getWorld();
        Location location = new Location(level, event.getPos());
        Player player = event.getPlayer();

        if (player instanceof DeployerFakePlayer) {

            if (location.getBlock() instanceof BlockDrawers) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent event) {

        if (event.getSource().getDirectEntity() instanceof FireworkRocketEntity) {
            event.setCanceled(true);
        }
    }
}
