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
        ItemBlockRenderTypes.setRenderLayer(InitItems.ROAD_LINE_1_SLOPE.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(InitItems.ROAD_LINE_1_SLOPE_MIRROR.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(InitItems.ROAD_LINE_2.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(InitItems.ROAD_LINE_2_SLOPE.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(InitItems.ROAD_LINE_2_SLOPE_MIRROR.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(InitItems.ROAD_LINE_3.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(InitItems.ROAD_LINE_4.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(InitItems.ROAD_LINE_5.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(InitItems.ROAD_LINE_6.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(InitItems.ROAD_LINE_7.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(InitItems.ROAD_LINE_8.get(), RenderType.cutout());

        ItemBlockRenderTypes.setRenderLayer(InitItems.BULLET_HOLE.get(), RenderType.translucent());

        ItemBlockRenderTypes.setRenderLayer(InitFluids.TANIUN_SOLUTION.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(InitFluids.TANIUN_SOLUTION.getFlowing(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(InitFluids.ACACIA_EXTRACT.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(InitFluids.ACACIA_EXTRACT.getFlowing(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(InitFluids.SWEETENED_ACACIA_EXTRACT.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(InitFluids.SWEETENED_ACACIA_EXTRACT.getFlowing(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(InitFluids.METHYLSULFONYLMETHANE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(InitFluids.METHYLSULFONYLMETHANE.getFlowing(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(InitFluids.METHYLAMINE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(InitFluids.METHYLAMINE.getFlowing(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(InitFluids.ERGOTAMINE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(InitFluids.ERGOTAMINE.getFlowing(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(InitFluids.MORPHINE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(InitFluids.MORPHINE.getFlowing(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(InitFluids.AMMONIUM_SULFATE_SOLUTION.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(InitFluids.AMMONIUM_SULFATE_SOLUTION.getFlowing(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(InitFluids.CRYSTAL_BLUE_DYE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(InitFluids.CRYSTAL_BLUE_DYE.getFlowing(), RenderType.translucent());
    }
}
