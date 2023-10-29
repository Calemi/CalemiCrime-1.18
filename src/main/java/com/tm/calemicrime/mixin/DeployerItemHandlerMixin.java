package com.tm.calemicrime.mixin;

import com.jaquadro.minecraft.storagedrawers.item.ItemDrawers;
import com.simibubi.create.content.kinetics.deployer.DeployerItemHandler;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DeployerItemHandler.class)
public abstract class DeployerItemHandlerMixin {

    @Inject(method = "set", at = @At("HEAD"), remap = false, cancellable = true)
    private void set(@NotNull ItemStack stack, CallbackInfo ci) {

        if (stack.getItem() instanceof ItemDrawers) {
            ci.cancel();
        }

       if (stack.getCount() > stack.getMaxStackSize()) {
            ci.cancel();
       }
    }

    @Inject(method = "insertItem", at = @At("HEAD"), remap = false, cancellable = true)
    private void insertItem(int slot, @NotNull ItemStack stack, boolean simulate, CallbackInfoReturnable<ItemStack> cir) {

        if (stack.getItem() instanceof ItemDrawers) {
            cir.setReturnValue(stack);
        }

        if (stack.getCount() > stack.getMaxStackSize()) {
            cir.setReturnValue(stack);
        }
    }
}
