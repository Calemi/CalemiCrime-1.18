package com.tm.calemicrime.init;

import com.tm.calemicrime.main.CCReference;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import slimeknights.mantle.registration.ModelFluidAttributes;
import slimeknights.mantle.registration.deferred.FluidDeferredRegister;
import slimeknights.mantle.registration.object.FluidObject;

/**
 * Handles setting up the Items for the mod.
 */
public class InitFluids {

    public static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(CCReference.MOD_ID);

    public static final FluidObject<ForgeFlowingFluid> METHYLSULFONYLMETHANE = FLUIDS.register("methylsulfonylmethane", ModelFluidAttributes.builder().sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY).density(1200).viscosity(1200).temperature(336), Material.WATER, 0);
}
