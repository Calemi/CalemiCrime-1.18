package com.tm.calemicrime.event;

import com.mrcrayfish.guns.client.render.gun.ModelOverrides;
import com.mrcrayfish.guns.client.render.gun.model.SimpleModel;
import com.tm.calemicrime.client.GunModels;
import com.tm.calemicrime.client.render.RenderDryingRack;
import com.tm.calemicrime.client.render.RenderMineGenerator;
import com.tm.calemicrime.client.render.RenderRadiationProjector;
import com.tm.calemicrime.client.render.RenderRegionProtector;
import com.tm.calemicrime.client.screen.ScreenMineGenerator;
import com.tm.calemicrime.client.screen.ScreenRentAcceptor;
import com.tm.calemicrime.init.InitBlockEntityTypes;
import com.tm.calemicrime.init.InitBlockRenderTypes;
import com.tm.calemicrime.init.InitItems;
import com.tm.calemicrime.init.InitMenuTypes;
import com.tm.calemicrime.main.CCReference;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetupEvent {

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onClientSetup(final FMLClientSetupEvent event) {

        MenuScreens.register(InitMenuTypes.RENT_ACCEPTOR.get(), ScreenRentAcceptor::new);
        MenuScreens.register(InitMenuTypes.MINE_GENERATOR.get(), ScreenMineGenerator::new);

        InitBlockRenderTypes.init();

        BlockEntityRenderers.register(InitBlockEntityTypes.REGION_PROTECTOR.get(), RenderRegionProtector::new);
        BlockEntityRenderers.register(InitBlockEntityTypes.MINE_GENERATOR.get(), RenderMineGenerator::new);
        BlockEntityRenderers.register(InitBlockEntityTypes.RADIATION_PROJECTOR.get(), RenderRadiationProjector::new);
        BlockEntityRenderers.register(InitBlockEntityTypes.DRYING_RACK.get(), RenderDryingRack::new);

        ModelOverrides.register(InitItems.DONOR_PISTOL.get(), new SimpleModel(GunModels.DONOR_PISTOL::getModel));
        ModelOverrides.register(InitItems.R99.get(), new SimpleModel(GunModels.R99::getModel));

        ItemProperties.register(InitItems.CAPE.get(), new ResourceLocation(CCReference.MOD_ID, "pattern"),
                (stack, level, player, damage) -> stack.getOrCreateTag().getInt("Pattern"));
    }
}
