package com.tm.calemicrime.mixin;

import com.tm.calemicrime.accessor.CorpseAccessor;
import de.maxhenkel.corpse.entities.CorpseEntity;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CorpseEntity.class)
public abstract class CorpseNBTMixin implements CorpseAccessor {

    @Shadow private int age;

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public void setAge(int amount) {
        age = amount;
    }
}
