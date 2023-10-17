package com.tm.calemicrime.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MineGeneratorEvents {

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent event) {

        if (event.getSource() == DamageSource.IN_WALL) {
            event.setCanceled(true);
        }
    }
}
