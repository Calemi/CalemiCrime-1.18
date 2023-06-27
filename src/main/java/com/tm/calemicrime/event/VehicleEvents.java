package com.tm.calemicrime.event;

import com.tm.calemicrime.accessor.PlaneDataSaver;
import dev.ftb.mods.ftbteams.data.Team;
import dev.ftb.mods.ftbteams.data.TeamManager;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xyz.przemyk.simpleplanes.entities.PlaneEntity;
import xyz.przemyk.simpleplanes.items.PlaneItem;

import java.util.UUID;

public class VehicleEvents {

    @SubscribeEvent
    public void onVehicleMount(ItemTooltipEvent event) {

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

    @SubscribeEvent
    public void onVehicleUse(PlayerInteractEvent.EntityInteract event) {

        if (event.getEntityLiving() == null || event.getTarget() == null || event.getHand() == InteractionHand.OFF_HAND) {
            return;
        }

        if (event.getEntityLiving() instanceof Player player) {

            Level level = player.getLevel();

            if (event.getTarget() instanceof PlaneEntity plane) {

                if (plane instanceof PlaneDataSaver mixin) {

                    if (mixin.getOwnerID() == null) {
                        mixin.setOwnerID(player.getUUID());
                        if (!level.isClientSide()) player.sendMessage(new TextComponent(ChatFormatting.GREEN + "The aircraft's owner has been set to you. You, your teammates, and your allies are the only ones that can ride in it."), Util.NIL_UUID);
                        event.setCanceled(true);
                        return;
                    }

                    if (player.isCreative()) {
                        return;
                    }

                    Player owner = level.getPlayerByUUID(mixin.getOwnerID());

                    if (owner == null) {
                        event.setCanceled(true);
                        return;
                    }

                    if (!player.getUUID().equals(mixin.getOwnerID())) {

                        TeamManager manager = TeamManager.INSTANCE;
                        Team team = manager.getPlayerTeam(mixin.getOwnerID());

                        if (team == null || (!team.isMember(player.getUUID()) && !team.isAlly(player.getUUID()))) {
                            if (!level.isClientSide()) player.sendMessage(new TextComponent(ChatFormatting.RED + "You do not own this aircraft or are a part of the owner's team/ally!"), Util.NIL_UUID);
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }
}
