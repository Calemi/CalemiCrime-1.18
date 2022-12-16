package com.tm.calemicrime.item.drug;

import com.tm.calemicrime.main.CalemiCrime;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class ItemDrug extends Item implements IItemDrug {

    public ItemDrug() {
        super(new Item.Properties().tab(CalemiCrime.TAB));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        onConsumed(player, 0);
        player.getCooldowns().addCooldown(this, getDuration() - (10 * 20));
        player.getItemInHand(hand).shrink(1);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
