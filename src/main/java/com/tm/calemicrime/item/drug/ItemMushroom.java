package com.tm.calemicrime.item.drug;

import com.tm.calemicrime.block.base.BlockItemBase;
import com.tm.calemicrime.init.InitItems;
import com.tm.calemicrime.init.InitMobEffects;
import com.tm.calemicrime.main.CalemiCrime;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemMushroom extends BlockItemBase implements IItemDrug {

    public ItemMushroom() {
        super(InitItems.PSILOCYBIN_MUSHROOM.get(), CalemiCrime.TAB);
    }

    @Override
    public int getDuration() {
        return 90 * 20;
    }

    @Override
    public void onConsumed(Player player, int additiveDuration) {
        player.addEffect(new MobEffectInstance(InitMobEffects.MUSHROOM_HIGH.get(), getDuration() + additiveDuration));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,  getDuration() + additiveDuration));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.getCooldowns().addCooldown(this, getDuration() - (10 * 20));
        player.getItemInHand(hand).shrink(1);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
