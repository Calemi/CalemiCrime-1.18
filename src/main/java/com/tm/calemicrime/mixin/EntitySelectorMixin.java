package com.tm.calemicrime.mixin;

import de.maxhenkel.car.entity.car.base.EntityVehicleBase;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Predicate;

@Mixin(EntitySelector.class)
public class EntitySelectorMixin {

    @Final
    @Shadow
    public static final Predicate<Entity> CONTAINER_ENTITY_SELECTOR = (p_20438_) -> {
        return p_20438_ instanceof Container && p_20438_.isAlive() && !(p_20438_ instanceof EntityVehicleBase);
    };
}
