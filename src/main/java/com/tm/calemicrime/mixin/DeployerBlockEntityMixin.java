package com.tm.calemicrime.mixin;

import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DeployerBlockEntity.class)
public abstract class DeployerBlockEntityMixin {

    @Shadow
    protected DeployerFakePlayer player;

    @Shadow
    protected List<ItemStack> overflowItems;

    @Inject(method = "activate", at = @At("HEAD"), remap = false, cancellable = true)
    private void activate(CallbackInfo ci) {

        ItemStack heldStack = player.getMainHandItem();

        if (!heldStack.isEmpty() && heldStack.getItem() == Items.BUCKET) {
            ci.cancel();
        }
    }
}
