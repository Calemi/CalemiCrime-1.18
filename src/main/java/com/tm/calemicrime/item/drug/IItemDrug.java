package com.tm.calemicrime.item.drug;

import net.minecraft.world.entity.player.Player;

public interface IItemDrug {

    int getDuration();
    void onConsumed(Player player, int additiveDuration);
    void onExpired(Player player);
}
