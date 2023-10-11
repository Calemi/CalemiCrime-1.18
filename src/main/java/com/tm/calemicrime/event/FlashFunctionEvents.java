package com.tm.calemicrime.event;

import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicrime.flashnpc.MoveOnFTBQuestFunction;
import com.tm.calemicrime.main.CCReference;
import flash.npcmod.core.functions.FunctionUtil;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class FlashFunctionEvents {

    private static final MoveOnFTBQuestFunction MOVE_ON_FTB_QUEST = new MoveOnFTBQuestFunction();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onWorldLoad(WorldEvent.Load event) {

        if (event.getWorld().isClientSide()) {
            return;
        }

        FunctionUtil.FUNCTIONS.add(MOVE_ON_FTB_QUEST);
    }
}
