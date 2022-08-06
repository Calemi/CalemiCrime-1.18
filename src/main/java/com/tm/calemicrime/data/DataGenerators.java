package com.tm.calemicrime.data;

import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicrime.main.CCReference;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = CCReference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {

        LogHelper.log(CCReference.MOD_NAME, "EVENTCALLED");

        //if (event.includeServer()) {

            LogHelper.log(CCReference.MOD_NAME, "SETTING UP DATA GENS");

            DataGenerator datagenerator = event.getGenerator();
            //datagenerator.addProvider(new CCSmelteryRecipesProvider(datagenerator));
        //}
    }
}
