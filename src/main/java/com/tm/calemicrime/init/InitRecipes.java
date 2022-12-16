package com.tm.calemicrime.init;

import com.tm.calemicrime.main.CCReference;
import com.tm.calemicrime.recipe.DruggedFoodRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class InitRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CCReference.MOD_ID);
    public static final RegistryObject<RecipeSerializer<DruggedFoodRecipe>> DRUGGED_FOOD = RECIPES.register("drugged_food", () -> new SimpleRecipeSerializer<>(DruggedFoodRecipe::new));

}
