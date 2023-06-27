package com.tm.calemicrime.init;

import com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours;
import com.simibubi.create.content.redstone.displayLink.DisplayBehaviour;
import com.tm.calemicrime.displaylink.TradingPostDisplaySource;
import com.tm.calemicrime.main.CCReference;
import com.tm.calemieconomy.init.InitBlockEntityTypes;
import net.minecraft.resources.ResourceLocation;

public class InitDisplayLinkBehaviours {

    public static void init() {

        ResourceLocation tradingPostResourceLocation = new ResourceLocation(CCReference.MOD_ID, "trading_post_display_source");
        DisplayBehaviour tradingPostBehaviour = AllDisplayBehaviours.register(tradingPostResourceLocation, new TradingPostDisplaySource());
        AllDisplayBehaviours.assignBlockEntity(tradingPostBehaviour, InitBlockEntityTypes.TRADING_POST.get());
    }
}
