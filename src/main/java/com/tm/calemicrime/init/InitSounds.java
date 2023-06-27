package com.tm.calemicrime.init;

import com.tm.calemicrime.main.CCReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class InitSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CCReference.MOD_ID);

    public static final RegistryObject<SoundEvent> AIRHORN = SOUNDS.register("airhorn", () -> new SoundEvent(new ResourceLocation("nifty", "airhorn")));

    public static final RegistryObject<SoundEvent> GEIGER_LOW = SOUNDS.register("player.geiger_low", () -> new SoundEvent(new ResourceLocation(CCReference.MOD_ID, "player.geiger_low")));
    public static final RegistryObject<SoundEvent> GEIGER_MEDIUM = SOUNDS.register("player.geiger_medium", () -> new SoundEvent(new ResourceLocation(CCReference.MOD_ID, "player.geiger_medium")));
    public static final RegistryObject<SoundEvent> GEIGER_HIGH = SOUNDS.register("player.geiger_high", () -> new SoundEvent(new ResourceLocation(CCReference.MOD_ID, "player.geiger_high")));
}
