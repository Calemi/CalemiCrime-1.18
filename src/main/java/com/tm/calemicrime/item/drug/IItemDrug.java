package com.tm.calemicrime.item.drug;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public interface IItemDrug {

    int getDuration();
    List<MobEffectInstance> getEffects(int additiveDuration);
    void onExpired(Player player);

    default void onConsumed(Player player, int additiveDuration) {

        for (MobEffectInstance effectInstance : getEffects(additiveDuration)) {
            player.addEffect(effectInstance);
        }
    }
}
