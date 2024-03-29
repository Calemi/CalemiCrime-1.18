package com.tm.calemicrime.event;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.tm.calemicrime.file.DryingRackRecipesFile;
import com.tm.calemicrime.init.InitItems;
import com.tm.calemicrime.item.drug.IItemDrug;
import com.wildcard.buddycards.item.BuddycardItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.Map;

public class ItemTooltipEvents {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onInformationEvent(ItemTooltipEvent event) {

        Item item = event.getItemStack().getItem();

        if (item instanceof IItemDrug drug) {

            if (drug.getEffects(0).size() > 1) {

                event.getToolTip().add(new TextComponent(ChatFormatting.GRAY + "When consumed:"));

                List<Pair<Attribute, AttributeModifier>> list1 = Lists.newArrayList();

                for (int i = 1; i < drug.getEffects(0).size(); i++){

                    MobEffectInstance effectInstance = drug.getEffects(0).get(i);

                    MutableComponent mutablecomponent = new TranslatableComponent(effectInstance.getDescriptionId());
                    MobEffect mobeffect = effectInstance.getEffect();
                    Map<Attribute, AttributeModifier> map = mobeffect.getAttributeModifiers();

                    if (!map.isEmpty()) {

                        for(Map.Entry<Attribute, AttributeModifier> entry : map.entrySet()) {
                            AttributeModifier attributemodifier = entry.getValue();
                            AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), mobeffect.getAttributeModifierValue(effectInstance.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                            list1.add(new Pair<>(entry.getKey(), attributemodifier1));
                        }
                    }

                    if (effectInstance.getAmplifier() > 0) {
                        mutablecomponent = new TranslatableComponent("potion.withAmplifier", mutablecomponent, new TranslatableComponent("potion.potency." + effectInstance.getAmplifier()));
                    }

                    if (effectInstance.getDuration() > 20) {
                        mutablecomponent = new TranslatableComponent("potion.withDuration", mutablecomponent, MobEffectUtil.formatDuration(effectInstance, 1));
                    }

                    event.getToolTip().add(mutablecomponent.withStyle(mobeffect.getCategory().getTooltipFormatting()));
                }
            }
        }

        for (DryingRackRecipesFile.DryingRackRecipe recipe : DryingRackRecipesFile.list) {

            if (item == recipe.getResult().getItem()) {
                event.getToolTip().add(new TextComponent(ChatFormatting.GRAY + "Obtained by drying " + highlight(recipe.getIngredient().getHoverName().getString()) + " on a " + highlight("Drying Rack")));
            }
        }

        if (item == InitItems.CANNABIS_LEAF.get()) {
            event.getToolTip().add(new TextComponent(ChatFormatting.GRAY + "Obtained by harvesting " + highlight("Cannabis Plant")));
            event.getToolTip().add(new TextComponent(ChatFormatting.GRAY + "Hold "  + highlight("Shears") + " & Use on " + highlight("Cannabis Plant")));
        }

        else if (item == InitItems.COCA_LEAF.get()) {
            event.getToolTip().add(new TextComponent(ChatFormatting.GRAY + "Obtained by harvesting " + highlight("Coca Plant")));
            event.getToolTip().add(new TextComponent(ChatFormatting.GRAY + "Hold " + highlight("Shears") + " & Use on " + highlight("Coca Plant")));
        }

        else if (item == InitItems.CRACKED_TANIUN.get()) {
            event.getToolTip().add(new TextComponent(ChatFormatting.GRAY + "Obtained by cracking " + highlight("Taniun Soaked Seeds") +" in " + highlight("Lava")));
            event.getToolTip().add(new TextComponent(ChatFormatting.GRAY + "Hold " + highlight("Taniun Soaked Seeds") + " & Hold Use on " + highlight("Lava")));
        }

        if (item instanceof BuddycardItem card) {

            if (card.getSet() == InitItems.CRIME_SET) {

                event.getToolTip().remove(1);

                if (Screen.hasShiftDown()) {

                    if (card.getCardNumber() == 1) addCardLore(event.getToolTip(), "Taniun L O R E");
                }

                else event.getToolTip().add(new TextComponent(ChatFormatting.GRAY + "Hold " + highlight("[SHIFT]") + ChatFormatting.GRAY + " to show card lore."));
            }
        }
    }

    private static void addCardLore(List<Component> tooltip, String lore) {
        tooltip.add(new TextComponent(lore));
    }

    private static String highlight(String value) {
        return ChatFormatting.YELLOW + value + ChatFormatting.GRAY;
    }
}
