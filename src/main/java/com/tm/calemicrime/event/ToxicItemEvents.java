package com.tm.calemicrime.event;

import com.tm.calemicrime.init.InitFluids;
import com.tm.calemicrime.item.ItemGasMask;
import com.tm.calemicrime.item.ItemToxic;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.smeltery.item.TankItem;

public class ToxicItemEvents {

    @SubscribeEvent
    public void onToxicLore(ItemTooltipEvent event) {

        if (isToxicItem(event.getItemStack())) {
            event.getToolTip().add(new TextComponent(ChatFormatting.GREEN + "TOXIC"));
            event.getToolTip().add(new TextComponent(ChatFormatting.DARK_GRAY + "" + ChatFormatting.ITALIC + "Holding this item without a Gas Mask will poison you!"));
        }
    }

    private boolean isToxicItem(ItemStack stack) {

        if (stack.getItem() instanceof ItemToxic) {
            return true;
        }

        else if (stack.getItem() instanceof BucketItem bucket) {
            return bucket.getFluid() == InitFluids.METHYLAMINE.get() || bucket.getFluid() == InitFluids.ERGOTAMINE.get();
        }

        FluidStack fluid = TankItem.getFluidTank(stack).getFluid();
        return fluid.getFluid() == InitFluids.METHYLAMINE.get() || fluid.getFluid() == InitFluids.ERGOTAMINE.get();
    }

    @SubscribeEvent
    public void onPlayerHoldingToxicItem(LivingEvent.LivingUpdateEvent event) {

        if (event.getEntityLiving() instanceof Player player) {

            for (ItemStack stack : player.getInventory().items) {

                if (isToxicItem(stack) && !(player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ItemGasMask)) {
                    player.addEffect(new MobEffectInstance(MobEffects.POISON, 20 * 60));
                    return;
                }
            }
        }
    }
}
