package com.tm.calemicrime.item;

import com.tm.calemicrime.client.screen.ScreenLootBox;
import com.tm.calemicrime.main.CalemiCrime;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ItemLootBox extends Item {

    public ItemLootBox() {
        super(new Properties().tab(CalemiCrime.TAB));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipList, TooltipFlag advanced) {

        String texture = stack.getOrCreateTag().getString("Pool");

        if (texture.equals("")) {
            texture = "generic";
        }

        tooltipList.add(new TextComponent(ChatFormatting.GOLD + "Pool: " + ChatFormatting.YELLOW + texture.toUpperCase()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        if (hand == InteractionHand.MAIN_HAND) {
            player.startUsingItem(hand);
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }

        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {

        if (livingEntity instanceof Player player ) {

            stack.shrink(1);

            String pool = stack.getOrCreateTag().getString("Pool");

            if (pool.equals("")) {
                pool = "generic";
            }

            if (level.isClientSide()) openOptionsGui(player, pool);
        }

        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 20 * 2;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }

    @OnlyIn(Dist.CLIENT)
    private void openOptionsGui(Player player, String pool) {
        Minecraft.getInstance().setScreen(new ScreenLootBox(player, pool));
    }
}
