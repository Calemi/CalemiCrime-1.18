package com.tm.calemicrime.event;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicrime.file.PreventBlockPlaceListFile;
import com.tm.calemicrime.main.CCReference;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class FilePreventionEvents {

    @SubscribeEvent
    public void onPreventionLore(ItemTooltipEvent event) {

        Item item = event.getItemStack().getItem();

        if (event.getPlayer() != null && item instanceof BlockItem blockItem) {

            if (!PreventBlockPlaceListFile.canPlaceBlock(event.getPlayer(), blockItem.getBlock())) {
                event.getToolTip().add(new TextComponent(ChatFormatting.RED + "Placing this block is prohibited!"));
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
}
