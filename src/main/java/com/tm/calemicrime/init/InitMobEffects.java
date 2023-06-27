package com.tm.calemicrime.init;

import com.tm.calemicrime.effect.DrugHighEffect;
import com.tm.calemicrime.item.drug.IItemDrug;
import com.tm.calemicrime.main.CCReference;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class InitMobEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, CCReference.MOD_ID);

    public static final RegistryObject<MobEffect> TANIUN_HIGH = MOB_EFFECTS
            .register("taniun_high", () -> new DrugHighEffect((IItemDrug)InitItems.CRACKED_TANIUN.get(), "phosphor"));
    public static final RegistryObject<MobEffect> KUSH_HIGH = MOB_EFFECTS
            .register("kush_high", () -> new DrugHighEffect((IItemDrug)InitItems.KUSH.get(),"desaturate"));
    public static final RegistryObject<MobEffect> LSD_HIGH = MOB_EFFECTS
            .register("lsd_high", () -> new DrugHighEffect((IItemDrug)InitItems.LSD.get(),"wobble"));
    public static final RegistryObject<MobEffect> MUSHROOM_HIGH = MOB_EFFECTS
            .register("mushroom_high", () -> new DrugHighEffect((IItemDrug)InitItems.PSILOCYBIN_MUSHROOM_ITEM.get(),"color_convolve"));

    public static final RegistryObject<MobEffect> METH_HIGH = MOB_EFFECTS
            .register("meth_high", () -> new DrugHighEffect((IItemDrug)InitItems.METH.get(), "ntsc"));
    public static final RegistryObject<MobEffect> BIKER_METH_HIGH = MOB_EFFECTS
            .register("biker_meth_high", () -> new DrugHighEffect((IItemDrug)InitItems.P2P_METH.get(), "spider"));
    public static final RegistryObject<MobEffect> KETAMINE_HIGH = MOB_EFFECTS
            .register("ketamine_high", () -> new DrugHighEffect((IItemDrug)InitItems.KETAMINE.get(), "invert"));

    public static final RegistryObject<MobEffect> COCAINE_HIGH = MOB_EFFECTS
            .register("cocaine_high", () -> new DrugHighEffect((IItemDrug)InitItems.COCAINE.get(), "blobs2"));
    public static final RegistryObject<MobEffect> HEROIN_HIGH = MOB_EFFECTS
            .register("heroin_high", () -> new DrugHighEffect((IItemDrug)InitItems.HEROIN.get(), "flip"));
    public static final RegistryObject<MobEffect> BLACK_TAR_HEROIN_HIGH = MOB_EFFECTS
            .register("black_tar_heroin_high", () -> new DrugHighEffect((IItemDrug)InitItems.BLACK_TAR_HEROIN.get(), "sobel"));
}
