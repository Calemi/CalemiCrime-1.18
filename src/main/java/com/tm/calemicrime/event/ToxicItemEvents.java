package com.tm.calemicrime.event;

import com.tm.calemicrime.init.InitFluids;
import com.tm.calemicrime.item.ItemToxic;
import com.tm.calemicrime.main.CalemiCrime;
import com.tm.calemicrime.util.HazardHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
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
            event.getToolTip().add(new TextComponent(ChatFormatting.DARK_GRAY + "" + ChatFormatting.ITALIC + "Holding this item without full set of Hazmat gear will poison you!"));
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

        if (event.getEntityLiving().getLevel().getGameTime() % 20 == 0) {

            if (event.getEntityLiving() instanceof Player player) {

                for (ItemStack stack : player.getInventory().items) {

                    float protection = HazardHelper.getHazardProtection(player);

                    if (isToxicItem(stack) && protection < 1) {

                        player.hurt(CalemiCrime.RADIATION, 1 - (1 * protection));
                        return;
                    }
                }
            }
        }
    }
}
