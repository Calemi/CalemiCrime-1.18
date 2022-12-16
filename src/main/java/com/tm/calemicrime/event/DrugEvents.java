package com.tm.calemicrime.event;

import com.tm.calemicrime.effect.DrugHighEffect;
import com.tm.calemicrime.item.drug.IItemDrug;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DrugEvents {

    @SubscribeEvent
    public void onDruggedFoodConsumed(LivingEntityUseItemEvent.Finish event) {

        CompoundTag nbt = event.getItem().getOrCreateTag();

        if (nbt.contains("Drug")) {

            Item item = Registry.ITEM.get(new ResourceLocation(nbt.getString("Drug")));

            if (item instanceof IItemDrug drug) {

                FoodProperties properties = event.getItem().getFoodProperties(event.getEntityLiving());

                float saturationRestored = properties.getSaturationModifier() * 2 * properties.getNutrition();
                float totalRestored = properties.getNutrition() + saturationRestored;

                drug.onConsumed((Player)event.getEntityLiving(), (int) ((totalRestored) * 40));
            }
        }
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {

        Level level = event.getEntityLiving().getLevel();

        if (level.isClientSide() && level.getGameTime() % 10 == 0) {

            LocalPlayer player = Minecraft.getInstance().player;

            for (MobEffectInstance mobEffectInstance : player.getActiveEffects()) {

                if (mobEffectInstance.getEffect() instanceof DrugHighEffect drug) {
                    setShader(drug.shaderName);
                    return;
                }
            }

            clearShader();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void clearShader() {

        GameRenderer renderer = Minecraft.getInstance().gameRenderer;

        if (renderer.currentEffect() != null) {
            renderer.shutdownEffect();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void setShader(String shaderName) {

        GameRenderer renderer = Minecraft.getInstance().gameRenderer;

        if (renderer.currentEffect() == null || !renderer.currentEffect().getName().equals("minecraft:shaders/post/" + shaderName + ".json")) {
            renderer.loadEffect(new ResourceLocation("shaders/post/" + shaderName + ".json"));
        }
    }
}
