package com.tm.calemicrime.init;

import com.tm.calemicrime.block.BlockRegionProtector;
import com.tm.calemicrime.block.BlockRentAcceptor;
import com.tm.calemicrime.block.base.BlockItemBase;
import com.tm.calemicrime.main.CCReference;
import com.tm.calemicrime.main.CalemiCrime;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * Handles setting up the Items for the mod.
 */
public class InitItems {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CCReference.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CCReference.MOD_ID);

    /**
     * Called to initialize the Items.
     */
    public static void init() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    //----- BLOCKS ------\\

    public static final RegistryObject<Block> REGION_PROJECTOR = regBlock("region_protector", CalemiCrime.TAB, BlockRegionProtector::new);
    public static final RegistryObject<Block> RENT_ACCEPTOR = regBlock("rent_acceptor", CalemiCrime.TAB, BlockRentAcceptor::new);

    //----- ITEMS ------\\

    public static final RegistryObject<Item> BAG = regItem("bag", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> PSEUDOEPHEDRINE = regItem("pseudoephedrine", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> METHYLSULFONYLMETHANE = regItem("methylsulfonylmethane", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> UNPROCESSED_METH = regItem("unprocessed_meth", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> UNPROCESSED_P2P_METH = regItem("unprocessed_p2p_meth", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));

    public static final RegistryObject<Item> METH = regItem("meth", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> P2P_METH = regItem("p2p_meth", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));




    /**
     * Used to register a Block.
     * @param name The name of the Block.
     * @param tab The Creative Tab for the Block.
     * @param sup The Item class.
     */
    public static RegistryObject<Block> regBlock(String name, CreativeModeTab tab, final Supplier<? extends Block> sup) {
        RegistryObject<Block> registryBlock = BLOCKS.register(name, sup);
        RegistryObject<Item> registryItem = ITEMS.register(name, () -> new BlockItemBase(registryBlock.get(), tab));
        return registryBlock;
    }

    /**
     * Used to register an Item.
     * @param name The name of the Item.
     * @param sup The Item class.
     */
    public static RegistryObject<Item> regItem(String name, final Supplier<? extends Item> sup) {
        return ITEMS.register(name, sup);
    }
}
