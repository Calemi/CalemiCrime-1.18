package com.tm.calemicrime.effect;

import com.tm.calemicrime.item.drug.IItemDrug;
import com.tm.calemicrime.item.drug.ItemDrug;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DrugHighEffect extends MobEffect {

    public IItemDrug item;
    public String shaderName;

    public DrugHighEffect(IItemDrug item, String shaderName) {
        super(MobEffectCategory.NEUTRAL, 0);
        this.item = item;
        this.shaderName = shaderName;
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }
}
