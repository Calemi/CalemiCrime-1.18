package com.tm.calemicrime.team;

import net.minecraft.world.entity.player.Player;

public class RegionTeamMember extends RegionTeamPlayer {

    private boolean isOwner = false;

    public RegionTeamMember(Player player) {
        super(player);
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwnerStatus(boolean value) {
        isOwner = value;
    }
}