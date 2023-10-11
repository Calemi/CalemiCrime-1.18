package com.tm.calemicrime.util;

import com.tm.calemicore.util.helper.SoundHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;

public class NotifyHelper {

    public static void notifyHotbar(Player player, ChatFormatting format, String text) {

        if (player instanceof ServerPlayer serverPlayer) {

            ClientboundSetActionBarTextPacket packet = new ClientboundSetActionBarTextPacket(new TextComponent(format + text));
            serverPlayer.connection.send(packet);
        }
    }

    public static void errorHotbar(Player player, String text) {
        errorHotbar(player, text, true);
    }

    public static void errorHotbar(Player player, String text, boolean sound) {
        notifyHotbar(player, ChatFormatting.RED, text);
        if (sound) SoundHelper.playAtPlayer(player, SoundEvents.ITEM_BREAK, 1, 0.5F);
    }

    public static void warnHotbar(Player player, String text) {
        notifyHotbar(player, ChatFormatting.GOLD, text);
        SoundHelper.playAtPlayer(player, SoundEvents.EXPERIENCE_ORB_PICKUP, 1, 0.5F);
    }

    public static void warnChat(Player player, String text) {
        if (!player.getLevel().isClientSide()) player.sendMessage(new TextComponent(ChatFormatting.GOLD + text), Util.NIL_UUID);
        SoundHelper.playAtPlayer(player, SoundEvents.EXPERIENCE_ORB_PICKUP, 1, 0.5F);
    }


    public static void successHotbar(Player player, String text) {
        notifyHotbar(player, ChatFormatting.GREEN, text);
        SoundHelper.playSimple(player, SoundEvents.EXPERIENCE_ORB_PICKUP);
    }
}
