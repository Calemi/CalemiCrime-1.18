package com.tm.calemicrime.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class DrugHighEffect extends MobEffect {

    public String shaderName;

    public DrugHighEffect(String shaderName) {
        super(MobEffectCategory.NEUTRAL, 0);
        this.shaderName = shaderName;
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }
}
