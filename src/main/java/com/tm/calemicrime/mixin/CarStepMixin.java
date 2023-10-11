package com.tm.calemicrime.mixin;

import de.maxhenkel.car.entity.car.base.EntityCarLicensePlateBase;
import de.maxhenkel.car.entity.car.base.EntityGenericCar;
import de.maxhenkel.car.entity.car.parts.Part;
import de.maxhenkel.car.entity.car.parts.PartWheelBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityGenericCar.class)
public abstract class CarStepMixin extends EntityCarLicensePlateBase {

    public CarStepMixin(EntityType type, Level worldIn) {
        super(type, worldIn);
    }

    @Shadow
    public abstract <T extends Part> T getPartByClass(Class<T> clazz);

    @Inject(method = "checkInitializing", at = @At("TAIL"), remap = false)
    public void checkInitializing(CallbackInfo info) {

        PartWheelBase partWheels = this.getPartByClass(PartWheelBase.class);
        if (partWheels != null) {
            this.maxUpStep = partWheels.getStepHeight() + 1;
        }
    }
}
