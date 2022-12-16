package com.tm.calemicrime.item.drug;

import com.tm.calemicrime.init.InitMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class ItemHeroin extends ItemDrug {

    @Override
    public int getDuration() {
        return 3 * 60 * 20;
    }

    @Override
    public void onConsumed(Player player, int additiveDuration) {
        player.addEffect(new MobEffectInstance(InitMobEffects.HEROIN_HIGH.get(), getDuration() + additiveDuration));
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,  getDuration() + additiveDuration));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP,  getDuration() + additiveDuration));
    }
}
