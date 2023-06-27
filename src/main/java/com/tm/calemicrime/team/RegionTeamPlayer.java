package com.tm.calemicrime.team;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class RegionTeamPlayer {

    private final UUID id;
    private String name;

    public RegionTeamPlayer(Player player) {
        this.id = player.getUUID();
        this.name = player.getName().getString();
    }

    public void refreshName(Level level) {

        Player player = level.getPlayerByUUID(getID());

        if (player != null) {
            name = player.getName().getString();
        }
    }

    public UUID getID() {
        return id;
    }

    public String getName() {
        return name;
    }
}
