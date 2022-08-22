package com.tm.calemicrime.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tm.calemicrime.blockentity.BlockEntityRegionProtector;
import com.tm.calemicrime.util.RegionRuleSet;
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

    public RenderRegionProtector(BlockEntityRendererProvider.Context pContext) {}

    @Override
    public void render(BlockEntityRegionProtector regionProtector, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {

        if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {

            if (regionProtector != null && regionProtector.getRegionOffset() != null && regionProtector.getRegionSize() != null) {

                if (regionProtector.isGlobal()) {
                    return;
                }

                Vec3 color = new Vec3(1, 1, 1);

                if (regionProtector.getRegionRuleSet().ruleSets[5] == RegionRuleSet.RuleOverrideType.ALLOW) {
                    color = new Vec3(1, 0, 0);
                }

                else if (regionProtector.getRegionType() == BlockEntityRegionProtector.RegionType.RESIDENTIAL) {
                    color = new Vec3(0, 1, 0);
                }

                else if (regionProtector.getRegionType() == BlockEntityRegionProtector.RegionType.COMMERCIAL) {
                    color = new Vec3(0, 0, 1);
                }

                VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.lines());
                LevelRenderer.renderLineBox(poseStack, vertexconsumer,
                        regionProtector.getRegionOffset().x, regionProtector.getRegionOffset().y, regionProtector.getRegionOffset().z,
                        regionProtector.getRegionOffset().x + regionProtector.getRegionSize().x, regionProtector.getRegionOffset().y + regionProtector.getRegionSize().y, (float) (regionProtector.getRegionOffset().z + regionProtector.getRegionSize().z),
                        (float)color.x, (float)color.y, (float)color.z, 1F);
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
