package com.tm.calemicrime.item.drug;

import com.tm.calemicrime.init.InitMobEffects;
import com.tm.calemicrime.main.CCConfig;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class ItemTaniun extends ItemDrug {

    @Override
    public int getDuration() {
        return CCConfig.drugs.taniunEffectDuration.get() * 20;
    }

    @Override
    public List<MobEffectInstance> getEffects(int additiveDuration) {
        List<MobEffectInstance> effects = new ArrayList<>();
        effects.add(new MobEffectInstance(InitMobEffects.TANIUN_HIGH.get(), getDuration() + additiveDuration));
        effects.add(new MobEffectInstance(MobEffects.DIG_SPEED,  getDuration() + additiveDuration, 2));
        effects.add(new MobEffectInstance(MobEffects.SATURATION,  getDuration() + additiveDuration, 2));
        return effects;
    }

    @Override
    public void onExpired(Player player) {}
}
