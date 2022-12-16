package com.tm.calemicrime.init;

import com.tm.calemicrime.effect.DrugHighEffect;
import com.tm.calemicrime.main.CCReference;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class InitMobEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, CCReference.MOD_ID);

    public static final RegistryObject<MobEffect> METH_HIGH = MOB_EFFECTS.register("meth_high", () -> new DrugHighEffect("ntsc"));
    public static final RegistryObject<MobEffect> BIKER_METH_HIGH = MOB_EFFECTS.register("biker_meth_high", () -> new DrugHighEffect("spider"));
    public static final RegistryObject<MobEffect> KUSH_HIGH = MOB_EFFECTS.register("kush_high", () -> new DrugHighEffect("desaturate"));
    public static final RegistryObject<MobEffect> HEROIN_HIGH = MOB_EFFECTS.register("heroin_high", () -> new DrugHighEffect("flip"));
    public static final RegistryObject<MobEffect> COCAINE_HIGH = MOB_EFFECTS.register("cocaine_high", () -> new DrugHighEffect("blobs2"));
    public static final RegistryObject<MobEffect> KETAMINE_HIGH = MOB_EFFECTS.register("ketamine_high", () -> new DrugHighEffect("invert"));
    public static final RegistryObject<MobEffect> LSD_HIGH = MOB_EFFECTS.register("lsd_high", () -> new DrugHighEffect("wobble"));
    public static final RegistryObject<MobEffect> MUSHROOM_HIGH = MOB_EFFECTS.register("mushroom_high", () -> new DrugHighEffect("color_convolve"));
}
