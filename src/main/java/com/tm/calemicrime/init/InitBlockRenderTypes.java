package com.tm.calemicrime.init;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;

public class InitBlockRenderTypes {

    public static void init() {
        ItemBlockRenderTypes.setRenderLayer(InitItems.SHEET_OF_METH.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(InitItems.SHEET_OF_P2P_METH.get(), RenderType.translucent());
    }
}
