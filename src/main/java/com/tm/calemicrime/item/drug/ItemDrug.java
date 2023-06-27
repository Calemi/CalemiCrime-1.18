package com.tm.calemicrime.item.drug;

import com.tm.calemicrime.main.CCConfig;
import com.tm.calemicrime.main.CalemiCrime;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public abstract class ItemDrug extends Item implements IItemDrug {

    public ItemDrug() {
        super(new Item.Properties().tab(CalemiCrime.TAB));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {

        if (livingEntity instanceof Player player ) {
            onConsumed(player, 0);
            player.getUseItem().shrink(1);
        }

        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return CCConfig.drugs.timeToConsumeDrug.get() * 20;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }
}
