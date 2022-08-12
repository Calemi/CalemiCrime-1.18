package com.tm.calemicrime.init;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;

public class InitBlockRenderTypes {

    public static void init() {
        ItemBlockRenderTypes.setRenderLayer(InitItems.SHEET_OF_METH.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(InitItems.SHEET_OF_P2P_METH.get(), RenderType.translucent());

        ItemBlockRenderTypes.setRenderLayer(InitItems.CANNABIS.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(InitItems.COCA.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(InitItems.PSILOCYBIN_MUSHROOM.get(), RenderType.cutout());

        ItemBlockRenderTypes.setRenderLayer(InitItems.ROAD_LINE_1.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(InitItems.ROAD_LINE_2.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(InitItems.ROAD_LINE_3.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(InitItems.ROAD_LINE_4.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(InitItems.ROAD_LINE_5.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(InitItems.ROAD_LINE_6.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(InitItems.ROAD_LINE_7.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(InitItems.ROAD_LINE_8.get(), RenderType.cutout());

        ItemBlockRenderTypes.setRenderLayer(InitFluids.METHYLSULFONYLMETHANE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(InitFluids.METHYLSULFONYLMETHANE.getFlowing(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(InitFluids.METHYLAMINE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(InitFluids.METHYLAMINE.getFlowing(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(InitFluids.ERGOTAMINE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(InitFluids.ERGOTAMINE.getFlowing(), RenderType.translucent());
    }
}
