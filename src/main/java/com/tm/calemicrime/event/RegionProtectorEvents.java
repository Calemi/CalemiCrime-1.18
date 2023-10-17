package com.tm.calemicrime.event;

import com.mrcrayfish.guns.entity.ProjectileEntity;
import com.mrcrayfish.guns.event.GunFireEvent;
import com.mrcrayfish.guns.event.GunProjectileHitEvent;
import com.simibubi.create.content.equipment.wrench.WrenchItem;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicrime.block.BlockBushPlant;
import com.tm.calemicrime.blockentity.BlockEntityRentAcceptor;
import com.tm.calemicrime.file.PlotBlockPlaceLimitFile;
import com.tm.calemicrime.main.CCReference;
import com.tm.calemicrime.util.NotifyHelper;
import com.tm.calemicrime.util.RegionHelper;
import com.tm.calemicrime.util.RegionProfile;
import com.tm.calemicrime.util.RegionRuleSet;
import com.tm.calemieconomy.block.BlockTradingPost;
import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import com.tm.calemieconomy.item.ItemSecurityWrench;
import de.maxhenkel.car.entity.car.base.EntityCarBase;
import de.maxhenkel.corpse.entities.CorpseEntity;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
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

public class RegionProtectorEvents {

    //-----------------------------------------------------------------------------------------------------------------------------------------------\\

    //RULE 0: BLOCK BREAKING CHECKS

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void checkBlockBreak(BlockEvent.BreakEvent event) {

        Level level = (Level) event.getWorld();
        Location location = new Location(level, event.getPos());
        Player player = event.getPlayer();

        if (RegionHelper.handleEventCancellation(event, level, player, location, 0)) {
            sendErrorMessage(player);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void checkBlockAttack(PlayerInteractEvent.LeftClickBlock event) {

        Level level = event.getWorld();
        Location location = new Location(level, event.getPos());
        Player player = event.getPlayer();

        if (RegionHelper.handleEventCancellation(event, level, player, location, 0)) {
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

            RegionProfile profile = RegionHelper.getPrioritizedRegionProfile(location, 1);
            Block blockBeingPlaced = event.getPlacedBlock().getBlock();

            if (player.isCreative()) {
                return;
            }

            if (profile != null) {

                //DEPLOYER CONDITION
                if (player instanceof DeployerFakePlayer deployer) {

                    if (profile.getType() == RegionProfile.Type.NONE) {
                        event.setCanceled(true);
                    }

                    return;
                }

                if (profile.getType() == RegionProfile.Type.RESIDENTIAL) {

                    if (blockBeingPlaced instanceof BlockTradingPost) {
                        player.sendMessage(new TextComponent(ChatFormatting.RED + "You cannot place a Trading Post in a residential plot!"), Util.NIL_UUID);
                        event.setCanceled(true);
                        return;
                    }
                }

                //COUNT BLOCK LIMIT
                PlotBlockPlaceLimitFile.BlockPlaceLimitEntry limitEntry = PlotBlockPlaceLimitFile.getEntry(blockBeingPlaced);

                if (limitEntry != null) {

                    AABB region = profile.getRegion();
                    int blockLimit = limitEntry.getLimit();
                    int blockCount = 0;

                   for (int x = 0; x < region.getXsize(); x++) {
                        for (int y = 0; y < region.getYsize(); y++) {
                            for (int z = 0; z < region.getZsize(); z++) {

                                int xRelative = (int) region.minX + x;
                                int yRelative = (int) region.minY + y;
                                int zRelative = (int) region.minZ + z;

                                BlockState blockStateInPlot = level.getBlockState(new BlockPos(xRelative, yRelative, zRelative));
                                Block blockInPlot = blockStateInPlot.getBlock();

                                if (blockInPlot == blockBeingPlaced) {

                                    if (blockInPlot instanceof BlockBushPlant) {

                                        if (!blockStateInPlot.getValue(BlockBushPlant.ROOT)) {
                                            continue;
                                        }
                                    }

                                    blockCount++;
                                }

                                if (blockCount > blockLimit) {
                                    sendErrorMessage(player, "You've reached the limit for that block in your plot!");
                                    event.setCanceled(true);
                                    return;
                                }
                            }
                        }
                    }
                }
            }

            if (RegionHelper.handleEventCancellation(event, level, player, location, 1)) {
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

        LogHelper.log(CCReference.MOD_NAME, location.getBlock());

        //BACKPACK CONDITION
        if (location.getBlock() instanceof BackpackBlock && player.isShiftKeyDown()) {

            //CHECK BLOCK BREAK
            if (RegionHelper.handleEventCancellation(event, level, player, location, 0)) {
                event.setUseBlock(Event.Result.DENY);
                event.setUseItem(Event.Result.DENY);
                event.setCanceled(true);
            }

            return;
        }

        //DEPLOYER CONDITION
        if (player instanceof DeployerFakePlayer deployer) {

            RegionProfile profile = RegionHelper.getPrioritizedRegionProfile(location, 2);

            if (profile != null && profile.getType() == RegionProfile.Type.NONE) {
                event.setUseBlock(Event.Result.DENY);
                event.setUseItem(Event.Result.DENY);
                event.setCanceled(true);
            }

            return;
        }

        //CREATE WRENCH CONDITION
        if (mainHand.getItem() instanceof WrenchItem && player.isCrouching()) {

            //CHECK BLOCK BREAK
            if (RegionHelper.handleEventCancellation(event, level, player, location, 0)) {
                event.setUseBlock(Event.Result.DENY);
                event.setUseItem(Event.Result.DENY);
                event.setCanceled(true);
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

            if (!RegionHelper.handleEventCancellation(event, level, player, new Location(location, direction), 2)) {
                foundAllow = true;
            }
        }

        if (foundAllow) {

            if (!(player.getItemInHand(event.getHand()).getItem() instanceof BlockItem) || !player.isCrouching()) {

                if (RegionHelper.handleEventCancellation(event, level, player, location, 2)) {
                    if (!player.getLevel().isClientSide()) player.sendMessage(new TextComponent(ChatFormatting.RED + "Couldn't place! Try crouching."), Util.NIL_UUID);
                    event.setUseBlock(Event.Result.DENY);
                    event.setUseItem(Event.Result.DENY);
                    event.setCanceled(true);
                }
            }
        }

        else {

            if (RegionHelper.handleEventCancellation(event, level, player, location, 2)) {
                event.setUseBlock(Event.Result.DENY);
                event.setUseItem(Event.Result.DENY);
                event.setCanceled(true);
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

            if (RegionHelper.handleEventCancellation(event, level, player, location, 2)) {
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

        if (event.getTarget() instanceof EntityCarBase || event.getTarget() instanceof PlaneEntity) {
            return;
        }

        if (damager instanceof Player player && !(target instanceof Player)) {

            if (RegionHelper.handleEventCancellation(event, level, player, location, 3)) {
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

        if (event.getTarget() instanceof EntityCarBase || event.getTarget() instanceof PlaneEntity || event.getTarget() instanceof CorpseEntity) {
            return;
        }

        //DEPLOYER CONDITION
        if (player instanceof DeployerFakePlayer deployer) {

            RegionProfile profile = RegionHelper.getPrioritizedRegionProfile(location, 4);

            if (profile != null && profile.getType() == RegionProfile.Type.NONE) {
                event.setCanceled(true);
            }

            return;
        }

        if (RegionHelper.handleEventCancellation(event, level, player, location, 4)) {
            if (event.getHand() == InteractionHand.MAIN_HAND) sendErrorMessage(player);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void checkEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {

        Level level = event.getWorld();
        Location location = new Location(level, event.getPos());
        Player player = event.getPlayer();

        if (event.getTarget() instanceof EntityCarBase || event.getTarget() instanceof PlaneEntity) {
            return;
        }

        if (RegionHelper.handleEventCancellation(event, level, player, location, 4)) {
            if (event.getHand() == InteractionHand.MAIN_HAND) sendErrorMessage(player);
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------\\

    //RULE 5: PVP CHECKS

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void checkPVP(AttackEntityEvent event) {

        Entity targetEntity = event.getTarget();
        Entity sourceEntity = event.getEntity();

        if (!(targetEntity instanceof Player) || !(sourceEntity instanceof Player sourcePlayer)) {
            return;
        }

        Location targetLocation = new Location(targetEntity);
        Location sourceLocation = new Location(sourceEntity);

        RegionProfile targetProfile = RegionHelper.getPrioritizedRegionProfile(targetLocation, 5);
        RegionProfile sourceProfile = RegionHelper.getPrioritizedRegionProfile(sourceLocation, 5);

        if (targetProfile != null && targetProfile.getRuleSet().ruleSets[5] == RegionRuleSet.RuleOverrideType.PREVENT) {
            sendErrorMessage(sourcePlayer);
            event.setCanceled(true);
            return;
        }

        if (sourceProfile != null && sourceProfile.getRuleSet().ruleSets[5] == RegionRuleSet.RuleOverrideType.PREVENT) {
            sendErrorMessage(sourcePlayer);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void checkProjectileImpact(ProjectileImpactEvent event) {

        Entity projectile = event.getEntity();
        Level level = projectile.getLevel();
        Location location = new Location(level, new BlockPos(event.getRayTraceResult().getLocation()));

        if (RegionHelper.handleEventCancellation(event, level, null, location, 5)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void checkBulletHit(GunProjectileHitEvent event) {

        ProjectileEntity projectile = event.getProjectile();
        Level level = projectile.getLevel();
        Location location = new Location(level, new BlockPos(event.getRayTrace().getLocation()));

        if (RegionHelper.handleEventCancellation(event, level, null, location, 5)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void checkBulletHit(GunFireEvent event) {

        Player shooter = event.getPlayer();
        Level level = shooter.getLevel();
        Location shooterLocation = new Location(shooter);

        if (RegionHelper.handleEventCancellation(event, level, null, shooterLocation, 5)) {
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

        RegionProfile profile = RegionHelper.getPrioritizedRegionProfile(location, 2);

        if (player.isCreative()) {
            return;
        }

        if (profile != null) {

            if (profile.getType() == RegionProfile.Type.COMMERCIAL) {

                player.sendMessage(new TextComponent(ChatFormatting.RED + "You cannot set your spawn in a commercial plot!"), Util.NIL_UUID);
                event.setCanceled(true);
            }
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------\\

    private void sendErrorMessage(Player player, String message) {
        NotifyHelper.errorHotbar(player, message);
    }

    private void sendErrorMessage(Player player) {
        NotifyHelper.errorHotbar(player, "You cannot do that in this area!");
    }
}
