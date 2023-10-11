package com.tm.calemicrime.event;

import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicrime.accessor.VehicleAccessor;
import com.tm.calemicrime.main.CCReference;
import com.tm.calemicrime.team.RegionTeam;
import com.tm.calemicrime.util.RegionTeamHelper;
import de.maxhenkel.car.entity.car.base.EntityCarBase;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xyz.przemyk.simpleplanes.entities.PlaneEntity;
import xyz.przemyk.simpleplanes.items.PlaneItem;

import java.util.UUID;

public class VehicleEvents {

    @SubscribeEvent
    public void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {

        Player player = event.player;

        if (player.getLevel().isClientSide() || player.isCreative() || player.isSpectator()) {
            return;
        }

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {

            ItemStack stack = player.getInventory().getItem(i);
            Item item = stack.getItem();

            if (item instanceof PlaneItem) {

                CompoundTag tag = stack.getTagElement("EntityTag");

                if (tag.hasUUID("owner")) {

                    UUID ownerID = tag.getUUID("owner");
                    Player owner = player.getLevel().getPlayerByUUID(ownerID);

                    if (owner == null) {
                        player.sendMessage(new TextComponent(ChatFormatting.RED + "Dropping vehicle item! You do not own this vehicle or are a member/ally of the owner's team!"), Util.NIL_UUID);
                        player.drop(stack, false);
                        player.getInventory().setItem(i, ItemStack.EMPTY);
                        return;
                    }

                    RegionTeam team = RegionTeamHelper.getTeam(owner);

                    if (team == null || (!team.isMember(player) && !team.isAlly(player))) {
                        player.sendMessage(new TextComponent(ChatFormatting.RED + "Dropping vehicle item! You do not own this vehicle or are a member/ally of the owner's team!"), Util.NIL_UUID);
                        player.drop(stack, false);
                        player.getInventory().setItem(i, ItemStack.EMPTY);
                        return;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onTooltipEvent(ItemTooltipEvent event) {

        Item item = event.getItemStack().getItem();

        if (item instanceof PlaneItem) {

            CompoundTag tag = event.getItemStack().getTagElement("EntityTag");

            String ownerName = "Not set";

            if (tag.hasUUID("owner")) {

                UUID ownerID = tag.getUUID("owner");
                Player owner = event.getPlayer().getLevel().getPlayerByUUID(ownerID);

                if (owner != null) {
                    ownerName = owner.getDisplayName().getString();
                }
            }

            event.getToolTip().add(new TextComponent(ChatFormatting.GRAY + "Owner: " + ChatFormatting.GOLD + ownerName));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onAttackEntityEvent(AttackEntityEvent event) {
        handleInteract(event, event.getPlayer(), event.getTarget(), InteractionHand.MAIN_HAND);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {

        if (event.getHand() == InteractionHand.OFF_HAND) {
            event.setCanceled(true);
            return;
        }

        handleInteract(event, event.getPlayer(), event.getTarget(), InteractionHand.MAIN_HAND);
    }

    public static void handleInteract(Event event, Entity source, Entity target, InteractionHand hand) {

        if (source == null || target == null) {
            return;
        }

        if (source.getLevel().isClientSide()) {
            event.setCanceled(true);
            return;
        }

        if (source instanceof Player player) {

            Level level = player.getLevel();

            if (target instanceof PlaneEntity || target instanceof EntityCarBase) {

                if (target instanceof VehicleAccessor mixin) {

                    if (mixin.getOwnerID() == null) {
                        mixin.setOwnerID(player.getUUID());
                        player.sendMessage(new TextComponent(ChatFormatting.GREEN + "You now own this vehicle. You, your teammates, and your allies are the only ones who can ride in it."), Util.NIL_UUID);
                        event.setCanceled(true);
                        return;
                    }

                    if (player.isCreative()) {
                        return;
                    }

                    if (!player.getUUID().equals(mixin.getOwnerID())) {

                        RegionTeam team = RegionTeamHelper.getTeamByPlayerID(mixin.getOwnerID());

                        LogHelper.log(CCReference.MOD_NAME, team);

                        if (team == null || (!team.isMember(player) && !team.isAlly(player))) {
                            player.sendMessage(new TextComponent(ChatFormatting.RED + "You do not own this vehicle or are a member/ally of the owner's team!"), Util.NIL_UUID);
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }
}
