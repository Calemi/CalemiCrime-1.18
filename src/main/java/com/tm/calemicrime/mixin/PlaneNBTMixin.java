package com.tm.calemicrime.mixin;

import com.tm.calemicrime.accessor.PlaneDataSaver;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.przemyk.simpleplanes.entities.PlaneEntity;

import java.util.UUID;

@Mixin(PlaneEntity.class)
public abstract class PlaneNBTMixin implements PlaneDataSaver {

    public UUID ownerID;

    @Override
    public UUID getOwnerID() {
        return ownerID;
    }

    @Override
    public void setOwnerID(UUID ownerID) {
        this.ownerID = ownerID;
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    public void readAdditionalSaveData(CompoundTag compound, CallbackInfo info) {

        if (compound.hasUUID("owner")) {
            ownerID = compound.getUUID("owner");
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    public void addAdditionalSaveData(CompoundTag compound, CallbackInfo info) {

        if (ownerID != null) {
            compound.putUUID("owner", ownerID);
        }
    }
}
