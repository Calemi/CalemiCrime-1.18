package com.tm.calemicrime.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tm.calemicore.util.render.RenderedFloatingItemStack;
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
public class RenderRegionProtector implements BlockEntityRenderer<BlockEntityRegionProtector> {

    private final RenderedFloatingItemStack renderedItemStack = new RenderedFloatingItemStack();

    public RenderRegionProtector(BlockEntityRendererProvider.Context pContext) {}

    @Override
    public void render(BlockEntityRegionProtector regionProtector, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {

        if (Minecraft.getInstance().player.isCreative()) {

            if (regionProtector != null && regionProtector.getRegionOffset() != null && regionProtector.getRegionEdge() != null) {
                VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.lines());
                LevelRenderer.renderLineBox(poseStack, vertexconsumer,
                        regionProtector.getRegionOffset().x, regionProtector.getRegionOffset().y, regionProtector.getRegionOffset().z,
                        regionProtector.getRegionOffset().x + regionProtector.getRegionEdge().x, regionProtector.getRegionOffset().y + regionProtector.getRegionEdge().y, regionProtector.getRegionOffset().z + regionProtector.getRegionEdge().z,
                        1F, 1F, 1F, 1F);
            }
        }
    }

    @Override
    public boolean shouldRender(BlockEntityRegionProtector regionProtector, Vec3 cameraPos) {
        return true;
    }

    @Override
    public boolean shouldRenderOffScreen(BlockEntityRegionProtector regionProtector) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 96;
    }
}
