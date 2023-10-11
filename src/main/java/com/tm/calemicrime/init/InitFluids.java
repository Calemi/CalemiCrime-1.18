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

    public static final FluidObject<ForgeFlowingFluid> TANIUN_SOLUTION = FLUIDS.register("taniun_solution", ModelFluidAttributes.builder().sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY).density(1200).viscosity(1200).temperature(336), Material.WATER, 0);
    public static final FluidObject<ForgeFlowingFluid> METHYLSULFONYLMETHANE = FLUIDS.register("methylsulfonylmethane", ModelFluidAttributes.builder().sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY).density(1200).viscosity(1200).temperature(336), Material.WATER, 0);
    public static final FluidObject<ForgeFlowingFluid> METHYLAMINE = FLUIDS.register("methylamine", ModelFluidAttributes.builder().sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY).density(1200).viscosity(1200).temperature(336), Material.WATER, 0);
    public static final FluidObject<ForgeFlowingFluid> ERGOTAMINE = FLUIDS.register("ergotamine", ModelFluidAttributes.builder().sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY).density(1200).viscosity(1200).temperature(336), Material.WATER, 0);
    public static final FluidObject<ForgeFlowingFluid> ACACIA_EXTRACT = FLUIDS.register("acacia_extract", ModelFluidAttributes.builder().sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY).density(1200).viscosity(1200).temperature(336), Material.WATER, 0);
    public static final FluidObject<ForgeFlowingFluid> SWEETENED_ACACIA_EXTRACT = FLUIDS.register("sweetened_acacia_extract", ModelFluidAttributes.builder().sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY).density(1200).viscosity(1200).temperature(336), Material.WATER, 0);
    public static final FluidObject<ForgeFlowingFluid> MORPHINE = FLUIDS.register("morphine", ModelFluidAttributes.builder().sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY).density(1200).viscosity(1200).temperature(336), Material.WATER, 0);
    public static final FluidObject<ForgeFlowingFluid> AMMONIUM_SULFATE_SOLUTION = FLUIDS.register("ammonium_sulfate_solution", ModelFluidAttributes.builder().sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY).density(1200).viscosity(1200).temperature(336), Material.WATER, 0);
    public static final FluidObject<ForgeFlowingFluid> CRYSTAL_BLUE_DYE = FLUIDS.register("crystal_blue_dye", ModelFluidAttributes.builder().sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY).density(1200).viscosity(1200).temperature(336), Material.WATER, 0);
}
