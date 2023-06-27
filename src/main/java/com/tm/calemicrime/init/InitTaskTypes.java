package com.tm.calemicrime.init;

import com.tm.calemicrime.ftbquests.BuyTask;
import com.tm.calemicrime.ftbquests.SellTask;
import com.tm.calemicrime.main.CCReference;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import net.minecraft.resources.ResourceLocation;

public interface InitTaskTypes {

    static void init() {}

    TaskType SELL = TaskTypes.register(new ResourceLocation(CCReference.MOD_ID, "sell"), SellTask::new, () -> Icon.getIcon("minecraft:item/gold_nugget"));
    TaskType BUY = TaskTypes.register(new ResourceLocation(CCReference.MOD_ID, "buy"), BuyTask::new, () -> Icon.getIcon("minecraft:item/gold_nugget"));
}
