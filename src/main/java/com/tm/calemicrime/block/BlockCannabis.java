package com.tm.calemicrime.block;

import com.tm.calemicrime.init.InitItems;
import com.tm.calemicrime.main.CCConfig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class BlockCannabis extends BlockBushPlant {

    @Override
    public ItemStack getHarvest() {
        return new ItemStack(InitItems.CANNABIS_LEAF.get());
    }

    @Override
    public ItemLike getSeeds() {
        return InitItems.CANNABIS_SEEDS.get();
    }

    @Override
    public boolean canUseBonemeal() {
        return CCConfig.drugs.cannabisPlantRequireBonemeal.get();
    }

    @Override
    public int growTime() {
        return CCConfig.drugs.cannabisPlantGrowTime.get();
    }
}
