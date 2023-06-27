package com.tm.calemicrime.event;

import com.shmeggels.niftyblocks.init.ItemInit;
import com.tm.calemicrime.init.InitSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class NiftyEvents {

    @SubscribeEvent
    public void onBlockExploded(LivingEntityUseItemEvent.Start event) {

        if (event.getEntityLiving() instanceof Player player) {

            if (event.getItem().getItem() == ItemInit.AIRHORN.get()) {
                player.getLevel().playSound(player, player, InitSounds.AIRHORN.get(), SoundSource.PLAYERS, 2, 1);
            }
        }
    }
}
