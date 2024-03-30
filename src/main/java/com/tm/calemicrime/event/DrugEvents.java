package com.tm.calemicrime.event;

import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicrime.effect.DrugHighEffect;
import com.tm.calemicrime.item.drug.IItemDrug;
import com.tm.calemicrime.main.CCReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DrugEvents {

    @SubscribeEvent
    public void onDruggedFoodConsumed(LivingEntityUseItemEvent.Finish event) {

        CompoundTag nbt = event.getItem().getOrCreateTag();

        if (nbt.contains("Drug")) {

            for (String string : nbt.getString("Drug").split("%")) {

                Item item = Registry.ITEM.get(new ResourceLocation(string));

                if (item instanceof IItemDrug drug) {

                    FoodProperties properties = event.getItem().getFoodProperties(event.getEntityLiving());

                    float saturationRestored = properties.getSaturationModifier() * 2 * properties.getNutrition();
                    float totalRestored = properties.getNutrition() + saturationRestored;

                    drug.onConsumed((Player)event.getEntityLiving(), (int) ((totalRestored) * 40));
                }
            }
        }
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {

        if (!(event.getEntityLiving() instanceof Player)) {
            return;
        }

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

    @SubscribeEvent
    public void onEffectExpire(PotionEvent.PotionExpiryEvent event) {

        if (event.getEntityLiving() == null || event.getPotionEffect() == null) {
            return;
        }

        LogHelper.log(CCReference.MOD_NAME, "EXPIRE");

        if (event.getPotionEffect().getEffect() instanceof DrugHighEffect highEffect && event.getEntityLiving() instanceof Player player) {
            highEffect.item.onExpired(player);
        }
    }

    @SubscribeEvent
    public void onEffectRemoved(PotionEvent.PotionRemoveEvent event) {

        if (event.getEntityLiving() == null || event.getPotionEffect() == null) {
            return;
        }

        LogHelper.log(CCReference.MOD_NAME, "REMOVE");

        if (event.getPotionEffect().getEffect() instanceof DrugHighEffect highEffect && event.getEntityLiving() instanceof Player player) {
            highEffect.item.onExpired(player);
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


            renderer.loadEffect(new ResourceLocation("shaders/post/" + shaderName + ".json"));
    }
}
