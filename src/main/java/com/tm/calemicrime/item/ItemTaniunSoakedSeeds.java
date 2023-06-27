package com.tm.calemicrime.item;

import com.tm.calemicrime.init.InitItems;
import com.tm.calemicrime.main.CCConfig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;

import java.util.Random;

public class ItemTaniunSoakedSeeds extends ItemWashable {

    @Override
    public LiquidBlock GetWashLiquid() {
        return (LiquidBlock) Blocks.LAVA;
    }

    @Override
    public ItemStack GetResult() {
        return new ItemStack(InitItems.CRACKED_TANIUN.get(), 1);
    }

    @Override
    public boolean consumeLiquid(Random rand) {
        return false;
    }

    public int getUseDuration(ItemStack stack) {
        return CCConfig.drugs.taniunSoakedSeedsCrackTime.get() * 20;
    }
}