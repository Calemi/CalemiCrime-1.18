package com.tm.calemicrime.main;

import com.mrcrayfish.guns.client.render.gun.ModelOverrides;
import com.mrcrayfish.guns.client.render.gun.model.SimpleModel;
import com.tm.calemicrime.client.GunModels;
import com.tm.calemicrime.client.render.RenderDryingRack;
import com.tm.calemicrime.client.render.RenderMineGenerator;
import com.tm.calemicrime.client.render.RenderRadiationProjector;
import com.tm.calemicrime.client.render.RenderRegionProtector;
import com.tm.calemicrime.client.screen.ScreenMineGenerator;
import com.tm.calemicrime.client.screen.ScreenRentAcceptor;
import com.tm.calemicrime.command.CrimeCommandsBase;
import com.tm.calemicrime.command.RegionTeamArgument;
import com.tm.calemicrime.event.*;
import com.tm.calemicrime.file.*;
import com.tm.calemicrime.init.*;
import com.tm.calemicrime.packet.CCPacketHandler;
import com.tm.calemicrime.tab.CCTab;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
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

    public static final DamageSource BLACK_TAR_EXPIRE = (new DamageSource("black_tar_expire")).bypassArmor();
    public static final DamageSource RADIATION = (new DamageSource("radiation")).bypassArmor();

    public static boolean isCuriosLoaded = false;

    /**
     * Everything starts here.
     */
    public CalemiCrime() {

        //Initializes the instance.
        instance = this;

        isCuriosLoaded = ModList.get().getModContainerById("curios").isPresent();

        MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        MOD_EVENT_BUS.addListener(this::onCommonSetup);
        MOD_EVENT_BUS.addListener(this::onClientSetup);
        MOD_EVENT_BUS.addListener(this::onSetupComplete);

        InitItems.init();
        InitBlockEntityTypes.BLOCK_ENTITY_TYPES.register(MOD_EVENT_BUS);
        InitRecipes.RECIPES.register(MOD_EVENT_BUS);
        InitFluids.FLUIDS.register(MOD_EVENT_BUS);
        InitMenuTypes.MENU_TYPES.register(MOD_EVENT_BUS);
        InitMobEffects.MOB_EFFECTS.register(MOD_EVENT_BUS);
        InitSounds.SOUNDS.register(MOD_EVENT_BUS);
        InitTaskTypes.init();

        PreventBlockPlaceListFile.init();
        PreventItemUseListFile.init();
        RentAcceptorTypesFile.init();
        LootBoxFile.init();
        PlotsFile.init();
        DryingRackRecipesFile.init();
        RegionTeamsFile.init();

        MinecraftForge.EVENT_BUS.register(this);

        CCConfig.init();
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        CCPacketHandler.init();
        MinecraftForge.EVENT_BUS.register(new CuriosEvent());
        MinecraftForge.EVENT_BUS.register(new RegionProtectorEvents());
        MinecraftForge.EVENT_BUS.register(new MiscPreventionEvents());
        MinecraftForge.EVENT_BUS.register(new ToxicItemEvents());
        MinecraftForge.EVENT_BUS.register(new FilePreventionEvents());
        MinecraftForge.EVENT_BUS.register(new DrugEvents());
        MinecraftForge.EVENT_BUS.register(new ItemTooltipEvents());
        MinecraftForge.EVENT_BUS.register(new NiftyEvents());
        MinecraftForge.EVENT_BUS.register(new VehicleEvents());
        MinecraftForge.EVENT_BUS.register(new ItemTradeEventListener());

        ArgumentTypes.register("cc:team", RegionTeamArgument.class, new EmptyArgumentSerializer<>(RegionTeamArgument::team));
    }

    private void onClientSetup(final FMLClientSetupEvent event) {

        MenuScreens.register(InitMenuTypes.RENT_ACCEPTOR.get(), ScreenRentAcceptor::new);
        MenuScreens.register(InitMenuTypes.MINE_GENERATOR.get(), ScreenMineGenerator::new);

        InitBlockRenderTypes.init();

        BlockEntityRenderers.register(InitBlockEntityTypes.REGION_PROTECTOR.get(), RenderRegionProtector::new);
        BlockEntityRenderers.register(InitBlockEntityTypes.MINE_GENERATOR.get(), RenderMineGenerator::new);
        BlockEntityRenderers.register(InitBlockEntityTypes.RADIATION_PROJECTOR.get(), RenderRadiationProjector::new);
        BlockEntityRenderers.register(InitBlockEntityTypes.DRYING_RACK.get(), RenderDryingRack::new);

        ModelOverrides.register(InitItems.DONOR_PISTOL.get(), new SimpleModel(GunModels.DONOR_PISTOL::getModel));
        ModelOverrides.register(InitItems.R99.get(), new SimpleModel(GunModels.R99::getModel));
    }

    private void onSetupComplete(final FMLLoadCompleteEvent event) {
        InitBrewingRecipes.init();
        InitCompostables.init();
        InitDisplayLinkBehaviours.init();
    }

    @SubscribeEvent
    public void onServerStarting(RegisterCommandsEvent event) {
        CrimeCommandsBase.register(event.getDispatcher());
    }
}
