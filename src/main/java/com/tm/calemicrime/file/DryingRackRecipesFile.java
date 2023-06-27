package com.tm.calemicrime.file;

import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tm.calemicrime.util.FileHelper;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class DryingRackRecipesFile {

    public static ArrayList<DryingRackRecipe> list;

    public static void init() {
        list = FileHelper.readFileOrCreate("DryingRackRecipes", getDefaults(), new TypeToken<ArrayList<DryingRackRecipe>>(){});
    }

    private static ArrayList<DryingRackRecipe> getDefaults() {

        ArrayList<DryingRackRecipe> entries = new ArrayList<>();
        entries.add(new DryingRackRecipe("calemicrime:cannabis_leaf", "calemicrime:dried_cannabis_leaf", 1000));
        entries.add(new DryingRackRecipe("minecraft:rotten_flesh", "minecraft:leather", 1000));
        return entries;
    }

    public static DryingRackRecipe getRecipe(ItemStack stack) {

        for (DryingRackRecipe recipe : list) {

            if (recipe.getIngredient().sameItem(stack)) {
                return recipe;
            }
        }

        return null;
    }

    public static final class DryingRackRecipe {

        private final String ingredient;
        private final String result;
        private final int ticks;

        public DryingRackRecipe(String ingredient, String result, int ticks) {
            this.ingredient = ingredient;
            this.result = result;
            this.ticks = ticks;
        }

        private ItemStack getStack(String str) {

            ItemParser parser;

            try {
                parser = (new ItemParser(new StringReader(str), false)).parse();
            } catch (CommandSyntaxException e) {
                return ItemStack.EMPTY;
            }

            ItemStack stack = new ItemStack(parser.getItem());
            stack.setTag(parser.getNbt());

            return stack;
        }

        public ItemStack getIngredient() {
            return getStack(ingredient);
        }

        public ItemStack getResult() {
            return getStack(result);
        }

        public int getTicks() {
            return ticks;
        }
    }
}