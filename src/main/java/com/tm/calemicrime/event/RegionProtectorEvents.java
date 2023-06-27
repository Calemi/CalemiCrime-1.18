package com.tm.calemicrime.event;

import com.mrcrayfish.guns.entity.ProjectileEntity;
import com.mrcrayfish.guns.event.GunProjectileHitEvent;
import com.simibubi.create.content.equipment.wrench.WrenchItem;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import com.tm.calemicore.util.Location;
import com.tm.calemicrime.blockentity.BlockEntityRegionProtector;
import com.tm.calemicrime.blockentity.BlockEntityRentAcceptor;
import com.tm.calemicrime.team.RegionTeam;
import com.tm.calemicrime.util.RegionRuleSet;
import com.tm.calemieconomy.block.BlockTradingPost;
import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import com.tm.calemieconomy.item.ItemSecurityWrench;
import de.maxhenkel.car.entity.car.base.EntityVehicleBase;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlock;
import xyz.przemyk.simpleplanes.entities.PlaneEntity;

import java.util.ArrayList;

public class RegionProtectorEvents {

    //-----------------------------------------------------------------------------------------------------------------------------------------------\\

    //RULE 0: BLOCK BREAKING CHECKS

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void checkBlockBreak(BlockEvent.BreakEvent event) {

        Level level = (Level) event.getWorld();
        Location location = new Location(level, event.getPos());
        Player player = event.getPlayer();

        if (handleEventCancellation(event, level, player, location, 0)) {
            sendErrorMessage(player);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void checkBlockAttack(PlayerInteractEvent.LeftClickBlock event) {

        Level level = event.getWorld();
        Location location = new Location(level, event.getPos());
        Player player = event.getPlayer();

        if (handleEventCancellation(event, level, player, location, 0)) {
            sendErrorMessage(player);
            event.setCanceled(true);
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------\\

    //RULE 1: BLOCK PLACING CHECKS

    @SubscribeEvent(priority = EventPriority.HIGHEST)
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

                //DEPLOYER CONDITION
                if (player instanceof DeployerFakePlayer deployer) {

                    if (regionProtector != null && regionProtector.regionType == BlockEntityRegionProtector.RegionType.NONE) {
                        event.setCanceled(true);
                    }

                    return;
                }

                if (regionProtector.regionType == BlockEntityRegionProtector.RegionType.RESIDENTIAL) {

                    if (blockBeingPlaced instanceof BlockTradingPost) {
                        player.sendMessage(new TextComponent(ChatFormatting.RED + "You cannot place a Trading Post in a residential plot!"), Util.NIL_UUID);
                        event.setCanceled(true);
                        return;
                    }
                }
            }

            if (handleEventCancellation(event, level, player, location, 1)) {

                event.setCanceled(true);
            }
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------\\

    //RULE 2: BLOCK USING CHECKS

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void checkBlockUse(PlayerInteractEvent.RightClickBlock event) {

        Level level = event.getWorld();
        Location location = new Location(level, event.getPos());
        Player player = event.getPlayer();
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        //BACKPACK CONDITION
        if (location.getBlock() instanceof BackpackBlock && player.isShiftKeyDown()) {

            //CHECK BLOCK BREAK
            if (handleEventCancellation(event, level, player, location, 0)) {
                event.setUseBlock(Event.Result.DENY);
                event.setUseItem(Event.Result.DENY);
                event.setCanceled(true);
            }

            return;
        }

        //DEPLOYER CONDITION
        if (player instanceof DeployerFakePlayer deployer) {

            BlockEntityRegionProtector regionProtector = getPrioritizedRegionProtector(location, 2);

            if (regionProtector != null && regionProtector.regionType == BlockEntityRegionProtector.RegionType.NONE) {
                event.setUseBlock(Event.Result.DENY);
                event.setUseItem(Event.Result.DENY);
            }

            return;
        }

        //CREATE WRENCH CONDITION
        if (mainHand.getItem() instanceof WrenchItem && player.isCrouching()) {

            //CHECK BLOCK BREAK
            if (handleEventCancellation(event, level, player, location, 0)) {
                event.setUseBlock(Event.Result.DENY);
                event.setUseItem(Event.Result.DENY);
            }

            return;
        }

        //SECURITY WRENCH CONDITION
        if (location.getBlockEntity() != null) {

            if (location.getBlockEntity() instanceof BlockEntityTradingPost) {

                if (!(mainHand.getItem() instanceof ItemSecurityWrench) && !(offHand.getItem() instanceof ItemSecurityWrench)) {
                    return;
                }
            }

            if (location.getBlockEntity() instanceof BlockEntityRentAcceptor) {
                return;
            }
        }

        boolean foundAllow = false;

        for (Direction direction : Direction.values()) {

            if (!handleEventCancellation(event, level, player, new Location(location, direction), 2)) {
                foundAllow = true;
            }
        }

        if (foundAllow) {

            if (!(player.getItemInHand(event.getHand()).getItem() instanceof BlockItem) || !player.isCrouching()) {

                if (handleEventCancellation(event, level, player, location, 2)) {
                    if (!player.getLevel().isClientSide()) player.sendMessage(new TextComponent(ChatFormatting.RED + "Couldn't place! Try crouching."), Util.NIL_UUID);
                    event.setUseBlock(Event.Result.DENY);
                    event.setUseItem(Event.Result.DENY);
                }
            }
        }

        else {

            if (handleEventCancellation(event, level, player, location, 2)) {
                event.setUseBlock(Event.Result.DENY);
                event.setUseItem(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
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

            if (handleEventCancellation(event, level, player, location, 2)) {
                sendErrorMessage(player);
                event.setCanceled(true);
            }
        }

        else if (hand == InteractionHand.MAIN_HAND) checkBucket(event, level, location, player, InteractionHand.OFF_HAND);
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------\\

    //RULE 3: ENTITY HURTING CHECKS

    @SubscribeEvent(priority = EventPriority.HIGHEST)
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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void checkEntityInteract(PlayerInteractEvent.EntityInteract event) {

        Level level = event.getWorld();
        Location location = new Location(level, event.getPos());
        Player player = event.getPlayer();

        if (event.getTarget() instanceof EntityVehicleBase || event.getTarget() instanceof PlaneEntity) {
            return;
        }

        if (location.getBlockEntity() != null) {

        }

        //DEPLOYER CONDITION
        if (player instanceof DeployerFakePlayer deployer) {

            BlockEntityRegionProtector regionProtector = getPrioritizedRegionProtector(location, 4);

            if (regionProtector != null && regionProtector.regionType == BlockEntityRegionProtector.RegionType.NONE) {
                event.setCanceled(true);
            }

            return;
        }

        if (handleEventCancellation(event, level, player, location, 4)) {
            if (event.getHand() == InteractionHand.MAIN_HAND) sendErrorMessage(player);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void checkEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {

        Level level = event.getWorld();
        Location location = new Location(level, event.getPos());
        Player player = event.getPlayer();

        if (event.getTarget() instanceof EntityVehicleBase || event.getTarget() instanceof PlaneEntity) {
            return;
        }

        if (handleEventCancellation(event, level, player, location, 4)) {
            if (event.getHand() == InteractionHand.MAIN_HAND) sendErrorMessage(player);
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------\\

    //RULE 5: PVP CHECKS

    @SubscribeEvent(priority = EventPriority.HIGHEST)
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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void checkProjectileImpact(ProjectileImpactEvent event) {

        Entity projectile = event.getEntity();
        Level level = projectile.getLevel();
        Location location = new Location(level, new BlockPos(event.getRayTraceResult().getLocation()));

        if (handleEventCancellation(event, level, null, location, 5)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void checkSetSpawn(PlayerSetSpawnEvent event) {

        Player player = event.getPlayer();
        Level level = player.getLevel();
        BlockPos spawnPos = event.getNewSpawn();

        if (spawnPos == null) {
            return;
        }

        Location location = new Location(level, spawnPos);

        BlockEntityRegionProtector regionProtector = getPrioritizedRegionProtector(location, 2);

        if (player.isCreative()) {
            return;
        }

        if (regionProtector != null) {

            if (regionProtector.regionType == BlockEntityRegionProtector.RegionType.COMMERCIAL) {

                player.sendMessage(new TextComponent(ChatFormatting.RED + "You cannot set your spawn in a commercial plot!"), Util.NIL_UUID);
                event.setCanceled(true);
            }
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------\\

    private boolean handleEventCancellation(Event event, LevelAccessor level, Player player, Location location, int ruleSetIndex) {

        BlockEntityRegionProtector regionProtector = getPrioritizedRegionProtector(location, ruleSetIndex);

        if (player == null || !player.isCreative()) {

            //LogHelper.log(CCReference.MOD_NAME, "EVENT CALLED");

            if (regionProtector != null) {

                //LogHelper.log(CCReference.MOD_NAME, "FOUND REGION PROTECTOR");

                BlockEntityRentAcceptor rentAcceptor = regionProtector.rentAcceptor;

                if (player != null && rentAcceptor != null) {

                    RegionTeam team = rentAcceptor.getResidentTeam();

                    if (team != null) {

                        //Fix ally
                        if (team.isMember(player) || (team.isAlly(player) && ruleSetIndex == 2)) {

                            if (rentAcceptor.regionRuleSetOverride.ruleSets[ruleSetIndex] != RegionRuleSet.RuleOverrideType.OFF) {

                                //LogHelper.log(CCReference.MOD_NAME, "TEAM FOUND! CHECKING NEW RULES");
                                return rentAcceptor.regionRuleSetOverride.ruleSets[ruleSetIndex] == RegionRuleSet.RuleOverrideType.PREVENT;
                            }
                        }
                    }
                }

                return regionProtector.regionRuleSet.ruleSets[ruleSetIndex] == RegionRuleSet.RuleOverrideType.PREVENT;
            }
        }

        return false;
    }

    private BlockEntityRegionProtector getPrioritizedRegionProtector(Location location, int ruleSetIndex) {

        ArrayList<BlockEntityRegionProtector> regionProtectorsAffectingLocation = new ArrayList<>();

        for (BlockEntityRegionProtector regionProtector : BlockEntityRegionProtector.regionProtectors) {

            if (!regionProtector.isRemoved()) {

                if (regionProtector.getRegion().contains(location.getVector()) || regionProtector.global) {
                    regionProtectorsAffectingLocation.add(regionProtector);
                }
            }
        }

        if (!regionProtectorsAffectingLocation.isEmpty()) {

            BlockEntityRegionProtector regionProtector = null;

            for (BlockEntityRegionProtector regionProtectorAffectingLocation : regionProtectorsAffectingLocation) {

                if (regionProtectorAffectingLocation.regionRuleSet.ruleSets[ruleSetIndex] != RegionRuleSet.RuleOverrideType.OFF) {

                    if (regionProtector == null || regionProtectorAffectingLocation.priority > regionProtector.priority) {
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
