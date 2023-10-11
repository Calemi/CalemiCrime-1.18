package com.tm.calemicrime.item.drug;

import com.tm.calemicrime.block.base.BlockItemBase;
import com.tm.calemicrime.init.InitItems;
import com.tm.calemicrime.init.InitMobEffects;
import com.tm.calemicrime.main.CCConfig;
import com.tm.calemicrime.main.CalemiCrime;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ItemMushroom extends BlockItemBase implements IItemDrug {

    public ItemMushroom() {
        super(InitItems.PSILOCYBIN_MUSHROOM.get(), CalemiCrime.TAB);
    }

    @Override
    public int getDuration() {
        return CCConfig.drugs.mushroomEffectDuration.get() * 20;
    }

    @Override
    public List<MobEffectInstance> getEffects(int additiveDuration) {
        List<MobEffectInstance> effects = new ArrayList<>();
        effects.add(new MobEffectInstance(InitMobEffects.MUSHROOM_HIGH.get(), getDuration() + additiveDuration));
        effects.add(new MobEffectInstance(MobEffects.DAMAGE_BOOST,  getDuration() + additiveDuration));
        return effects;
    }

    @Override
    public void onExpired(Player player) {}

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        onConsumed(player, 0);
        player.getCooldowns().addCooldown(this, getDuration() - (10 * 20));
        player.getItemInHand(hand).shrink(1);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
