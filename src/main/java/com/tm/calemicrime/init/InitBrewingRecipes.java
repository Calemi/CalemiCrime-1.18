package com.tm.calemicrime.init;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;

public class InitBrewingRecipes {

    public static void init() {

        ItemStack water_bottle = new ItemStack(Items.POTION);
        PotionUtils.setPotion(water_bottle, Potions.WATER);

        BrewingRecipeRegistry.addRecipe(Ingredient.of(water_bottle), Ingredient.of(InitItems.PSEUDOEPHEDRINE.get()), new ItemStack(InitItems.METHYLSULFONYLMETHANE_BOTTLE.get()));

        BrewingRecipeRegistry.addRecipe(Ingredient.of(water_bottle), Ingredient.of(Items.ACACIA_LOG), new ItemStack(InitItems.ACACIA_EXTRACT_BOTTLE.get()));
        BrewingRecipeRegistry.addRecipe(Ingredient.of(InitItems.ACACIA_EXTRACT_BOTTLE.get()), Ingredient.of(Items.SUGAR), new ItemStack(InitItems.SWEETENED_ACACIA_EXTRACT_BOTTLE.get()));

        BrewingRecipeRegistry.addRecipe(Ingredient.of(water_bottle), Ingredient.of(InitItems.ERGOT.get()), new ItemStack(InitItems.ERGOTAMINE_BOTTLE.get()));
    }
}
