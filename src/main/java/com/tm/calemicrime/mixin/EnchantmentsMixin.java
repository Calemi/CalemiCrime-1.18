package com.tm.calemicrime.mixin;

import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicrime.main.CCReference;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProtectionEnchantment.class)
public abstract class EnchantmentsMixin {

    /**
     * @author Calemi
     * @reason To stop pushing of Cars.
     */
    @Inject(method = "getDamageProtection", at = @At("HEAD"), cancellable = true)
    public void getDamageProtection(int level, DamageSource source, CallbackInfoReturnable<Integer> cir) {

        if (source.isProjectile()) {
            LogHelper.log(CCReference.MOD_NAME, "OVERRIDE");
            cir.setReturnValue(0);
        }
    }
}
