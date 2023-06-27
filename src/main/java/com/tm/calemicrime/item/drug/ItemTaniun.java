package com.tm.calemicrime.item.drug;

import com.tm.calemicrime.init.InitMobEffects;
import com.tm.calemicrime.main.CCConfig;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class ItemTaniun extends ItemDrug {

    @Override
    public int getDuration() {
        return CCConfig.drugs.taniunEffectDuration.get() * 20;
    }

    @Override
    public void onConsumed(Player player, int additiveDuration) {
        player.addEffect(new MobEffectInstance(InitMobEffects.TANIUN_HIGH.get(), getDuration() + additiveDuration));
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED,  getDuration() + additiveDuration, 2));
        player.addEffect(new MobEffectInstance(MobEffects.SATURATION,  getDuration() + additiveDuration, 2));
    }

    @Override
    public void onExpired(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, CCConfig.drugs.drugWithdrawEffectDuration.get() * 20));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, CCConfig.drugs.drugWithdrawEffectDuration.get() * 20, 2));
    }
}
