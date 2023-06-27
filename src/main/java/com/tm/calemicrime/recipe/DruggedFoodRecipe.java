package com.tm.calemicrime.recipe;

import com.tm.calemicrime.init.InitRecipes;
import com.tm.calemicrime.item.drug.IItemDrug;
import com.tm.calemicrime.item.drug.ItemDrug;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class DruggedFoodRecipe extends CustomRecipe {

    public DruggedFoodRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingContainer inv, Level world) {

        int foodCount = 0;
        int drugCount = 0;

        for (int i = 0; i < inv.getContainerSize(); i++) {

            ItemStack stackInSlot = inv.getItem(i);

            if (stackInSlot.getItem().isEdible() && stackInSlot.getItem().getCraftingRemainingItem() == null) {
                foodCount++;
            }

            else if (stackInSlot.getItem() instanceof IItemDrug) {
                drugCount++;
            }

            else if (!stackInSlot.isEmpty()) {
                return false;
            }
        }

        return foodCount == 1 && drugCount >= 1;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {

        ItemStack food = ItemStack.EMPTY;

        for (int i = 0; i < inv.getContainerSize(); i++) {

            ItemStack stackInSlot = inv.getItem(i);

            if (stackInSlot.getItem().isEdible()) {
                food = stackInSlot.copy();
                food.setCount(1);
            }
        }

        if (!food.isEmpty()) {

            for (int i = 0; i < inv.getContainerSize(); i++) {

                ItemStack stackInSlot = inv.getItem(i);

                if (stackInSlot.getItem() instanceof IItemDrug) {

                    CompoundTag nbt = food.getOrCreateTag();

                    String drugString = nbt.getString("Drug");
                    drugString += stackInSlot.getItem().getRegistryName().toString() + "%";
                    nbt.putString("Drug", drugString);

                    food.setTag(nbt);
                }
            }
        }

        return food;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return InitRecipes.DRUGGED_FOOD.get();
    }
}