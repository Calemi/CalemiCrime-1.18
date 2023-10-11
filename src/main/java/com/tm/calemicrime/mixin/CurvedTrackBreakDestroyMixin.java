package com.tm.calemicrime.mixin;

import com.simibubi.create.content.trains.track.CurvedTrackDestroyPacket;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicrime.main.CCReference;
import de.maxhenkel.car.entity.car.base.EntityCarBase;
import de.maxhenkel.car.entity.car.parts.PartWheelBase;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Surrogate;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CurvedTrackDestroyPacket.class)
public abstract class CurvedTrackBreakDestroyMixin {

    @Inject(method = "applySettings*", at = @At(value = "HEAD"), remap = false, cancellable = true)
    protected void applySettings(ServerPlayer player, TrackBlockEntity be, CallbackInfo info) {

        LogHelper.log(CCReference.MOD_NAME, "BREAK");

        if (!player.isCreative() && !player.isSpectator()) {
            info.cancel();
        }
    }
}
