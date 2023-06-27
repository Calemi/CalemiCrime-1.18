package com.tm.calemicrime.event;

import com.tm.calemicore.util.Location;
import com.tm.calemicrime.file.PreventBlockPlaceListFile;
import com.tm.calemicrime.file.PreventItemUseListFile;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class FilePreventionEvents {

    @SubscribeEvent
    public void onPreventionLore(ItemTooltipEvent event) {

        Item item = event.getItemStack().getItem();

        if (event.getPlayer() != null) {

            if (item instanceof BlockItem blockItem) {

                if (!PreventBlockPlaceListFile.canPlaceBlock(event.getPlayer(), blockItem.getBlock())) {
                    event.getToolTip().add(new TextComponent(ChatFormatting.RED + "Placing this block is prohibited!"));
                }
            }

            if (!PreventItemUseListFile.canUseItem(event.getPlayer(), item)) {
                event.getToolTip().add(new TextComponent(ChatFormatting.RED + "Using this item is prohibited!"));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {

        Location location = new Location(event.getEntity().getLevel(), event.getPos());

        if (!event.getWorld().isClientSide() && event.getEntity() instanceof Player player) {

            if (!PreventBlockPlaceListFile.canPlaceBlock(player, event.getBlockSnapshot().getCurrentBlock().getBlock())) {
                player.sendMessage(new TextComponent(ChatFormatting.RED + "You cannot place that type of block!"), Util.NIL_UUID);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBlockUse(LivingEntityUseItemEvent event) {

        if (!event.getEntity().getLevel().isClientSide() && event.getEntity() instanceof Player player) {

            if (!PreventItemUseListFile.canUseItem(player, event.getItem().getItem())) {
                player.sendMessage(new TextComponent(ChatFormatting.RED + "You cannot use that type of item!"), Util.NIL_UUID);
                event.setCanceled(true);
            }
        }
    }
}
