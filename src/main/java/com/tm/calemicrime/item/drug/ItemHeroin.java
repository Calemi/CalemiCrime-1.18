package com.tm.calemicrime.item.drug;

import com.tm.calemicrime.init.InitMobEffects;
import com.tm.calemicrime.main.CCConfig;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class ItemHeroin extends ItemDrug {

    @Override
    public int getDuration() {
        return CCConfig.drugs.heroinEffectDuration.get() * 20;
    }

    @Override
    public List<MobEffectInstance> getEffects(int additiveDuration) {
        List<MobEffectInstance> effects = new ArrayList<>();
        effects.add(new MobEffectInstance(InitMobEffects.HEROIN_HIGH.get(), getDuration() + additiveDuration));
        effects.add(new MobEffectInstance(MobEffects.SLOW_FALLING,  getDuration() + additiveDuration));
        effects.add(new MobEffectInstance(MobEffects.JUMP,  getDuration() + additiveDuration));
        return effects;
    }

    @Override
    public void onExpired(Player player) {}
}
