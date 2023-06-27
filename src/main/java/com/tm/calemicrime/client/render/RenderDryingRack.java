package com.tm.calemicrime.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.tm.calemicore.util.render.RenderedItemStack;
import com.tm.calemicrime.block.BlockDryingRack;
import com.tm.calemicrime.blockentity.BlockEntityDryingRack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderDryingRack implements BlockEntityRenderer<BlockEntityDryingRack> {

    private final RenderedItemStack renderedItemStack = new RenderedItemStack();

    public RenderDryingRack(BlockEntityRendererProvider.Context pContext) {}

    @Override
    public void render(BlockEntityDryingRack rack, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {

        if (!(Minecraft.getInstance().player.distanceToSqr(rack.getLocation().getVector()) > 500.0D)) {

            this.renderedItemStack.setStack(rack.dryingStack);

            if (!rack.dryingStack.isEmpty() && rack.getLocation().getBlockState().hasProperty(BlockDryingRack.FACING)) {

                poseStack.pushPose();


                Direction facing = rack.getLocation().getBlockState().getValue(BlockDryingRack.FACING);

                float rotation = 0;

                switch (facing) {
                    default -> poseStack.translate(0.5D, 0.25D, 0.95D);
                    case EAST -> {
                        poseStack.translate(0.05D, 0.25D, 0.5D);
                        poseStack.mulPose(Quaternion.fromXYZDegrees(new Vector3f(0, 90, 0)));
                    }
                    case SOUTH -> {
                        poseStack.translate(0.5D, 0.25D, 0.05D);
                        poseStack.mulPose(Quaternion.fromXYZDegrees(new Vector3f(0, 180, 0)));
                    }
                    case WEST -> {
                        poseStack.translate(0.95D, 0.25D, 0.5D);
                        poseStack.mulPose(Quaternion.fromXYZDegrees(new Vector3f(0, 270, 0)));
                    }
                }

                poseStack.scale(1.5F, 1.5F, 1.5F);

                this.renderedItemStack.render(poseStack, buffer, packedLight, packedOverlay);

                poseStack.popPose();
            }

        }
    }
}
