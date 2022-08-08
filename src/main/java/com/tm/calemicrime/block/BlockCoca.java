package com.tm.calemicrime.block;

import com.tm.calemicrime.init.InitItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class BlockCoca extends BlockBushPlant {

    @Override
    public ItemStack getHarvest() {
        return new ItemStack(InitItems.COCA_LEAF.get());
    }

    @Override
    public ItemLike getSeeds() {
        return InitItems.COCA_SEEDS.get();
    }
}
