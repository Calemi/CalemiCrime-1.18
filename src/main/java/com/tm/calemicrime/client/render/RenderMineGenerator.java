package com.tm.calemicrime.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tm.calemicore.util.render.RenderedFloatingItemStack;
import com.tm.calemicrime.blockentity.BlockEntityMineGenerator;
import com.tm.calemicrime.blockentity.BlockEntityRegionProtector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderMineGenerator implements BlockEntityRenderer<BlockEntityMineGenerator> {

    public RenderMineGenerator(BlockEntityRendererProvider.Context pContext) {}

    @Override
    public void render(BlockEntityMineGenerator mineGenerator, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {

        if (Minecraft.getInstance().player.isCreative()) {

            if (mineGenerator != null && mineGenerator.getRegionOffset() != null && mineGenerator.getRegionSize() != null) {
                VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.lines());
                LevelRenderer.renderLineBox(poseStack, vertexconsumer,
                        mineGenerator.getRegionOffset().x, mineGenerator.getRegionOffset().y, mineGenerator.getRegionOffset().z,
                        mineGenerator.getRegionOffset().x + mineGenerator.getRegionSize().x, mineGenerator.getRegionOffset().y + mineGenerator.getRegionSize().y, mineGenerator.getRegionOffset().z + mineGenerator.getRegionSize().z,
                        1F, 1F, 1F, 1F);
            }
        }
    }

    @Override
    public boolean shouldRender(BlockEntityMineGenerator mineGenerator, Vec3 cameraPos) {
        return true;
    }

    @Override
    public boolean shouldRenderOffScreen(BlockEntityMineGenerator mineGenerator) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 96;
    }
}
