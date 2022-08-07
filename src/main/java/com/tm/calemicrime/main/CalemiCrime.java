package com.tm.calemicrime.main;

import com.tm.calemicrime.client.render.RenderRegionProtector;
import com.tm.calemicrime.client.screen.ScreenRentAcceptor;
import com.tm.calemicrime.event.PreventionEvents;
import com.tm.calemicrime.event.RegionProtectorEvents;
import com.tm.calemicrime.init.*;
import com.tm.calemicrime.packet.CCPacketHandler;
import com.tm.calemicrime.tab.CCTab;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
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

    /**
     * Everything starts here.
     */
    public CalemiCrime() {

        //Initializes the instance.
        instance = this;

        MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        MOD_EVENT_BUS.addListener(this::onCommonSetup);
        MOD_EVENT_BUS.addListener(this::onClientSetup);
        MOD_EVENT_BUS.addListener(this::onSetupComplete);

        InitItems.init();
        InitBlockEntityTypes.BLOCK_ENTITY_TYPES.register(MOD_EVENT_BUS);
        InitFluids.FLUIDS.register(MOD_EVENT_BUS);
        InitMenuTypes.MENU_TYPES.register(MOD_EVENT_BUS);
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        CCPacketHandler.init();
        MinecraftForge.EVENT_BUS.register(new RegionProtectorEvents());
        MinecraftForge.EVENT_BUS.register(new PreventionEvents());
    }

    private void onClientSetup(final FMLClientSetupEvent event) {

        MenuScreens.register(InitMenuTypes.RENT_ACCEPTOR.get(), ScreenRentAcceptor::new);

        InitBlockRenderTypes.init();

        BlockEntityRenderers.register(InitBlockEntityTypes.REGION_PROTECTOR.get(), RenderRegionProtector::new);
    }

    private void onSetupComplete(final FMLLoadCompleteEvent event) {

        ItemStack water_bottle = new ItemStack(Items.POTION);
        PotionUtils.setPotion(water_bottle, Potions.WATER);

        BrewingRecipeRegistry.addRecipe(Ingredient.of(water_bottle), Ingredient.of(InitItems.PSEUDOEPHEDRINE.get()), new ItemStack(InitItems.METHYLSULFONYLMETHANE.get()));

        BrewingRecipeRegistry.addRecipe(Ingredient.of(water_bottle), Ingredient.of(Items.ACACIA_LOG), new ItemStack(InitItems.ACACIA_EXTRACT.get()));
        BrewingRecipeRegistry.addRecipe(Ingredient.of(InitItems.ACACIA_EXTRACT.get()), Ingredient.of(Items.SUGAR), new ItemStack(InitItems.SWEETENED_ACACIA_EXTRACT.get()));
    }

}
