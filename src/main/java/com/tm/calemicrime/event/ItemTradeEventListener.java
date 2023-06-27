package com.tm.calemicrime.event;

import com.tm.calemicrime.ftbquests.BuyTask;
import com.tm.calemicrime.ftbquests.SellTask;
import com.tm.calemieconomy.event.ItemTradeEvent;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.latvian.mods.itemfilters.api.IItemFilter;
import dev.latvian.mods.itemfilters.api.ItemFiltersAPI;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class ItemTradeEventListener {

    @SubscribeEvent
    public void onItemSold (ItemTradeEvent.Sell event) {

        if (event.getPlayer().getLevel().isClientSide()) {
            return;
        }

        TeamData data = ServerQuestFile.INSTANCE.getData(event.getPlayer());

        List<SellTask> sellTasks = ServerQuestFile.INSTANCE.collect(SellTask.class);

        for (SellTask sellTask : sellTasks) {

            IItemFilter filter = ItemFiltersAPI.getFilter(sellTask.item);

            if (!data.isCompleted(sellTask)) {

                if (filter != null && filter.filter(sellTask.item, event.getStack())) {
                    data.addProgress(sellTask, event.getAmount());
                }

                else if (event.getStack().sameItem(sellTask.item)) {
                    data.addProgress(sellTask, event.getAmount());
                }
            }
        }
    }

    @SubscribeEvent
    public void onItemBought (ItemTradeEvent.Buy event) {

        if (event.getPlayer().getLevel().isClientSide()) {
            return;
        }

        TeamData data = ServerQuestFile.INSTANCE.getData(event.getPlayer());

        List<BuyTask> buyTasks = ServerQuestFile.INSTANCE.collect(BuyTask.class);

        for (BuyTask buyTask : buyTasks) {

            IItemFilter filter = ItemFiltersAPI.getFilter(buyTask.item);

            if (!data.isCompleted(buyTask)) {

                if (filter != null && filter.filter(buyTask.item, event.getStack())) {
                    data.addProgress(buyTask, event.getAmount());
                }

                else if (event.getStack().sameItem(buyTask.item)) {
                    data.addProgress(buyTask, event.getAmount());
                }
            }
        }
    }
}