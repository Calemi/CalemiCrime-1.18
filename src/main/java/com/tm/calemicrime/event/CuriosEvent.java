package com.tm.calemicrime.event;

import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicrime.main.CCReference;
import com.tm.calemicrime.main.CalemiCrime;
import com.tm.calemieconomy.main.CEReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import top.theillusivec4.curios.api.SlotTypeMessage;

@Mod.EventBusSubscriber(modid = CCReference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CuriosEvent {

    private static final ResourceLocation EMPTY_CAPE_SLOT = new ResourceLocation(CCReference.MOD_ID, "gui/empty_cape_slot");

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        event.addSprite(EMPTY_CAPE_SLOT);
    }

    @SubscribeEvent
    public static void sendImc(InterModEnqueueEvent event) {

        LogHelper.log(CEReference.MOD_NAME, "Curios Loaded: " + CalemiCrime.isCuriosLoaded);

        if (CalemiCrime.isCuriosLoaded) {

            LogHelper.log(CEReference.MOD_NAME, "Found Curios. Adding Cape Slot.");
            InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("cape").icon(EMPTY_CAPE_SLOT).build());
        }
    }
}
