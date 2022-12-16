package com.tm.calemicrime.event;

import com.mrcrayfish.guns.entity.ProjectileEntity;
import com.mrcrayfish.guns.event.GunProjectileHitEvent;
import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicrime.blockentity.BlockEntityRegionProtector;
import com.tm.calemicrime.blockentity.BlockEntityRentAcceptor;
import com.tm.calemicrime.main.CCReference;
import com.tm.calemicrime.util.RegionRuleSet;
import com.tm.calemieconomy.block.BlockTradingPost;
import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import com.tm.calemieconomy.item.ItemSecurityWrench;
import dev.ftb.mods.ftbteams.data.Team;
import dev.ftb.mods.ftbteams.data.TeamManager;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;

public class RegionProtectorEvents {

    //-----------------------------------------------------------------------------------------------------------------------------------------------\\

    //RULE 0: BLOCK BREAKING CHECKS

    @SubscribeEvent
    public void checkBlockBreak(BlockEvent.BreakEvent event) {

        Level level = (Level) event.getWorld();
        Location location = new Location(level, event.getPos());
        Player player = event.getPlayer();

        if (handleEventCancellation(event, level, player, location, 0)) {
            sendErrorMessage(player);
            event.setCanceled(true);
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------\\

    //RULE 1: BLOCK PLACING CHECKS

    @SubscribeEvent
    public void checkBlockPlace(BlockEvent.EntityPlaceEvent event) {

        Level level = (Level) event.getWorld();
        Location location = new Location(level, event.getPos());
        Entity entity = event.getEntity();

        if (entity instanceof Player player) {

            BlockEntityRegionProtector regionProtector = getPrioritizedRegionProtector(location, 1);
            Block blockBeingPlaced = event.getPlacedBlock().getBlock();

            if (player.isCreative()) {
                return;
            }

            if (regionProtector != null) {

                if (regionProtector.getRegionType() == BlockEntityRegionProtector.RegionType.RESIDENTIAL) {

                    if (blockBeingPlaced instanceof BlockTradingPost) {
                        player.sendMessage(new TextComponent(ChatFormatting.RED + "You cannot place a Trading Post in a residential plot!"), Util.NIL_UUID);
                        event.setCanceled(true);
                        return;
                    }
                }
            }

            if (handleEventCancellation(event, level, player, location, 1)) {
                sendErrorMessage(player);
                event.setCanceled(true);
            }
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------\\

    //RULE 2: BLOCK USING CHECKS

    @SubscribeEvent
    public void checkBlockUse(PlayerInteractEvent.RightClickBlock event) {

        Level level = event.getWorld();
        Location location = new Location(level, event.getPos());
        Player player = event.getPlayer();

        if (location.getBlockEntity() != null) {

            if (location.getBlockEntity() instanceof BlockEntityTradingPost) {

                if (!(player.getMainHandItem().getItem() instanceof ItemSecurityWrench) && !(player.getOffhandItem().getItem() instanceof ItemSecurityWrench)) {
                    return;
                }
            }

            if (location.getBlockEntity() instanceof BlockEntityRentAcceptor) {
                return;
            }
        }

        if (handleEventCancellation(event, level, player, location, 2)) {
            event.setUseBlock(Event.Result.DENY);
            event.setUseItem(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void checkBucketUse(FillBucketEvent event) {

        Level level = event.getWorld();
        Location location = new Location(event.getEntity().getLevel(), new BlockPos(event.getTarget().getLocation()));
        Player player = event.getPlayer();

        if (player.isCreative()) {
            return;
        }

        checkBucket(event, level, location, player, InteractionHand.MAIN_HAND);
    }

    private void checkBucket(FillBucketEvent event, Level level, Location location, Player player, InteractionHand hand) {

        if (player.getItemInHand(hand).getItem() instanceof BucketItem bucket) {

            if (event.getEmptyBucket().getItem() == Items.BUCKET || bucket.getFluid() == Fluids.WATER) {

                if (handleEventCancellation(event, level, player, location, 2)) {
                    sendErrorMessage(player);
                    event.setCanceled(true);
                }
            }

            else {
                sendErrorMessage(player, "You cannot place any liquid other than water!");
                event.setCanceled(true);
            }
        }

        else if (hand == InteractionHand.MAIN_HAND) checkBucket(event, level, location, player, InteractionHand.OFF_HAND);
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------\\

    //RULE 3: ENTITY HURTING CHECKS

    @SubscribeEvent
    public void checkEntityHurt(AttackEntityEvent event) {

        Entity damager = event.getEntity();
        Entity target = event.getTarget();
        Level level = damager.getLevel();
        Location location = new Location(target);

        if (damager instanceof Player player && !(target instanceof Player)) {

            if (handleEventCancellation(event, level, player, location, 3)) {
                sendErrorMessage(player);
                event.setCanceled(true);
            }
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------\\

    //RULE 4: ENTITY INTERACTING CHECKS

    @SubscribeEvent
    public void checkEntityInteract(PlayerInteractEvent.EntityInteract event) {

        Level level = event.getWorld();
        Location location = new Location(level, event.getPos());
        Player player = event.getPlayer();

        if (handleEventCancellation(event, level, player, location, 4)) {
            if (event.getHand() == InteractionHand.MAIN_HAND) sendErrorMessage(player);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void checkEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {

        Level level = event.getWorld();
        Location location = new Location(level, event.getPos());
        Player player = event.getPlayer();

        if (handleEventCancellation(event, level, player, location, 4)) {
            if (event.getHand() == InteractionHand.MAIN_HAND) sendErrorMessage(player);
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------\\

    //RULE 5: PVP CHECKS

    @SubscribeEvent
    public void checkPVP(AttackEntityEvent event) {

        Entity damager = event.getEntity();
        Entity target = event.getTarget();
        Level level = damager.getLevel();
        Location location = new Location(target);

        if (damager instanceof Player playerDamager && target instanceof Player playerTarget) {

            if (handleEventCancellation(event, level, playerDamager, location, 5)) {
                sendErrorMessage(playerDamager);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void checkProjectileImpact(ProjectileImpactEvent event) {

        Entity projectile = event.getEntity();
        Level level = projectile.getLevel();
        Location location = new Location(level, new BlockPos(event.getRayTraceResult().getLocation()));

        if (handleEventCancellation(event, level, null, location, 5)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void checkBulletHit(GunProjectileHitEvent event) {

        ProjectileEntity projectile = event.getProjectile();
        Level level = projectile.getLevel();
        Location location = new Location(level, new BlockPos(event.getRayTrace().getLocation()));

        if (handleEventCancellation(event, level, null, location, 5)) {
            event.setCanceled(true);
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------\\

    //MISC CHECKS

    @SubscribeEvent
    public void checkSetSpawn(PlayerSetSpawnEvent event) {

        Player player = event.getPlayer();
        Level level = player.getLevel();
        Location location = new Location(level, event.getNewSpawn());

        BlockEntityRegionProtector regionProtector = getPrioritizedRegionProtector(location, 2);

        if (player.isCreative()) {
            return;
        }

        if (regionProtector != null) {

            if (regionProtector.getRegionType() == BlockEntityRegionProtector.RegionType.COMMERCIAL) {

                player.sendMessage(new TextComponent(ChatFormatting.RED + "You cannot set your spawn in a commercial plot!"), Util.NIL_UUID);
                event.setCanceled(true);
            }
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------\\

    private boolean handleEventCancellation(Event event, LevelAccessor level, Player player, Location location, int ruleSetIndex) {

        BlockEntityRegionProtector regionProtector = getPrioritizedRegionProtector(location, ruleSetIndex);

        if (player == null || !player.isCreative()) {

            LogHelper.log(CCReference.MOD_NAME, "EVENT CALLED");

            if (regionProtector != null) {

                LogHelper.log(CCReference.MOD_NAME, "FOUND REGION PROTECTOR");

                BlockEntityRentAcceptor rentAcceptor = regionProtector.getRentAcceptor();
                TeamManager manager = TeamManager.INSTANCE;

                if (player != null && rentAcceptor != null && manager != null) {

                    Team team = rentAcceptor.getResidentTeamServer(manager);

                    if (team != null) {

                        if (team.isMember(player.getUUID()) || team.isAlly(player.getUUID())) {

                            if (rentAcceptor.getRegionRuleSetOverride().ruleSets[ruleSetIndex] != RegionRuleSet.RuleOverrideType.OFF) {

                                LogHelper.log(CCReference.MOD_NAME, "TEAM FOUND! CHECKING NEW RULES");
                                return rentAcceptor.getRegionRuleSetOverride().ruleSets[ruleSetIndex] == RegionRuleSet.RuleOverrideType.PREVENT;
                            }
                        }
                    }
                }

                return regionProtector.getRegionRuleSet().ruleSets[ruleSetIndex] == RegionRuleSet.RuleOverrideType.PREVENT;
            }
        }

        return false;
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

    private void sendErrorMessage(Player player, String message) {
        if (!player.getLevel().isClientSide()) player.sendMessage(new TextComponent(ChatFormatting.RED + message), Util.NIL_UUID);
    }

    private void sendErrorMessage(Player player) {
        if (!player.getLevel().isClientSide()) player.sendMessage(new TextComponent(ChatFormatting.RED + "You cannot do that in this area!"), Util.NIL_UUID);
    }
}
