package com.tm.calemicrime.mixin;

import de.maxhenkel.car.entity.car.base.EntityCarBase;
import de.maxhenkel.car.entity.car.base.EntityGenericCar;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityGenericCar.class)
public abstract class VehicleStepMixin extends EntityCarBase {

    public VehicleStepMixin(EntityType type, Level worldIn) {
        super(type, worldIn);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo info) {
        this.maxUpStep = 1;
    }
}
