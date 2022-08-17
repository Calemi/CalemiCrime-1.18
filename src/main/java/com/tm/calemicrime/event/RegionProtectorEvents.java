package com.tm.calemicrime.event;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicrime.blockentity.BlockEntityRegionProtector;
import com.tm.calemicrime.blockentity.BlockEntityRentAcceptor;
import com.tm.calemicrime.file.PreventBlockPlaceListFile;
import com.tm.calemicrime.main.CCReference;
import com.tm.calemicrime.util.RegionRuleSet;
import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import dev.ftb.mods.ftbteams.data.TeamManager;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;

public class RegionProtectorEvents {

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {

        Location location = new Location(event.getPlayer().getLevel(), event.getPos());

        handleEventCancellation(event, event.getWorld(), event.getPlayer(), location, 0);
    }

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {

        Location location = new Location(event.getEntity().getLevel(), event.getPos());

        if (event.getEntity() instanceof Player player) {

            handleEventCancellation(event, event.getWorld(), player, location, 1);
        }
    }

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

    @SubscribeEvent
    public void onEntityHurt(AttackEntityEvent event) {

        Location location = new Location(event.getEntity().getLevel(), event.getEntity().getOnPos().offset(0, 1, 0));

        if (!(event.getEntity() instanceof Player)) {
            handleEventCancellation(event, event.getEntity().getLevel(), event.getPlayer(), location, 3);
        }

        else handleEventCancellation(event, event.getEntity().getLevel(), event.getPlayer(), location, 4);
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {

        Location location = new Location(event.getEntity().getLevel(), event.getPos());
        handleEventCancellation(event, event.getWorld(), event.getPlayer(), location, 4);
    }

    private void handleEventCancellation(Event event, LevelAccessor level, Player player, Location location, int ruleSetIndex) {

        if (!level.isClientSide() && (player == null || !player.isCreative())) {

            LogHelper.log(CCReference.MOD_NAME, "EVENT CALLED");

            BlockEntityRegionProtector regionProtector = getPrioritizedRegionProtector(location, ruleSetIndex);

            if (regionProtector != null) {

                LogHelper.log(CCReference.MOD_NAME, "FOUND REGION PROTECTOR");

                BlockEntityRentAcceptor rentAcceptor = regionProtector.getRentAcceptor();

                boolean hasPlayer = player != null;
                boolean hasRentAcceptor = rentAcceptor != null;
                boolean sameTeam = hasRentAcceptor && TeamManager.INSTANCE.getPlayerTeam(player.getUUID()) == rentAcceptor.getResidentTeam();
                boolean ruleNotOff = hasRentAcceptor && rentAcceptor.getRegionRuleSetOverride().ruleSets[ruleSetIndex] != RegionRuleSet.RuleOverrideType.OFF;

                if (hasPlayer && hasRentAcceptor && sameTeam && ruleNotOff) {

                    if (rentAcceptor.getRegionRuleSetOverride().ruleSets[ruleSetIndex] == RegionRuleSet.RuleOverrideType.PREVENT) {
                        event.setCanceled(true);
                    }
                }

                else if (regionProtector.getRegionRuleSet().ruleSets[ruleSetIndex] == RegionRuleSet.RuleOverrideType.PREVENT) {

                    player.sendMessage(new TextComponent(ChatFormatting.RED + "You cannot do that in this area!"), Util.NIL_UUID);

                    event.setCanceled(true);
                }
            }
        }
    }

    private BlockEntityRegionProtector getPrioritizedRegionProtector(Location location, int ruleSetIndex) {

        ArrayList<BlockEntityRegionProtector> regionProtectorsAffectingLocation = new ArrayList<>();

        for (BlockEntityRegionProtector regionProtector : BlockEntityRegionProtector.getRegionProtectors()) {

            if (!regionProtector.isRemoved() && (regionProtector.getRegion().contains(location.getVector()) || regionProtector.isGlobal())) {
                regionProtectorsAffectingLocation.add(regionProtector);
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
