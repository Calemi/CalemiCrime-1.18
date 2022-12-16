package com.tm.calemicrime.item.drug;

import com.tm.calemicrime.init.InitMobEffects;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class ItemLSD extends ItemDrug {

    @Override
    public int getDuration() {
        return 60 * 20;
    }

    @Override
    public void onConsumed(Player player, int additiveDuration) {
        player.addEffect(new MobEffectInstance(InitMobEffects.LSD_HIGH.get(), getDuration() + additiveDuration));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,  getDuration() + additiveDuration, 1));
        player.getLevel().playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 1, 1, false);
    }
}
