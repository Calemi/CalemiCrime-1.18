package com.tm.calemicrime.main;

import com.tm.calemicrime.client.render.RenderRegionProtector;
import com.tm.calemicrime.client.screen.ScreenRentAcceptor;
import com.tm.calemicrime.event.RegionProtectorEvents;
import com.tm.calemicrime.init.InitBlockEntityTypes;
import com.tm.calemicrime.init.InitItems;
import com.tm.calemicrime.init.InitMenuTypes;
import com.tm.calemicrime.packet.CCPacketHandler;
import com.tm.calemicrime.tab.CCTab;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * The main class for Calemi's Economy
 */
@Mod(CCReference.MOD_ID)
public class CalemiCrime {

    /**
     * A reference to the instance of the mod.
     */
    public static CalemiCrime instance;

    /**
     * Used to register the client and common setup methods.
     */
    public static IEventBus MOD_EVENT_BUS;

    public static final CreativeModeTab TAB = new CCTab();

    /**
     * Everything starts here.
     */
    public CalemiCrime() {

        //Initializes the instance.
        instance = this;

        MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        MOD_EVENT_BUS.addListener(this::onCommonSetup);
        MOD_EVENT_BUS.addListener(this::onClientSetup);

        InitItems.init();
        InitBlockEntityTypes.BLOCK_ENTITY_TYPES.register(MOD_EVENT_BUS);
        InitMenuTypes.MENU_TYPES.register(MOD_EVENT_BUS);
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        CCPacketHandler.init();
        MinecraftForge.EVENT_BUS.register(new RegionProtectorEvents());
    }

    private void onClientSetup(final FMLClientSetupEvent event) {

        MenuScreens.register(InitMenuTypes.RENT_ACCEPTOR.get(), ScreenRentAcceptor::new);

        BlockEntityRenderers.register(InitBlockEntityTypes.REGION_PROTECTOR.get(), RenderRegionProtector::new);
    }
}
