package com.tm.calemicrime.ftbquests;

import com.tm.calemicrime.init.InitTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.IconAnimation;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.math.Bits;
import dev.ftb.mods.ftbquests.item.FTBQuestsItems;
import dev.ftb.mods.ftbquests.net.FTBQuestsNetHandler;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import dev.ftb.mods.ftbquests.util.NBTUtils;
import dev.latvian.mods.itemfilters.api.ItemFiltersAPI;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SellTask extends Task {

    public ItemStack item;
    public long count;

    public SellTask(Quest quest) {
        super(quest);
        item = ItemStack.EMPTY;
        count = 1;
    }

    @Override
    public TaskType getType() {
        return InitTaskTypes.SELL;
    }

    @Override
    public long getMaxProgress() {
        return count;
    }

    @Override
    public void writeData(CompoundTag nbt) {
        super.writeData(nbt);
        NBTUtils.write(nbt, "item", item);

        if (count > 1) {
            nbt.putLong("count", count);
        }
    }

    @Override
    public void readData(CompoundTag nbt) {
        super.readData(nbt);
        item = NBTUtils.read(nbt, "item");
        count = Math.max(nbt.getLong("count"), 1L);
    }

    @Override
    public void writeNetData(FriendlyByteBuf buffer) {
        super.writeNetData(buffer);

        int flags = 0;
        flags = Bits.setFlag(flags, 0x01, count > 1L);
        buffer.writeVarInt(flags);

        FTBQuestsNetHandler.writeItemType(buffer, item);

        if (count > 1L) {
            buffer.writeVarLong(count);
        }
    }

    @Override
    public void readNetData(FriendlyByteBuf buffer) {
        super.readNetData(buffer);
        int flags = buffer.readVarInt();
        item = FTBQuestsNetHandler.readItemType(buffer);
        count = Bits.getFlag(flags, 0x01) ? buffer.readVarLong() : 1L;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getConfig(ConfigGroup config) {
        super.getConfig(config);
        config.addItemStack("item", item, v -> item = v, ItemStack.EMPTY, true, false).setNameKey("ftbquests.task.calemicrime.sell");
        config.addLong("count", count, v -> count = v, 1, 1, Long.MAX_VALUE).setNameKey("ftbquests.task.ftbquests.item.count");
    }

    @OnlyIn(Dist.CLIENT)
    public MutableComponent getAltTitle() {
        return new TextComponent("Sell x" + count + " " + item.getHoverName().getString());
    }

    @OnlyIn(Dist.CLIENT)
    public Icon getAltIcon() {
        List<Icon> icons = new ArrayList();
        Iterator var2 = this.getValidDisplayItems().iterator();

        while(var2.hasNext()) {
            ItemStack stack = (ItemStack)var2.next();
            ItemStack copy = stack.copy();
            copy.setCount(1);
            Icon icon = ItemIcon.getItemIcon(copy);
            if (!icon.isEmpty()) {
                icons.add(icon);
            }
        }

        if (icons.isEmpty()) {
            return ItemIcon.getItemIcon((Item) FTBQuestsItems.MISSING_ITEM.get());
        } else {
            return IconAnimation.fromList(icons, false);
        }
    }

    public List<ItemStack> getValidDisplayItems() {
        List<ItemStack> list = new ArrayList();
        ItemFiltersAPI.getDisplayItemStacks(this.item, list);
        return list;
    }
}
