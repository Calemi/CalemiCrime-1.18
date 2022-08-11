package com.tm.calemicrime.init;

import com.tm.calemicrime.block.*;
import com.tm.calemicrime.block.base.BlockItemBase;
import com.tm.calemicrime.item.ItemGasMask;
import com.tm.calemicrime.item.ItemToxic;
import com.tm.calemicrime.main.CCReference;
import com.tm.calemicrime.main.CalemiCrime;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
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
    public static final RegistryObject<Block> MINE_GENERATOR = regBlock("mine_generator", CalemiCrime.TAB, BlockMineGenerator::new);

    public static final RegistryObject<Block> SHEET_OF_METH = regBlock("sheet_of_meth", CalemiCrime.TAB, BlockSheetOfMeth::new);
    public static final RegistryObject<Block> SHEET_OF_P2P_METH = regBlock("sheet_of_p2p_meth", CalemiCrime.TAB, BlockSheetOfMeth::new);

    //PLANTS
    public static final RegistryObject<Block> CANNABIS = BLOCKS.register("cannabis", BlockCannabis::new);
    public static final RegistryObject<Item> CANNABIS_SEEDS = ITEMS.register("cannabis_seeds", () -> new BlockItemBase(CANNABIS.get(), CalemiCrime.TAB));

    public static final RegistryObject<Block> COCA = BLOCKS.register("coca", BlockCoca::new);
    public static final RegistryObject<Item> COCA_SEEDS = ITEMS.register("coca_seeds", () -> new BlockItemBase(COCA.get(), CalemiCrime.TAB));

    public static final RegistryObject<Block> PSILOCYBIN_MUSHROOM = regBlock("psilocybin_mushroom", CalemiCrime.TAB, () -> new MushroomBlock(BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_BROWN).noCollission().randomTicks().instabreak().sound(SoundType.GRASS).lightLevel((p) -> {return 1;}), () -> {return TreeFeatures.HUGE_BROWN_MUSHROOM;}));

    //----- ITEMS ------\\

    public static final RegistryObject<Item> GAS_MASK = regItem("gas_mask", ItemGasMask::new);
    public static final RegistryObject<Item> BAG = regItem("bag", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));

    public static final RegistryObject<Item> TEENTH_OF_METH = regItem("teenth_of_meth", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> TEENTH_OF_BIKER_METH = regItem("teenth_of_biker_meth", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> TEENTH_OF_KUSH = regItem("teenth_of_kush", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> TEENTH_OF_HEROIN = regItem("teenth_of_heroin", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> TEENTH_OF_COCAINE = regItem("teenth_of_cocaine", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));

    //METH
    public static final RegistryObject<Item> PSEUDOEPHEDRINE = regItem("pseudoephedrine", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> UNPROCESSED_METH = regItem("unprocessed_meth", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> METH = regItem("meth", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));

    //P2P METH
    public static final RegistryObject<Item> METHYLSULFONYLMETHANE = regItem("methylsulfonylmethane", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> UNPROCESSED_P2P_METH = regItem("unprocessed_p2p_meth", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> P2P_METH = regItem("p2p_meth", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));

    //KETAMINE
    public static final RegistryObject<Item> SILICA_DUST = regItem("silica_dust", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> SILICA_GEL = regItem("silica_gel", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> ACACIA_EXTRACT = regItem("acacia_extract", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));

    public static final RegistryObject<Item> SWEETENED_ACACIA_EXTRACT = regItem("sweetened_acacia_extract", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> KETAMINE = regItem("ketamine", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));

    public static final RegistryObject<Item> CANNABIS_LEAF = regItem("cannabis_leaf", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> KUSH = regItem("kush", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));

    //LSD
    public static final RegistryObject<Item> ERGOT = regItem("ergot", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> ERGOTAMINE = regItem("ergotamine", () -> new ItemToxic(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> LSD = regItem("lsd", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));

    //HEROIN
    public static final RegistryObject<Item> HEROIN = regItem("heroin", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));

    //COCAINE
    public static final RegistryObject<Item> COCA_LEAF = regItem("coca_leaf", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> COCAINE = regItem("cocaine", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));

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
