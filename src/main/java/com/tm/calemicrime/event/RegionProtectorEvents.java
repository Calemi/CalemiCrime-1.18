package com.tm.calemicrime.event;

import com.mrcrayfish.guns.event.GunFireEvent;
import com.mrcrayfish.guns.event.GunProjectileHitEvent;
import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicrime.blockentity.BlockEntityRegionProtector;
import com.tm.calemicrime.blockentity.BlockEntityRentAcceptor;
import com.tm.calemicrime.main.CCReference;
import com.tm.calemicrime.util.RegionRuleSet;
import com.tm.calemieconomy.block.BlockTradingPost;
import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;

public class RegionProtectorEvents {

    /**
     * BLOCK BREAKING RULE CHECK
     */
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        Location location = new Location(event.getPlayer().getLevel(), event.getPos());
        handleEventCancellation(event, event.getWorld(), event.getPlayer(), location, 0);
    }

    /**
     * BLOCK PLACING RULE CHECK
     */
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {

        Location location = new Location(event.getEntity().getLevel(), event.getPos());

        if (event.getEntity() instanceof Player player) {

            BlockEntityRegionProtector regionProtector = getPrioritizedRegionProtector(location, 1);
            Block block = event.getPlacedBlock().getBlock();

            if (!player.isCreative() && !player.getLevel().isClientSide()) {

                if (regionProtector != null) {

                    if (regionProtector.getRegionType() == BlockEntityRegionProtector.RegionType.COMMERCIAL) {

                        if (block instanceof BedBlock) {
                            player.sendMessage(new TextComponent(ChatFormatting.RED + "You cannot place a bed in a commercial plot!"), Util.NIL_UUID);
                            event.setCanceled(true);
                            return;
                        }
                    }

                    else if (regionProtector.getRegionType() == BlockEntityRegionProtector.RegionType.RESIDENTIAL) {

                        if (block instanceof BlockTradingPost) {
                            player.sendMessage(new TextComponent(ChatFormatting.RED + "You cannot place a Trading Post in a residential plot!"), Util.NIL_UUID);
                            event.setCanceled(true);
                            return;
                        }
                    }
                }
            }

            handleEventCancellation(event, event.getWorld(), player, location, 1);
        }
    }

    /**
     * BLOCK USING RULE CHECK
     */
    @SubscribeEvent
    public void onBlockUse(PlayerInteractEvent.RightClickBlock event) {

        Location location = new Location(event.getEntity().getLevel(), event.getPos());

        if (location.getBlockEntity() != null) {

            if (location.getBlockEntity() instanceof BlockEntityTradingPost || location.getBlockEntity() instanceof BlockEntityRentAcceptor) {
                return;
            }
        }

        if (event.getEntity() instanceof Player player) {
            handleEventCancellation(event, event.getWorld(), player, location, 2);
        }
    }

    /**
     * BLOCK USING RULE BUCKET CHECK
     */
    @SubscribeEvent
    public void onBucketUse(FillBucketEvent event) {

        Location location = new Location(event.getEntity().getLevel(), new BlockPos(event.getTarget().getLocation()));

        if (event.getEntity() instanceof Player player) {

            if (player.isCreative()) {
                return;
            }

            if (player.getMainHandItem().getItem() instanceof BucketItem bucket) {

                if (event.getEmptyBucket().getItem() == Items.BUCKET || bucket.getFluid() == Fluids.WATER) {
                    handleEventCancellation(event, event.getWorld(), player, location, 2);
                }

                else {
                    event.setCanceled(true);
                }
            }

            else if (player.getOffhandItem().getItem() instanceof BucketItem bucket) {

                if (event.getEmptyBucket().getItem() == Items.BUCKET || bucket.getFluid() == Fluids.WATER) {
                    handleEventCancellation(event, event.getWorld(), player, location, 2);
                }

                else {
                    event.setCanceled(true);
                }
            }
        }
    }

    /**
     * ENTITY & PLAYER HURTING RULE CHECK
     */
    @SubscribeEvent
    public void onEntityHurt(AttackEntityEvent event) {

        Location location = new Location(event.getTarget());

        if (!(event.getTarget() instanceof Player)) {
            handleEventCancellation(event, event.getEntity().getLevel(), event.getPlayer(), location, 3);
        }

        else handleEventCancellation(event, event.getEntity().getLevel(), event.getPlayer(), location, 5);
    }

    /**
     * ENTITY & PLAYER SHOOTING PROJECTILE RULE CHECK
     */
    @SubscribeEvent
    public void onArrowShot(ProjectileImpactEvent event) {
        Location location = new Location(event.getEntity().getLevel(), new BlockPos(event.getRayTraceResult().getLocation()));
        handleEventCancellation(event, event.getEntity().getLevel(), null, location, 5);
    }

    /**
     * ENTITY & PLAYER SHOOTING GUN RULE CHECK
     */
    @SubscribeEvent
    public void onBulletHit(GunProjectileHitEvent event) {
        Location location = new Location(event.getProjectile().getLevel(), new BlockPos(event.getRayTrace().getLocation()));
        handleEventCancellation(event, event.getProjectile().getLevel(), null, location, 5);
    }

    /**
     * ENTITY INTERACTING RULE CHECK
     */
    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {

        Location location = new Location(event.getEntity().getLevel(), event.getPos());
        handleEventCancellation(event, event.getWorld(), event.getPlayer(), location, 4);
    }

    private void handleEventCancellation(Event event, LevelAccessor level, Player player, Location location, int ruleSetIndex) {
        handleEventCancellation(getPrioritizedRegionProtector(location, ruleSetIndex), event, level, player, location, ruleSetIndex);
    }

    private void handleEventCancellation(BlockEntityRegionProtector regionProtector, Event event, LevelAccessor level, Player player, Location location, int ruleSetIndex) {

        if (!level.isClientSide() && (player == null || !player.isCreative())) {

            LogHelper.log(CCReference.MOD_NAME, "EVENT CALLED");

            if (regionProtector != null) {

                LogHelper.log(CCReference.MOD_NAME, "FOUND REGION PROTECTOR");

                BlockEntityRentAcceptor rentAcceptor = regionProtector.getRentAcceptor();

                boolean hasPlayer = player != null;
                boolean hasRentAcceptor = rentAcceptor != null;
                boolean sameTeamOrAlly = hasRentAcceptor && (rentAcceptor.getResidentTeam().isMember(player.getUUID()) || rentAcceptor.getResidentTeam().isAlly(player.getUUID()));
                boolean ruleNotOff = hasRentAcceptor && rentAcceptor.getRegionRuleSetOverride().ruleSets[ruleSetIndex] != RegionRuleSet.RuleOverrideType.OFF;

                if (hasPlayer && hasRentAcceptor && sameTeamOrAlly && ruleNotOff) {

                    if (rentAcceptor.getRegionRuleSetOverride().ruleSets[ruleSetIndex] == RegionRuleSet.RuleOverrideType.PREVENT) {
                        event.setCanceled(true);
                    }
                }

                else if (regionProtector.getRegionRuleSet().ruleSets[ruleSetIndex] == RegionRuleSet.RuleOverrideType.PREVENT) {

                    if (player != null) player.sendMessage(new TextComponent(ChatFormatting.RED + "You cannot do that in this area!"), Util.NIL_UUID);

                    event.setCanceled(true);
                }
            }
        }
    }

    private BlockEntityRegionProtector getPrioritizedRegionProtector(Location location, int ruleSetIndex) {

        ArrayList<BlockEntityRegionProtector> regionProtectorsAffectingLocation = new ArrayList<>();

        for (BlockEntityRegionProtector regionProtector : BlockEntityRegionProtector.getRegionProtectors()) {

            if (!regionProtector.isRemoved()) {

                if (regionProtector.getRegion().contains(location.getVector()) || regionProtector.isGlobal()) {
                    regionProtectorsAffectingLocation.add(regionProtector);
                }
            }
        }

        if (!regionProtectorsAffectingLocation.isEmpty()) {

            BlockEntityRegionProtector regionProtector = null;

            for (BlockEntityRegionProtector regionProtectorAffectingLocation : regionProtectorsAffectingLocation) {

                if (regionProtectorAffectingLocation.getRegionRuleSet().ruleSets[ruleSetIndex] != RegionRuleSet.RuleOverrideType.OFF) {

                    if (regionProtector == null || regionProtectorAffectingLocation.getPriority() > regionProtector.getPriority()) {
                        regionProtector = regionProtectorAffectingLocation;
                    }
                }
            }

            return regionProtector;
        }

        return null;
    }
}
