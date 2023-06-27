package com.tm.calemicrime.init;

import com.mrcrayfish.guns.item.GunItem;
import com.tm.calemicrime.block.*;
import com.tm.calemicrime.block.base.BlockItemBase;
import com.tm.calemicrime.item.MedalTypes;
import com.tm.calemicrime.item.*;
import com.tm.calemicrime.item.drug.*;
import com.tm.calemicrime.main.CCReference;
import com.tm.calemicrime.main.CalemiCrime;
import com.wildcard.buddycards.block.BuddycardBoosterBoxBlock;
import com.wildcard.buddycards.core.BuddycardSet;
import com.wildcard.buddycards.item.*;
import com.wildcard.buddycards.registries.BuddycardsBlocks;
import com.wildcard.buddycards.registries.BuddycardsItems;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Handles setting up the Items for the mod.
 */
public class InitItems {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CCReference.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CCReference.MOD_ID);

    public static final BuddycardSet CRIME_SET = new BuddycardSet("crime");
    public static final BuddycardsItems.BuddycardRequirement CRIME_REQUIREMENT = () -> ModList.get().isLoaded(CCReference.MOD_ID);

    /**
     * Called to initialize the Items.
     */
    public static void init() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());

        regCards(CRIME_SET, 1, 1, Rarity.COMMON, BuddycardsItems.DEFAULT_CARD_PROPERTIES, CRIME_REQUIREMENT);
    }

    //----- BLOCKS ------\\

    public static final RegistryObject<Block> REGION_PROJECTOR = regBlock("region_protector", CalemiCrime.TAB, BlockRegionProtector::new);
    public static final RegistryObject<Block> RENT_ACCEPTOR = regBlock("rent_acceptor", CalemiCrime.TAB, BlockRentAcceptor::new);
    public static final RegistryObject<Block> MINE_GENERATOR = regBlock("mine_generator", CalemiCrime.TAB, BlockMineGenerator::new);
    public static final RegistryObject<Block> RADIATION_PROJECTOR = regBlock("radiation_projector", CalemiCrime.TAB, BlockRadiationProjector::new);
    public static final RegistryObject<Block> DRYING_RACK = regBlock("drying_rack", CalemiCrime.TAB, BlockDryingRack::new);

    public static final RegistryObject<Block> SHEET_OF_METH = regBlock("sheet_of_meth", CalemiCrime.TAB, BlockSheetOfMeth::new);
    public static final RegistryObject<Block> SHEET_OF_P2P_METH = regBlock("sheet_of_p2p_meth", CalemiCrime.TAB, BlockSheetOfMeth::new);

    //PLANTS
    public static final RegistryObject<Block> CANNABIS = regBlockOnly("cannabis", BlockCannabis::new);
    public static final RegistryObject<Item> CANNABIS_SEEDS = regItem("cannabis_seeds", () -> new BlockItemBase(CANNABIS.get(), CalemiCrime.TAB));

    public static final RegistryObject<Block> COCA = regBlockOnly("coca", BlockCoca::new);
    public static final RegistryObject<Item> COCA_SEEDS = regItem("coca_seeds", () -> new BlockItemBase(COCA.get(), CalemiCrime.TAB));

    public static final RegistryObject<Block> PSILOCYBIN_MUSHROOM = regBlockOnly("psilocybin_mushroom", () -> new MushroomBlock(BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_BROWN).noCollission().randomTicks().instabreak().sound(SoundType.GRASS).lightLevel((p) -> {return 1;}), () -> {return TreeFeatures.HUGE_BROWN_MUSHROOM;}));
    public static final RegistryObject<Item> PSILOCYBIN_MUSHROOM_ITEM = regItem("psilocybin_mushroom", ItemMushroom::new);

    //ROADS
    public static final RegistryObject<Block> ROAD_LINE_1 = regBlock("road_line_1", CalemiCrime.TAB, BlockRoadLine::new);
    public static final RegistryObject<Block> ROAD_LINE_1_SLOPE = regBlock("road_line_1_slope", CalemiCrime.TAB, BlockRoadLine::new);
    public static final RegistryObject<Block> ROAD_LINE_1_SLOPE_MIRROR = regBlock("road_line_1_slope_mirror", CalemiCrime.TAB, BlockRoadLine::new);
    public static final RegistryObject<Block> ROAD_LINE_2_SLOPE = regBlock("road_line_2_slope", CalemiCrime.TAB, BlockRoadLine::new);
    public static final RegistryObject<Block> ROAD_LINE_2_SLOPE_MIRROR = regBlock("road_line_2_slope_mirror", CalemiCrime.TAB, BlockRoadLine::new);
    public static final RegistryObject<Block> ROAD_LINE_2 = regBlock("road_line_2", CalemiCrime.TAB, BlockRoadLine::new);
    public static final RegistryObject<Block> ROAD_LINE_3 = regBlock("road_line_3", CalemiCrime.TAB, BlockRoadLine::new);
    public static final RegistryObject<Block> ROAD_LINE_4 = regBlock("road_line_4", CalemiCrime.TAB, BlockRoadLine::new);
    public static final RegistryObject<Block> ROAD_LINE_5 = regBlock("road_line_5", CalemiCrime.TAB, BlockRoadLine::new);
    public static final RegistryObject<Block> ROAD_LINE_6 = regBlock("road_line_6", CalemiCrime.TAB, BlockRoadLine::new);
    public static final RegistryObject<Block> ROAD_LINE_7 = regBlock("road_line_7", CalemiCrime.TAB, BlockRoadLine::new);
    public static final RegistryObject<Block> ROAD_LINE_8 = regBlock("road_line_8", CalemiCrime.TAB, BlockRoadLine::new);

    public static final RegistryObject<Block> BULLET_HOLE = regBlock("bullet_hole", CalemiCrime.TAB, BlockBulletHole::new);

    //----- ITEMS ------\\

    public static final RegistryObject<Item> REGION_WAND = regItem("region_wand", ItemRegionWand::new);

    public static final RegistryObject<Item> BAG = regItem("bag", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> ROAD_KIT = regItem("road_kit", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));

    public static final RegistryObject<Item> TEENTH_OF_TANIUN = regItem("teenth_of_taniun", ItemTaniun::new);
    public static final RegistryObject<Item> TEENTH_OF_KUSH = regItem("teenth_of_kush", ItemKush::new);
    public static final RegistryObject<Item> TEENTH_OF_METH = regItem("teenth_of_meth", ItemMeth::new);
    public static final RegistryObject<Item> TEENTH_OF_BIKER_METH = regItem("teenth_of_biker_meth", ItemBikerMeth::new);
    public static final RegistryObject<Item> TEENTH_OF_COCAINE = regItem("teenth_of_cocaine", ItemCocaine::new);
    public static final RegistryObject<Item> TEENTH_OF_HEROIN = regItem("teenth_of_heroin", ItemHeroin::new);
    public static final RegistryObject<Item> TEENTH_OF_BLACK_TAR_HEROIN = regItem("teenth_of_black_tar_heroin", ItemBlackTarHeroin::new);

    //TANIUN
    public static final RegistryObject<Item> DRIED_SEEDS = regItem("dried_seeds", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> TANIUN_SOAKED_SEEDS = regItem("taniun_soaked_seeds", ItemTaniunSoakedSeeds::new);
    public static final RegistryObject<Item> CRACKED_TANIUN = regItem("cracked_taniun", ItemTaniun::new);

    //KUSH
    public static final RegistryObject<Item> CANNABIS_LEAF = regItem("cannabis_leaf", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> DRIED_CANNABIS_LEAF = regItem("dried_cannabis_leaf", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> KUSH = regItem("kush", ItemKush::new);

    //LSD
    public static final RegistryObject<Item> ERGOT = regItem("ergot", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> ERGOTAMINE_BOTTLE = regItem("ergotamine_bottle", () -> new ItemToxic(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> LSD = regItem("lsd", ItemLSD::new);

    //METH
    public static final RegistryObject<Item> PSEUDOEPHEDRINE = regItem("pseudoephedrine", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> METHYLSULFONYLMETHANE_BOTTLE = regItem("methylsulfonylmethane_bottle", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> UNPROCESSED_METH = regItem("unprocessed_meth", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> METH = regItem("meth", ItemMeth::new);

    //P2P METH
    public static final RegistryObject<Item> UNPROCESSED_P2P_METH = regItem("unprocessed_p2p_meth", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> P2P_METH = regItem("p2p_meth", ItemBikerMeth::new);

    //KETAMINE
    public static final RegistryObject<Item> SILICA_DUST = regItem("silica_dust", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> SILICA_GEL = regItem("silica_gel", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> ACACIA_EXTRACT_BOTTLE = regItem("acacia_extract_bottle", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> SWEETENED_ACACIA_EXTRACT_BOTTLE = regItem("sweetened_acacia_extract_bottle", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> KETAMINE = regItem("ketamine", ItemKetamine::new);

    //COCAINE
    public static final RegistryObject<Item> COCA_LEAF = regItem("coca_leaf", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> COCAINE = regItem("cocaine", ItemCocaine::new);

    //HEROIN
    public static final RegistryObject<Item> HEROIN = regItem("heroin", ItemHeroin::new);

    //BLACK TAR HEROIN
    public static final RegistryObject<Item> BLACK_TAR_HEROIN = regItem("black_tar_heroin", ItemBlackTarHeroin::new);

    //MORPHINE
    public static final RegistryObject<Item> MORPHINE_BOTTLE = regItem("morphine_bottle", () -> new Item(new Item.Properties().tab(CalemiCrime.TAB)));

    //COSMETICS
    public static final RegistryObject<Item> LOOT_BOX = regItem("loot_box", ItemLootBox::new);
    public static final RegistryObject<Item> CAPE = regItem("cape", ItemCape::new);
    public static final RegistryObject<Item> DONOR_PISTOL = regItem("donor_pistol", () -> new GunItem(new Item.Properties().stacksTo(1).tab(CalemiCrime.TAB)));
    public static final RegistryObject<Item> R99 = regItem("r99", () -> new GunItem(new Item.Properties().stacksTo(1).tab(CalemiCrime.TAB)));

    //BUDDYCARDS

    public static final RegistryObject<BuddycardPackItem> PACK = ITEMS.register("buddycard_pack_crime", () -> new BuddycardSetPackItem(CRIME_REQUIREMENT, CRIME_SET, 4, 1, BuddycardsItems.DEFAULT_RARITY_WEIGHTS, BuddycardsItems.DEFAULT_PACK_PROPERTIES));

    public static final RegistryObject<BuddycardBoosterBoxBlock> BOOSTER_BOX = BLOCKS.register("buddycard_booster_box_crime", () -> new BuddycardBoosterBoxBlock(CRIME_REQUIREMENT, BuddycardsBlocks.BOOSTER_BOX_PROPERTIES));
    public static final RegistryObject<BuddycardBoosterBoxItem> BOOSTER_BOX_ITEM = ITEMS.register("buddycard_booster_box_crime", () -> new BuddycardBoosterBoxItem(BOOSTER_BOX.get(), PACK, BuddycardsItems.DEFAULT_UNCOMMON_PROPERTIES));

    public static final RegistryObject<BuddycardBinderItem> BINDER = ITEMS.register("buddycard_binder_crime", () -> new BuddycardBinderItem(CRIME_REQUIREMENT, BuddycardsItems.DEFAULT_BINDER_PROPERTIES));
    public static final RegistryObject<BuddysteelSetMedalItem> MEDAL = ITEMS.register("buddysteel_medal_crime", () -> new BuddysteelSetMedalItem(CRIME_REQUIREMENT, MedalTypes.CRIME_SET, CRIME_SET, BuddycardsItems.DEFAULT_CURIO_PROPERTIES));


    public static RegistryObject<Block> regBlock(String name, CreativeModeTab tab, final Supplier<? extends Block> sup) {
        RegistryObject<Block> registryBlock = BLOCKS.register(name, sup);
        RegistryObject<Item> registryItem = ITEMS.register(name, () -> new BlockItemBase(registryBlock.get(), tab));
        return registryBlock;
    }

    public static RegistryObject<Block> regBlockOnly(String name, final Supplier<? extends Block> sup) {
        return BLOCKS.register(name, sup);
    }

    public static RegistryObject<Item> regItem(String name, final Supplier<? extends Item> sup) {
        return ITEMS.register(name, sup);
    }

    public static void regCards(BuddycardSet set, int startValue, int amount, Rarity rarity, Item.Properties properties, BuddycardsItems.BuddycardRequirement requirement) {
        Objects.requireNonNull(set);
        for (int i = startValue; i < amount + startValue; i++) {
            int finalI = i;
            ITEMS.register("buddycard_" + set.getName() + finalI, () -> new BuddycardItem(requirement, set, finalI, rarity, properties));
        }
    }
}
