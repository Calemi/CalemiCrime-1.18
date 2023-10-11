package com.tm.calemicrime.item;

import com.tm.calemicrime.integration.curios.CuriosIntegration;
import com.tm.calemicrime.main.CalemiCrime;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCape extends Item {

    public ItemCape() {
        super(new Properties().tab(CalemiCrime.TAB).stacksTo(1));
    }

    @Override
    public Component getName(ItemStack stack) {
        int patternID = stack.getOrCreateTag().getInt("Pattern");
        return new TranslatableComponent("item.calemicrime.cape." + patternID);
    }

    /*@Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipList, TooltipFlag advanced) {

        String texture = stack.getOrCreateTag().getString("Pattern");

        if (texture.equals("")) {
            texture = "normal_red";
        }

        tooltipList.add(new TextComponent(ChatFormatting.GOLD + "Pattern: " + ChatFormatting.YELLOW + texture.toUpperCase()));
    }*/

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @org.jetbrains.annotations.Nullable CompoundTag nbt) {

        if (CalemiCrime.isCuriosLoaded) {
            return CuriosIntegration.capeCapability();
        }

        return super.initCapabilities(stack, nbt);
    }
}
