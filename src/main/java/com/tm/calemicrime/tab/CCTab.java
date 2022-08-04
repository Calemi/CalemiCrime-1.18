package com.tm.calemicrime.tab;

import com.tm.calemicrime.init.InitItems;
import com.tm.calemicrime.main.CCReference;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class CCTab extends CreativeModeTab {

    public CCTab() {
        super(CCReference.MOD_ID + ".tabMain");
    }

    @Override
    public ItemStack makeIcon () {
        return new ItemStack(InitItems.REGION_PROJECTOR.get());
    }
}
