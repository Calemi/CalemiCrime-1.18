package com.tm.calemicrime.datagen;

import com.tm.calemicrime.main.CCReference;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = CCReference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGen {

    @SubscribeEvent
    static void onGatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(new CardModelGen(event.getGenerator(), event.getExistingFileHelper()));
    }
}