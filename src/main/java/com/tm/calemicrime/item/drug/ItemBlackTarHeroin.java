package com.tm.calemicrime.item.drug;

import com.tm.calemicrime.init.InitMobEffects;
import com.tm.calemicrime.main.CCConfig;
import com.tm.calemicrime.main.CalemiCrime;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class ItemBlackTarHeroin extends ItemDrug {

    @Override
    public int getDuration() {
        return CCConfig.drugs.blackTarHeroinEffectDuration.get() * 20;
    }

    @Override
    public List<MobEffectInstance> getEffects(int additiveDuration) {
        List<MobEffectInstance> effects = new ArrayList<>();
        effects.add(new MobEffectInstance(InitMobEffects.BLACK_TAR_HEROIN_HIGH.get(), getDuration() + additiveDuration));
        effects.add(new MobEffectInstance(MobEffects.DAMAGE_BOOST,  getDuration() + additiveDuration, 1));
        effects.add(new MobEffectInstance(MobEffects.HEALTH_BOOST,  getDuration() + additiveDuration, 1));
        return effects;
    }

    @Override
    public void onExpired(Player player) {
        player.hurt(CalemiCrime.BLACK_TAR_EXPIRE, Integer.MAX_VALUE);
    }
}
