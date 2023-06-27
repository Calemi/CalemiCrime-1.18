package com.tm.calemicrime.ftbquests;

import com.tm.calemicrime.init.InitTaskTypes;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.math.Bits;
import dev.ftb.mods.ftbquests.net.FTBQuestsNetHandler;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import dev.ftb.mods.ftbquests.util.NBTUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
}
