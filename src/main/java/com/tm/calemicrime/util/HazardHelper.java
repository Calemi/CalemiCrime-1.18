package com.tm.calemicrime.util;

import cofh.thermal.core.item.HazmatArmorItem;
import com.tm.calemicore.util.helper.SoundHelper;
import com.tm.calemicrime.init.InitSounds;
import com.tm.calemicrime.main.CalemiCrime;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HazardHelper {

    public static float getHazardProtection(Player player) {

        float protection = 0;

        for (int i = 0; i < 4; i++) {

            if (player.getInventory().getArmor(i).getItem() instanceof HazmatArmorItem) {
                protection += 0.25F;
            }
        }

        return protection;
    }

    public static void attemptRadiationDamage(Player player, float radiationStrength) {

        float protection = getHazardProtection(player);

        if (protection < 1) {
            player.hurt(CalemiCrime.RADIATION, radiationStrength - (radiationStrength * protection));
        }

        for (int i = 0; i < 4; i++) {

            int rand = player.getLevel().getRandom().nextInt(100);

            if (rand <= radiationStrength * 10) {

                ItemStack stack = player.getInventory().getArmor(i);

                if (stack.getItem() instanceof HazmatArmorItem) {

                    int armorIndex = i;
                    stack.hurtAndBreak(1, player, (livingEntity) -> {
                        livingEntity.broadcastBreakEvent(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, armorIndex));
                    });
                }
            }
        }
    }

    public static void playGeigerSound(Player player, float radiationStrength) {

        SoundEvent sound = InitSounds.GEIGER_LOW.get();

        if (radiationStrength > 1.5F) {
            sound = InitSounds.GEIGER_MEDIUM.get();
        }

        if (radiationStrength > 2.5F) {
            sound = InitSounds.GEIGER_HIGH.get();
        }

        SoundHelper.playAtPlayer(player, sound, 0.5F, 1);
    }
}
