package com.tm.calemicrime.client;

import com.tm.calemicrime.main.CCReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CCReference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public enum GunModels {

    DONOR_PISTOL("gun/donor_pistol"),
    R99("gun/r99");

    private final ResourceLocation modelLoc;
    private BakedModel cachedModel;

    GunModels(String modelName) {
        this.modelLoc = new ResourceLocation(CCReference.MOD_ID, "special/" + modelName);
    }

    public BakedModel getModel() {
        if (this.cachedModel == null) {
            this.cachedModel = Minecraft.getInstance().getModelManager().getModel(this.modelLoc);
        }
        return this.cachedModel;
    }

    @SubscribeEvent
    public static void onRegisterAdditionalModelEvent(final ModelRegistryEvent event) {
        for (GunModels specialModel : values()) {
            ForgeModelBakery.addSpecialModel(specialModel.modelLoc);
        }
    }

    @SubscribeEvent
    public static void onBakingCompletedEvent(final ModelBakeEvent event) {
        for (GunModels specialModel : values()) {
            specialModel.cachedModel = null;
        }
    }
}