package com.tm.calemicrime.init;

import com.tm.calemieconomy.main.CEReference;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatType;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class InitStats {

    public static final ResourceKey<Registry<ResourceLocation>> STAT_KEY = ResourceKey.createRegistryKey(new ResourceLocation("custom_stat"));

    public static final DeferredRegister<StatType<?>> STAT_TYPES = DeferredRegister.create(ForgeRegistries.STAT_TYPES, CEReference.MOD_ID);
    public static final DeferredRegister<ResourceLocation> STATS = DeferredRegister.create(STAT_KEY, CEReference.MOD_ID);

    public static void init(){}
}