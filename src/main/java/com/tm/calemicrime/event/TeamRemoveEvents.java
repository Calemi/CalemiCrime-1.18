package com.tm.calemicrime.event;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ftb.mods.ftbteams.data.PartyTeam;
import dev.ftb.mods.ftbteams.data.Team;
import dev.ftb.mods.ftbteams.data.TeamManager;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

public class TeamRemoveEvents {

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {

        if (event.world.isClientSide()) {
            return;
        }

        if (event.world.getGameTime() % 20 == 0) {

            TeamManager manager = TeamManager.INSTANCE;

            for (Team team : manager.getTeams()) {

                if (team instanceof PartyTeam partyTeam) {

                    for (UUID id : team.getMembers()) {

                        Player player = event.world.getPlayerByUUID(id);

                        if (player != null) {
                            player.sendMessage(new TextComponent(ChatFormatting.RED + "FTBTeams are disabled!"), Util.NIL_UUID);
                            player.sendMessage(new TextComponent(ChatFormatting.RED + "Use: /crime team create <teamname>"),  Util.NIL_UUID);

                            try {
                                partyTeam.leave((ServerPlayer) player);
                            }

                            catch (CommandSyntaxException e) {
                                e.printStackTrace();
                            }

                            break;
                        }
                    }
                }
            }
        }
    }
}
