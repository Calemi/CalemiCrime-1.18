package com.tm.calemicrime.item.drug;

import com.tm.calemicrime.init.InitMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class ItemCocaine extends ItemDrug {

    @Override
    public int getDuration() {
        return 60 * 20;
    }

    @Override
    public void onConsumed(Player player, int additiveDuration) {
        player.addEffect(new MobEffectInstance(InitMobEffects.COCAINE_HIGH.get(), getDuration() + additiveDuration));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP,  getDuration() + additiveDuration, 1));
    }
}
