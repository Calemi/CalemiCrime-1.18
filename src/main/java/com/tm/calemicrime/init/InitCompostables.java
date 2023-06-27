package com.tm.calemicrime.init;

import net.minecraft.world.level.block.ComposterBlock;

public class InitCompostables {

    public static void init() {

        ComposterBlock.COMPOSTABLES.put(InitItems.COCA_SEEDS.get(), 0.3F);
        ComposterBlock.COMPOSTABLES.put(InitItems.CANNABIS_SEEDS.get(), 0.3F);
        ComposterBlock.COMPOSTABLES.put(InitItems.COCA_LEAF.get(), 0.6F);
        ComposterBlock.COMPOSTABLES.put(InitItems.CANNABIS_LEAF.get(), 0.6F);
        ComposterBlock.COMPOSTABLES.put(InitItems.ERGOT.get(), 0.75F);
        ComposterBlock.COMPOSTABLES.put(InitItems.KUSH.get(), 0.9F);
    }
}
