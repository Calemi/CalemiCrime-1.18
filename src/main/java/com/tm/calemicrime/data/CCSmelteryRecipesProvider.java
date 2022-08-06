package com.tm.calemicrime.data;

import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicrime.init.InitItems;
import com.tm.calemicrime.main.CCReference;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.data.recipe.ICommonRecipeHelper;
import slimeknights.tconstruct.library.data.recipe.ISmelteryRecipeHelper;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;

import java.util.function.Consumer;

public class CCSmelteryRecipesProvider extends RecipeProvider implements IConditionBuilder, ISmelteryRecipeHelper, ICommonRecipeHelper {

    public CCSmelteryRecipesProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {

        LogHelper.log(CCReference.MOD_NAME, "METH RECIPE");

        String castingFolder = "smeltery/casting/seared/";

        ItemCastingRecipeBuilder.tableRecipe(InitItems.UNPROCESSED_METH.get())
                .setCast(Blocks.GLASS_PANE, true)
                .setFluidAndTime(TinkerFluids.moltenCopper, true, FluidValues.INGOT * 4)
                .save(consumer, modResource(castingFolder + "unprocessed_meth"));
    }

    @Override
    public String getName() {
        return "Calemi's Organized Crime Smeltery Recipes";
    }

    @Override
    public String getModId() {
        return CCReference.MOD_ID;
    }
}
