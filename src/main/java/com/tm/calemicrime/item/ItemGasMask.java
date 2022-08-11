package com.tm.calemicrime.item;

import com.tm.calemicrime.main.CalemiCrime;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

public class ItemGasMask extends ArmorItem {

    public ItemGasMask() {
        super(CCArmorTiers.BLESSED_NIGHT, EquipmentSlot.HEAD, new Item.Properties().tab(CalemiCrime.TAB).stacksTo(1));
    }
}
